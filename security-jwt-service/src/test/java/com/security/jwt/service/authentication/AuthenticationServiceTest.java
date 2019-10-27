package com.security.jwt.service.authentication;

import com.security.jwt.ObjectGeneratorForTest;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.TokenVerificationEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.service.authentication.generator.Spring5MicroserviceAuthenticationGenerator;
import com.security.jwt.util.JwtUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static com.security.jwt.enums.AuthenticationConfigurationEnum.SPRING5_MICROSERVICES;
import static com.security.jwt.enums.TokenVerificationEnum.CORRECT_TOKEN;
import static com.security.jwt.enums.TokenVerificationEnum.EXPIRED_TOKEN;
import static com.security.jwt.enums.TokenVerificationEnum.UNKNOWN_ERROR;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private JwtUtil mockJwtUtil;

    @Mock
    private TextEncryptor mockEncryptor;

    private AuthenticationService authenticationService;

    @BeforeEach
    public void init() {
        authenticationService = new AuthenticationService(mockApplicationContext, mockJwtClientDetailsService, mockJwtUtil, mockEncryptor);
    }


    static Stream<Arguments> getAuthenticationInformationTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        UserDetails userDetails = ObjectGeneratorForTest.buildDefaultUser();
        Spring5MicroserviceAuthenticationGenerator authenticationGenerator = mock(Spring5MicroserviceAuthenticationGenerator.class);
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
    public void getAuthenticationInformation_testCases(String clientId, UserDetails userDetails, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                                       JwtClientDetails clientDetailsResult, Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                                       boolean isResultEmpty) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);

        if (null != authenticationGenerator) {
            when(authenticationGenerator.getRawAuthenticationInformation(userDetails)).thenReturn(rawAuthenticationInformation);
        }
        if (null != clientDetailsResult) {
            when(mockJwtUtil.generateJwtToken(anyMap(), eq(clientDetailsResult.getJwtAlgorithm()), anyString(), anyInt())).thenReturn(of("JWT token"));
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
            assertNotNull(result.get().getAccessToken());
            assertNotNull(result.get().getRefreshToken());
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


    static Stream<Arguments> checkAccessTokenTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        return Stream.of(
                //@formatter:off
                //            accessToken,       clientId,   clientDetailsResult,   isTokenValidResult,   isAccessToken,   expectedException
                Arguments.of( null,              null,       null,                  null,                 false,           ClientNotFoundException.class ),
                Arguments.of( "ItDoesNotCare",   null,       null,                  null,                 false,           ClientNotFoundException.class ),
                Arguments.of( "ItDoesNotCare",   clientId,   null,                  null,                 false,           ClientNotFoundException.class ),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         EXPIRED_TOKEN,        false,           TokenExpiredException.class ),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         UNKNOWN_ERROR,        false,           UnAuthorizedException.class ),
                Arguments.of( "InvalidToken",    clientId,   clientDetails,         CORRECT_TOKEN,        false,           UnAuthorizedException.class ),
                Arguments.of( "ValidToken",      clientId,   clientDetails,         CORRECT_TOKEN,        true,            null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("checkAccessTokenTestCases")
    @DisplayName("checkAccessToken: test cases")
    public void checkAccessToken_testCases(String accessToken, String clientId, JwtClientDetails clientDetailsResult, TokenVerificationEnum isTokenValidResult,
                                           boolean isAccessToken, Class<? extends Exception> expectedException) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        when(mockJwtUtil.isTokenValid(accessToken, decryptedJwtSecret)).thenReturn(isTokenValidResult);
        when(mockJwtUtil.getKey(eq(accessToken), eq(decryptedJwtSecret), anyString(), eq(String.class))).thenReturn(isAccessToken ? empty() : of(""));
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
        }
        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.checkAccessToken(accessToken, clientId));
        }
        else {
            authenticationService.checkAccessToken(accessToken, clientId);
        }
    }


    static Stream<Arguments> checkRefreshTokenTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        return Stream.of(
                //@formatter:off
                //            refreshToken,      clientId,   clientDetailsResult,   isTokenValidResult,   isRefreshToken,   expectedException
                Arguments.of( null,              null,       null,                  null,                 false,            ClientNotFoundException.class),
                Arguments.of( "ItDoesNotCare",   null,       null,                  null,                 false,            ClientNotFoundException.class),
                Arguments.of( "ItDoesNotCare",   clientId,   null,                  null,                 false,            ClientNotFoundException.class),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         EXPIRED_TOKEN,        false,            TokenExpiredException.class),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         UNKNOWN_ERROR,        false,            UnAuthorizedException.class),
                Arguments.of( "InvalidToken",    clientId,   clientDetails,         CORRECT_TOKEN,        false,            UnAuthorizedException.class),
                Arguments.of( "ValidToken",      clientId,   clientDetails,         CORRECT_TOKEN,        true,             null)
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("checkRefreshTokenTestCases")
    @DisplayName("checkRefreshToken: test cases")
    public void checkRefreshToken_testCases(String refreshToken, String clientId, JwtClientDetails clientDetailsResult, TokenVerificationEnum isTokenValidResult,
                                            boolean isRefreshToken, Class<? extends Exception> expectedException) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        when(mockJwtUtil.isTokenValid(refreshToken, decryptedJwtSecret)).thenReturn(isTokenValidResult);
        when(mockJwtUtil.getKey(eq(refreshToken), eq(decryptedJwtSecret), anyString(), eq(String.class))).thenReturn(isRefreshToken ? of("") : empty());
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
        }
        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.checkRefreshToken(refreshToken, clientId));
        }
        else {
            authenticationService.checkRefreshToken(refreshToken, clientId);
        }
    }


    static Stream<Arguments> getUsernameTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        Spring5MicroserviceAuthenticationGenerator authenticationGenerator = mock(Spring5MicroserviceAuthenticationGenerator.class);
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        Optional<String> getUsernameResult = of("username value");
        return Stream.of(
                //@formatter:off
                //            token,             clientId,   authenticationGenerator,   clientDetailsResult,   getUsernameResult,   expectedException,               expectedResult
                Arguments.of( null,              null,       null,                      null,                  null,                null,                            empty() ),
                Arguments.of( "ItDoesNotCare",   null,       null,                      null,                  null,                ClientNotFoundException.class,   empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,   null,                      null,                  null,                null,                            empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   null,                  null,                ClientNotFoundException.class,   empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   clientDetails,         empty(),             null,                            empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   clientDetails,         getUsernameResult,   null,                            getUsernameResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getUsernameTestCases")
    @DisplayName("getUsername: test cases")
    public void getUsername_testCases(String token, String clientId, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                      JwtClientDetails clientDetailsResult, Optional<String> getUsernameResult, Class<? extends Exception> expectedException,
                                      Optional<String> expectedResult) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
            when(mockJwtUtil.getUsername(token, decryptedJwtSecret, authenticationGenerator.getUsernameKey())).thenReturn(getUsernameResult);
        }
        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getUsername(token, clientId));
        }
        else {
            Optional<String> result = authenticationService.getUsername(token, clientId);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> getRolesTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        Spring5MicroserviceAuthenticationGenerator authenticationGenerator = mock(Spring5MicroserviceAuthenticationGenerator.class);
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        Set<String> getRolesResult = new HashSet<>(asList("admin"));
        return Stream.of(
                //@formatter:off
                //            token,             clientId,   authenticationGenerator,   clientDetailsResult,   getRolesResult,    expectedException,               expectedResult
                Arguments.of( null,              null,       null,                      null,                  null,              null,                            new HashSet<>() ),
                Arguments.of( "ItDoesNotCare",   null,       null,                      null,                  null,              ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   null,                      null,                  null,              null,                            new HashSet<>() ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   null,                  null,              ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   clientDetails,         new HashSet<>(),   null,                            new HashSet<>() ),
                Arguments.of( "ItDoesNotCare",   clientId,   authenticationGenerator,   clientDetails,         getRolesResult,    null,                            getRolesResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRolesTestCases")
    @DisplayName("getRoles: test cases")
    public void getRoles_testCases(String token, String clientId, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                      JwtClientDetails clientDetailsResult, Set<String> getRolesResult, Class<? extends Exception> expectedException,
                                      Set<String> expectedResult) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
            when(mockJwtUtil.getRoles(token, decryptedJwtSecret, authenticationGenerator.getRolesKey())).thenReturn(getRolesResult);
        }
        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getRoles(token, clientId));
        }
        else {
            Set<String> result = authenticationService.getRoles(token, clientId);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> getAdditionalInformationTestCases() {
        String clientId = "clientId value";
        JwtClientDetails clientDetails = ObjectGeneratorForTest.buildDefaultJwtClientDetails(clientId);
        Map<String, Object> getExceptGivenKeysResult = new HashMap<String, Object>() {{
            put("username", "username value");
            put("roles", asList("admin"));
            put("age", 23);
        }};
        return Stream.of(
                //@formatter:off
                //            token,             clientId,   clientDetailsResult,   getExceptGivenKeysResult,    expectedException,               expectedResult
                Arguments.of( null,              null,       null,                  null,                        null,                            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",   null,       null,                  null,                        ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   null,                  null,                        ClientNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         new HashMap<>(),             null,                            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",   clientId,   clientDetails,         getExceptGivenKeysResult,    null,                            getExceptGivenKeysResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAdditionalInformationTestCases")
    @DisplayName("getAdditionalInformation: test cases")
    public void getAdditionalInformation_testCases(String token, String clientId, JwtClientDetails clientDetailsResult,Map<String, Object> getExceptGivenKeysResult,
                                                   Class<? extends Exception> expectedException, Map<String, Object> expectedResult) {
        String decryptedJwtSecret = "secretKey_ForTestingPurpose@12345#";

        when(mockEncryptor.decrypt(anyString())).thenReturn(decryptedJwtSecret);
        if (null == clientDetailsResult) {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenThrow(ClientNotFoundException.class);
        }
        else {
            when(mockJwtClientDetailsService.findByClientId(clientId)).thenReturn(clientDetailsResult);
            when(mockJwtUtil.getExceptGivenKeys(eq(token), eq(decryptedJwtSecret), anySet())).thenReturn(getExceptGivenKeysResult);
        }
        if (null != expectedException) {
            assertThrows(expectedException, () -> authenticationService.getAdditionalInformation(token, clientId));
        }
        else {
            Map<String, Object> result = authenticationService.getAdditionalInformation(token, clientId);
            assertEquals(expectedResult, result);
        }
    }

}