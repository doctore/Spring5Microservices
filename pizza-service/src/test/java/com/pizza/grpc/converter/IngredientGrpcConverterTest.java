package com.pizza.grpc.converter;

import com.spring5microservices.grpc.IngredientResponse;
import com.pizza.model.Ingredient;
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

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildIngredientResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = IngredientGrpcConverterImpl.class)
public class IngredientGrpcConverterTest {

    @Autowired
    private IngredientGrpcConverter converter;


    @Test
    @DisplayName("fromDtoToModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        IngredientResponse dto = buildIngredientResponse(1, "Jam");
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtoToOptionalModel: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtoToOptionalModel_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        IngredientResponse dto = buildIngredientResponse(1, "Jam");
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtoToOptionalModel(dto)
        );
    }


    @Test
    @DisplayName("fromDtosToModels: when method is invoked then UnsupportedOperationException is thrown")
    public void fromDtosToModels_whenMethodIsInvoked_thenUnsupportedOperationExceptionIsThrown() {
        IngredientResponse dto = buildIngredientResponse(1, "Jam");
        assertThrows(
                UnsupportedOperationException.class,
                () -> converter.fromDtosToModels(List.of(dto))
        );
    }


    static Stream<Arguments> fromModelToDtoTestCases() {
        Ingredient model = buildIngredient(1, "Jam");
        return Stream.of(
                //@formatter:off
                //            modelToConvert
                Arguments.of( model )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToDto: when the given model is not null then the equivalent dto is returned")
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned(Ingredient modelToConvert) {
        IngredientResponse equivalentDto = converter.fromModelToDto(modelToConvert);
        checkProperties(modelToConvert, equivalentDto);
    }


    @Test
    @DisplayName("fromModelToOptionalDto: when given model is null then empty Optional is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        Optional<IngredientResponse> equivalentDto = converter.fromModelToOptionalDto(null);

        assertNotNull(equivalentDto);
        assertFalse(equivalentDto.isPresent());
    }


    @ParameterizedTest
    @MethodSource("fromModelToDtoTestCases")
    @DisplayName("fromModelToOptionalDto: when given model is not null then Optional with equivalent Dto is returned")
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentDtoIsReturned(Ingredient modelToConvert) {
        Optional<IngredientResponse> equivalentDto = converter.fromModelToOptionalDto(modelToConvert);

        assertTrue(equivalentDto.isPresent());
        checkProperties(modelToConvert, equivalentDto.get());
    }


    @Test
    @DisplayName("fromModelsToDtos: when given collection is null then empty list is returned")
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        assertTrue(converter.fromModelsToDtos(null).isEmpty());
    }


    static Stream<Arguments> fromModelsToDtosTestCases() {
        Ingredient model = buildIngredient(1, "Jam");
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
    public void fromEntitiesToDtos_whenGivenCollectionIsNotNull_thenEquivalentCollectionDtosIsReturned(List<Ingredient> listOfModelsToConvert) {
        List<IngredientResponse> equivalentDtos = converter.fromModelsToDtos(listOfModelsToConvert);

        assertNotNull(equivalentDtos);
        assertEquals(listOfModelsToConvert.size(), equivalentDtos.size());
        for (int i = 0; i < equivalentDtos.size(); i++) {
            checkProperties(listOfModelsToConvert.get(i), equivalentDtos.get(i));
        }
    }


    private void checkProperties(Ingredient model,
                                 IngredientResponse dto) {
        assertNotNull(model);
        assertNotNull(dto);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
    }

}
