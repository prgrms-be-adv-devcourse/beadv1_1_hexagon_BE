package com.example.searchservice.selfpromotion.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import co.elastic.clients.elasticsearch.indices.analyze.AnalyzeToken;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.entity.SelfPromotionDocumentEntity;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.vo.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfPromotionServiceImpl implements SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticsearchClient esClient;

    @Override
    public Page<SelfPromotionResponseDto> search(
            String query,
            SearchScope scope,
            PaymentType paymentType,
            Long maxPay,
            int page,
            int size)
    {
        boolean hasQuery = query != null && !query.isEmpty(); // 검색문이 있는지 여부
        boolean hasPayFilter = (paymentType != null || maxPay != null); // 급여 필터가 있는지 여부

        // 검색문, 급여필터가 없다면 전체 검색
        if(!hasQuery && !hasPayFilter) {
            PageRequest sortedByUpdatedAt = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.DESC, "updatedAt")
            );

            return selfPromotionRepository.findAll(sortedByUpdatedAt)
                    .map(SelfPromotionResponseDto::from);
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        /*
         * "query": {
         *   "bool": {
         *     "must":   [ { ...multi_match or match... } ], 검색문이 있는 경우 must 쿼리 추가
         *     "filter": [
         *       { "term":  { "payment_type": "..." } }, 급여 지급 방식이 있는 경우 filter 쿼리 추가
         *       { "range": { "pay_amount": { "lte": ... } } } 급여가 있는 경우 filter 쿼리 하나 더 추가
         *     ]
         *   }
         * }
         */

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    if(hasQuery) { // 검색문이 있는 경우 must 쿼리 추가
                        switch (scope) {
                            case ALL -> b.must(
                                    MultiMatchQuery.of(m -> m
                                            .query(query)
                                            .fields("title^2", "content")
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                            case TITLE -> b.must(
                                    MatchQuery.of(m -> m
                                            .field("title")
                                            .query(query)
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                            case CONTENT -> b.must(
                                    MatchQuery.of(m -> m
                                            .field("content")
                                            .query(query)
                                            .minimumShouldMatch("2<70%")
                                            .fuzziness("1")
                                    )._toQuery()
                            );
                        }
                    }

                    if (paymentType != null) { // 급여 지급 방식이 있는 경우 filter 쿼리 추가
                        b.filter(
                                TermQuery.of(t -> t
                                        .field("payment_type")
                                        .value(paymentType.name())
                                )._toQuery()
                        );
                    }

                    if (maxPay != null) { // 급여가 있는 경우 filter 쿼리 하나 더 추가
                        b.filter(
                                RangeQuery.of(r -> r
                                        .number(n -> n
                                                .field("pay_amount")
                                                .lte(maxPay.doubleValue())
                                        )
                                )._toQuery()
                        );
                    }

                    return b;
                }))
                .withPageable(pageRequest)
                .build();

        SearchHits<SelfPromotionDocumentEntity> hits =
                elasticsearchOperations.search(nativeQuery, SelfPromotionDocumentEntity.class);

        List<SelfPromotionResponseDto> content = hits.getSearchHits().stream()
                .map(hit -> SelfPromotionResponseDto.from(hit.getContent()))
                .toList();

        return new PageImpl<>(content, pageRequest, hits.getTotalHits());
    }

    @Override
    public List<String> getSuggestions(String query, int size) {
        SearchHits<SelfPromotionDocumentEntity> searchHits = selfPromotionRepository.autoComplete(query);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(SelfPromotionDocumentEntity::getTitle)
                .map(this::extractNounsWithEs)
                .distinct()
                .limit(size)
                .toList();
    }

    @Override
    public void saveAll(List<SelfPromotionDocumentEntity> selfPromotions) {
        selfPromotionRepository.saveAll(selfPromotions);
    }

    @Override
    public void save(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    @Override
    public void update(SelfPromotionDocumentEntity selfPromotion) {
        selfPromotionRepository.save(selfPromotion);
    }

    @Override
    public void delete(String code) {
        selfPromotionRepository.deleteById(code);
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
