package org.hexagon.core.events.selfpromotion;

import java.util.List;
import org.hexagon.core.vo.SelfPromotion;

public record SelfPromotionInitEvent(
        List<SelfPromotion> selfPromotions
) {

}
