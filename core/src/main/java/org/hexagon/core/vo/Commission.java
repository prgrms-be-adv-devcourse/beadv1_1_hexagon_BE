package org.hexagon.core.vo;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record Commission(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        List<String> tags,
        LocalDate startedAt,
        LocalDate endedAt,
        PaymentType paymentType,
        Long payAmount,
        Boolean isOpen,
        Instant updatedAt
) {

}
