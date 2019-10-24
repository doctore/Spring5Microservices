package com.security.jwt.enums;

/**
 * Include all possible results after a token verification
 */
public enum TokenVerificationEnum {
    CORRECT_TOKEN,
    EXPIRED_TOKEN,
    INVALID_SECRET_KEY,
    INVALID_TOKEN,
    UNKNOWN_ERROR;
}
