package org.hexagon.core.events.contract;

public record CommissionOpenCloseEvent(
        String commissionCode,
        boolean isOpen
) {

}
