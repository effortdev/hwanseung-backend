package com.hwanseung.backend.domain.user.entity;

//회원의 권한(역할) 을 구분하기 위해 사용
public enum Role {
    ROLE_USER("USER"), //일반 사용자
    ROLE_ADMIN("ADMIN"), //관리자
    ROLE_SUPER("SUPER"),
    ROLE_SUB("SUB");
    //각 역할이 가지고 있는 문자열 값(예: "USER", "ADMIN")    을 저장할 변수
    // "USER", "ADMIN"
    private String value;

    // Constructor
    Role(String value) {
        this.value = value;
    }

    // GetValue
    public String getValue() {
        return this.value;
    }
}