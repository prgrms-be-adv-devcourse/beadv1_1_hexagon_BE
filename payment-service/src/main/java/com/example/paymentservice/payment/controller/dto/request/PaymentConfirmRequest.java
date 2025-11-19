package com.example.paymentservice.payment.controller.dto.request;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        int amount
) {

}
