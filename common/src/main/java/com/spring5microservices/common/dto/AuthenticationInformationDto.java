package com.spring5microservices.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Required information to send as response in the {@code login} request
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class AuthenticationInformationDto implements Serializable {

    private static final long serialVersionUID = -4007535195077048326L;

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "expires_in")
    private int expiresIn;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    private String scope;

    @JsonProperty(value = "jti")
    private String jwtId;

    private Map<String, Object> additionalInfo;

}
