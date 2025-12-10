package com.example.searchservice.commission.service;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.vo.OpenStatus;
import com.example.searchservice.common.vo.SearchScope;
import java.time.LocalDate;
import org.hexagon.core.vo.PaymentType;
import java.util.List;
import org.springframework.data.domain.Page;

public interface CommissionService {

    public Page<CommissionResponseDto> search(String query, SearchScope scope, List<String> tags, PaymentType paymentType, Long minPay, LocalDate startedAt, LocalDate endedAt, OpenStatus openStatus, int page, int size);

    public List<String> getSuggestions(String query, int size);

    public void saveAll(List<CommissionDocumentEntity> commissions);

    public void save(CommissionDocumentEntity commission);

    public void update(CommissionDocumentEntity commission);

    public void delete(String code);

}
