package com.security.jwt.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException() {
        super();
    }

    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ClientNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
