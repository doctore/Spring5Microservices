package com.security.jwt.controller;

import com.security.jwt.ObjectGeneratorForTest;
import com.security.jwt.TestUtil;
import com.security.jwt.configuration.rest.GlobalErrorWebExceptionHandler;
import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
public class SecurityControllerTest {

    @Mock
    private SecurityService mockSecurityService;

    MockMvc mockMvc;

    private SecurityController securityController;

    @BeforeEach
    public void init() {
        securityController = new SecurityController(mockSecurityService);
        mockMvc = standaloneSetup(securityController)
                     .setControllerAdvice(GlobalErrorWebExceptionHandler.class)
                     .build();
    }


    @Test
    @DisplayName("login: when no content is given then internal server error is returned")
    public void login_whenNoContentIsGiven_thenInternalServerErrorHttpCodeAndGenericErrorMessageAreReturned() throws Exception {
        // Given
        String clientId = "ItDoesNotCare";

        // When/Then
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/" + clientId))
               .andExpect(status().isInternalServerError())
               .andExpect(content().contentType(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Internal error in the application"));
    }


    @Test
    @DisplayName("login: when given information does not verify validations then bad request error is returned with validation errors")
    public void login_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() throws Exception {
        // Given
        String clientId = "ItDoesNotCare";
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").build();
        String jsonRequestDto = TestUtil.mapToJson(requestDto);

        // When/Then
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/" + clientId)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isUnprocessableEntity())
               .andExpect(content().contentType(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Error in the given parameters: [Field error in object 'authenticationRequestDto' "
                                         + "on field 'password' due to: must not be null]"));
    }


    @Test
    @DisplayName("login: when no authentication information is generated then bad request error is returned")
    public void login_whenNoAuthenticationInformationDtoIsGenerated_thenBadRequestHttpCodeAndEmptyBodyAreReturned() throws Exception {
        // Given
        String clientId = "ItDoesNotCare";
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").password("password").build();
        String jsonRequestDto = TestUtil.mapToJson(requestDto);

        // When
        when(mockSecurityService.login(clientId, requestDto.getUsername(), requestDto.getPassword())).thenReturn(empty());

        // Then
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/" + clientId)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(""));
    }


    @Test
    @DisplayName("login: when authentication information is generated then Ok Http code and authentication information are returned")
    public void login_whenAuthenticationInformationDtoIsGenerated_thenOkHttpCodeAndAuthenticationInformationAreReturned() throws Exception {
        // Given
        String clientId = "ItDoesNotCare";
        AuthenticationRequestDto requestDto = AuthenticationRequestDto.builder().username("username").password("password").build();
        String jsonRequestDto = TestUtil.mapToJson(requestDto);
        AuthenticationInformationDto expectedAuthenticationInfo = ObjectGeneratorForTest.buildDefaultAuthenticationInformation();

        // When
        when(mockSecurityService.login(clientId, requestDto.getUsername(), requestDto.getPassword())).thenReturn(of(expectedAuthenticationInfo));

        // Then
        mockMvc.perform(post(RestRoutes.SECURITY.ROOT + RestRoutes.SECURITY.LOGIN + "/" + clientId)
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonRequestDto))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
               .andExpect(jsonPath("$.access_token", is(expectedAuthenticationInfo.getAccessToken())))
               .andExpect(jsonPath("$.token_type", is(expectedAuthenticationInfo.getTokenType())))
               .andExpect(jsonPath("$.refresh_token", is(expectedAuthenticationInfo.getRefreshToken())))
               .andExpect(jsonPath("$.expires_in", is(expectedAuthenticationInfo.getExpiresIn())))
               .andExpect(jsonPath("$.jti", is(expectedAuthenticationInfo.getJwtId())))
               .andExpect(jsonPath("$.scope", is(expectedAuthenticationInfo.getScope())))
               .andExpect(jsonPath("$.additionalInfo", is(expectedAuthenticationInfo.getAdditionalInfo())));
    }

}
