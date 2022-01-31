package org.netcracker.eventteammatessearch.entity;

public enum Status {
    PENDING("Ожидает рассмотрения"),
    ACCEPTED("Рассмотрена - принята"),
    REJECTED("Рассмотрена - отклонена");

    private String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
