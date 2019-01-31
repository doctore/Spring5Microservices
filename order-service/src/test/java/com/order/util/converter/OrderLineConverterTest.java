package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.OrderLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderLineConverterTest {

    @Autowired
    private OrderLineConverter orderLineConverter;


    @Test
    public void fromDtoToModel_whenGivenDtoAndOrderIdAreNull_thenNullIsReturned() {
        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(null, null);

        // Then
        assertNull(orderLine);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        // Given
        Integer orderId = 1;

        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(null, orderId);

        // Then
        assertNotNull(orderLine);
        assertEquals(orderId, orderLine.getOrderId());
        assertNull(orderLine.getId());
        assertNull(orderLine.getPizzaId());
        assertNull(orderLine.getAmount());
        assertNull(orderLine.getCost());
    }


    @Test
    public void fromDtoToModel_whenGivenOrderIdIsNull_thenModelWithoutOrderIdIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(1).pizza(pizzaDto).cost(12D).amount((short)1).build();

        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(orderLineDto, null);

        // Then
        assertNotNull(orderLine);
        assertNull(orderLine.getOrderId());
        checkProperties(orderLine, orderLineDto);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoAndOrderIdAreNotNull_thenModelWithAllPropertiesNotNullIsReturned() {
        // Given
        Integer orderId = 1;
        PizzaDto pizzaDto = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(1).pizza(pizzaDto).cost(12D).amount((short)1).build();

        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(orderLineDto, orderId);

        // Then
        assertNotNull(orderLine);
        assertEquals(orderId, orderLine.getOrderId());
        checkProperties(orderLine, orderLineDto);
    }






    private void checkProperties(OrderLine orderLine, OrderLineDto orderLineDto) {
        assertNotNull(orderLine);
        assertNotNull(orderLineDto);
        assertEquals(orderLine.getId(), orderLineDto.getId());
        assertEquals(orderLine.getCost(), orderLineDto.getCost());
        assertEquals(orderLine.getAmount(), orderLineDto.getAmount());
        assertEquals(orderLine.getPizzaId(), orderLineDto.getPizza().getId());
    }

}
