package org.hexagon.core.events.member;

public record MemberUpdatedEvent(
    String memberCode,
    String nickName
) {

}
