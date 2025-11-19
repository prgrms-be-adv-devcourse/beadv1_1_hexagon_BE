package org.hexagon.core.vo;

import java.time.Instant;

public record SelfPromotion(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        PaymentType paymentType,
        Long payAmount,
        Instant updatedAt
) {

}
