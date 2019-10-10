package com.security.jwt.enums;

public enum TokenKeyEnum {

    ACCESS_TOKEN("access_token"),
    AUTHORITIES("authorities"),
    CLIENT_ID("clientId"),
    EXPIRATION_TIME("exp"),
    EXPIRES_IN("expires_in"),
    JWT_ID("jti"),
    REFRESH_JWT_ID("ati"),
    REFRESH_TOKEN("refresh_token"),
    TOKEN_TYPE("token_type"),
    USERNAME("username");

    private String key;

    TokenKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
