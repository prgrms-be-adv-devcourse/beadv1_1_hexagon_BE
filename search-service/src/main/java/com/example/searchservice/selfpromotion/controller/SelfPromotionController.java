package com.example.searchservice.selfpromotion.controller;

import com.example.searchservice.commission.exception.CommissionErrorCode;
import com.example.searchservice.commission.exception.CommissionException;
import com.example.searchservice.common.vo.SearchScope;
import com.example.searchservice.selfpromotion.controller.swagger.SelfPromotionControllerSwagger;
import com.example.searchservice.selfpromotion.dto.SelfPromotionResponseDto;
import com.example.searchservice.selfpromotion.service.SelfPromotionService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/self-promotions")
@Validated
public class SelfPromotionController implements SelfPromotionControllerSwagger {

    private final SelfPromotionService selfPromotionService;

    @GetMapping
    public ResponseDto<Page<SelfPromotionResponseDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") SearchScope scope,
            @RequestParam(name = "payment-type", required = false) PaymentType paymentType,
            @RequestParam(name = "max-pay", required = false) Long maxPay,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(30) int size) {

        if(paymentType == null && maxPay != null) {
            throw new CommissionException(CommissionErrorCode.COMMISSION_PAY_FILTER_ERROR);
        }

        Page<SelfPromotionResponseDto> result;

        result = selfPromotionService.search(query, scope, paymentType, maxPay, page, size);

        return ResponseDto.success(result);
    }

    @GetMapping("/suggest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<String>> suggest(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseDto.success(selfPromotionService.getSuggestions(query, size));
    }

}
