package org.hexagon.core.events.commissions;

import java.util.List;
import org.hexagon.core.vo.Tag;

public record CommissionInitEvent(
        List<Tag> tags
) {

}
