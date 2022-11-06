package com.spring5microservices.common.enums;

/**
 * Extended Http responses
 */
public enum ExtendedHttpStatus {

    TOKEN_EXPIRED(440, "The token has expired");

    private final int value;
    private final String reasonPhrase;

    ExtendedHttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

}
