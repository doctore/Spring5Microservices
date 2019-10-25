package com.security.jwt.enums;

import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;

/**
 * Include all possible results after a token verification.
 */
public enum TokenVerificationEnum {
    CORRECT_TOKEN,
    EXPIRED_TOKEN,
    INVALID_SECRET_KEY,
    INVALID_TOKEN,
    UNKNOWN_ERROR;

    /**
     * Taking into account the current {@link TokenVerificationEnum} value, if it is necessary throws its related {@link Exception}
     *
     * @param errorMessage
     *    {@link String} with the error message to include in the exception
     */
    public void throwRelatedExceptionIfRequired(String errorMessage) {
        switch (this) {
            case EXPIRED_TOKEN:
                throw new TokenExpiredException(errorMessage);
            case INVALID_SECRET_KEY:
            case INVALID_TOKEN:
            case UNKNOWN_ERROR:
                throw new UnAuthorizedException(errorMessage);
        }
    }
}
