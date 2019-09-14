package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderConverterTest {

    @Autowired
    private OrderConverter orderConverter;


    @Test
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        // When
        Order order = orderConverter.fromDtoToModel(null);

        // Then
        assertNull(order);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().id(1).code("Order 1").created(new Date()).build();

        // When
        Order order = orderConverter.fromDtoToModel(orderDto);

        // Then
        checkProperties(order, orderDto);
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<Order> optionalOrder = orderConverter.fromDtoToOptionalModel(null);

        // Then
        assertNotNull(optionalOrder);
        assertFalse(optionalOrder.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        OrderDto orderDto = OrderDto.builder().id(2).code("Order 2").created(new Date()).build();

        // When
        Optional<Order> optionalOrder = orderConverter.fromDtoToOptionalModel(orderDto);

        // Then
        assertNotNull(optionalOrder);
        assertTrue(optionalOrder.isPresent());
        checkProperties(optionalOrder.get(), orderDto);
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderConverter.fromDtosToModels(null);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<Order> orders = orderConverter.fromDtosToModels(new ArrayList<>());

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        OrderDto orderDto1 = OrderDto.builder().id(1).code("Order 1").created(new Date()).build();
        OrderDto orderDto2 = OrderDto.builder().id(2).code("Order 2").created(new Date()).build();

        Order order1 = Order.builder().id(orderDto1.getId()).code(orderDto1.getCode())
                                      .created(new Timestamp(orderDto1.getCreated().getTime())).build();
        Order order2 = Order.builder().id(orderDto2.getId()).code(orderDto2.getCode())
                                      .created(new Timestamp(orderDto2.getCreated().getTime())).build();
        // When
        List<Order> orders = orderConverter.fromDtosToModels(Arrays.asList(orderDto1, orderDto2));

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertThat(orders, containsInAnyOrder(order1, order2));
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        // When
        OrderDto orderDto = orderConverter.fromModelToDto(null);

        // Then
        assertNull(orderDto);
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned() {
        // Given
        Order order = Order.builder().id(1).code("Order 1").created(new Timestamp(new Date().getTime())).build();

        // When
        OrderDto orderDto = orderConverter.fromModelToDto(order);

        // Then
        checkProperties(order, orderDto);
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<OrderDto> optionalOrderDto = orderConverter.fromModelToOptionalDto(null);

        // Then
        assertNotNull(optionalOrderDto);
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        Order order = Order.builder().id(1).code("Order 1").created(new Timestamp(new Date().getTime())).build();

        // When
        Optional<OrderDto> optionalOrderDto = orderConverter.fromModelToOptionalDto(order);

        // Then
        assertNotNull(optionalOrderDto);
        assertTrue(optionalOrderDto.isPresent());
        checkProperties(order, optionalOrderDto.get());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<OrderDto> orderDtos = orderConverter.fromModelsToDtos(null);

        // Then
        assertNotNull(orderDtos);
        assertTrue(orderDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<OrderDto> orderDtos = orderConverter.fromModelsToDtos(new ArrayList<>());

        // Then
        assertNotNull(orderDtos);
        assertTrue(orderDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        Order order1 = Order.builder().id(1).code("Order 1").created(new Timestamp(new Date().getTime())).build();
        Order order2 = Order.builder().id(2).code("Order 2").created(new Timestamp(new Date().getTime())).build();

        OrderDto orderDto1 = OrderDto.builder().id(order1.getId()).code(order1.getCode()).created(order1.getCreated()).build();
        OrderDto orderDto2 = OrderDto.builder().id(order2.getId()).code(order2.getCode()).created(order2.getCreated()).build();

        // When
        List<OrderDto> orderDtos = orderConverter.fromModelsToDtos(Arrays.asList(order1, order2));

        // Then
        assertNotNull(orderDtos);
        assertEquals(2, orderDtos.size());
        assertThat(orderDtos, containsInAnyOrder(orderDto1, orderDto2));
    }


    private void checkProperties(Order order, OrderDto orderDto) {
        assertNotNull(order);
        assertNotNull(orderDto);
        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getCode(), orderDto.getCode());
        assertEquals(order.getCreated().getTime(), orderDto.getCreated().getTime());
    }

}
