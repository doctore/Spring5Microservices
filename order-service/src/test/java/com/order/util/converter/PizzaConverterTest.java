package com.order.util.converter;

import com.order.dto.PizzaDto;
import com.order.model.Pizza;
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
public class PizzaConverterTest {

    @Autowired
    private PizzaConverter pizzaConverter;


    @Test
    public void fromDtoToModel_whenGivenDtoIsNull_thenNullIsReturned() {
        // When
        Pizza pizza = pizzaConverter.fromDtoToModel(null);

        // Then
        assertNull(pizza);
    }


    @Test
    public void fromDtoToModel_whenGivenDtoIsNotNull_thenEquivalentModelIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50D).build();

        // When
        Pizza pizza = pizzaConverter.fromDtoToModel(pizzaDto);

        // Then
        checkProperties(pizza, pizzaDto);
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaConverter.fromDtoToOptionalModel(null);

        // Then
        assertNotNull(optionalPizza);
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void fromDtoToOptionalModel_whenGivenDtoIsNotNull_thenOptionalOfEquivalentEntityIsReturned() {
        // Given
        PizzaDto pizzaDto = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50D).build();

        // When
        Optional<Pizza> optionalPizza = pizzaConverter.fromDtoToOptionalModel(pizzaDto);

        // Then
        assertNotNull(optionalPizza);
        assertTrue(optionalPizza.isPresent());
        checkProperties(optionalPizza.get(), pizzaDto);
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNull_thenEmptyCollectionIsReturned() {
        // When
        Collection<Pizza> pizzas = pizzaConverter.fromDtosToModels(null);

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsEmpty_thenEmptyCollectionIsReturned() {
        // When
        Collection<Pizza> pizzas = pizzaConverter.fromDtosToModels(new ArrayList<>());

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void fromDtosToModels_whenGivenCollectionIsNotEmpty_thenEquivalentCollectionOfModelsIsReturned() {
        // Given
        PizzaDto pizzaDto1 = PizzaDto.builder().id((short)1).name("Carbonara").cost(7.50D).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id((short)2).name("Hawaiian").cost(8D).build();

        Pizza pizza1 = Pizza.builder().id(pizzaDto1.getId()).name(pizzaDto1.getName()).build();
        Pizza pizza2 = Pizza.builder().id(pizzaDto2.getId()).name(pizzaDto2.getName()).build();

        // When
        Collection<Pizza> pizzas = pizzaConverter.fromDtosToModels(Arrays.asList(pizzaDto1, pizzaDto2));

        // Then
        assertNotNull(pizzas);
        assertEquals(2, pizzas.size());
        assertThat(pizzas, containsInAnyOrder(pizza1, pizza2));
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNull_thenNullIsReturned() {
        // When
        PizzaDto pizzaDto = pizzaConverter.fromModelToDto(null);

        // Then
        assertNull(pizzaDto);
    }


    @Test
    public void fromModelToDto_whenGivenModelIsNotNull_thenEquivalentDtoIsReturned() {
        // Given
        Pizza pizza = Pizza.builder().id((short)1).name("Hawaiian").cost(8D).build();

        // When
        PizzaDto pizzaDto = pizzaConverter.fromModelToDto(pizza);

        // Then
        checkProperties(pizza, pizzaDto);
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNull_thenEmptyOptionalIsReturned() {
        // When
        Optional<PizzaDto> optionalPizzaDto = pizzaConverter.fromModelToOptionalDto(null);

        // Then
        assertNotNull(optionalPizzaDto);
        assertFalse(optionalPizzaDto.isPresent());
    }


    @Test
    public void fromModelToOptionalDto_whenGivenModelIsNotNull_thenOptionalOfEquivalentEntityIsReturned() {
        // Given
        Pizza pizza = Pizza.builder().id((short)1).name("Hawaiian").cost(8D).build();

        // When
        Optional<PizzaDto> optionalPizzaDto = pizzaConverter.fromModelToOptionalDto(pizza);

        // Then
        assertNotNull(optionalPizzaDto);
        assertTrue(optionalPizzaDto.isPresent());
        checkProperties(pizza, optionalPizzaDto.get());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNull_thenEmptyCollectionIsReturned() {
        // When
        Collection<PizzaDto> pizzaDtos = pizzaConverter.fromModelsToDtos(null);

        // Then
        assertNotNull(pizzaDtos);
        assertTrue(pizzaDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsEmpty_thenEmptyCollectionIsReturned() {
        // When
        Collection<PizzaDto> pizzaDtos = pizzaConverter.fromModelsToDtos(new ArrayList<>());

        // Then
        assertNotNull(pizzaDtos);
        assertTrue(pizzaDtos.isEmpty());
    }


    @Test
    public void fromModelsToDtos_whenGivenCollectionIsNotEmpty_thenEquivalentCollectionOfModelsIsReturned() {
        // Given
        Pizza pizza1 = Pizza.builder().id((short)1).name("Carbonara").cost(7.50D).build();
        Pizza pizza2 = Pizza.builder().id((short)2).name("Hawaiian").cost(8D).build();

        PizzaDto pizzaDto1 = PizzaDto.builder().id(pizza1.getId()).name(pizza1.getName()).cost(pizza1.getCost()).build();
        PizzaDto pizzaDto2 = PizzaDto.builder().id(pizza2.getId()).name(pizza2.getName()).cost(pizza2.getCost()).build();

        // When
        Collection<PizzaDto> pizzaDtos = pizzaConverter.fromModelsToDtos(Arrays.asList(pizza1, pizza2));

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
