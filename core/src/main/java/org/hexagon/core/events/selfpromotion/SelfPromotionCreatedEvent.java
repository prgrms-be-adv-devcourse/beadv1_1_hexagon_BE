package org.hexagon.core.events.selfpromotion;

import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

public record SelfPromotionCreatedEvent(
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
