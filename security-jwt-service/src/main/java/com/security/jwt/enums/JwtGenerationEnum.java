package com.security.jwt.enums;

import com.security.jwt.interfaces.ITokenInformation;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.jwt.Spring5MicroserviceJwtGenerator;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Manage the configuration of existing Jwt token providers
 */
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

    /**
     * Get the {@link JwtGenerationEnum} which clientId matches with the given one.
     *
     * @param clientId
     *    ClientId to search
     *
     * @return {@link Optional} of {@link JwtClientDetails} is exists, {@link Optional#empty()} otherwise
     */
    public static Optional<JwtGenerationEnum> getByClientId(@Nullable String clientId) {
        return ofNullable(clientId)
                .flatMap(id -> Arrays.stream(JwtGenerationEnum.values()).filter(e -> clientId.equals(e.clientId)).findFirst());
    }

}
