package com.example.recommendationservice.client.dto.output;

import org.hexagon.core.vo.PaymentType;

public record SelfPromotionReadOutput(
    String title,
    String content,
    PaymentType paymentType,
    Long unitAmount
) {
}
