package com.security.jwt.service.authentication;

import com.security.jwt.configuration.Constants;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.AuthenticationGeneratorEnum;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.service.authentication.generator.Spring5MicroserviceAuthenticationGenerator;
import com.security.jwt.util.JwtUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationGeneratorServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private JwtClientDetailsService mockJwtClientDetailsService;

    @Mock
    private JwtUtil mockJwtUtil;

    @Mock
    private TextEncryptor mockEncryptor;

    private AuthenticationGeneratorService authenticationGeneratorService;


    @BeforeEach
    public void init() {
        authenticationGeneratorService = new AuthenticationGeneratorService(mockApplicationContext, mockJwtClientDetailsService, mockJwtUtil, mockEncryptor);
    }


    static Stream<Arguments> getAuthenticationInformationTestCases() {
        String clientId = AuthenticationGeneratorEnum.SPRING5_MICROSERVICES.getClientId();
        String username = "username value";
        Spring5MicroserviceAuthenticationGenerator authenticationGenerator = Mockito.mock(Spring5MicroserviceAuthenticationGenerator.class);
        JwtClientDetails clientDetails = getDefaultClientDetails(clientId);

        return Stream.of(
                //@formatter:off
                //            clientId,   username,   authenticationGenerator,   clientDetailsResult,   rawAuthenticationInformation,   expectedResult
                //Arguments.of( null,       null,       null,                      null,                  null,                           empty() ),
                //Arguments.of( clientId,   null,       null,                      null,                  null,                           empty() ),
                //Arguments.of( clientId,   username,   null,                      null,                  null,                           empty() ),
                //Arguments.of( clientId,   username,   authenticationGenerator,   null,                  null,                           empty() ),

                Arguments.of( clientId,   username,   authenticationGenerator,   clientDetails,         null,                           empty() )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("getAuthenticationInformationTestCases")
    @DisplayName("getAuthenticationInformation: test cases")
    public void getAuthenticationInformation_testCases(String clientId, String username, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                                       JwtClientDetails clientDetailsResult, RawAuthenticationInformationDto rawAuthenticationInformation,
                                                       Optional<AuthenticationInformationDto> expectedResult) {
        Optional<String> jwtToken = of("JWT token");

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockJwtClientDetailsService.findByClientId(eq(clientId))).thenReturn(clientDetailsResult);

        if (null != authenticationGenerator){
            when(authenticationGenerator.getRawAuthenticationInformation(username)).thenReturn(rawAuthenticationInformation);
        }
        if (null != clientDetailsResult) {
            when(mockJwtUtil.generateJwtToken(any(Map.class), eq(clientDetailsResult.getJwtAlgorithm()), anyString(), anyInt())).thenReturn(jwtToken);
        }
        Optional<AuthenticationInformationDto> t = authenticationGeneratorService.getAuthenticationInformation(clientId, username);


        assertEquals(expectedResult, authenticationGeneratorService.getAuthenticationInformation(clientId, username));
    }


    private static JwtClientDetails getDefaultClientDetails(String clientId) {
        return JwtClientDetails.builder()
                .clientId(clientId)
                .jwtAlgorithm(SignatureAlgorithm.HS256)
                .jwtSecret(Constants.JWT_SECRET_PREFIX + "secretKey")
                .accessTokenValidity(100)
                .refreshTokenValidity(150)
                .tokenType("Bearer")
                .build();
    }




}

