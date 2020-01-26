package com.security.jwt.enums;

import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.interfaces.IAuthenticationGenerator;
import com.security.jwt.interfaces.IUserService;
import com.security.jwt.application.spring5microservices.service.UserService;
import com.security.jwt.application.spring5microservices.service.Spring5MicroserviceAuthenticationGenerator;
import org.springframework.lang.Nullable;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Manage the configuration of existing Jwt token providers
 */
public enum AuthenticationConfigurationEnum {
    SPRING5_MICROSERVICES ("Spring5Microservices", Spring5MicroserviceAuthenticationGenerator.class, UserService.class);

    private String clientId;
    private Class<? extends IAuthenticationGenerator> authenticationGeneratorClass;
    private Class<? extends IUserService> userServiceClass;

    AuthenticationConfigurationEnum(String clientId, Class<? extends IAuthenticationGenerator> authenticationGeneratorClass,
                                    Class<? extends IUserService> userServiceClass) {
        this.clientId = clientId;
        this.authenticationGeneratorClass = authenticationGeneratorClass;
        this.userServiceClass = userServiceClass;
    }

    public String getClientId() {
        return clientId;
    }
    public Class<? extends IAuthenticationGenerator> getAuthenticationGeneratorClass() {
        return authenticationGeneratorClass;
    }
    public Class<? extends IUserService> getUserServiceClass() {
        return userServiceClass;
    }

    /**
     * Get the {@link AuthenticationConfigurationEnum} which clientId matches with the given one.
     *
     * @param clientId
     *    ClientId to search
     *
     * @return {@link AuthenticationConfigurationEnum}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in the {@code enum}
     */
    public static AuthenticationConfigurationEnum getByClientId(@Nullable String clientId) {
        return ofNullable(clientId)
                .flatMap(id -> Arrays.stream(AuthenticationConfigurationEnum.values()).filter(e -> clientId.equals(e.clientId)).findFirst())
                .orElseThrow(() -> new ClientNotFoundException(format("The given clientId: %s was not found in AuthenticationGeneratorEnum", clientId)));
    }

}
