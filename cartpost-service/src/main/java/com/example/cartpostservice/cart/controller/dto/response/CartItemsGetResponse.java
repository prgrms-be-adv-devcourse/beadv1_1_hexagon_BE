package com.example.cartpostservice.cart.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "장바구니 아이템 목록 조회 응답 DTO")
public record CartItemsGetResponse(

        String commissionCode,

        List<ContractInfo> contractInfos,

        Instant startedAt,

        Instant endedAt,

        String paymentType,

        Long amount

) {

}

