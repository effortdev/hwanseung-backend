package com.hwanseung.backend.domain.user.entity;

public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_SUPER("SUPER"),
    ROLE_SUB("SUB");
    private String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}