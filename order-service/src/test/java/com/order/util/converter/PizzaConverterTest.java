package com.order.util.converter;

import com.order.dto.PizzaDto;
import com.order.model.Pizza;
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

import static com.order.TestDataFactory.buildPizza;
import static com.order.TestDataFactory.buildPizzaDto;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PizzaConverterImpl.class)
public class PizzaConverterTest {

    @Autowired
    private PizzaConverter converter;

    @Test
    @DisplayName("fromDtoToModel: when given dto is null then null model is returned")
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        assertNull(converter.fromDtoToModel(null));
    }


    static Stream<Arguments> fromDtoToModelTestCases() {
        PizzaDto dto = buildPizzaDto((short)1, "Carbonara", 12.10D);
        return Stream.of(
                //@formatter:off
                //            dtoToConvert
                Arguments.of( new PizzaDto() ),
                Arguments.of( dto )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToModel: when the given dto is not null then the equivalent model is returned")
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned(PizzaDto dtoToConvert) {
        Pizza equivalentModel = converter.fromDtoToModel(dtoToConvert);
        checkProperties(equivalentModel, dtoToConvert);
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when given dto is null then empty Optional is returned")
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        Optional<Pizza> equivalentModel = converter.fromDtoToOptionalModel(null);

        assertNotNull(equivalentModel);
        assertFalse(equivalentModel.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromDtoToModelTestCases")
    @DisplayName("fromDtoToOptionalModel: when given dto is not null then Optional with equivalent model is returned")
    public void fromModelToOptionalDto_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned(PizzaDto dtoToConvert) {
        Optional<Pizza> equivalentModel = converter.fromDtoToOptionalModel(dtoToConvert);

        assertTrue(equivalentModel.isPresent());
        checkProperties(equivalentModel.get(), dtoToConvert);
    }


    @Test
    @DisplayName("fromDtosToModels: when given collection is null then empty list is returned")
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromDtosToModels(null).isEmpty());
    }


    static Stream<Arguments> fromDtosToModelsTestCases() {
        PizzaDto dto = buildPizzaDto((short)1, "Carbonara", 12.10D);
        return Stream.of(
                //@formatter:off
                //            listOfDtosToConvert
                Arguments.of( new ArrayList<>() ),
                Arguments.of( asList(dto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDtosToModelsTestCases")
    @DisplayName("fromDtosToModels: when the given collection is not null then the returned one of equivalent models is returned")
    public void fromDtosToModels_whenGivenCollectionIsNotNull_thenEquivalentCollectionModelsIsReturned(List<PizzaDto> listOfDtosToConvert) {
        List<Pizza> equivalentModels = converter.fromDtosToModels(listOfDtosToConvert);

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
        Pizza model = buildPizza((short)1, "Carbonara", 12.10D);
        return Stream.of(
                //@formatter:off
                //            modelToConvert
                Arguments.of( new Pizza() ),
                Arguments.of( model )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToDto: when the given model is not null then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned(Pizza modelToConvert) {
        PizzaDto equivalentDto = converter.fromModelToDto(modelToConvert);
        checkProperties(modelToConvert, equivalentDto);
    }

    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<PizzaDto> equivalentDto = converter.fromModelToOptionalDto(null);

        assertNotNull(equivalentDto);
        assertFalse(equivalentDto.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToOptionalDto: when given model is not null then Optional with equivalent Dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentDtoIsReturned(Pizza modelToConvert) {
        Optional<PizzaDto> equivalentDto = converter.fromModelToOptionalDto(modelToConvert);

        assertTrue(equivalentDto.isPresent());
        checkProperties(modelToConvert, equivalentDto.get());
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromModelsToDtos(null).isEmpty());
    }


    static Stream<Arguments> fromModelsToDtosTestCases() {
        Pizza model = buildPizza((short)1, "Carbonara", 12.10D);
        return Stream.of(
                //@formatter:off
                //            listOfModelsToConvert
                Arguments.of( new ArrayList<>() ),
                Arguments.of( asList(model) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelsToDtosTestCases")
    @DisplayName("fromModelsToDtos: when the given collection is not null then the returned one of equivalent dtos is returned")
    public void fromEntitiesToDtos_whenGivenCollectionIsNotNull_thenEquivalentCollectionDtosIsReturned(List<Pizza> listOfModelsToConvert) {
        List<PizzaDto> equivalentDtos = converter.fromModelsToDtos(listOfModelsToConvert);

        assertNotNull(equivalentDtos);
        assertEquals(listOfModelsToConvert.size(), equivalentDtos.size());
        for (int i = 0; i < equivalentDtos.size(); i++) {
            checkProperties(listOfModelsToConvert.get(i), equivalentDtos.get(i));
        }
    }

    private void checkProperties(Pizza model, PizzaDto dto) {
        assertNotNull(model);
        assertNotNull(dto);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getCost(), dto.getCost());
    }

}
