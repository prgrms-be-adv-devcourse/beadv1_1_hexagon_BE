package com.example.searchservice.commission.service;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.common.vo.SearchScope;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;

    @Override
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
            case all       -> commissionRepository.searchAll(query, pageable).map(CommissionResponseDto::from);
            case title     -> commissionRepository.searchTitle(query, pageable).map(CommissionResponseDto::from);
            case content   -> commissionRepository.searchContent(query, pageable).map(CommissionResponseDto::from);
        };
    }

    @Override
    public List<String> getSuggestions(String query, int size) {
        SearchHits<CommissionDocumentEntity> searchHits = commissionRepository.autoComplete(query);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CommissionDocumentEntity::getTitle)
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
        commissionRepository.findById(commission.getCode())
                .ifPresentOrElse(existing -> {
                    existing.setCode(commission.getCode());
                    existing.setTitle(commission.getTitle());
                    existing.setContent(commission.getContent());
                    existing.setMemberCode(commission.getMemberCode());
                    existing.setMemberNickname(commission.getMemberNickname());
                    existing.setTags(commission.getTags());
                    existing.setStartedAt(commission.getStartedAt());
                    existing.setEndedAt(commission.getEndedAt());
                    existing.setPaymentType(commission.getPaymentType());
                    existing.setPayAmount(commission.getPayAmount());
                    existing.setIsClosed(commission.getIsClosed());
                    existing.setUpdatedAt(commission.getUpdatedAt());

                    commissionRepository.save(existing);
                }, () -> {
                    commissionRepository.save(commission);
                });
    }

    @Override
    public void delete(String code) {
        commissionRepository.deleteById(code);
    }
}
