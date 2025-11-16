package com.example.searchservice.commission.controller;

import com.example.searchservice.commission.common.PaymentType;
import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.service.CommissionService;
import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.commission.controller.swagger.CommissionSearchControllerSwagger;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/commissions")
@RequiredArgsConstructor
public class CommissionSearchController implements CommissionSearchControllerSwagger {

    private final CommissionService commissionService;

    @GetMapping
    public BaseResponse<Page<CommissionResponseDto>> search(String query, SearchScope scope, List<String> tags, PaymentType paymentType,
            Long minPay, LocalDate startedAt, LocalDate endedAt, int page, int size) {

        Page<CommissionResponseDto> result;

        result = commissionService.search(query, scope, page, size);

        return BaseResponse.ok(result);
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<CommissionResponseDto> suggest(String query, int size) {
        return null;
    }
}
