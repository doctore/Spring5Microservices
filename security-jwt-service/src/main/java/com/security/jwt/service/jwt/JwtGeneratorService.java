package com.security.jwt.service.jwt;

import com.security.jwt.dto.RawTokenInformationDto;
import com.security.jwt.enums.JwtGeneratorConfigurationEnum;
import com.security.jwt.interfaces.ITokenInformation;
import com.security.jwt.model.JwtClientDetails;
import com.spring5microservices.common.dto.TokenInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
public class JwtGeneratorService {

    private ApplicationContext applicationContext;
    private JwtClientDetailsService jwtClientDetailsService;
    private TextEncryptor encryptor;

    @Autowired
    public JwtGeneratorService(@Lazy ApplicationContext applicationContext, @Lazy JwtClientDetailsService jwtClientDetailsService,
                               @Lazy TextEncryptor encryptor) {
        this.applicationContext = applicationContext;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.encryptor = encryptor;
    }


    public Optional<TokenInformationDto> generateTokenResponse(String clientId, String username) {
        return JwtGeneratorConfigurationEnum.getByClientId(clientId)
                .map(generatorConfiguration -> applicationContext.getBean(generatorConfiguration.getJwtGeneratorClass()))
                .map(generator -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
                    RawTokenInformationDto jwtRawInformation = generator.getTokenInformation(username);
                    String jti = UUID.randomUUID().toString();







                    return new TokenInformationDto();
                });
    }


/*
// Access token
{
  "user_name": "admin",
  "exp": 1570962243,
  "authorities": [
    "USER",
    "ADMIN"
  ],
  "jti": "54f732a2-4c83-42c8-b684-6d0e64b51f81",
  "client_id": "Spring5Microservices"
}
*/


/*
// Refresh token
{
  "user_name": "admin",
  "ati": "54f732a2-4c83-42c8-b684-6d0e64b51f81",
  "exp": 1570964943,
  "jti": "7670a168-8ed2-4b3c-884e-a11738b12a5a",
  "client_id": "Spring5Microservices"
}
 */


/*
// FINAL TOKEN
{
    "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImV4cCI6MTU3MDk2MjI0MywiYXV0aG9yaXRpZXMiOlsiVVNFUiIsIkFETUlOIl0sImp0aSI6IjU0ZjczMmEyLTRjODMtNDJjOC1iNjg0LTZkMGU2NGI1MWY4MSIsImNsaWVudF9pZCI6IlNwcmluZzVNaWNyb3NlcnZpY2VzIn0.5--ZTwe52aeO6vzhCo9smXvoFrv1lThoboi8ih-COXGCe9TkGShaWnid_A77Nt8yTbKAuzxqUHuLv0XXblvsMg",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF0aSI6IjU0ZjczMmEyLTRjODMtNDJjOC1iNjg0LTZkMGU2NGI1MWY4MSIsImV4cCI6MTU3MDk2NDk0MywianRpIjoiNzY3MGExNjgtOGVkMi00YjNjLTg4NGUtYTExNzM4YjEyYTVhIiwiY2xpZW50X2lkIjoiU3ByaW5nNU1pY3Jvc2VydmljZXMifQ.-YpUKUQsw6qTre6W_Zas31jgVc-JjFyBoLpk-zFi1bj-LaPcqBDosjED9njVz9fs5Rg0_Rlr1aesgzA1GomQBg",
    "expires_in": 899,
    "scope": "read",
    "jti": "54f732a2-4c83-42c8-b684-6d0e64b51f81",
    "additionalInfo": {
        "authorities": [
            "USER",
            "ADMIN"
        ],
        "username": "admin"
    }
}
 */



}
