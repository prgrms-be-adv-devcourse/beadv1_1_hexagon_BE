package org.hexagon.core.events.commission;

import java.util.List;
import org.hexagon.core.vo.Commission;

public record CommissionInitEvent(
        List<Commission> commissions
) {

}
