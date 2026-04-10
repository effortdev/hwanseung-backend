package com.hwanseung.backend.domain.admin.dto;

public enum Status {
    ACTIVE("ACTIVE"),
    SUSPENDED("SUSPENDED"),
    SECESSION("SECESSION"),
    BLOCKED("BLOCKED"),
    PENDING("PENDING");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

}
