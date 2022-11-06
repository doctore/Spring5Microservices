package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.OrderLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrderLine;
import static com.order.TestDataFactory.buildOrderLineDto;
import static com.order.TestDataFactory.buildPizzaDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = { OrderLineConverterImpl.class, OrderLineConverterImpl_.class })
public class OrderLineConverterTest {

    @Autowired
    private OrderLineConverter converter;

    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(converter.fromDtoToModel(null));
    }


    static Stream<Arguments> fromDtoToModelTestCases() {
        PizzaDto pizzaDto = buildPizzaDto((short)1, "Carbonara", 12.10D);
        OrderLineDto dto = buildOrderLineDto(1, 2, pizzaDto, (short)5, 12.10D);
        return Stream.of(
                //@formatter:off
                //            dtoToConvert
                Arguments.of( new OrderLineDto() ),
                Arguments.of( dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToModel: when the given dto is not null then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned(OrderLineDto dtoToConvert) {
        OrderLine equivalentModel = converter.fromDtoToModel(dtoToConvert);
        checkProperties(equivalentModel, dtoToConvert);
    }


    @Test
    @DisplayName("fromDtoToModel: when given dto and id are null then null model is returned")
    public void fromDtoToModel_whenGivenDtoAndIdAreNull_thenNullIsReturned() {
        assertNull(converter.fromDtoToModel(null, null));
    }


    static Stream<Arguments> fromDtoToModelWithIdTestCases() {
        PizzaDto pizzaDto = buildPizzaDto((short)1, "Carbonara", 12.10D);
        OrderLineDto dto = buildOrderLineDto(1, 2, pizzaDto, (short)5, 12.10D);
        return Stream.of(
                //@formatter:off
                //            dtoToConvert,        id
                Arguments.of( new OrderLineDto(),  null ),
                Arguments.of( dto,                 1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelWithIdTestCases")
    @DisplayName("fromDtoToModel: when the given dto and/or id are not null then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoAndOrIdAreNotNull_thenEquivalentModelIsReturned(OrderLineDto dtoToConvert,
                                                                                           Integer orderId) {
        OrderLine equivalentModel = converter.fromDtoToModel(dtoToConvert, orderId);
        checkProperties(equivalentModel, dtoToConvert, orderId);
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderLine> equivalentModel = converter.fromDtoToOptionalModel(null);

        assertNotNull(equivalentModel);
        assertFalse(equivalentModel.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToOptionalModel: when given dto is not null then Optional with equivalent model is returned")
    public void fromModelToOptionalDto_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned(OrderLineDto dtoToConvert) {
        Optional<OrderLine> equivalentModel = converter.fromDtoToOptionalModel(dtoToConvert);

        assertTrue(equivalentModel.isPresent());
        checkProperties(equivalentModel.get(), dtoToConvert);
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is null then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromDtosToModels(null).isEmpty());
    }


    static Stream<Arguments> fromDtosToModelsTestCases() {
        PizzaDto pizzaDto = buildPizzaDto((short)1, "Carbonara", 12.10D);
        OrderLineDto dto = buildOrderLineDto(1, 2, pizzaDto, (short)5, 12.10D);
        return Stream.of(
                //@formatter:off
                //            listOfDtosToConvert
                Arguments.of( new ArrayList<>() ),
                Arguments.of( List.of(dto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtosToModelsTestCases")
    @DisplayName("fromDtosToModels: when the given collection is not null then the returned one of equivalent models is returned")
    public void fromDtosToModels_whenGivenCollectionIsNotNull_thenEquivalentCollectionModelsIsReturned(List<OrderLineDto> listOfDtosToConvert) {
        List<OrderLine> equivalentModels = converter.fromDtosToModels(listOfDtosToConvert);

        assertNotNull(equivalentModels);
        assertEquals(listOfDtosToConvert.size(), equivalentModels.size());
        for (int i = 0; i < equivalentModels.size(); i++) {
            checkProperties(equivalentModels.get(i), listOfDtosToConvert.get(i));
        }
    }


    @Test
    @DisplayName("fromModelToDto: when given model is null then null dto is returned")
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        assertNull(converter.fromModelToDto(null));
    }


    static Stream<Arguments> fromModelToDtoTestCases() {
        OrderLine model = buildOrderLine(1, 2, (short)3, (short)5, 12.10D);
        return Stream.of(
                //@formatter:off
                //            modelToConvert
                Arguments.of( new OrderLine() ),
                Arguments.of( model )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToDto: when the given model is not null then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned(OrderLine modelToConvert) {
        OrderLineDto equivalentDto = converter.fromModelToDto(modelToConvert);
        checkProperties(modelToConvert, equivalentDto);
    }

    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderLineDto> equivalentDto = converter.fromModelToOptionalDto(null);

        assertNotNull(equivalentDto);
        assertFalse(equivalentDto.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToOptionalDto: when given model is not null then Optional with equivalent Dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentDtoIsReturned(OrderLine modelToConvert) {
        Optional<OrderLineDto> equivalentDto = converter.fromModelToOptionalDto(modelToConvert);

        assertTrue(equivalentDto.isPresent());
        checkProperties(modelToConvert, equivalentDto.get());
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromModelsToDtos(null).isEmpty());
    }


    static Stream<Arguments> fromModelsToDtosTestCases() {
        OrderLine model = buildOrderLine(1, 2, (short)3, (short)5, 12.10D);
        return Stream.of(
                //@formatter:off
                //            listOfModelsToConvert
                Arguments.of( new ArrayList<>() ),
                Arguments.of( List.of(model) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelsToDtosTestCases")
    @DisplayName("fromModelsToDtos: when the given collection is not null then the returned one of equivalent dtos is returned")
    public void fromEntitiesToDtos_whenGivenCollectionIsNotNull_thenEquivalentCollectionDtosIsReturned(List<OrderLine> listOfModelsToConvert) {
        List<OrderLineDto> equivalentDtos = converter.fromModelsToDtos(listOfModelsToConvert);

        assertNotNull(equivalentDtos);
        assertEquals(listOfModelsToConvert.size(), equivalentDtos.size());
        for (int i = 0; i < equivalentDtos.size(); i++) {
            checkProperties(listOfModelsToConvert.get(i), equivalentDtos.get(i));
        }
    }


    private void checkProperties(OrderLine orderLine, OrderLineDto orderLineDto, Integer orderId) {
        assertNotNull(orderLine);
        assertNotNull(orderLineDto);
        assertEquals(orderLine.getId(), orderLineDto.getId());
        assertEquals(orderLine.getCost(), orderLineDto.getCost());
        assertEquals(orderLine.getAmount(), orderLineDto.getAmount());
        if (null != orderLine.getPizzaId()) {
            assertEquals(orderLine.getPizzaId(), orderLineDto.getPizza().getId());
        }
        if (null != orderId) {
            assertEquals(orderLine.getOrderId(), orderId);
        }
        else {
            assertEquals(orderLine.getOrderId(), orderLineDto.getOrderId());
        }
    }

    private void checkProperties(OrderLine orderLine, OrderLineDto orderLineDto) {
        checkProperties(orderLine, orderLineDto, null);
    }

}
