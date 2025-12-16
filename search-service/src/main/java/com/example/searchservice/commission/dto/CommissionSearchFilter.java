package com.example.searchservice.commission.dto;

import com.example.searchservice.commission.vo.OpenStatus;
import com.example.searchservice.common.vo.SearchScope;
import org.hexagon.core.vo.PaymentType;
import java.time.LocalDate;
import java.util.List;

public record CommissionSearchFilter(
        SearchScope scope,
        List<String> tags,
        PaymentType paymentType,
        Long minPay,
        LocalDate startedAt,
        LocalDate endedAt,
        OpenStatus openStatus
) {

}
