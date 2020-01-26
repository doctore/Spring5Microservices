package com.security.jwt.service;

import com.security.jwt.ObjectGeneratorForTest;
import com.security.jwt.application.spring5microservices.service.UserService;
import com.security.jwt.exception.ClientNotFoundException;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static java.util.Arrays.asList;
import static com.security.jwt.enums.AuthenticationConfigurationEnum.SPRING5_MICROSERVICES;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class SecurityServiceTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private AuthenticationService mockAuthenticationService;

    private SecurityService securityService;

    @BeforeEach
    public void init() {
        securityService = new SecurityService(mockApplicationContext, mockAuthenticationService);
    }


    static Stream<Arguments> loginTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        String username = "username value";
        UserDetails userDetails = ObjectGeneratorForTest.buildDefaultUser();
        String password = userDetails.getPassword();
        UserService userService = mock(UserService.class);
        Optional<AuthenticationInformationDto> authenticationInformation = of(ObjectGeneratorForTest.buildDefaultAuthenticationInformation());
        return Stream.of(
                //@formatter:off
                //            clientId,     username,   password,   userService,   userDetails,   passwordsMatch,  expectedException,                 authenticationInformation,   expectedResult
                Arguments.of( null,         null,       null,       null,          null,          false,           ClientNotFoundException.class,     null,                        null ),
                Arguments.of( "NotFound",   null,       null,       null,          null,          false,           ClientNotFoundException.class,     null,                        null ),
                Arguments.of( clientId,     null,       null,       userService,   null,          false,           UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( clientId,     username,   null,       userService,   userDetails,   false,           UnauthorizedException.class,       null,                        null ),
                Arguments.of( clientId,     username,   password,   userService,   userDetails,   true,            null,                              empty(),                     empty() ),
                Arguments.of( clientId,     username,   password,   userService,   userDetails,   true,            null,                              authenticationInformation,   authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loginTestCases")
    @DisplayName("login: test cases")
    public void login_testCases(String clientId, String username, String password, UserService userService, UserDetails userDetails,
                                boolean passwordsMatch, Class<? extends Exception> expectedException,
                                Optional<AuthenticationInformationDto> authenticationInformation, Optional<AuthenticationInformationDto> expectedResult) {

        when(mockApplicationContext.getBean(UserService.class)).thenReturn(userService);
        when(mockAuthenticationService.getAuthenticationInformation(clientId, userDetails)).thenReturn(authenticationInformation);
        if (null != userService) {
            if (null == username) {
                when(userService.loadUserByUsername(username)).thenThrow(UsernameNotFoundException.class);
            }
            else {
                when(userService.loadUserByUsername(username)).thenReturn(userDetails);
            }
            when(userService.passwordsMatch(anyString(), eq(userDetails))).thenReturn(passwordsMatch);
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.login(clientId, username, password));
        }
        else {
            Optional<AuthenticationInformationDto> result = securityService.login(clientId, username, password);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> refreshTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        String username = "username value";

        UserService userService = mock(UserService.class);
        Optional<AuthenticationInformationDto> authenticationInformation = of(ObjectGeneratorForTest.buildDefaultAuthenticationInformation());
        return Stream.of(
                //@formatter:off
                //            refreshToken,      clientId,     usernameResult,   userService,   expectedException,                 authenticationInformation,   expectedResult
                Arguments.of( null,              null,         null,             null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( null,              "NotFound",   null,             null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( null,              clientId,     null,             null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( "ItDoesNotCare",   null,         username,         null,          ClientNotFoundException.class,     null,                        null ),
                Arguments.of( "ItDoesNotCare",   "NotFound",   username,         null,          ClientNotFoundException.class,     null,                        null ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         null,          null,                              empty(),                     empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         userService,   null,                              empty(),                     empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         userService,   null,                              authenticationInformation,   authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("refreshTestCases")
    @DisplayName("refresh: test cases")
    public void refresh_testCases(String refreshToken, String clientId, String usernameResult, UserService userService,
                                  Class<? extends Exception> expectedException, Optional<AuthenticationInformationDto> authenticationInformation,
                                  Optional<AuthenticationInformationDto> expectedResult) {
        UserDetails userDetails = ObjectGeneratorForTest.buildDefaultUser();
        Map<String, Object> payload = new HashMap<>();

        when(mockAuthenticationService.getPayloadOfToken(refreshToken, clientId, false)).thenReturn(payload);
        when(mockAuthenticationService.getUsername(payload, clientId)).thenReturn(ofNullable(usernameResult));
        when(mockAuthenticationService.getAuthenticationInformation(clientId, userDetails)).thenReturn(authenticationInformation);
        when(mockApplicationContext.getBean(UserService.class)).thenReturn(userService);
        if (null != userService) {
            if (null == usernameResult) {
                when(userService.loadUserByUsername(usernameResult)).thenThrow(UsernameNotFoundException.class);
            }
            else {
                when(userService.loadUserByUsername(usernameResult)).thenReturn(userDetails);
            }
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.refresh(refreshToken, clientId));
        }
        else {
            Optional<AuthenticationInformationDto> result = securityService.refresh(refreshToken, clientId);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> getAuthorizationInformationTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        String usernameResult = "username value";
        Set<String> rolesResult = new HashSet<>(asList("admin", "user"));
        Map<String, Object> additionalInfoResult = new HashMap<String, Object>() {{
            put(NAME.getKey(), "name value");
        }};
        UsernameAuthoritiesDto usernameAuthorities = ObjectGeneratorForTest.buildUsernameAuthorities(usernameResult, rolesResult, additionalInfoResult);
        return Stream.of(
                //@formatter:off
                //            accessToken,       clientId,     usernameResult,   rolesResult,   additionalInfoResult,   expectedException,                 expectedResult
                Arguments.of( null,              null,         null,             null,          null,                   UsernameNotFoundException.class,   null ),
                Arguments.of( null,              "NotFound",   null,             null,          null,                   UsernameNotFoundException.class,   null ),
                Arguments.of( null,              clientId,     null,             null,          null,                   UsernameNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,     null,             null,          null,                   UsernameNotFoundException.class,   null ),
                Arguments.of( "ItDoesNotCare",   clientId,     usernameResult,   rolesResult,   additionalInfoResult,   null,                              usernameAuthorities )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getAuthorizationInformationTestCases")
    @DisplayName("getAuthorizationInformation: test cases")
    public void getAuthorizationInformation_testCases(String accessToken, String clientId, String usernameResult, Set<String> rolesResult,
                                                      Map<String, Object> additionalInfoResult, Class<? extends Exception> expectedException,
                                                      UsernameAuthoritiesDto expectedResult) {
        Map<String, Object> payload = new HashMap<>();

        when(mockAuthenticationService.getPayloadOfToken(accessToken, clientId, true)).thenReturn(payload);
        when(mockAuthenticationService.getUsername(payload, clientId)).thenReturn(ofNullable(usernameResult));
        when(mockAuthenticationService.getRoles(payload, clientId)).thenReturn(rolesResult);
        when(mockAuthenticationService.getCustomInformationIncludedByClient(payload, clientId)).thenReturn(additionalInfoResult);
        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.getAuthorizationInformation(accessToken, clientId));
        }
        else {
            UsernameAuthoritiesDto result = securityService.getAuthorizationInformation(accessToken, clientId);
            assertEquals(expectedResult, result);
        }
    }

}
