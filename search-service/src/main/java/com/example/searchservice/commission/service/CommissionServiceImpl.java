package com.example.searchservice.commission.service;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
}
