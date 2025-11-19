package com.example.paymentservice.payment.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentConfirmResponse(
        @JsonProperty("paymentKey") String paymentKey,
        @JsonProperty("orderId") String orderId,
        @JsonProperty("totalAmount") int amount,
        @JsonProperty("status") String status,
        @JsonProperty("approvedAt") String approvedAt,
        @JsonProperty("method") String method,
        @JsonProperty("code") String code,   // 결제 상태 코드
        @JsonProperty("mId") String mId      // 가맹점 ID
) {}
