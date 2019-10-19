package com.security.jwt.enums;

import com.security.jwt.interfaces.IAuthenticationGenerator;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.authentication.generator.Spring5MicroserviceAuthenticationGenerator;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Manage the configuration of existing Jwt token providers
 */
public enum AuthenticationGeneratorEnum {
    SPRING5_MICROSERVICES ("Spring5Microservices", Spring5MicroserviceAuthenticationGenerator.class);

    private String clientId;
    private Class<? extends IAuthenticationGenerator> authenticationGeneratorClass;

    AuthenticationGeneratorEnum(String clientId, Class<? extends IAuthenticationGenerator> authenticationGeneratorClass) {
        this.clientId = clientId;
        this.authenticationGeneratorClass = authenticationGeneratorClass;
    }

    public String getClientId() {
        return clientId;
    }
    public Class<? extends IAuthenticationGenerator> getAuthenticationGeneratorClass() {
        return authenticationGeneratorClass;
    }

    /**
     * Get the {@link AuthenticationGeneratorEnum} which clientId matches with the given one.
     *
     * @param clientId
     *    ClientId to search
     *
     * @return {@link Optional} of {@link JwtClientDetails} is exists, {@link Optional#empty()} otherwise
     */
    public static Optional<AuthenticationGeneratorEnum> getByClientId(@Nullable String clientId) {
        return ofNullable(clientId)
                .flatMap(id -> Arrays.stream(AuthenticationGeneratorEnum.values()).filter(e -> clientId.equals(e.clientId)).findFirst());
    }

}
