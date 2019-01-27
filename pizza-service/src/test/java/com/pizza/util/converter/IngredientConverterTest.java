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
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IngredientConverterTest {

    @Autowired
    private IngredientConverter ingredientConverter;


    @Test
    public void fromDtoToEntity_whenGivenDtoIsNull_thenEmptyEntityIsReturned() {
        // When
        Ingredient ingredient = ingredientConverter.fromDtoToEntity(null);

        // Then
        assertNotNull(ingredient);
        assertNull(ingredient.getId());
        assertNull(ingredient.getName());
    }


    @Test
    public void fromDtoToEntity_whenGivenDtoIsNotNull_thenMirrorEntityIsReturned() {
        // Given
        IngredientDto ingredientDto = IngredientDto.builder().id(1).name("Garlic").build();

        // When
        Ingredient ingredient = ingredientConverter.fromDtoToEntity(ingredientDto);

        // Then
        assertNotNull(ingredient);
        assertEquals(ingredientDto.getId(), ingredient.getId());
        assertEquals(ingredientDto.getName(), ingredient.getName());
    }


    @Test
    public void fromDtoToOptionalEntity_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<Ingredient> optionalIngredient = ingredientConverter.fromDtoToOptionalEntity(null);

        // Then
        assertNotNull(optionalIngredient);
        assertFalse(optionalIngredient.isPresent());
    }


    @Test
    public void fromDtoToOptionalEntity_whenGivenDtoIsNotNull_thenMirrorEntityIsReturned() {
        // Given
        IngredientDto ingredientDto = IngredientDto.builder().id(1).name("Garlic").build();

        // When
        Optional<Ingredient> optionalIngredient = ingredientConverter.fromDtoToOptionalEntity(ingredientDto);

        // Then
        assertNotNull(optionalIngredient);
        assertTrue(optionalIngredient.isPresent());
        assertEquals(ingredientDto.getId(), optionalIngredient.get().getId());
        assertEquals(ingredientDto.getName(), optionalIngredient.get().getName());
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsNull_thenEmptyCollectionIsReturned() {
        // When
        Collection<Ingredient> ingredients = ingredientConverter.fromDtosToEntities(null);

        // Then
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsEmpty_thenEmptyCollectionIsReturned() {
        // When
        Collection<Ingredient> ingredients = ingredientConverter.fromDtosToEntities(new ArrayList<>());

        // Then
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsNotEmpty_thenMirrorCollectionOfEntitiesIsReturned() {
        // Given
        IngredientDto ingredientDto1 = IngredientDto.builder().id(1).name("Garlic").build();
        IngredientDto ingredientDto2 = IngredientDto.builder().id(2).name("Cheese").build();

        Ingredient ingredient1 = Ingredient.builder().id(ingredientDto1.getId()).name(ingredientDto1.getName()).build();
        Ingredient ingredient2 = Ingredient.builder().id(ingredientDto2.getId()).name(ingredientDto2.getName()).build();

        // When
        Collection<Ingredient> ingredients = ingredientConverter.fromDtosToEntities(Arrays.asList(ingredientDto1, ingredientDto2));

        // Then
        assertNotNull(ingredients);
        assertEquals(2, ingredients.size());
        assertThat(ingredients, containsInAnyOrder(ingredient1, ingredient2));
    }


    @Test
    public void fromEntityToDto_whenGivenEntityIsNull_thenEmptyDtoIsReturned() {
        // When
        IngredientDto ingredientDto = ingredientConverter.fromEntityToDto(null);

        // Then
        assertNotNull(ingredientDto);
        assertNull(ingredientDto.getId());
        assertNull(ingredientDto.getName());
    }


    @Test
    public void fromEntityToDto_whenGivenEntityIsNotNull_thenMirrorDtoIsReturned() {
        // Given
        Ingredient ingredient = Ingredient.builder().id(1).name("Garlic").build();

        // When
        IngredientDto ingredientDto = ingredientConverter.fromEntityToDto(ingredient);

        // Then
        assertNotNull(ingredientDto);
        assertEquals(ingredient.getId(), ingredientDto.getId());
        assertEquals(ingredient.getName(), ingredientDto.getName());
    }


    @Test
    public void fromEntityToOptionalDto_whenGivenEntityIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<IngredientDto> optionalIngredientDto = ingredientConverter.fromEntityToOptionalDto(null);

        // Then
        assertNotNull(optionalIngredientDto);
        assertFalse(optionalIngredientDto.isPresent());
    }


    @Test
    public void fromEntityToOptionalDto_whenGivenEntityIsNotNull_thenMirrorEntityIsReturned() {
        // Given
        Ingredient ingredient = Ingredient.builder().id(1).name("Garlic").build();

        // When
        Optional<IngredientDto> optionalIngredientDto = ingredientConverter.fromEntityToOptionalDto(ingredient);

        // Then
        assertNotNull(optionalIngredientDto);
        assertTrue(optionalIngredientDto.isPresent());
        assertEquals(ingredient.getId(), optionalIngredientDto.get().getId());
        assertEquals(ingredient.getName(), optionalIngredientDto.get().getName());
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsNull_thenEmptyCollectionIsReturned() {
        // When
        Collection<IngredientDto> ingredientDtos = ingredientConverter.fromEntitiesToDtos(null);

        // Then
        assertNotNull(ingredientDtos);
        assertTrue(ingredientDtos.isEmpty());
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsEmpty_thenEmptyCollectionIsReturned() {
        // When
        Collection<IngredientDto> ingredientDtos = ingredientConverter.fromEntitiesToDtos(new ArrayList<>());

        // Then
        assertNotNull(ingredientDtos);
        assertTrue(ingredientDtos.isEmpty());
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsNotEmpty_thenMirrorCollectionOfEntitiesIsReturned() {
        // Given
        Ingredient ingredient1 = Ingredient.builder().id(1).name("Garlic").build();
        Ingredient ingredient2 = Ingredient.builder().id(2).name("Cheese").build();

        IngredientDto ingredientDto1 = IngredientDto.builder().id(ingredient1.getId()).name(ingredient1.getName()).build();
        IngredientDto ingredientDto2 = IngredientDto.builder().id(ingredient2.getId()).name(ingredient2.getName()).build();

        // When
        Collection<IngredientDto> ingredientDtos = ingredientConverter.fromEntitiesToDtos(Arrays.asList(ingredient1, ingredient2));

        // Then
        assertNotNull(ingredientDtos);
        assertEquals(2, ingredientDtos.size());
        assertThat(ingredientDtos, containsInAnyOrder(ingredientDto1, ingredientDto2));
    }

}
