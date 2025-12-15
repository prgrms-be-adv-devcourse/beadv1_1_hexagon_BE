package com.example.searchservice.commission.service;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.common.vo.SearchScope;
import java.util.List;
import org.springframework.data.domain.Page;

public interface CommissionService {

    public Page<CommissionResponseDto> search(String query, SearchScope scope, int page, int size);

    public List<String> getSuggestions(String query, int size);

    public void upsert(CommissionDocumentEntity commission);

    public void delete(String code);

}
