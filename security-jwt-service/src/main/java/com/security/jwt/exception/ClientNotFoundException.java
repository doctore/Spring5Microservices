package com.security.jwt.exception;

import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;

/**
 * Thrown if an {@link JwtClientDetailsService} implementation cannot locate a {@link JwtClientDetails} by its clientId.
 */
public class ClientNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 338648360450595760L;

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
