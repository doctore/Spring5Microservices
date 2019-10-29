package com.security.jwt.service;

import com.security.jwt.ObjectGeneratorForTest;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.service.authentication.AuthenticationService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
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


    /*
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
                Arguments.of( clientId,     username,   null,       userService,   userDetails,   false,           UnAuthorizedException.class,       null,                        null ),
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
            when(userService.passwordsMatch(anyString(), anyString())).thenReturn(passwordsMatch);
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.login(clientId, username, password));
        }
        else {
            Optional<AuthenticationInformationDto> result = securityService.login(clientId, username, password);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> refreshTokenTestCases() {
        String clientId = SPRING5_MICROSERVICES.getClientId();
        String username = "username value";
        UserDetails userDetails = ObjectGeneratorForTest.buildDefaultUser();
        UserService userService = mock(UserService.class);
        Optional<AuthenticationInformationDto> authenticationInformation = of(ObjectGeneratorForTest.buildDefaultAuthenticationInformation());
        return Stream.of(
                //@formatter:off
                //            refreshToken,      clientId,     usernameResult,   userService,   userDetails,   expectedException,                 authenticationInformation,   expectedResult
                Arguments.of( null,              null,         null,             null,          null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( null,              "NotFound",   null,             null,          null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( null,              clientId,     null,             null,          null,          UsernameNotFoundException.class,   null,                        null ),
                Arguments.of( "ItDoesNotCare",   null,         username,         null,          null,          ClientNotFoundException.class,     null,                        null ),
                Arguments.of( "ItDoesNotCare",   "NotFound",   username,         null,          null,          ClientNotFoundException.class,     null,                        null ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         null,          null,          null,                              empty(),                     empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         userService,   null,          null,                              empty(),                     empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         userService,   userDetails,   null,                              empty(),                     empty() ),
                Arguments.of( "ItDoesNotCare",   clientId,     username,         userService,   userDetails,   null,                              authenticationInformation,   authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("refreshTokenTestCases")
    @DisplayName("refreshToken: test cases")
    public void refreshToken_testCases(String refreshToken, String clientId, String usernameResult, UserService userService, UserDetails userDetails,
                                       Class<? extends Exception> expectedException,
                                       Optional<AuthenticationInformationDto> authenticationInformation, Optional<AuthenticationInformationDto> expectedResult) {

        when(mockApplicationContext.getBean(UserService.class)).thenReturn(userService);
        when(mockAuthenticationService.getUsername(refreshToken, clientId)).thenReturn(ofNullable(usernameResult));
        when(mockAuthenticationService.getAuthenticationInformation(clientId, userDetails)).thenReturn(authenticationInformation);
        if (null != userService) {
            if (null == usernameResult) {
                when(userService.loadUserByUsername(usernameResult)).thenThrow(UsernameNotFoundException.class);
            }
            else {
                when(userService.loadUserByUsername(usernameResult)).thenReturn(userDetails);
            }
        }

        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.refreshToken(refreshToken, clientId));
        }
        else {
            Optional<AuthenticationInformationDto> result = securityService.refreshToken(refreshToken, clientId);
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
        when(mockAuthenticationService.getUsername(accessToken, clientId)).thenReturn(ofNullable(usernameResult));
        when(mockAuthenticationService.getRoles(accessToken, clientId)).thenReturn(rolesResult);
        when(mockAuthenticationService.getAdditionalInformation(accessToken, clientId)).thenReturn(additionalInfoResult);
        if (null != expectedException) {
            assertThrows(expectedException, () -> securityService.getAuthorizationInformation(accessToken, clientId));
        }
        else {
            UsernameAuthoritiesDto result = securityService.getAuthorizationInformation(accessToken, clientId);
            assertEquals(expectedResult, result);
        }
    }
     */

}
