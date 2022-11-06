package com.order.util.converter;

import com.order.dto.OrderDto;

import com.order.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.order.TestDataFactory.buildOrder;
import static com.order.TestDataFactory.buildOrderDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = OrderConverterImpl.class)
public class OrderConverterTest {

    @Autowired
    private OrderConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(converter.fromDtoToModel(null));
    }


    static Stream<Arguments> fromDtoToModelTestCases() {
        OrderDto dto = buildOrderDto(1, "Order1", new Date(), List.of());
        return Stream.of(
                //@formatter:off
                //            dtoToConvert
                Arguments.of( new OrderDto() ),
                Arguments.of( dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToModel: when the given dto is not null then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned(OrderDto dtoToConvert) {
        Order equivalentModel = converter.fromDtoToModel(dtoToConvert);
        checkProperties(equivalentModel, dtoToConvert);
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<Order> equivalentModel = converter.fromDtoToOptionalModel(null);

        assertNotNull(equivalentModel);
        assertFalse(equivalentModel.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToOptionalModel: when given dto is not null then Optional with equivalent model is returned")
    public void fromModelToOptionalDto_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned(OrderDto dtoToConvert) {
        Optional<Order> equivalentModel = converter.fromDtoToOptionalModel(dtoToConvert);

        assertTrue(equivalentModel.isPresent());
        checkProperties(equivalentModel.get(), dtoToConvert);
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is null then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromDtosToModels(null).isEmpty());
    }


    static Stream<Arguments> fromDtosToModelsTestCases() {
        OrderDto dto = buildOrderDto(1, "Order1", new Date(), List.of());
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
    public void fromDtosToModels_whenGivenCollectionIsNotNull_thenEquivalentCollectionModelsIsReturned(List<OrderDto> listOfDtosToConvert) {
        List<Order> equivalentModels = converter.fromDtosToModels(listOfDtosToConvert);

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
        Order model = buildOrder(1, "Oder1", new Timestamp(new Date().getTime()));
        return Stream.of(
                //@formatter:off
                //            modelToConvert
                Arguments.of( new Order() ),
                Arguments.of( model )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToDto: when the given model is not null then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned(Order modelToConvert) {
        OrderDto equivalentDto = converter.fromModelToDto(modelToConvert);
        checkProperties(modelToConvert, equivalentDto);
    }

    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<OrderDto> equivalentDto = converter.fromModelToOptionalDto(null);

        assertNotNull(equivalentDto);
        assertFalse(equivalentDto.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToOptionalDto: when given model is not null then Optional with equivalent Dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentDtoIsReturned(Order modelToConvert) {
        Optional<OrderDto> equivalentDto = converter.fromModelToOptionalDto(modelToConvert);

        assertTrue(equivalentDto.isPresent());
        checkProperties(modelToConvert, equivalentDto.get());
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromModelsToDtos(null).isEmpty());
    }


    static Stream<Arguments> fromModelsToDtosTestCases() {
        Order model = buildOrder(1, "Oder1", new Timestamp(new Date().getTime()));
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
    public void fromEntitiesToDtos_whenGivenCollectionIsNotNull_thenEquivalentCollectionDtosIsReturned(List<Order> listOfModelsToConvert) {
        List<OrderDto> equivalentDtos = converter.fromModelsToDtos(listOfModelsToConvert);

        assertNotNull(equivalentDtos);
        assertEquals(listOfModelsToConvert.size(), equivalentDtos.size());
        for (int i = 0; i < equivalentDtos.size(); i++) {
            checkProperties(listOfModelsToConvert.get(i), equivalentDtos.get(i));
        }
    }


    private void checkProperties(Order model, OrderDto dto) {
        assertNotNull(model);
        assertNotNull(dto);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getCode(), dto.getCode());
        if (null != model.getCreated()) {
            assertEquals(model.getCreated().getTime(), dto.getCreated().getTime());
        }
    }

}
