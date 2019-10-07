package com.security.jwt.configuration.jwt;

import com.security.jwt.service.jwt.Spring5MicroserviceJwtGenerator;
import com.spring5microservices.common.interfaces.ITokenInformation;

public enum JwtGenerationEnum {
    SPRING5_MICROSERVICES ("Spring5Microservices", Spring5MicroserviceJwtGenerator.class);

    private String clientId;
    private Class<? extends ITokenInformation> jwtGeneratorClass;

    JwtGenerationEnum(String clientId, Class<? extends ITokenInformation> jwtGeneratorClass) {
        this.jwtGeneratorClass = jwtGeneratorClass;
    }

    public String getClientId() {
        return clientId;
    }

    public Class<? extends ITokenInformation> getJwtGeneratorClass() {
        return jwtGeneratorClass;
    }
}
