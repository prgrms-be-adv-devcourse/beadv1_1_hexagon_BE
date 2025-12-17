package org.hexagon.core.events.contract;

import java.time.Instant;

public record ContractEvent(
        String contractCode,
        String commissionCode,
        Instant createdAt,
        String status
) {

}
