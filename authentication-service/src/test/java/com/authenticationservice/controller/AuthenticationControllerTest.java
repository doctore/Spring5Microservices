package com.authenticationservice.controller;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.configuration.rest.GlobalErrorWebExceptionHandler;
import com.authenticationservice.configuration.rest.RestRoutes;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.dto.UsernameAuthoritiesDto;
import com.authenticationservice.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService mockAuthenticationService;

    MockMvc mockMvc;

    private AuthenticationController authenticationController;


    @Before
    public void setUp() {
        authenticationController = new AuthenticationController(mockAuthenticationService);
        mockMvc = standaloneSetup(authenticationController)
                 .setControllerAdvice(GlobalErrorWebExceptionHandler.class)
                 .build();
    }


    @Test
    public void login_whenNoContentIsGiven_thenInternalServerErrorHttpCodeAndMissingBodyErrorMessageAreReturned() throws Exception {
        // When/Then
        mockMvc.perform(post(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN))
               .andExpect(status().isInternalServerError())
               .andExpect(content().contentType(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Internal error in the application"));
    }


    @Test
    public void login_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() throws Exception {
        // Given
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").build();
        String jsonRequestDto = mapToJson(requestDto);

        // When/Then
        mockMvc.perform(post(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isUnprocessableEntity())
               .andExpect(content().contentType(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Error in the given parameters: [Field error in object 'authenticationRequestDto' "
                                         + "on field 'password' due to: must not be null]"));
    }


    @Test
    public void login_whenGenerateJwtTokenDoesNotReturnAValidOne_thenBadRequestHttpCodeAndEmptyBodyAreReturned() throws Exception {
        // Given
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").password("password").build();
        String jsonRequestDto = mapToJson(requestDto);

        // When
        when(mockAuthenticationService.generateJwtToken(requestDto)).thenReturn(Optional.empty());

        mockMvc.perform(post(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(""));
    }


    @Test
    public void login_whenGenerateJwtTokenReturnsAValidOne_thenOkHttpCodeAndJwtTokenAreReturned() throws Exception {
        // Given
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").password("password").build();
        String jsonRequestDto = mapToJson(requestDto);
        String expectedJwtToken = "expectedJwtToken";

        // When
        when(mockAuthenticationService.generateJwtToken(requestDto)).thenReturn(Optional.of(expectedJwtToken));

        mockMvc.perform(post(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isOk())
               .andExpect(content().contentType(Constants.TEXT_PLAIN_UTF8_VALUE))
               .andExpect(content().string(expectedJwtToken));
    }


    @Test
    public void validateToken_whenGivenJwtTokenIsNotValid_thenOkHttpCodeAndFalseAsJsonResponseAreReturned() throws Exception {
        // Given
        String notValidToken = "notValidToken";

        // When
        when(mockAuthenticationService.isJwtTokenValid(notValidToken)).thenReturn(false);

        mockMvc.perform(get(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.VALIDATE + "/" + notValidToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(content().string("false"));
    }


    @Test
    public void validateToken_whenGivenJwtTokenIsValid_thenOkHttpCodeAndTrueAsJsonResponseAreReturned() throws Exception {
        // Given
        String validToken = "validToken";

        // When
        when(mockAuthenticationService.isJwtTokenValid(validToken)).thenReturn(true);

        mockMvc.perform(get(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.VALIDATE + "/" + validToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(content().string("true"));
    }


    @Test
    public void getAuthenticationInformation_whenGivenJwtTokenIsNotValid_thenUnauthorizedHttpCodeIsReturned() throws Exception {
        // Given
        String notValidToken = "notValidToken";

        // When
        when(mockAuthenticationService.getAuthenticationInformation(notValidToken)).thenReturn(Optional.empty());

        mockMvc.perform(get(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.AUTHENTICATION_INFO + "/" + notValidToken))
               .andExpect(status().isUnauthorized());
    }


    @Test
    public void getAuthenticationInformation_whenGivenJwtTokenIsValid_thenUnauthorizedHttpCodeIsReturned() throws Exception {
        // Given
        String validToken = "validToken";
        UsernameAuthoritiesDto usernameAuthorities = UsernameAuthoritiesDto.builder().username("username")
                                                                                     .authorities(new HashSet<>(Arrays.asList("admin")))
                                                                                     .build();
        // When
        when(mockAuthenticationService.getAuthenticationInformation(validToken)).thenReturn(Optional.of(usernameAuthorities));

        mockMvc.perform(get(RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.AUTHENTICATION_INFO + "/" + validToken))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.username", is(usernameAuthorities.getUsername())))
               .andExpect(jsonPath("$.authorities", hasItems(usernameAuthorities.getAuthorities().toArray())));
    }


    /**
     * Maps the incoming object into a JSON-formatted string
     *
     * @param object
     *    Object to map into string
     *
     * @return {@link String} with JSON-formatted given object properties
     */
    private <T> String mapToJson(T object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(object);
    }

}
