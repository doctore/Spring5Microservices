package com.order.controller;

import com.order.configuration.Constants;
import com.order.configuration.rest.RestRoutes;
import com.order.configuration.security.SecurityManager;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.service.OrderService;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static com.order.TestUtil.fromJson;
import static com.order.TestUtil.toJson;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(value = OrderController.class)
public class OrderControllerTest {

    @MockBean
    private SecurityManager mockSecurityManager;

    @MockBean
    private OrderService mockOrderService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    @DisplayName("create: when no logged user is given then unauthorized Http code is returned")
    public void create_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        mockMvc.perform(
                post(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(new OrderDto())))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_USER})
    @DisplayName("create: when no valid role is given then forbidden Http code is returned")
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        OrderDto dtoToCreate = new OrderDto(null, "Order 1", new Date(), asList());

        // When/Then
        mockMvc.perform(
                post(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToCreate)))
                .andExpect(status().isForbidden());
    }


    static Stream<Arguments> createWhenDtoDoesNotVerifyValidationsTestCases() {
        OrderDto dto1 = new OrderDto(null, "Order 1", null, asList());
        OrderDto dto2 = new OrderDto(null, null, new Date(), asList());
        ErrorResponseDto response1 = new ErrorResponseDto(VALIDATION,
                asList("Field error in object 'orderDto' on field 'created' due to: must not be null"));
        ErrorResponseDto response2 = new ErrorResponseDto(VALIDATION,
                asList("Field error in object 'orderDto' on field 'code' due to: must not be null"));
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,   expectedResponse
                Arguments.of( dto1,          response1 ),
                Arguments.of( dto2,          response2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("createWhenDtoDoesNotVerifyValidationsTestCases")
    @DisplayName("create: when dto does not verify validations")
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(
            OrderDto dtoToCreate, ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                post(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));

        thenHttpErrorIsReturned(result, BAD_REQUEST, expectedResponse);
        verifyNoInteractions(mockOrderService);
    }


    static Stream<Arguments> create_validDtoTestCases() {
        PizzaDto pizzaDto = new PizzaDto((short)1, "Carbonara", 7.50);
        OrderLineDto orderLineDto = new OrderLineDto(10, null, pizzaDto, (short)2, 15D);
        OrderDto dto = new OrderDto(null, "Order 1", new Date(), asList(orderLineDto));
        return Stream.of(
                //@formatter:off
                //            serviceResult,   expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),         UNPROCESSABLE_ENTITY,     null ),
                Arguments.of( of(dto),         CREATED,                  dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    @MethodSource("create_validDtoTestCases")
    @DisplayName("create: when given dto verifies the validations then the suitable Http code is returned")
    public void create_whenGivenDtoVerifiesValidations_thenSuitableHttpCodeIsReturned(Optional<OrderDto> serviceResult,
                                                                                      HttpStatus expectedResultHttpCode, OrderDto expectedBodyResult) {
        // Given
        OrderDto dtoToCreate = new OrderDto(null, "Order 1", new Date(), asList());

        // When
        when(mockOrderService.save(dtoToCreate)).thenReturn(serviceResult);

        ResultActions result = mockMvc.perform(
                post(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToCreate)));

        // Then
        result.andExpect(status().is(expectedResultHttpCode.value()));
        assertEquals(expectedBodyResult, fromJson(result.andReturn().getResponse().getContentAsString(), OrderDto.class));
        verify(mockOrderService, times(1)).save(dtoToCreate);
    }


    @Test
    @SneakyThrows
    @DisplayName("findByIdWithOrderLines: when no logged user is given then unauthorized Http code is returned")
    public void findByIdWithOrderLines_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // Given
        Integer validOrderId = 1;

        // When/Then
        mockMvc.perform(get(RestRoutes.ORDER.ROOT + "/" + validOrderId + RestRoutes.ORDER.WITH_ORDERLINES))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {"NOT_EXISTING"})
    @DisplayName("findByIdWithOrderLines: when no valid authority is given then forbidden Http code is returned")
    public void findByIdWithOrderLines_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        Integer validOrderId = 1;

        // When/Then
        mockMvc.perform(get(RestRoutes.ORDER.ROOT + "/" + validOrderId + RestRoutes.ORDER.WITH_ORDERLINES))
                .andExpect(status().isForbidden());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_USER})
    @DisplayName("findByIdWithOrderLines: when id does not verify validations then bad request Http code is returned")
    public void findByIdWithOrderLines_whenTheIdDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        Integer notValidOrderId = 0;
        ErrorResponseDto expectedResponse = new ErrorResponseDto(VALIDATION, asList("Error in path 'findByIdWithOrderLines.id' due to: must be greater than 0"));

        // When/Then
        ResultActions result = mockMvc.perform(get(RestRoutes.ORDER.ROOT + "/" + notValidOrderId + RestRoutes.ORDER.WITH_ORDERLINES));

        thenHttpErrorIsReturned(result, BAD_REQUEST, expectedResponse);
        verifyNoInteractions(mockOrderService);
    }


    static Stream<Arguments> findByIdWithOrderLines_validIdTestCases() {
        PizzaDto pizzaDto = new PizzaDto((short)1, "Carbonara", 7.50);
        OrderLineDto orderLineDto = new OrderLineDto(10, 1, pizzaDto, (short)2, 15D);
        OrderDto dto = new OrderDto(1, "Order 1", new Date(), asList(orderLineDto));
        return Stream.of(
                //@formatter:off
                //            serviceResult,   expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),         NOT_FOUND,                null ),
                Arguments.of( of(dto),         OK,                       dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    @MethodSource("findByIdWithOrderLines_validIdTestCases")
    @DisplayName("findByIdWithOrderLines: when given Id verifies the validations then the suitable Http code is returned")
    public void findByIdWithOrderLines_whenGivenIdVerifiesValidations_thenSuitableHttpCodeIsReturned(Optional<OrderDto> serviceResult,
                                                                                                     HttpStatus expectedResultHttpCode, OrderDto expectedBodyResult) {
        // Given
        Integer validOrderId = 1;

        // When
        when(mockOrderService.findByIdWithOrderLines(validOrderId)).thenReturn(serviceResult);

        ResultActions result = mockMvc.perform(get(RestRoutes.ORDER.ROOT + "/" + validOrderId + RestRoutes.ORDER.WITH_ORDERLINES));

        // Then
        result.andExpect(status().is(expectedResultHttpCode.value()));
        assertEquals(expectedBodyResult, fromJson(result.andReturn().getResponse().getContentAsString(), OrderDto.class));
        verify(mockOrderService, times(1)).findByIdWithOrderLines(validOrderId);
    }


    @Test
    @SneakyThrows
    @DisplayName("update: when no logged user is given then unauthorized Http code is returned")
    public void update_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        mockMvc.perform(
                put(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(new OrderDto())))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_USER})
    @DisplayName("update: when no valid role is given then forbidden Http code is returned")
    public void update_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        OrderDto dtoToUpdate = new OrderDto(null, "Order 1", new Date(), asList());

        // When/Then
        mockMvc.perform(
                put(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToUpdate)))
                .andExpect(status().isForbidden());
    }


    static Stream<Arguments> updateWhenDtoDoesNotVerifyValidationsTestCases() {
        OrderDto dto1 = new OrderDto(null, "Order 1", null, asList());
        OrderDto dto2 = new OrderDto(null, null, new Date(), asList());
        ErrorResponseDto response1 = new ErrorResponseDto(VALIDATION,
                asList("Field error in object 'orderDto' on field 'created' due to: must not be null"));
        ErrorResponseDto response2 = new ErrorResponseDto(VALIDATION,
                asList("Field error in object 'orderDto' on field 'code' due to: must not be null"));
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,   expectedResponse
                Arguments.of( dto1,          response1 ),
                Arguments.of( dto2,          response2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("updateWhenDtoDoesNotVerifyValidationsTestCases")
    @DisplayName("update: when dto does not verify validations")
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(
            OrderDto dtoToCreate, ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                put(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));

        thenHttpErrorIsReturned(result, BAD_REQUEST, expectedResponse);
        verifyNoInteractions(mockOrderService);
    }


    static Stream<Arguments> update_validDtoTestCases() {
        PizzaDto pizzaDto = new PizzaDto((short)1, "Carbonara", 7.50);
        OrderLineDto orderLineDto = new OrderLineDto(10, 1, pizzaDto, (short)2, 15D);
        OrderDto dto = new OrderDto(1, "Order 1", new Date(), asList(orderLineDto));
        return Stream.of(
                //@formatter:off
                //            serviceResult,   expectedResultHttpCode,   expectedBodyResult
                Arguments.of( empty(),         UNPROCESSABLE_ENTITY,     null ),
                Arguments.of( of(dto),         CREATED,                  dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    @MethodSource("update_validDtoTestCases")
    @DisplayName("update: when given dto verifies the validations then the suitable Http code is returned")
    public void update_whenGivenDtoVerifiesValidations_thenSuitableHttpCodeIsReturned(Optional<OrderDto> serviceResult,
                                                                                      HttpStatus expectedResultHttpCode, OrderDto expectedBodyResult) {
        // Given
        OrderDto dtoToUpdate = new OrderDto(1, "Order 1", new Date(), asList());

        // When
        when(mockOrderService.save(dtoToUpdate)).thenReturn(serviceResult);

        ResultActions result = mockMvc.perform(
                post(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(dtoToUpdate)));

        // Then
        result.andExpect(status().is(expectedResultHttpCode.value()));
        assertEquals(expectedBodyResult, fromJson(result.andReturn().getResponse().getContentAsString(), OrderDto.class));
        verify(mockOrderService, times(1)).save(dtoToUpdate);
    }


    @SneakyThrows
    private void thenHttpErrorIsReturned(ResultActions webResult, HttpStatus expectedHttpCode,
                                         ErrorResponseDto errorResponse) {
        webResult.andExpect(status().is(expectedHttpCode.value()));
        assertEquals(errorResponse, fromJson(webResult.andReturn().getResponse().getContentAsString(), ErrorResponseDto.class));
    }

}