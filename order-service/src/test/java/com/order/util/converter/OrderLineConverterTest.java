package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.OrderLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

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
    public void fromDtoToModel_whenGivenDtoIsNullAndOrderIdIsNotNull_thenNullIsReturned() {
        // Given
        Integer orderId = 1;

        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(null, orderId);

        // Then
        assertNull(orderLine);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNotNullAndOrderIdIsNull_thenModelWithoutOrderIdIsReturned() {
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


    @Test
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(null);

        // Then
        assertNull(orderLine);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(1).orderId(4).pizza(pizzaDto).cost(12D).amount((short)1).build();

        // When
        OrderLine orderLine = orderLineConverter.fromDtoToModel(orderLineDto);

        // Then
        checkProperties(orderLine, orderLineDto);
        assertEquals(orderLine.getOrderId(), orderLineDto.getOrderId());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoAndOrderIdAreNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(null, null);

        // Then
        assertNotNull(optionalOrderLine);
        assertFalse(optionalOrderLine.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNullAndOrderIdAreNotNull_thenEmptyOptionalIsReturned() {
        // Given
        Integer orderId = 2;

        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(null, orderId);

        // When
        assertNotNull(optionalOrderLine);
        assertFalse(optionalOrderLine.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNullAndOrderIdIsNull_thenOptionalOfModelWithoutOrderIdIsReturned()  {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(2).pizza(pizzaDto).cost(17D).amount((short)2).build();

        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(orderLineDto, null);

        // When
        assertTrue(optionalOrderLine.isPresent());
        assertNull(optionalOrderLine.get().getOrderId());
        checkProperties(optionalOrderLine.get(), orderLineDto);
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoAndOrderIdAreNotNull_thenOptionalOfModelWithAllPropertiesNotNullIsReturned() {
        // Given
        Integer orderId = 1;
        PizzaDto pizzaDto = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(2).pizza(pizzaDto).cost(17D).amount((short)2).build();

        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(orderLineDto, orderId);

        // Then
        assertTrue(optionalOrderLine.isPresent());
        assertEquals(orderId, optionalOrderLine.get().getOrderId());
        checkProperties(optionalOrderLine.get(), orderLineDto);
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(null);

        // Then
        assertNotNull(optionalOrderLine);
        assertFalse(optionalOrderLine.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();
        OrderLineDto orderLineDto = OrderLineDto.builder().id(2).pizza(pizzaDto).cost(17D).amount((short)2).build();

        // When
        Optional<OrderLine> optionalOrderLine = orderLineConverter.fromDtoToOptionalModel(orderLineDto);

        // Then
        assertNotNull(optionalOrderLine);
        assertTrue(optionalOrderLine.isPresent());
        checkProperties(optionalOrderLine.get(), orderLineDto);
        assertEquals(orderLineDto.getOrderId(), optionalOrderLine.get().getOrderId());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionAndOrderIdAreNull_thenEmptyListIsReturned() {
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(null ,null);

        // Then
        assertNotNull(orderLines);
        assertTrue(orderLines.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNullAndOrderIdIsNotNull_thenEmptyListIsReturned() {
        // Given
        Integer orderId = 3;

        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(null, orderId);

        // Then
        assertNotNull(orderLines);
        assertTrue(orderLines.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmptyAndOrderIdIsNull_thenEquivalentListOfModelsWithoutOrderIdIsReturned() {
        // Given
        PizzaDto pizzaDto1 = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).pizza(pizzaDto1).cost(12D).amount((short)1).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).pizza(pizzaDto2).cost(17D).amount((short)2).build();

        OrderLine orderLine1 = OrderLine.builder().id(orderLineDto1.getId()).amount(orderLineDto1.getAmount())
                                                  .cost(orderLineDto1.getCost()).pizzaId(pizzaDto1.getId()).build();

        OrderLine orderLine2 = OrderLine.builder().id(orderLineDto2.getId()).amount(orderLineDto2.getAmount())
                                                  .cost(orderLineDto2.getCost()).pizzaId(pizzaDto2.getId()).build();
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(Arrays.asList(orderLineDto1, orderLineDto2), null);

        // Then
        assertNotNull(orderLines);
        assertEquals(2, orderLines.size());
        assertThat(orderLines, containsInAnyOrder(orderLine1, orderLine2));
        orderLines.forEach(ol -> assertNull(ol.getOrderId()));
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmptyAndOrderIdIsNotNull_thenEquivalentListOfModelsWithOrderIdIsReturned() {
        // Given
        Integer orderId = 1;
        PizzaDto pizzaDto1 = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).pizza(pizzaDto1).cost(12D).amount((short)1).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).pizza(pizzaDto2).cost(17D).amount((short)2).build();

        OrderLine orderLine1 = OrderLine.builder().id(orderLineDto1.getId()).orderId(orderId).amount(orderLineDto1.getAmount())
                                        .cost(orderLineDto1.getCost()).pizzaId(pizzaDto1.getId()).build();

        OrderLine orderLine2 = OrderLine.builder().id(orderLineDto2.getId()).orderId(orderId).amount(orderLineDto2.getAmount())
                                        .cost(orderLineDto2.getCost()).pizzaId(pizzaDto2.getId()).build();
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(Arrays.asList(orderLineDto1, orderLineDto2), orderId);

        // Then
        assertNotNull(orderLines);
        assertEquals(2, orderLines.size());
        assertThat(orderLines, containsInAnyOrder(orderLine1, orderLine2));
        orderLines.forEach(ol -> assertEquals(orderId, ol.getOrderId()));
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(null);

        // Then
        assertNotNull(orderLines);
        assertTrue(orderLines.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(new ArrayList<>());

        // Then
        assertNotNull(orderLines);
        assertTrue(orderLines.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        PizzaDto pizzaDto1 = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(1).orderId(1).pizza(pizzaDto1).cost(12D).amount((short)1).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(2).orderId(1).pizza(pizzaDto2).cost(17D).amount((short)2).build();

        OrderLine orderLine1 = OrderLine.builder().id(orderLineDto1.getId()).orderId(orderLineDto1.getOrderId()).amount(orderLineDto1.getAmount())
                                                  .cost(orderLineDto1.getCost()).pizzaId(pizzaDto1.getId()).build();

        OrderLine orderLine2 = OrderLine.builder().id(orderLineDto2.getId()).orderId(orderLineDto2.getOrderId()).amount(orderLineDto2.getAmount())
                                                  .cost(orderLineDto2.getCost()).pizzaId(pizzaDto2.getId()).build();
        // When
        List<OrderLine> orderLines = orderLineConverter.fromDtosToModels(Arrays.asList(orderLineDto1, orderLineDto2));

        // Then
        assertNotNull(orderLines);
        assertEquals(2, orderLines.size());
        assertThat(orderLines, containsInAnyOrder(orderLine1, orderLine2));
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        // When
        OrderLineDto orderLineDto = orderLineConverter.fromModelToDto(null);

        // Then
        assertNull(orderLineDto);
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned() {
        // Given
        OrderLine orderLine = OrderLine.builder().id(1).orderId(2).amount((short)1).cost(11D).pizzaId((short)3).build();

        // When
        OrderLineDto orderLineDto = orderLineConverter.fromModelToDto(orderLine);

        // Then
        checkProperties(orderLine, orderLineDto);
        assertEquals(orderLine.getOrderId(), orderLineDto.getOrderId());
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<OrderLineDto> optionalOrderLineDto = orderLineConverter.fromModelToOptionalDto(null);

        // Then
        assertNotNull(optionalOrderLineDto);
        assertFalse(optionalOrderLineDto.isPresent());
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        OrderLine orderLine = OrderLine.builder().id(1).orderId(2).amount((short)1).cost(11D).pizzaId((short)3).build();

        // When
        Optional<OrderLineDto> optionalOrderLineDto = orderLineConverter.fromModelToOptionalDto(orderLine);

        // Then
        assertNotNull(optionalOrderLineDto);
        assertTrue(optionalOrderLineDto.isPresent());
        checkProperties(orderLine, optionalOrderLineDto.get());
        assertEquals(orderLine.getOrderId(), optionalOrderLineDto.get().getOrderId());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<OrderLineDto> orderLineDtos = orderLineConverter.fromModelsToDtos(null);

        // Then
        assertNotNull(orderLineDtos);
        assertTrue(orderLineDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<OrderLineDto> orderLineDtos = orderLineConverter.fromModelsToDtos(new ArrayList<>());

        // Then
        assertNotNull(orderLineDtos);
        assertTrue(orderLineDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        OrderLine orderLine1 = OrderLine.builder().id(1).orderId(1).amount((short)2).cost(24D).pizzaId((short)1).build();
        OrderLine orderLine2 = OrderLine.builder().id(2).orderId(1).amount((short)1).cost(8.50D).pizzaId((short)2).build();

        PizzaDto pizzaDto1 = PizzaDto.builder().id((short)1).name("Carbonara").cost(12D).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8.5D).build();

        OrderLineDto orderLineDto1 = OrderLineDto.builder().id(orderLine1.getId()).orderId(orderLine1.getOrderId())
                                                           .pizza(pizzaDto1).cost(orderLine1.getCost()).amount(orderLine1.getAmount()).build();
        OrderLineDto orderLineDto2 = OrderLineDto.builder().id(orderLine2.getId()).orderId(orderLine2.getOrderId())
                                                           .pizza(pizzaDto2).cost(orderLine2.getCost()).amount(orderLine2.getAmount()).build();
        // When
        List<OrderLineDto> orderLineDtos = orderLineConverter.fromModelsToDtos(Arrays.asList(orderLine1, orderLine2));

        // Then
        assertNotNull(orderLineDtos);
        assertEquals(2, orderLineDtos.size());
        assertThat(orderLineDtos, containsInAnyOrder(orderLineDto1, orderLineDto2));
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
