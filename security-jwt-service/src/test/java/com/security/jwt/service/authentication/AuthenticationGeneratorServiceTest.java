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

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.Optional.of;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
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
        JwtClientDetails clientDetails = buildDefaultClientDetails(clientId);
        RawAuthenticationInformationDto rawAuthenticationInformation = buildDefaultRawAuthenticationInformation();
        return Stream.of(
                //@formatter:off
                //            clientId,   username,   authenticationGenerator,   clientDetailsResult,   rawAuthenticationInformation,   isResultEmpty
                Arguments.of( null,       null,       null,                      clientDetails,         null,                           true ),
                Arguments.of( clientId,   null,       null,                      clientDetails,         null,                           true ),
                Arguments.of( clientId,   username,   null,                      clientDetails,         null,                           true ),
                Arguments.of( clientId,   username,   authenticationGenerator,   clientDetails,         null,                           false ),
                Arguments.of( clientId,   username,   authenticationGenerator,   clientDetails,         rawAuthenticationInformation,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAuthenticationInformationTestCases")
    @DisplayName("getAuthenticationInformation: test cases")
    public void getAuthenticationInformation_testCases(String clientId, String username, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                                       JwtClientDetails clientDetailsResult, RawAuthenticationInformationDto rawAuthenticationInformation,
                                                       boolean isResultEmpty) {
        Optional<String> jwtToken = of("JWT token");
        String jwtSecret = "secretKey";

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockJwtClientDetailsService.findByClientId(eq(clientId))).thenReturn(clientDetailsResult);
        when(mockEncryptor.decrypt(anyString())).thenReturn(jwtSecret);

        if (null != authenticationGenerator){
            when(authenticationGenerator.getRawAuthenticationInformation(username)).thenReturn(rawAuthenticationInformation);
        }
        if (null != clientDetailsResult) {
            when(mockJwtUtil.generateJwtToken(anyMap(), eq(clientDetailsResult.getJwtAlgorithm()), anyString(), anyInt())).thenReturn(jwtToken);
        }
        Optional<AuthenticationInformationDto> result = authenticationGeneratorService.getAuthenticationInformation(clientId, username);
        verifyGetAuthenticationInformationResult(clientDetailsResult, rawAuthenticationInformation, result, isResultEmpty);
    }


    private void verifyGetAuthenticationInformationResult(JwtClientDetails clientDetailsResult, RawAuthenticationInformationDto rawAuthenticationInformation,
                                                          Optional<AuthenticationInformationDto> result, boolean isResultEmpty) {
        if (isResultEmpty)
            assertFalse(result.isPresent());
        else {
            assertTrue(result.isPresent());
            assertNotNull(result.get().getAccessToken());
            assertNotNull(result.get().getRefreshToken());
            assertEquals(clientDetailsResult.getAccessTokenValidity(), result.get().getExpiresIn());
            assertEquals(clientDetailsResult.getTokenType(), result.get().getTokenType());
            assertNull(result.get().getScope());
            assertFalse(result.get().getJwtId().isEmpty());
            if (null == rawAuthenticationInformation) {
                assertNull(result.get().getAdditionalInfo());
            }
            else {
                assertEquals(rawAuthenticationInformation.getAdditionalTokenInformation(), result.get().getAdditionalInfo());
            }
        }
    }

    private static JwtClientDetails buildDefaultClientDetails(String clientId) {
        return JwtClientDetails.builder()
                .clientId(clientId)
                .jwtAlgorithm(SignatureAlgorithm.HS256)
                .jwtSecret(Constants.JWT_SECRET_PREFIX + "secretKey")
                .accessTokenValidity(100)
                .refreshTokenValidity(150)
                .tokenType("Bearer")
                .build();
    }

    private static RawAuthenticationInformationDto buildDefaultRawAuthenticationInformation() {
        return RawAuthenticationInformationDto.builder()
                .accessTokenInformation(new HashMap<String, Object>() {{
                    put(USERNAME.getKey(), "username value");
                    put(AUTHORITIES.getKey(), asList("admin"));
                }})
                .refreshTokenInformation(new HashMap<String, Object>() {{
                    put(USERNAME.getKey(), "username value");
                }})
                .additionalTokenInformation(new HashMap<String, Object>() {{
                    put(NAME.getKey(), "name value");
                }})
                .build();
    }

}
