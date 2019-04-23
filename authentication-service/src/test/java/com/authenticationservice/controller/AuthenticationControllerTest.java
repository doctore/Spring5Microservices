package com.authenticationservice.controller;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.configuration.rest.GlobalErrorWebExceptionHandler;
import com.authenticationservice.configuration.rest.RestRoutes;
import com.authenticationservice.dto.AuthenticationRequestDto;
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

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
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
               .andExpect(content().contentType(Constants.TEXT_PLAIN_JSON_UTF8_VALUE))
               .andExpect(content().string(expectedJwtToken));
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
