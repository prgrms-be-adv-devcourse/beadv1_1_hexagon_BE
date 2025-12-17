package org.hexagon.core.events.commission;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.vo.PaymentType;

public record CommissionUpsertEvent(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        List<String> tagCodes,
        LocalDate startedAt,
        LocalDate endedAt,
        PaymentType paymentType,
        Long payAmount,
        Boolean isOpen,
        Instant updatedAt
) {

}
