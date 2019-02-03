package com.pizza.util.converter;

import com.pizza.dto.IngredientDto;
import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PizzaConverterTest {

    @Autowired
    private PizzaConverter pizzaConverter;

    // Ingredients used in the tests
    Set<Ingredient> ingredients = new HashSet<>();
    Set<IngredientDto> ingredientDtos = new HashSet<>();


    @Before
    public void init() {
        Ingredient mozzarella = Ingredient.builder().id(1).name("Mozzarella").build();
        Ingredient oregano = Ingredient.builder().id(2).name("Oregano").build();
        ingredients.addAll(Arrays.asList(mozzarella, oregano));

        IngredientDto mozzarellaDto = IngredientDto.builder().id(mozzarella.getId()).name(mozzarella.getName()).build();
        IngredientDto oreganoDto = IngredientDto.builder().id(oregano.getId()).name(oregano.getName()).build();
        ingredientDtos.addAll(Arrays.asList(mozzarellaDto, oreganoDto));
    }


    @Test
    public void fromDtoToEntity_whenGivenDtoIsNull_thenNullIsReturned() {
        // When
        Pizza pizza = pizzaConverter.fromDtoToEntity(null);

        // Then
        assertNull(pizza);
    }


    @Test
    public void fromDtoToEntity_whenGivenDtoIsNotNull_thenEquivalentEntityIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id(1).name("Carbonara").cost(7.50D).ingredients(ingredientDtos).build();

        // When
        Pizza pizza = pizzaConverter.fromDtoToEntity(pizzaDto);

        // Then
        checkProperties(pizza, pizzaDto);
        assertThat(ingredients, containsInAnyOrder(pizza.getIngredients().toArray()));
    }


    @Test
    public void fromDtoToOptionalEntity_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaConverter.fromDtoToOptionalEntity(null);

        // Then
        assertNotNull(optionalPizza);
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void fromDtoToOptionalEntity_whenGivenDtoIsNotNull_thenOptionalOfEquivalentEntityIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id(1).name("Carbonara").cost(7.50D).ingredients(ingredientDtos).build();

        // When
        Optional<Pizza> optionalPizza = pizzaConverter.fromDtoToOptionalEntity(pizzaDto);

        // Then
        assertNotNull(optionalPizza);
        assertTrue(optionalPizza.isPresent());
        checkProperties(optionalPizza.get(), pizzaDto);
        assertThat(ingredients, containsInAnyOrder(optionalPizza.get().getIngredients().toArray()));
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaConverter.fromDtosToEntities(null);

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaConverter.fromDtosToEntities(new ArrayList<>());

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void fromDtosToEntities_whenGivenCollectionIsNotEmpty_thenEquivalentListOfEntitiesIsReturned() {
        // Given
        PizzaDto pizzaDto1 = PizzaDto.builder().id(1).name("Carbonara").cost(7.50D).ingredients(new HashSet<>()).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id(2).name("Hawaiian").cost(8D).ingredients(ingredientDtos).build();

        Pizza pizza1 = Pizza.builder().id(pizzaDto1.getId()).name(pizzaDto1.getName()).ingredients(new HashSet<>()).build();
        Pizza pizza2 = Pizza.builder().id(pizzaDto2.getId()).name(pizzaDto2.getName()).ingredients(ingredients).build();

        // When
        List<Pizza> pizzas = pizzaConverter.fromDtosToEntities(Arrays.asList(pizzaDto1, pizzaDto2));

        // Then
        assertNotNull(pizzas);
        assertEquals(2, pizzas.size());
        assertThat(pizzas, containsInAnyOrder(pizza1, pizza2));
    }


    @Test
    public void fromEntityToDto_whenGivenEntityIsNull_thenNullIsReturned() {
        // When
        PizzaDto pizzaDto = pizzaConverter.fromEntityToDto(null);

        // Then
        assertNull(pizzaDto);
    }


    @Test
    public void fromEntityToDto_whenGivenEntityIsNotNull_thenEquivalentDtoIsReturned() {
        // Given
        Pizza pizza = Pizza.builder().id(1).name("Hawaiian").cost(8D).ingredients(ingredients).build();

        // When
        PizzaDto pizzaDto = pizzaConverter.fromEntityToDto(pizza);

        // Then
        checkProperties(pizza, pizzaDto);
        assertThat(ingredientDtos, containsInAnyOrder(pizzaDto.getIngredients().toArray()));
    }


    @Test
    public void fromEntityToOptionalDto_whenGivenEntityIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<PizzaDto> optionalPizzaDto = pizzaConverter.fromEntityToOptionalDto(null);

        // Then
        assertNotNull(optionalPizzaDto);
        assertFalse(optionalPizzaDto.isPresent());
    }


    @Test
    public void fromEntityToOptionalDto_whenGivenEntityIsNotNull_thenOptionalOfEquivalentEntityIsReturned() {
        // Given
        Pizza pizza = Pizza.builder().id(1).name("Hawaiian").cost(8D).ingredients(ingredients).build();

        // When
        Optional<PizzaDto> optionalPizzaDto = pizzaConverter.fromEntityToOptionalDto(pizza);

        // Then
        assertNotNull(optionalPizzaDto);
        assertTrue(optionalPizzaDto.isPresent());
        checkProperties(pizza, optionalPizzaDto.get());
        assertThat(ingredientDtos, containsInAnyOrder(optionalPizzaDto.get().getIngredients().toArray()));
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsNull_thenEmptyListIsReturned() {
        // When
        List<PizzaDto> pizzaDtos = pizzaConverter.fromEntitiesToDtos(null);

        // Then
        assertNotNull(pizzaDtos);
        assertTrue(pizzaDtos.isEmpty());
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsEmpty_thenEmptyListIsReturned() {
        // When
        List<PizzaDto> pizzaDtos = pizzaConverter.fromEntitiesToDtos(new ArrayList<>());

        // Then
        assertNotNull(pizzaDtos);
        assertTrue(pizzaDtos.isEmpty());
    }


    @Test
    public void fromEntitiesToDtos_whenGivenCollectionIsNotEmpty_thenEquivalentListOfEntitiesIsReturned() {
        // Given
        Pizza pizza1 = Pizza.builder().id(1).name("Carbonara").cost(7.50D).ingredients(ingredients).build();
        Pizza pizza2 = Pizza.builder().id(2).name("Hawaiian").cost(8D).ingredients(new HashSet<>()).build();

        PizzaDto pizzaDto1 = PizzaDto.builder().id(pizza1.getId()).name(pizza1.getName()).cost(pizza1.getCost())
                                                                  .ingredients(ingredientDtos).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id(pizza2.getId()).name(pizza2.getName()).cost(pizza2.getCost())
                                                                  .ingredients(new HashSet<>()).build();
        // When
        List<PizzaDto> pizzaDtos = pizzaConverter.fromEntitiesToDtos(Arrays.asList(pizza1, pizza2));

        // Then
        assertNotNull(pizzaDtos);
        assertEquals(2, pizzaDtos.size());
        assertThat(pizzaDtos, containsInAnyOrder(pizzaDto1, pizzaDto2));
    }


    private void checkProperties(Pizza pizza, PizzaDto pizzaDto) {
        assertNotNull(pizza);
        assertNotNull(pizzaDto);
        assertEquals(pizza.getId(), pizzaDto.getId());
        assertEquals(pizza.getName(), pizzaDto.getName());
        assertEquals(pizza.getCost(), pizzaDto.getCost());
    }

}
