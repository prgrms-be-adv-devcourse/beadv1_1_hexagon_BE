package com.example.searchservice.commission.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.dto.CommissionSearchFilter;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.commission.vo.OpenStatus;
import com.example.searchservice.common.vo.SearchScope;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.vo.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient esClient;


    public Page<CommissionResponseDto> search(String query, SearchScope scope, int page, int size) {

        if (query == null || query.isBlank()) {
            PageRequest sortedByUpdatedAt = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );

            return commissionRepository.findAll(sortedByUpdatedAt)
                    .map(CommissionResponseDto::from);
        }

        PageRequest pageable = PageRequest.of(page, size);

        return switch (scope) {
            case all -> commissionRepository.searchAll(query, pageable).map(CommissionResponseDto::from);
            case title -> commissionRepository.searchTitle(query, pageable).map(CommissionResponseDto::from);
            case content -> commissionRepository.searchContent(query, pageable).map(CommissionResponseDto::from);
        };
    }

    @Override
    public Page<CommissionResponseDto> search(
            String query,
            CommissionSearchFilter filter,
            int page,
            int size) {

        SearchScope scope = filter.scope();
        List<String> tags = filter.tags();
        PaymentType paymentType = filter.paymentType();
        Long minPay = filter.minPay();
        LocalDate startedAt = filter.startedAt();
        LocalDate endedAt = filter.endedAt();
        OpenStatus openStatus = filter.openStatus();

        boolean hasQuery = (query != null && !query.isEmpty());
        boolean hasTagsFilter = (tags != null && !tags.isEmpty());
        boolean hasStartFilter = (startedAt != null);
        boolean hasEndFilter = (endedAt != null);

        PageRequest pageRequest = PageRequest.of(page, size);

        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(qry -> qry.bool(boolQuery -> {

                    switch (openStatus) {
                        case open -> boolQuery.filter(
                                TermQuery.of(t -> t
                                        .field("is_open")
                                        .value(true)
                                )._toQuery()
                        );
                        case closed -> boolQuery.filter(
                                TermQuery.of(t -> t
                                        .field("is_open")
                                        .value(false)
                                )._toQuery()
                        );
                        case all -> {
                            // 상태 필터 안 건다 (열린 + 마감 전체)
                        }
                    }

                    if (hasQuery) { // 검색문이 있는 경우 must 쿼리 추가
                        switch (scope) {
                            case all -> boolQuery.must(
                                    MultiMatchQuery.of(m -> m
                                            .query(query)
                                            .fields("title^2", "content")
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                            case title -> boolQuery.must(
                                    MatchQuery.of(m -> m
                                            .field("title")
                                            .query(query)
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                            case content -> boolQuery.must(
                                    MatchQuery.of(m -> m
                                            .field("content")
                                            .query(query)
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                        }
                    }

                    if (hasTagsFilter) {
                        boolQuery.filter(
                                TermsQuery.of(fn -> fn
                                        .field("tags")
                                        .terms(ts -> ts.value(
                                                tags.stream()
                                                        .map(FieldValue::of)
                                                        .toList()
                                        ))
                                )._toQuery()
                        );
                    }

                    if (paymentType != null) { // 급여 지급 방식이 있는 경우 filter 쿼리 추가
                        boolQuery.filter(
                                TermQuery.of(t -> t
                                        .field("payment_type")
                                        .value(paymentType.name())
                                )._toQuery()
                        );
                    }

                    if (minPay != null) { // 급여가 있는 경우 filter 쿼리 하나 더 추가
                        boolQuery.filter(
                                RangeQuery.of(r -> r
                                        .number(n -> n
                                                .field("pay_amount")
                                                .gte(minPay.doubleValue())
                                        )
                                )._toQuery()
                        );
                    }

                    if (hasStartFilter) {
                        boolQuery.filter(
                                RangeQuery.of(fn -> fn
                                        .date(dr -> dr
                                                .field("started_at")
                                                .gte(startedAt.toString())
                                        )
                                )._toQuery()
                        );
                    }

                    if (hasEndFilter) {
                        boolQuery.filter(
                                RangeQuery.of(fn -> fn
                                        .date(dr -> dr
                                                .field("ended_at")
                                                .lte(endedAt.toString())
                                        )
                                )._toQuery()
                        );
                    }

                    return boolQuery;
                }))
                .withPageable(pageRequest);

        if(!hasQuery) {
            nativeQueryBuilder.withSort(sort -> sort
                    .field(f -> f
                            .field("updated_at")
                            .order(SortOrder.Desc)
                    )
            );
        }

        NativeQuery nativeQuery = nativeQueryBuilder.build();

        SearchHits<CommissionDocumentEntity> hits =
                elasticsearchOperations.search(nativeQuery, CommissionDocumentEntity.class);

        List<CommissionResponseDto> content = hits.getSearchHits().stream()
                .map(hit -> CommissionResponseDto.from(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageRequest, hits.getTotalHits());
    }

    @Override
    public List<String> getSuggestions(String query, int size) {
        SearchHits<CommissionDocumentEntity> searchHits = commissionRepository.autoComplete(query);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CommissionDocumentEntity::getTitle)
                .map(this::extractNounsWithEs)
                .distinct()
                .limit(size)
                .toList();
    }

    @Override
    public void saveAll(List<CommissionDocumentEntity> commissions) {
        commissionRepository.saveAll(commissions);
    }

    @Override
    public void save(CommissionDocumentEntity commission) {
        commissionRepository.save(commission);
    }

    @Override
    public void update(CommissionDocumentEntity commission) {
        commissionRepository.save(commission);
    }

    @Override
    public void delete(String code) {
        commissionRepository.deleteById(code);
    }

    // ES에 _analyze 요청 보내 "korean_noun_analyzer" 통해 명사만 남기는 로직
    private String extractNounsWithEs(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        try {
            AnalyzeRequest req = AnalyzeRequest.of(a -> a
                    .index("commissions")            // 인덱스 이름
                    .analyzer("korean_noun_analyzer") // 네가 정의한 analyzer
                    .text(text)
            );

            AnalyzeResponse response = esClient
                    .indices()
                    .analyze(req);

            List<String> tokens = response.tokens()
                    .stream()
                    .map(AnalyzeToken::token)
                    .toList();

            return String.join(" ", tokens);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
