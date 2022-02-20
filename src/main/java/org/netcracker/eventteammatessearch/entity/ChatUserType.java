package org.netcracker.eventteammatessearch.entity;

public enum ChatUserType {
    MAIN_ADMIN("Главный модератор"),
    ADMIN("Модератор"),
    READ_WRITE("Обычный пользователь"),
    ONLY_READ("Читатель");

    private String code;

    ChatUserType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
