package com.security.jwt.exception;

/**
 * Thrown when the {@code token} is not a valid one.
 */
public class TokenInvalidException extends RuntimeException {

    private static final long serialVersionUID = -7862642265730188024L;

    public TokenInvalidException() {
        super();
    }

    public TokenInvalidException(String message) {
        super(message);
    }

    public TokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenInvalidException(Throwable cause) {
        super(cause);
    }

    protected TokenInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
