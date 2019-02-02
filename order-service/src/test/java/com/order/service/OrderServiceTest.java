package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Mock
    private OrderDao mockOrderDao;

    private OrderService orderService;


    @Before
    public void init() {
        orderService = new OrderService(mockOrderDao);
    }


    @Test
    public void findByIdWithOrderLines_whenNullIdIsGiven_thenEmptyOptionalIsReturned() {
        // When
        when(mockOrderDao.fetchToOrderDtoByIdWithOrderLineDto(anyInt())).thenReturn(Optional.empty());
        Optional<OrderDto> optionalOrderDto = orderService.findByIdWithOrderLines(null);

        // Then
        assertNotNull(optionalOrderDto);
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void findByIdWithOrderLines_whenNonExistingIdInDatabaseIsGiven_thenEmptyOptionalIsReturned() {
        // When
        when(mockOrderDao.fetchToOrderDtoByIdWithOrderLineDto(anyInt())).thenReturn(Optional.empty());
        Optional<OrderDto> optionalOrderDto = orderService.findByIdWithOrderLines(-1);

        // Then
        assertNotNull(optionalOrderDto);
        assertFalse(optionalOrderDto.isPresent());
    }


    @Test
    public void findByIdWithOrderLines_whenExistingIdInDatabaseIsGiven_thenOptionalOfRelatedModelIsReturned() {
        // Given
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).pizza(hawaiian).cost(8D).amount((short)1).build();

        OrderDto orderDto = OrderDto.builder().id(1).code("Order 1").created(new Timestamp(new Date().getTime()))
                                              .orderLines(Arrays.asList(orderLineDto1, orderLineDto2)).build();
        // When
        when(mockOrderDao.fetchToOrderDtoByIdWithOrderLineDto(anyInt())).thenReturn(Optional.of(orderDto));
        Optional<OrderDto> optionalOrderDto = orderService.findByIdWithOrderLines(orderDto.getId());

        // Then
        assertTrue(optionalOrderDto.isPresent());
        assertEquals(orderDto, optionalOrderDto.get());
        assertThat(optionalOrderDto.get().getOrderLines(), containsInAnyOrder(orderDto.getOrderLines().toArray()));
        assertThat(Arrays.asList(optionalOrderDto.get().getOrderLines().get(0).getPizza(),
                                 optionalOrderDto.get().getOrderLines().get(1).getPizza()),
                   containsInAnyOrder(carbonara, hawaiian));
    }

}
