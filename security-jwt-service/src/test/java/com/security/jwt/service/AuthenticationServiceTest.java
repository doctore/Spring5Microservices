package com.security.jwt.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.security.jwt.ObjectGeneratorForTest;
import com.security.jwt.configuration.security.JweConfiguration;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.application.spring5microservices.service.AuthenticationGenerator;
import com.security.jwt.util.JweUtil;
import com.security.jwt.util.JwsUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.security.jwt.enums.TokenKeyEnum.AUDIENCE;
import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.EXPIRATION_TIME;
import static com.security.jwt.enums.TokenKeyEnum.ISSUED_AT;
import static com.security.jwt.enums.TokenKeyEnum.JWT_ID;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.REFRESH_JWT_ID;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.Arrays.asList;
import static com.security.jwt.enums.AuthenticationConfigurationEnum.SPRING5_MICROSERVICES;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private JwtClientDetailsService mockJwtClientDetailsService;

    @Mock
    private JweConfiguration mockJweConfiguration;

    @Mock
    private JweUtil mockJweUtil;

    @Mock
    private JwsUtil mockJwsUtil;

    @Mock
    private TextEncryptor mockEncryptor;

    private AuthenticationService authenticationService;

    @BeforeEach
    public void init() {
        authenticationService = new AuthenticationService(mockApplicationContext, mockJwtClientDetailsService, mockJweConfiguration,
                mockJweUtil, mockJwsUtil, mockEncryptor);
        when(mockJweConfiguration.getEncryptionSecret()).thenReturn("EncryptionSecret");
    }


    static Stream<Arguments> getAuthenticationInformationTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        UserDetails userDetails = ObjectGeneratorForTest.buildDefaultUser();
        AuthenticationGenerator authenticationGenerator = mock(AuthenticationGenerator.class);
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        Optional<RawAuthenticationInformationDto> rawAuthenticationInformation = of(ObjectGeneratorForTest.buildDefaultRawAuthenticationInformation());
        return Stream.of(
                //@formatter:off
                //            clientId,   userDetails,   authenticationGenerator,   clientDetailsResult,   rawAuthenticationInformation,   isResultEmpty
                Arguments.of( null,       null,          null,                      null,                  null,                           true ),
                Arguments.of( clientId,   null,          null,                      clientDetails,         null,                           true ),
                Arguments.of( clientId,   userDetails,   null,                      clientDetails,         null,                           true ),
                Arguments.of( clientId,   userDetails,   authenticationGenerator,   clientDetails,         empty(),                        true ),
                Arguments.of( clientId,   userDetails,   authenticationGenerator,   clientDetails,         rawAuthenticationInformation,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAuthenticationInformationTestCases")
    @DisplayName("getAuthenticationInformation: test cases")
    public void getAuthenticationInformation_testCases(String clientId, UserDetails userDetails, AuthenticationGenerator authenticationGenerator,
                                                       JwtClientDetails clientDetailsResult, Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                       boolean isResultEmpty) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockApplicationContext.getBean(AuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        if (null != authenticationGenerator) {
            when(authenticationGenerator.getRawAuthenticationInformation(userDetails)).thenReturn(rawAuthenticationInformation);
        }
        if (null != clientDetailsResult) {
            JWSAlgorithm algorithm = clientDetailsResult.getSignatureAlgorithm().getAlgorithm();
            when(mockJweUtil.generateToken(anyMap(), eq(algorithm), anyString(), anyString(), anyInt())).thenReturn("JWE token");
            when(mockJwsUtil.generateToken(anyMap(), eq(algorithm), anyString(), anyInt())).thenReturn("JWS token");
        }
        Optional<AuthenticationInformationDto> result = authenticationService.getAuthenticationInformation(clientId, userDetails);
        verifyGetAuthenticationInformationResult(clientDetailsResult, rawAuthenticationInformation, result, isResultEmpty);
    }

    private void verifyGetAuthenticationInformationResult(JwtClientDetails clientDetailsResult, Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                          Optional<AuthenticationInformationDto> result, boolean isResultEmpty) {
        if (isResultEmpty)
            assertFalse(result.isPresent());
        else {
            assertTrue(result.isPresent());
            assertEquals(clientDetailsResult.getAccessTokenValidity(), result.get().getExpiresIn());
            assertEquals(clientDetailsResult.getTokenType(), result.get().getTokenType());
            assertNull(result.get().getScope());
            assertFalse(result.get().getJwtId().isEmpty());
            if (!rawAuthenticationInformation.isPresent()) {
                assertNull(result.get().getAdditionalInfo());
            } else {
                assertEquals(rawAuthenticationInformation.get().getAdditionalTokenInformation(), result.get().getAdditionalInfo());
            }
        }
    }


    static Stream<Arguments> getPayloadOfTokenTestCases() {
        String clientId = "clientId";
        JwtClientDetails clientDetailsJWE = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        clientDetailsJWE.setUseJwe(true);
        JwtClientDetails clientDetailsJWS = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        clientDetailsJWS.setUseJwe(false);
        Map<String, Object> payloadFromAccessToken = new HashMap<String, Object>() {{
            put(AUTHORITIES.getKey(), asList("admin"));
            put(JWT_ID.getKey(), "jti value");
            put(USERNAME.getKey(), "name value");
        }};
        Map<String, Object> payloadFromRefreshToken = new HashMap<String, Object>() {{
            put(AUTHORITIES.getKey(), asList("admin"));
            put(REFRESH_JWT_ID.getKey(), "ati value");
            put(USERNAME.getKey(), "name value");
        }};
        return Stream.of(
                //@formatter:off
                //            token,             clientId,   isAccessToken,   clientDetailsResult,   payload,                   expectedException,               expectedResult
                Arguments.of( null,              null,       false,           null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( null,              null,       true,            null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,       false,           null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   null,       true,            null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            null,                  null,                      ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWS,      null,                      UnauthorizedException.class,     null ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWE,      null,                      UnauthorizedException.class,     null ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWS,      null,                      null,                            null ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWE,      null,                      null,                            null ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWS,      payloadFromAccessToken,    UnauthorizedException.class,     null ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWE,      payloadFromAccessToken,    UnauthorizedException.class,     null ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWS,      payloadFromAccessToken,    null,                            payloadFromAccessToken ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWE,      payloadFromAccessToken,    null,                            payloadFromAccessToken ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWS,      payloadFromRefreshToken,   null,                            payloadFromRefreshToken ),
                Arguments.of( "ItDoesNotCare",   clientId,   false,           clientDetailsJWE,      payloadFromRefreshToken,   null,                            payloadFromRefreshToken ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWS,      payloadFromRefreshToken,   UnauthorizedException.class,     null ),
                Arguments.of( "ItDoesNotCare",   clientId,   true,            clientDetailsJWE,      payloadFromRefreshToken,   UnauthorizedException.class,     null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadOfTokenTestCases")
    @DisplayName("getPayloadOfToken: test cases")
    public void getPayloadOfToken_testCases(String token, String clientId, boolean isAccessToken, JwtClientDetails clientDetailsResult,
                                            Map<String, Object> payload, Class<? extends Exception> expectedException, Map<String, Object> expectedResult) {
        String decryptedJwsSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwsSecret);
        when(mockJwsUtil.getPayloadExceptGivenKeys(token, decryptedJwsSecret, new HashSet<>())).thenReturn(payload);
        when(mockJweUtil.getPayloadExceptGivenKeys(eq(token), eq(decryptedJwsSecret), anyString(), anySet())).thenReturn(payload);
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getPayloadOfToken(token, clientId, isAccessToken));
        }
        else {
            assertEquals(payload, authenticationService.getPayloadOfToken(token, clientId, isAccessToken));
        }
    }


    static Stream<Arguments> getUsernameTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        AuthenticationGenerator authenticationGenerator = mock(AuthenticationGenerator.class);
        String username = "username value";
        Map<String, Object> payloadWithUsername = new HashMap<String, Object>() {{
            put(USERNAME.getKey(), username);
        }};
        Map<String, Object> payloadWithoutUsername = new HashMap<String, Object>() {{
            put(NAME.getKey(), "name value");
        }};
        return Stream.of(
                //@formatter:off
                //            payload,                  clientId,   authenticationGenerator,   expectedException,               expectedResult
                Arguments.of( null,                     null,       null,                      null,                            empty() ),
                Arguments.of( new HashMap<>(),          null,       null,                      ClientNotFoundException.class,   null ),
                Arguments.of( new HashMap<>(),          clientId,   null,                      null,                            empty() ),
                Arguments.of( new HashMap<>(),          clientId,   authenticationGenerator,   null,                            empty() ),
                Arguments.of( payloadWithoutUsername,   clientId,   authenticationGenerator,   null,                            empty() ),
                Arguments.of( payloadWithUsername,      clientId,   authenticationGenerator,   null,                            of(username) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getUsernameTestCases")
    @DisplayName("getUsername: test cases")
    public void getUsername_testCases(Map<String, Object> payload, String clientId, AuthenticationGenerator authenticationGenerator,
                                      Class<? extends Exception> expectedException, Optional<String> expectedResult) {

        when(mockApplicationContext.getBean(AuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        if (null != authenticationGenerator) {
            when(authenticationGenerator.getUsernameKey()).thenReturn(USERNAME.getKey());
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getUsername(payload, clientId));
        }
        else {
            Optional<String> result = authenticationService.getUsername(payload, clientId);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> getRolesTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        AuthenticationGenerator authenticationGenerator = mock(AuthenticationGenerator.class);
        List<String> roles = asList("admin", "user");
        Map<String, Object> payloadWithRoles = new HashMap<String, Object>() {{
            put(AUTHORITIES.getKey(), roles);
        }};
        Map<String, Object> payloadWithoutRoles = new HashMap<String, Object>() {{
            put(NAME.getKey(), "name value");
        }};
        return Stream.of(
                //@formatter:off
                //            payload,               clientId,   authenticationGenerator,   expectedException,               expectedResult
                Arguments.of( null,                  null,       null,                      null,                            new HashSet<>() ),
                Arguments.of( new HashMap<>(),       null,       null,                      ClientNotFoundException.class,   null ),
                Arguments.of( new HashMap<>(),       clientId,   null,                      null,                            new HashSet<>() ),
                Arguments.of( new HashMap<>(),       clientId,   authenticationGenerator,   null,                            new HashSet<>() ),
                Arguments.of( payloadWithoutRoles,   clientId,   authenticationGenerator,   null,                            new HashSet<>() ),
                Arguments.of( payloadWithRoles,      clientId,   authenticationGenerator,   null,                            new HashSet<>(roles) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRolesTestCases")
    @DisplayName("getRoles: test cases")
    public void getRoles_testCases(Map<String, Object> payload, String clientId, AuthenticationGenerator authenticationGenerator,
                                   Class<? extends Exception> expectedException, Set<String> expectedResult) {

        when(mockApplicationContext.getBean(AuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        if (null != authenticationGenerator) {
            when(authenticationGenerator.getRolesKey()).thenReturn(AUTHORITIES.getKey());
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getRoles(payload, clientId));
        }
        else {
            Set<String> result = authenticationService.getRoles(payload, clientId);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> getCustomInformationIncludedByClientTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        AuthenticationGenerator authenticationGenerator = mock(AuthenticationGenerator.class);
        Map<String, Object> sourcePayload = new HashMap<String, Object>() {{
            put("age", 32);
            put(AUTHORITIES.getKey(), asList("admin", "user"));
            put(AUDIENCE.getKey(), clientId);
            put(EXPIRATION_TIME.getKey(), 123456789);
            put(ISSUED_AT.getKey(), "iat value");
            put(JWT_ID.getKey(), "jti value");
            put(NAME.getKey(), "name value");
            put(USERNAME.getKey(), "username value");
        }};
        Map<String, Object> finalPayload = new HashMap<String, Object>() {{
            put("age", 32);
            put(NAME.getKey(), "name value");
        }};
        return Stream.of(
                //@formatter:off
                //            payload,           clientId,   authenticationGenerator,   expectedException,               expectedResult
                Arguments.of( null,              null,       null,                      null,                            new HashMap<>() ),
                Arguments.of( new HashMap<>(),   null,       null,                      ClientNotFoundException.class,   null ),
                Arguments.of( new HashMap<>(),   clientId,   null,                      null,                            new HashMap<>() ),
                Arguments.of( new HashMap<>(),   clientId,   authenticationGenerator,   null,                            new HashMap<>() ),
                Arguments.of( sourcePayload,     clientId,   authenticationGenerator,   null,                            finalPayload )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getCustomInformationIncludedByClientTestCases")
    @DisplayName("getCustomInformationIncludedByClient: test cases")
    public void getCustomInformationIncludedByClient_testCases(Map<String, Object> payload, String clientId,
                                                               AuthenticationGenerator authenticationGenerator,
                                                               Class<? extends Exception> expectedException, Map<String, Object> expectedResult) {
        when(mockApplicationContext.getBean(AuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        if (null != authenticationGenerator) {
            when(authenticationGenerator.getUsernameKey()).thenReturn(USERNAME.getKey());
            when(authenticationGenerator.getRolesKey()).thenReturn(AUTHORITIES.getKey());
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getCustomInformationIncludedByClient(payload, clientId));
        }
        else {
            Map<String, Object> result = authenticationService.getCustomInformationIncludedByClient(payload, clientId);
            assertEquals(expectedResult, result);
        }
    }

}