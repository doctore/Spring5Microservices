package com.security.jwt.enums;

public enum TokenKeyEnum {

    AUTHORITIES("authorities"),
    CLIENT_ID("clientId"),
    JWT_ID("jti"),
    NAME("name"),
    REFRESH_JWT_ID("ati"),
    USERNAME("username");

    private String key;

    TokenKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
