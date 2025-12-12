package com.example.searchservice.commission.controller;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.dto.CommissionSearchFilter;
import com.example.searchservice.commission.exception.CommissionErrorCode;
import com.example.searchservice.commission.exception.CommissionException;
import com.example.searchservice.commission.service.CommissionService;
import com.example.searchservice.commission.vo.OpenStatus;
import com.example.searchservice.common.vo.Pagination;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.commission.controller.swagger.CommissionControllerSwagger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/commissions")
@RequiredArgsConstructor
@Validated
public class CommissionController implements CommissionControllerSwagger {

    private final CommissionService commissionService;

    @GetMapping
    public ResponseDto<Page<CommissionResponseDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") SearchScope scope,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(name = "payment-type", required = false) PaymentType paymentType,
            @RequestParam(name = "min-pay", required = false) Long minPay,
            @RequestParam(name = "started-at", required = false) LocalDate startedAt,
            @RequestParam(name = "ended-at", required = false) LocalDate endedAt,
            @RequestParam(name = "open-status", defaultValue = "open") OpenStatus openStatus,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Max(30) int size
    ) {
        if(paymentType == null && minPay != null) {
            throw new CommissionException(CommissionErrorCode.COMMISSION_PAY_FILTER_ERROR);
        }

        if(startedAt != null && endedAt != null) {
            if(endedAt.isBefore(startedAt)) {
                throw new CommissionException(CommissionErrorCode.COMMISSION_DATE_FILTER_ERROR);
            }
        }

        CommissionSearchFilter filter = new CommissionSearchFilter(
                scope, tags, paymentType, minPay, startedAt, endedAt, openStatus
        );

        Page<CommissionResponseDto> result;

        result = commissionService.search(query, filter, page, size);

        return ResponseDto.success(result);
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<String>> suggest(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseDto.success(commissionService.getSuggestions(query, size));
    }
}
