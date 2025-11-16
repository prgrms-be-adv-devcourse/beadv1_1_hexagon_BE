package com.example.searchservice.commission.service;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.common.vo.SearchScope;
import org.springframework.data.domain.Page;

public interface CommissionService {

    public Page<CommissionResponseDto> search(String query, SearchScope scope, int page, int size);
}
