package com.spring5microservices.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Required information to send as response in the {@code login} request
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"jwtId"})
@Data
@NoArgsConstructor
@ApiModel(description="Returned data after authenticate a user")
public class AuthenticationInformationDto implements Serializable {

    private static final long serialVersionUID = -4007535195077048326L;

    @ApiModelProperty(required = true)
    @JsonProperty(value = "access_token")
    private String accessToken;

    @ApiModelProperty(position = 1, required = true, value = "access token expiration time in seconds")
    @JsonProperty(value = "expires_in")
    private int expiresIn;

    @ApiModelProperty(position = 2, required = true, value = "roles of the logged user containing only lowercase letters")
    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(position = 3, required = true)
    @JsonProperty(value = "token_type")
    private String tokenType;

    @ApiModelProperty(position = 4)
    private String scope;

    @ApiModelProperty(position = 5, required = true, value = "identifier of every authentication information instance")
    @JsonProperty(value = "jti")
    private String jwtId;

    @ApiModelProperty(position = 6, value = "data extra returned by security service")
    private Map<String, Object> additionalInfo;

}
