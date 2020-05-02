package com.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.order.OrderServiceApplication;
import com.order.configuration.Constants;
import com.order.configuration.rest.RestRoutes;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderControllerTest {

    @Autowired
    ApplicationContext context;

    private WebTestClient webTestClient;

    @MockBean
    private OrderService mockOrderService;


    @BeforeEach
    public void setup() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }


    @Test
    public void create_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(new OrderDto()), OrderDto.class)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").created(new Date()).orderLines(new ArrayList<>()).build();

        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenGivenDtoDoesNotVerifyTheValidations_thenUnprocessableEntityHttpCodeAndValidationErrorsAreReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").orderLines(new ArrayList<>()).build();

        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("Error in the given parameters: [Field error in object 'orderDto' on field 'created' due to: must not be null]");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenSaveDoesNotReturnAnEntity_thenUnprocessableEntityHttpCodeAndEmptyBodyAreReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").created(new Date()).orderLines(new ArrayList<>()).build();

        // When
        when(mockOrderService.save(any())).thenReturn(Optional.empty());

        // Then
        webTestClient.post()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenNotEmptyDtoIsGiven_thenOkHttpCodeAndOrderDtoAreReturned() {
        // Given
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        OrderLineDto beforeOrderLineDto = OrderLineDto.builder().pizza(carbonara).cost(15D).amount((short)2).build();
        OrderDto beforeOrderDto = OrderDto.builder().code("Order 1").created(new Date())
                                                    .orderLines(Arrays.asList(beforeOrderLineDto)).build();

        OrderLineDto afterOrderLineDto = OrderLineDto.builder().id(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderDto afterOrderDto = OrderDto.builder().id(1).code("Order 1").created(new Date())
                                                   .orderLines(Arrays.asList(afterOrderLineDto)).build();
        // When
        when(mockOrderService.save(beforeOrderDto)).thenReturn(Optional.of(afterOrderDto));

        // Then
        webTestClient.post()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(beforeOrderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(afterOrderDto.getId())
                     .jsonPath("$.code").isEqualTo(afterOrderDto.getCode())
                     .jsonPath("$.created").isEqualTo(afterOrderDto.getCreated())
                     .jsonPath("$.orderLines.[0].id").isEqualTo(afterOrderLineDto.getId())
                     .jsonPath("$.orderLines.[0].amount").isEqualTo(Integer.valueOf(afterOrderLineDto.getAmount()))
                     .jsonPath("$.orderLines.[0].cost").isEqualTo(afterOrderLineDto.getCost())
                     .jsonPath("$.orderLines.[0].pizza.id").isEqualTo(Integer.valueOf(afterOrderLineDto.getPizza().getId()))
                     .jsonPath("$.orderLines.[0].pizza.name").isEqualTo(afterOrderLineDto.getPizza().getName())
                     .jsonPath("$.orderLines.[0].pizza.cost").isEqualTo(afterOrderLineDto.getPizza().getCost());

        verify(mockOrderService, times(1)).save(beforeOrderDto);
    }


    @Test
    public void findByIdWithOrderLines_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // Given
        String validOrderId = "1";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.ORDER.ROOT + "/" + validOrderId + RestRoutes.ORDER.WITH_ORDERLINES)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {"NOT_EXISTING"})
    public void findByIdWithOrderLines_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        String validOrderId = "1";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.ORDER.ROOT + "/" + validOrderId + RestRoutes.ORDER.WITH_ORDERLINES)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByIdWithOrderLines_whenTheIdDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        String notValidOrderId = "0";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.ORDER.ROOT + "/" + notValidOrderId + RestRoutes.ORDER.WITH_ORDERLINES)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("The following constraints have failed: findByIdWithOrderLines.id: must be greater than 0");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByIdWithOrderLines_whenTheIdDoesNotExist_thenNotFoundHttpCodeAndEmptyBodyAreReturned() {
        // When
        when(mockOrderService.findByIdWithOrderLines(anyInt())).thenReturn(Optional.empty());

        // Then
        webTestClient.get()
                     .uri(RestRoutes.ORDER.ROOT + "/1" + RestRoutes.ORDER.WITH_ORDERLINES)
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByIdWithOrderLines_whenTheIdExists_thenOkHttpCodeAndOrderWithOrderlinesAreReturned() throws JsonProcessingException {
        // Given
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).pizza(hawaiian).cost(8D).amount((short)1).build();

        OrderDto orderDto = OrderDto.builder().id(1).code("Order 1").created(new Timestamp(new Date().getTime()))
                                              .orderLines(Arrays.asList(orderLineDto1, orderLineDto2)).build();
        // When
        when(mockOrderService.findByIdWithOrderLines(anyInt())).thenReturn(Optional.of(orderDto));

        // Then
        webTestClient.get()
                     .uri(RestRoutes.ORDER.ROOT + "/" + orderDto.getId() + RestRoutes.ORDER.WITH_ORDERLINES)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(orderDto.getId())
                     .jsonPath("$.code").isEqualTo(orderDto.getCode())
                     .jsonPath("$.orderLines.[0].id").isEqualTo(orderLineDto1.getId())
                     .jsonPath("$.orderLines.[0].amount").isEqualTo(Integer.valueOf(orderLineDto1.getAmount()))
                     .jsonPath("$.orderLines.[0].cost").isEqualTo(orderLineDto1.getCost())
                     .jsonPath("$.orderLines.[0].pizza.id").isEqualTo(Integer.valueOf(carbonara.getId()))
                     .jsonPath("$.orderLines.[0].pizza.name").isEqualTo(carbonara.getName())
                     .jsonPath("$.orderLines.[0].pizza.cost").isEqualTo(carbonara.getCost())
                     .jsonPath("$.orderLines.[1].id").isEqualTo(orderLineDto2.getId())
                     .jsonPath("$.orderLines.[1].amount").isEqualTo(Integer.valueOf(orderLineDto2.getAmount()))
                     .jsonPath("$.orderLines.[1].cost").isEqualTo(orderLineDto2.getCost())
                     .jsonPath("$.orderLines.[1].pizza.id").isEqualTo(Integer.valueOf(hawaiian.getId()))
                     .jsonPath("$.orderLines.[1].pizza.name").isEqualTo(hawaiian.getName())
                     .jsonPath("$.orderLines.[1].pizza.cost").isEqualTo(hawaiian.getCost());
    }


    @Test
    public void update_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(new OrderDto()), OrderDto.class)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void update_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").created(new Date()).orderLines(new ArrayList<>()).build();

        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenGivenDtoDoesNotVerifyTheValidations_thenUnprocessableEntityHttpCodeAndValidationErrorsAreReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").orderLines(new ArrayList<>()).build();

        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("Error in the given parameters: [Field error in object 'orderDto' on field 'created' due to: must not be null]");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenSaveDoesNotReturnAnEntity_thenNotFoundHttpCodeAndEmptyBodyAreReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().code("Order 1").created(new Date()).orderLines(new ArrayList<>()).build();

        // When
        when(mockOrderService.save(any())).thenReturn(Optional.empty());

        // Then
        webTestClient.put()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(orderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenNotEmptyDtoIsGiven_thenOkHttpCodeAndOrderDtoAreReturned() {
        // Given
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        OrderLineDto beforeOrderLineDto = OrderLineDto.builder().id(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderDto beforeOrderDto = OrderDto.builder().id(1).code("Order 1").created(new Date())
                                                    .orderLines(Arrays.asList(beforeOrderLineDto)).build();

        OrderLineDto afterOrderLineDto = OrderLineDto.builder().id(1).pizza(carbonara).cost(7.50D).amount((short)1).build();
        OrderDto afterOrderDto = OrderDto.builder().id(1).code("Order 1 updated").created(new Date())
                                                   .orderLines(Arrays.asList(afterOrderLineDto)).build();
        // When
        when(mockOrderService.save(beforeOrderDto)).thenReturn(Optional.of(afterOrderDto));

        // Then
        webTestClient.put()
                     .uri(RestRoutes.ORDER.ROOT)
                     .body(Mono.just(beforeOrderDto), OrderDto.class)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(afterOrderDto.getId())
                     .jsonPath("$.code").isEqualTo(afterOrderDto.getCode())
                     .jsonPath("$.created").isEqualTo(afterOrderDto.getCreated())
                     .jsonPath("$.orderLines.[0].id").isEqualTo(afterOrderLineDto.getId())
                     .jsonPath("$.orderLines.[0].amount").isEqualTo(Integer.valueOf(afterOrderLineDto.getAmount()))
                     .jsonPath("$.orderLines.[0].cost").isEqualTo(afterOrderLineDto.getCost())
                     .jsonPath("$.orderLines.[0].pizza.id").isEqualTo(Integer.valueOf(afterOrderLineDto.getPizza().getId()))
                     .jsonPath("$.orderLines.[0].pizza.name").isEqualTo(afterOrderLineDto.getPizza().getName())
                     .jsonPath("$.orderLines.[0].pizza.cost").isEqualTo(afterOrderLineDto.getPizza().getCost());

        verify(mockOrderService, times(1)).save(beforeOrderDto);
    }

}
