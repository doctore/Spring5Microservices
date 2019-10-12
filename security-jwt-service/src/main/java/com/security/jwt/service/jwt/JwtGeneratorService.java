package com.security.jwt.service.jwt;

import com.security.jwt.dto.TokenInformationDto;
import com.security.jwt.enums.JwtGenerationEnum;
import com.security.jwt.model.JwtClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service
public class JwtGeneratorService {

    private ApplicationContext applicationContext;
    private JwtClientDetailsService jwtClientDetailsService;

    @Autowired
    public JwtGeneratorService(@Lazy ApplicationContext applicationContext, @Lazy JwtClientDetailsService jwtClientDetailsService) {
        this.applicationContext = applicationContext;
        this.jwtClientDetailsService = jwtClientDetailsService;
    }


    public Optional<TokenInformationDto> generateTokenResponse(String clientId, String username) {
        return JwtGenerationEnum.getByClientId(clientId)
                .map(generator -> applicationContext.getBean(generator.getJwtGeneratorClass()))
                .map(tokenInformation -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);

                    // TODO: Include the functionality to add information to TokenInformationDto
                    //TokenRawInformationDto rawInformation = tokenInformation.getTokenInformation(username);

                    return new TokenInformationDto();
                });
    }


/*
// Access token
{
  "exp": 1570638836,
  "jti": "7565113e-85a7-47f6-b184-c9025814bcc4",
  "client_id": "Spring5Microservices"
}
*/


/*
// Refresh token
{
  "ati": "7565113e-85a7-47f6-b184-c9025814bcc4",
  "exp": 1570641536,
  "jti": "b91f82ff-3353-486f-8868-9cc502b6cfb2",
  "client_id": "Spring5Microservices"
}
 */


/*
// FINAL TOKEN
{
    "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImV4cCI6MTU3MDYzOTA0OCwiYXV0aG9yaXRpZXMiOlsiVVNFUiIsIkFETUlOIl0sImp0aSI6IjY0MTA5MGUxLWY0YzQtNDUyOS05YjljLTJhMjYwOGI5ZTg1OCIsImNsaWVudF9pZCI6IlNwcmluZzVNaWNyb3NlcnZpY2VzIn0.Od0VwpxBx8ZbXnSnJqoemgodxgCejfgNu2thbL-xVKuDupblQe-Ei0nt6bgPRDYcPCZXzplj9z5dXUIlj1i6NA",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF0aSI6IjY0MTA5MGUxLWY0YzQtNDUyOS05YjljLTJhMjYwOGI5ZTg1OCIsImV4cCI6MTU3MDY0MTc0OCwianRpIjoiYjM3Njg0ZjMtMzRjYy00NDdiLThkMzAtYmEzMzgwNjVhZWY4IiwiY2xpZW50X2lkIjoiU3ByaW5nNU1pY3Jvc2VydmljZXMifQ.XeYzAlB1j5LVOic-E5axXHcrFFQ86pn1H2bE8s2lYK0fXPdVGF4vR1hrSihUbTCbRSytKgGy7wmhclC8GfThqQ",
    "expires_in": 899,
    "scope": "read",
    "jti": "641090e1-f4c4-4529-9b9c-2a2608b9e858",
    "authorities": [
        "USER",
        "ADMIN"
    ],
    "username": "admin"
}
 */



}
