package com.order.service;

import com.order.dao.OrderLineDao;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.OrderLine;
import com.order.util.converter.OrderLineConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderLineServiceTest {

    @Mock
    private OrderLineDao mockOrderLineDao;

    @Mock
    private OrderLineConverter mockOrderLineConverter;

    private OrderLineService orderLineService;


    @Before
    public void init() {
        orderLineService = new OrderLineService(mockOrderLineDao, mockOrderLineConverter);
    }


    @Test
    public void saveAll_whenCollectionOfOrderLinesIsNullAndOrderIdIsNull_thenEmptyListIsReturned() {
        // When
        List<OrderLineDto> orderLineDtos = orderLineService.saveAll(null, null);

        // Then
        assertNotNull(orderLineDtos);
        assertTrue(orderLineDtos.isEmpty());

        verify(mockOrderLineConverter, times(0)).fromDtosToModels(any(), anyInt());
        verify(mockOrderLineConverter, times(0)).fromModelsToDtos(any());
        verify(mockOrderLineDao, times(0)).saveAll(any());
    }


    @Test
    public void saveAll_whenCollectionOfOrderLinesIsNullAndOrderIdIsNotNull_thenEmptyListIsReturned() {
        // When
        List<OrderLineDto> orderLineDtos = orderLineService.saveAll(null, 1);

        // Then
        assertNotNull(orderLineDtos);
        assertTrue(orderLineDtos.isEmpty());

        verify(mockOrderLineConverter, times(0)).fromDtosToModels(any(), anyInt());
        verify(mockOrderLineConverter, times(0)).fromModelsToDtos(any());
        verify(mockOrderLineDao, times(0)).saveAll(any());
    }


    @Test(expected = IllegalArgumentException.class)
    public void saveAll_whenCollectionOfOrderLinesIsNotNullAndOrderIdIsNull_thenIllegalArgumentExceptionIsThrown() {
        // Given
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().pizza(hawaiian).cost(8D).amount((short)1).build();

        // When
        orderLineService.saveAll(Arrays.asList(orderLineDto1, orderLineDto2), null);
    }


    @Test
    public void saveAll_whenCollectionOfOrderLinesIsNotNullAndOrderIdIsNotNull_thenPersistedListIsReturned() {
        // Given
        Integer orderId = 1;
        PizzaDto carbonara = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50).build();
        PizzaDto hawaiian = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().pizza(carbonara).cost(15D).amount((short)2).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().pizza(hawaiian).cost(8D).amount((short)1).build();

        OrderLine orderLine1 = OrderLine.builder().id(1).orderId(orderId).pizzaId(carbonara.getId())
                                                  .amount(orderLineDto1.getAmount()).cost(orderLineDto1.getCost()).build();
        OrderLine orderLine2 = OrderLine.builder().id(2).orderId(orderId).pizzaId(hawaiian.getId())
                                                  .amount(orderLineDto2.getAmount()).cost(orderLineDto2.getCost()).build();
        // When
        when(mockOrderLineConverter.fromDtosToModels(Arrays.asList(orderLineDto1, orderLineDto2), orderId))
                .thenReturn(Arrays.asList(orderLine1, orderLine2));
        when(mockOrderLineDao.saveAll(Arrays.asList(orderLine1, orderLine2))).thenReturn(Arrays.asList(orderLine1, orderLine2));
        when(mockOrderLineConverter.fromModelsToDtos(Arrays.asList(orderLine1, orderLine2)))
                .thenReturn(Arrays.asList(orderLineDto1, orderLineDto2));

        List<OrderLineDto> orderLineDtos = orderLineService.saveAll(Arrays.asList(orderLineDto1, orderLineDto2), orderId);

        // Then
        assertNotNull(orderLineDtos);
        assertEquals(2, orderLineDtos.size());
        assertThat(orderLineDtos, contains(orderLineDto1, orderLineDto2));

        verify(mockOrderLineConverter, times(1)).fromDtosToModels(Arrays.asList(orderLineDto1, orderLineDto2), orderId);
        verify(mockOrderLineConverter, times(1)).fromModelsToDtos(Arrays.asList(orderLine1, orderLine2));
        verify(mockOrderLineDao, times(1)).saveAll(Arrays.asList(orderLine1, orderLine2));
    }

}
