package org.hexagon.core.events.tag;

import java.util.List;
import org.hexagon.core.vo.Tag;

public record TagInitEvent(
        List<Tag> tags
) {

}
