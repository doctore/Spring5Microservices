package com.order.controller;

import com.order.configuration.Constants;
import com.order.configuration.documentation.DocumentationConfiguration;
import com.order.configuration.rest.RestRoutes;
import com.order.configuration.security.SecurityManager;
import com.order.configuration.security.WebSecurityConfiguration;
import com.order.dto.IngredientAmountDto;
import com.order.grpc.client.GrpcClientRunner;
import com.order.service.IngredientService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildIngredientAmount;
import static com.order.TestUtil.fromJson;
import static com.order.TestUtil.fromJsonSet;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = IngredientController.class)
@Import(WebSecurityConfiguration.class)
public class IngredientControllerTest {

    @MockBean
    private SecurityManager mockSecurityManager;

    @MockBean
    private IngredientService mockIngredientService;

    @MockBean
    private DocumentationConfiguration documentationConfiguration;

    // To avoid gRPC client initialization
    @MockBean
    private GrpcClientRunner grpcClientRunner;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    @DisplayName("getSummaryByOrderId: when no logged user is given then unauthorized Http code is returned")
    public void getSummaryByOrderId_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // Given
        int validOrderId = 1;

        // When/Then
        mockMvc.perform(get(RestRoutes.INGREDIENT.ROOT + "/" + validOrderId + RestRoutes.INGREDIENT.SUMMARY))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {"NOT_EXISTING"})
    @DisplayName("getSummaryByOrderId: when no valid authority is given then forbidden Http code is returned")
    public void getSummaryByOrderId_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        int validOrderId = 1;

        // When/Then
        mockMvc.perform(get(RestRoutes.INGREDIENT.ROOT + "/" + validOrderId + RestRoutes.INGREDIENT.SUMMARY))
                .andExpect(status().isForbidden());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_USER})
    @DisplayName("getSummaryByOrderId: when id does not verify validations then bad request Http code is returned")
    public void getSummaryByOrderId_whenTheIdDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        int notValidOrderId = 0;
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                VALIDATION,
                List.of("Error in path 'getSummaryByOrderId.orderId' due to: must be greater than 0")
        );

        // When/Then
        ResultActions result = mockMvc.perform(get(RestRoutes.INGREDIENT.ROOT + "/" + notValidOrderId + RestRoutes.INGREDIENT.SUMMARY));

        thenHttpErrorIsReturned(result, BAD_REQUEST, expectedResponse);
        verifyNoInteractions(mockIngredientService);
    }




    static Stream<Arguments> getSummaryByOrderId_validIdTestCases() {
        Set<IngredientAmountDto> ingredientsSummary = Set.of(
                buildIngredientAmount("Mozzarella", 2),
                buildIngredientAmount("Oregano", 1)
        );
        return Stream.of(
                //@formatter:off
                //            serviceResult,            expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),                  NOT_FOUND,                null ),
                Arguments.of( of(ingredientsSummary),   OK,                       ingredientsSummary )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    @MethodSource("getSummaryByOrderId_validIdTestCases")
    @DisplayName("getSummaryByOrderId: when given orderId verifies the validations then the suitable Http code is returned")
    public void getSummaryByOrderId_whenGivenIdVerifiesValidations_thenSuitableHttpCodeIsReturned(Optional<Set<IngredientAmountDto>> serviceResult,
                                                                                                  HttpStatus expectedResultHttpCode,
                                                                                                  Set<IngredientAmountDto> expectedBodyResult) {
        // Given
        Integer validOrderId = 1;

        // When
        when(mockIngredientService.getSummaryByOrderId(validOrderId)).thenReturn(serviceResult);

        ResultActions result = mockMvc.perform(get(RestRoutes.INGREDIENT.ROOT + "/" + validOrderId + RestRoutes.INGREDIENT.SUMMARY));

        // Then
        result.andExpect(status().is(expectedResultHttpCode.value()));
        assertEquals(expectedBodyResult, fromJsonSet(result.andReturn().getResponse().getContentAsString(), IngredientAmountDto.class));
        verify(mockIngredientService, times(1)).getSummaryByOrderId(validOrderId);
    }


    @SneakyThrows
    private void thenHttpErrorIsReturned(ResultActions webResult, HttpStatus expectedHttpCode,
                                         ErrorResponseDto errorResponse) {
        webResult.andExpect(status().is(expectedHttpCode.value()));
        assertEquals(errorResponse, fromJson(webResult.andReturn().getResponse().getContentAsString(), ErrorResponseDto.class));
    }

}
