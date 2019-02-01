package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNull_thenOptionalOfEquivalentEntityIsReturned() {
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
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyCollectionIsReturned() {
        // When
        Collection<Order> orders = orderConverter.fromDtosToModels(null);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyCollectionIsReturned() {
        // When
        Collection<Order> orders = orderConverter.fromDtosToModels(new ArrayList<>());

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmpty_thenEquivalentCollectionOfModelsIsReturned() {
        // Given
        OrderDto orderDto1 = OrderDto.builder().id(1).code("Order 1").created(new Date()).build();
        OrderDto orderDto2 = OrderDto.builder().id(2).code("Order 2").created(new Date()).build();

        Order order1 = Order.builder().id(orderDto1.getId()).code(orderDto1.getCode())
                                      .created(new Timestamp(orderDto1.getCreated().getTime())).build();
        Order order2 = Order.builder().id(orderDto2.getId()).code(orderDto2.getCode())
                                      .created(new Timestamp(orderDto2.getCreated().getTime())).build();
        // When
        Collection<Order> orders = orderConverter.fromDtosToModels(Arrays.asList(orderDto1, orderDto2));

        // Then
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertThat(orders, containsInAnyOrder(order1, order2));
    }


    private void checkProperties(Order order, OrderDto orderDto) {
        assertNotNull(order);
        assertNotNull(orderDto);
        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getCode(), orderDto.getCode());
        assertEquals(order.getCreated().getTime(), orderDto.getCreated().getTime());
    }

}
