package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static com.security.jwt.ObjectGeneratorForTest.buildDefaultAuthenticationInformation;
import static com.security.jwt.ObjectGeneratorForTest.buildDefaultAuthenticationRequest;
import static com.security.jwt.TestUtil.fromJson;
import static com.security.jwt.TestUtil.toJson;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = SecurityController.class)
public class SecurityControllerTest {

    @MockBean
    private SecurityService mockSecurityService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    @DisplayName("login: when no content is given then internal server error is returned")
    public void login_whenNoContentIsGiven_thenInternalServerErrorHttpCodeAndGenericErrorMessageAreReturned() {
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/ItDoesNotCare"))
               .andExpect(status().isInternalServerError())
               .andExpect(content().contentType(TEXT_PLAIN))
               .andExpect(content().string("Internal error in the application"));
    }


    static Stream<Arguments> login_invalidAuthenticationRequestDtoTestCases() {
        String errorMessagePrefix = "Error in the given parameters: ";
        String longString = String.join("", Collections.nCopies(150, "a"));
        return Stream.of(
                //@formatter:off
                //            invalidAuthenticationRequestDto,                                expectedError
                Arguments.of( buildDefaultAuthenticationRequest().withUsername(null),         errorMessagePrefix + "[Field error in object 'authenticationRequestDto' "
                                                                                                                 + "on field 'username' due to: must not be null]" ),
                Arguments.of( buildDefaultAuthenticationRequest().withPassword(null),         errorMessagePrefix + "[Field error in object 'authenticationRequestDto' "
                                                                                                                 + "on field 'password' due to: must not be null]" ),
                Arguments.of( buildDefaultAuthenticationRequest().withUsername(longString),   errorMessagePrefix + "[Field error in object 'authenticationRequestDto' "
                                                                                                                 + "on field 'username' due to: size must be between 1 and 64]" ),
                Arguments.of( buildDefaultAuthenticationRequest().withPassword(longString),   errorMessagePrefix + "[Field error in object 'authenticationRequestDto' "
                                                                                                                 + "on field 'password' due to: size must be between 1 and 128]" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("login_invalidAuthenticationRequestDtoTestCases")
    @DisplayName("login: when given information does not verify validations then bad request error is returned with validation errors")
    public void login_whenGivenEmailRequestDoesNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(AuthenticationRequestDto invalidAuthenticationRequestDto,
                                                                                                                         String expectedErrors) {
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/ItDoesNotCare")
               .contentType(APPLICATION_JSON_UTF8_VALUE)
               .content(toJson(invalidAuthenticationRequestDto)))
               .andExpect(status().isBadRequest())
               .andExpect(content().contentType(TEXT_PLAIN))
               .andExpect(content().string(expectedErrors));

        verifyZeroInteractions(mockSecurityService);
    }


    static Stream<Arguments> login_validAuthenticationRequestDtoTestCases() {
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
    @MethodSource("login_validAuthenticationRequestDtoTestCases")
    @DisplayName("login: when given authentication request verifies the validations then the suitable Http code is returned")
    public void login_whenGivenEmailRequestVerifiersValidations_thenSuitableHttpCodeIsReturned(Optional<AuthenticationInformationDto> authenticationInformation,
                                                                                               HttpStatus expectedResultHttpCode,
                                                                                               AuthenticationInformationDto expectedBodyResult) {
        String clientId = "ItDoesNotCare";
        AuthenticationRequestDto authenticationRequestDto = buildDefaultAuthenticationRequest();

        when(mockSecurityService.login(clientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())).thenReturn(authenticationInformation);

        ResultActions result = mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/ItDoesNotCare")
                                      .contentType(APPLICATION_JSON_UTF8_VALUE)
                                      .content(toJson(authenticationRequestDto)))
                                      .andExpect(status().is(expectedResultHttpCode.value()));

        assertEquals(expectedBodyResult, fromJson(result.andReturn().getResponse().getContentAsString(), AuthenticationInformationDto.class));
        verify(mockSecurityService, times(1)).login(clientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword());
    }

}
