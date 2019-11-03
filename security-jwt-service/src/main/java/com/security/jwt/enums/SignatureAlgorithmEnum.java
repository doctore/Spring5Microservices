package com.security.jwt.enums;

import com.nimbusds.jose.JWSAlgorithm;

/**
 * Allowed algorithms to sign a JWT token
 */
public enum SignatureAlgorithmEnum {

    HS256(JWSAlgorithm.HS256),
    HS384(JWSAlgorithm.HS384),
    HS512(JWSAlgorithm.HS512);

    private JWSAlgorithm algorithm;

    SignatureAlgorithmEnum(JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

}
