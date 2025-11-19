package org.hexagon.core.events;

import java.time.Instant;

public record ContractEvent(
        String contractCode,
        Instant createdAt,
        String status
) {

}
