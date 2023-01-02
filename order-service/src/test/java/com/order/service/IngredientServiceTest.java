package com.order.service;

import com.order.dto.IngredientAmountDto;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.grpc.service.IngredientServiceGrpcImpl;
import com.spring5microservices.grpc.IngredientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildIngredientAmount;
import static com.order.TestDataFactory.buildIngredientResponse;
import static com.order.TestDataFactory.buildOrderDto;
import static com.order.TestDataFactory.buildOrderLineDto;
import static com.order.TestDataFactory.buildPizzaDto;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class IngredientServiceTest {

    @Mock
    private IngredientServiceGrpcImpl mockIngredientServiceGrpc;

    @Mock
    private OrderService mockOrderService;

    private IngredientService service;


    @BeforeEach
    public void init() {
        service = new IngredientService(mockIngredientServiceGrpc, mockOrderService);
    }


    static Stream<Arguments> getSummaryByOrderId_NoResultOrEmptyTestCases() {
        OrderLineDto orderLineWithoutPizza =  buildOrderLineDto(20, 1, null, (short)1, 7.50D);
        OrderDto orderWithoutPizza = buildOrderDto(1, "Order1", new Date(), List.of(orderLineWithoutPizza));
        return Stream.of(
                //@formatter:off
                //            orderId,                     orderServiceResult,      expectedResult
                Arguments.of( null,                        empty(),                 empty() ),
                Arguments.of( orderWithoutPizza.getId(),   empty(),                 empty() ),
                Arguments.of( orderWithoutPizza.getId(),   of(orderWithoutPizza),   of(Set.of()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getSummaryByOrderId_NoResultOrEmptyTestCases")
    @DisplayName("getSummaryByOrderId: no result or empty test cases")
    public void getSummaryByOrderId_NoResultOrEmpty_testCases(Integer orderId,
                                                              Optional<OrderDto> orderServiceResult,
                                                              Optional<Set<IngredientAmountDto>> expectedResult) {
        when(mockOrderService.findByIdWithOrderLines(orderId)).thenReturn(orderServiceResult);
        when(mockIngredientServiceGrpc.getByPizzaId(anyShort())).thenReturn(List.of());

        Optional<Set<IngredientAmountDto>> result = service.getSummaryByOrderId(orderId);

        assertEquals(expectedResult, result);
    }


    @Test
    @DisplayName("getSummaryByOrderId: when there is a list of ingredients then the summary is returned")
    public void getSummaryByOrderId_whenThereIsAListOfIngredients_thenSummaryIsReturned() {
        IngredientResponse ingredient1 = buildIngredientResponse(1, "Cheese");
        IngredientResponse ingredient2 = buildIngredientResponse(2, "Egg");
        IngredientResponse ingredient3 = buildIngredientResponse(3, "Mozzarella");
        IngredientResponse ingredient4 = buildIngredientResponse(4, "Oregano");
        IngredientResponse ingredient5 = buildIngredientResponse(5, "Pineapple");
        List<IngredientResponse> ingredientsPizza1 = List.of(ingredient1, ingredient2, ingredient4, ingredient5);
        List<IngredientResponse> ingredientsPizza2 = List.of(ingredient1, ingredient3, ingredient4, ingredient5);

        PizzaDto pizza1 = buildPizzaDto((short)30, "Carbonara", 6D);
        PizzaDto pizza2 = buildPizzaDto((short)31, "Margherita", 7D);
        OrderLineDto orderLine1 =  buildOrderLineDto(21, 2, pizza1, (short)2, 12D);
        OrderLineDto orderLine2 =  buildOrderLineDto(22, 2, pizza2, (short)3, 21D);
        OrderDto order = buildOrderDto(2, "Order2", new Date(), List.of(orderLine1, orderLine2));

        Set<IngredientAmountDto> expectedResult = Set.of(
                buildIngredientAmount(ingredient1.getName(), 2),
                buildIngredientAmount(ingredient2.getName(), 1),
                buildIngredientAmount(ingredient3.getName(), 1),
                buildIngredientAmount(ingredient4.getName(), 2),
                buildIngredientAmount(ingredient5.getName(), 2)
        );

        when(mockOrderService.findByIdWithOrderLines(order.getId())).thenReturn(of(order));
        when(mockIngredientServiceGrpc.getByPizzaId(pizza1.getId())).thenReturn(ingredientsPizza1);
        when(mockIngredientServiceGrpc.getByPizzaId(pizza2.getId())).thenReturn(ingredientsPizza2);

        Optional<Set<IngredientAmountDto>> result = service.getSummaryByOrderId(order.getId());

        assertEquals(of(expectedResult), result);
    }

}
