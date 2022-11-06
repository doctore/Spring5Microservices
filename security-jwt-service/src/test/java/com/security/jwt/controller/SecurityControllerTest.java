package com.security.jwt.controller;

import com.security.jwt.SecurityJwtServiceApplication;
import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.security.jwt.TestDataFactory.buildAuthenticationRequest;
import static com.security.jwt.TestDataFactory.buildDefaultAuthenticationInformation;
import static com.security.jwt.TestDataFactory.buildUsernameAuthorities;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(classes = SecurityJwtServiceApplication.class)
public class SecurityControllerTest {

    @Autowired
    ApplicationContext context;

    @MockBean
    private SecurityService mockSecurityService;

    // To avoid Hazelcast instance creation
    @MockBean
    @Qualifier("cacheManager")
    private CacheManager mockCacheManager;

    private WebTestClient webTestClient;

    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }


    @Test
    @SneakyThrows
    @DisplayName("login: when no basic authentication is provided then unauthorized code is returned")
    public void login_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        AuthenticationRequestDto authenticationRequest = buildAuthenticationRequest("usernameValue", "passwordValue");

        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN)
                .body(Mono.just(authenticationRequest), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockSecurityService);
    }


    static Stream<Arguments> login_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(150, "a"));
        return Stream.of(
                //@formatter:off
                //            invalidAuthenticationRequestDto,                                        expectedError
                Arguments.of( buildAuthenticationRequest(null, "passwordValue"),     "Field error in object 'authenticationRequestDto' "
                                                                                                    + "on field 'username' due to: must not be null" ),
                Arguments.of( buildAuthenticationRequest("usernameValue", null),     "Field error in object 'authenticationRequestDto' "
                                                                                                    + "on field 'password' due to: must not be null" ),
                Arguments.of( buildAuthenticationRequest(longString, "passwordValue"),        "Field error in object 'authenticationRequestDto' on "
                                                                                                    + "field 'username' due to: size must be between 1 and 64" ),
                Arguments.of( buildAuthenticationRequest("usernameValue", longString),       "Field error in object 'authenticationRequestDto' on "
                                                                                                    + "field 'password' due to: size must be between 1 and 128" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("login_invalidParametersTestCases")
    @DisplayName("login: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void login_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestDto invalidAuthenticationRequestDto,
                                                                                                                     String expectedErrors) {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(VALIDATION, List.of(expectedErrors));

        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN)
                .body(Mono.just(invalidAuthenticationRequestDto), AuthenticationRequestDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockSecurityService);
    }


    static Stream<Arguments> login_validParametersTestCases() {
        AuthenticationInformationDto authenticationInformation = buildDefaultAuthenticationInformation();
        return Stream.of(
                //@formatter:off
                //            securityServiceResult,           expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                         UNPROCESSABLE_ENTITY,     null ),
                Arguments.of( of(authenticationInformation),   OK,                       authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("login_validParametersTestCases")
    @DisplayName("login: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void login_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                          HttpStatus expectedResultHttpCode,
                                                                                          AuthenticationInformationDto expectedBodyResult) {
        String clientId = "ItDoesNotCare";
        AuthenticationRequestDto authenticationRequestDto = buildAuthenticationRequest("usernameValue", "passwordValue");

        when(mockSecurityService.login(clientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())).thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN)
                .body(Mono.just(authenticationRequestDto), AuthenticationRequestDto.class)
                .exchange();

        response.expectStatus().isEqualTo(expectedResultHttpCode);
        if (null == expectedBodyResult) {
            response.expectBody().isEmpty();
        }
        else {
            response.expectBody(AuthenticationInformationDto.class)
                    .isEqualTo(expectedBodyResult);
        }
        verify(mockSecurityService, times(1)).login(clientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword());
    }


    @Test
    @SneakyThrows
    @DisplayName("refresh: when no basic authentication is provided then unauthorized code is returned")
    public void refresh_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.REFRESH)
                .body(Mono.just("ItDoesNotCare"), String.class)
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockSecurityService);
    }


    @Test
    @SneakyThrows
    @DisplayName("refresh: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void refresh_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(VALIDATION, List.of("refreshToken: size must be between 1 and 2147483647"));

        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.REFRESH)
                .body(Mono.just(""), String.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockSecurityService);
    }

    static Stream<Arguments> refresh_validParametersTestCases() {
        AuthenticationInformationDto authenticationInformation = buildDefaultAuthenticationInformation();
        return Stream.of(
                //@formatter:off
                //            securityServiceResult,           expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                         UNAUTHORIZED,             null ),
                Arguments.of( of(authenticationInformation),   OK,                       authenticationInformation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("refresh_validParametersTestCases")
    @DisplayName("refresh: when given parameters verify the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void refresh_whenGivenParametersVerifyValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                            HttpStatus expectedResultHttpCode,
                                                                                            AuthenticationInformationDto expectedBodyResult) {
        String clientId = "ItDoesNotCare";
        String refreshToken = "refreshToken";

        when(mockSecurityService.refresh(refreshToken, clientId)).thenReturn(authenticationInformation);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.REFRESH)
                .body(Mono.just(refreshToken), String.class)
                .exchange();

        response.expectStatus().isEqualTo(expectedResultHttpCode);
        if (null == expectedBodyResult) {
            response.expectBody().isEmpty();
        }
        else {
            response.expectBody(AuthenticationInformationDto.class)
                    .isEqualTo(expectedBodyResult);
        }
        verify(mockSecurityService, times(1)).refresh(refreshToken, clientId);
    }


    @Test
    @SneakyThrows
    @DisplayName("authorizationInformation: when no basic authentication is provided then unauthorized code is returned")
    public void authorizationInformation_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.AUTHORIZATION_INFO)
                .body(Mono.just("ItDoesNotCare"), String.class)
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockSecurityService);
    }


    @Test
    @SneakyThrows
    @DisplayName("authorizationInformation: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser
    public void authorizationInformation_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() {
        ErrorResponseDto expectedResponse = new ErrorResponseDto(VALIDATION, List.of("accessToken: size must be between 1 and 2147483647"));

        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.AUTHORIZATION_INFO)
                .body(Mono.just(""), String.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);

        verifyNoInteractions(mockSecurityService);
    }

    
    @Test
    @DisplayName("authorizationInformation: when given authentication request verifies the validations then the suitable Http code is returned")
    @WithMockUser(username = "ItDoesNotCare")
    public void authorizationInformation_whenParametersVerifyValidations_thenSuitableHttpCodeIsReturned() {
        String clientId = "ItDoesNotCare";
        String accessToken = "accessToken";
        UsernameAuthoritiesDto usernameAuthorities = buildUsernameAuthorities("username", Set.of("admin"), new HashMap<>());

        when(mockSecurityService.getAuthorizationInformation(accessToken, clientId)).thenReturn(usernameAuthorities);

        webTestClient.post()
                .uri(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.AUTHORIZATION_INFO)
                .body(Mono.just(accessToken), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsernameAuthoritiesDto.class)
                .isEqualTo(usernameAuthorities);

        verify(mockSecurityService, times(1)).getAuthorizationInformation(accessToken, clientId);
    }

}
