package com.gatewayserver.enums;

/**
 * Http headers included in the custom Zuul filters
 */
public enum HttpHeaderEnum {

    CORRELATION_ID("ms-correlation-id");

    private String httpHeader;

    HttpHeaderEnum(String httpHeader) {
        this.httpHeader = httpHeader;
    }
    public String getHttpHeader() {
        return this.httpHeader;
    }

}
