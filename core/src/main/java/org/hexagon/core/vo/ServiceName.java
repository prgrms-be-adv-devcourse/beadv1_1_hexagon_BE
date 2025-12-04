package org.hexagon.core.vo;

public enum ServiceName {
    MEMBERS,
    COMMISSIONS,
    SELF_PROMOTIONS,
    CHATS;

    public String toLower() {
        return this.name().toLowerCase();
    }
}
