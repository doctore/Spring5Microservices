package com.pizza.util.converter;

import com.pizza.dto.IngredientDto;
import com.pizza.model.Ingredient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IngredientConverterTest {

    @Autowired
    private IngredientConverter ingredientConverter;


    @Test
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        // When
        Ingredient ingredient = ingredientConverter.fromDtoToModel(null);

        // Then
        assertNull(ingredient);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned() {
        // Given
        IngredientDto ingredientDto = IngredientDto.builder().id(1).name("Garlic").build();

        // When
        Ingredient ingredient = ingredientConverter.fromDtoToModel(ingredientDto);

        // Then
        checkProperties(ingredient, ingredientDto);
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<Ingredient> optionalIngredient = ingredientConverter.fromDtoToOptionalModel(null);

        // Then
        assertNotNull(optionalIngredient);
        assertFalse(optionalIngredient.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        IngredientDto ingredientDto = IngredientDto.builder().id(1).name("Garlic").build();

        // When
        Optional<Ingredient> optionalIngredient = ingredientConverter.fromDtoToOptionalModel(ingredientDto);

        // Then
        assertNotNull(optionalIngredient);
        assertTrue(optionalIngredient.isPresent());
        checkProperties(optionalIngredient.get(), ingredientDto);
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<Ingredient> ingredients = ingredientConverter.fromDtosToModels(null);

        // Then
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<Ingredient> ingredients = ingredientConverter.fromDtosToModels(new ArrayList<>());

        // Then
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        IngredientDto ingredientDto1 = IngredientDto.builder().id(1).name("Garlic").build();
        IngredientDto ingredientDto2 = IngredientDto.builder().id(2).name("Cheese").build();

        Ingredient ingredient1 = Ingredient.builder().id(ingredientDto1.getId()).name(ingredientDto1.getName()).build();
        Ingredient ingredient2 = Ingredient.builder().id(ingredientDto2.getId()).name(ingredientDto2.getName()).build();

        // When
        List<Ingredient> ingredients = ingredientConverter.fromDtosToModels(Arrays.asList(ingredientDto1, ingredientDto2));

        // Then
        assertNotNull(ingredients);
        assertEquals(2, ingredients.size());
        assertThat(ingredients, containsInAnyOrder(ingredient1, ingredient2));
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        // When
        IngredientDto ingredientDto = ingredientConverter.fromModelToDto(null);

        // Then
        assertNull(ingredientDto);
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned() {
        // Given
        Ingredient ingredient = Ingredient.builder().id(1).name("Garlic").build();

        // When
        IngredientDto ingredientDto = ingredientConverter.fromModelToDto(ingredient);

        // Then
        checkProperties(ingredient, ingredientDto);
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<IngredientDto> optionalIngredientDto = ingredientConverter.fromModelToOptionalDto(null);

        // Then
        assertNotNull(optionalIngredientDto);
        assertFalse(optionalIngredientDto.isPresent());
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentModelIsReturned() {
        // Given
        Ingredient ingredient = Ingredient.builder().id(1).name("Garlic").build();

        // When
        Optional<IngredientDto> optionalIngredientDto = ingredientConverter.fromModelToOptionalDto(ingredient);

        // Then
        assertNotNull(optionalIngredientDto);
        assertTrue(optionalIngredientDto.isPresent());
        checkProperties(ingredient, optionalIngredientDto.get());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<IngredientDto> ingredientDtos = ingredientConverter.fromModelsToDtos(null);

        // Then
        assertNotNull(ingredientDtos);
        assertTrue(ingredientDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<IngredientDto> ingredientDtos = ingredientConverter.fromModelsToDtos(new ArrayList<>());

        // Then
        assertNotNull(ingredientDtos);
        assertTrue(ingredientDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNotEmpty_thenEquivalentListOfModelsIsReturned() {
        // Given
        Ingredient ingredient1 = Ingredient.builder().id(1).name("Garlic").build();
        Ingredient ingredient2 = Ingredient.builder().id(2).name("Cheese").build();

        IngredientDto ingredientDto1 = IngredientDto.builder().id(ingredient1.getId()).name(ingredient1.getName()).build();
        IngredientDto ingredientDto2 = IngredientDto.builder().id(ingredient2.getId()).name(ingredient2.getName()).build();

        // When
        List<IngredientDto> ingredientDtos = ingredientConverter.fromModelsToDtos(Arrays.asList(ingredient1, ingredient2));

        // Then
        assertNotNull(ingredientDtos);
        assertEquals(2, ingredientDtos.size());
        assertThat(ingredientDtos, containsInAnyOrder(ingredientDto1, ingredientDto2));
    }


    private void checkProperties(Ingredient ingredient, IngredientDto ingredientDto) {
        assertNotNull(ingredient);
        assertNotNull(ingredientDto);
        assertEquals(ingredient.getId(), ingredientDto.getId());
        assertEquals(ingredient.getName(), ingredientDto.getName());
    }

}
