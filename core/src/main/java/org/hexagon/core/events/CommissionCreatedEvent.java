package org.hexagon.core.events;

public record CommissionCreatedEvent(
        String code,
        String title,
        String content,
        String memberCode,
        String memberNickname,
        String 
) {
}
