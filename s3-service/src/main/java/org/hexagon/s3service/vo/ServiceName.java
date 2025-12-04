package org.hexagon.s3service.vo;

public enum ServiceName {
    MEMBERS,
    COMMISSIONS,
    SELF_PROMOTIONS,
    CHATS;

    public String toLower() {
        return this.name().toLowerCase();
    }
}
