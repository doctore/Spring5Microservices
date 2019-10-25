package com.security.jwt.exception;

/**
 * Thrown when an unauthorized request is detected
 */
public class UnAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = 3939493371098418268L;

    public UnAuthorizedException() {
        super();
    }

    public UnAuthorizedException(String message) {
        super(message);
    }

    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizedException(Throwable cause) {
        super(cause);
    }

    protected UnAuthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
