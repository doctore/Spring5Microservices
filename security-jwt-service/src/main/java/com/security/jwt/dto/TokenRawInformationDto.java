package com.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Required information to generate the final token sent as response
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class TokenRawInformationDto {

    Map<String, Object> accessTokenInformation;
    Map<String, Object> refreshTokenInformation;
    Map<String, Object> additionalTokenInformation;

}
