package org.hexagon.core.events.contract;

import java.time.Instant;

public record ContractEvent(
        String memberCode,
        String contractCode,
        Instant createdAt,
        String status
) {

}
