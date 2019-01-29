package com.pizza.repository;

import com.pizza.model.Pizza;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class PizzaRepositoryTest {

    @Autowired
    private PizzaRepository pizzaRepository;

    // Elements used to test the functionality
    private Pizza carbonara;
    private Pizza hawaiian;
    private Pizza margherita;


    @Before
    public void init() {
        carbonara = pizzaRepository.findWithIngredientsByName("Carbonara").get();
        hawaiian = pizzaRepository.findWithIngredientsByName("Hawaiian").get();
        margherita = pizzaRepository.findWithIngredientsByName("Margherita").get();
    }


    @Test
    public void findPageWithIngredients_whenNullPageableIsGiven_thenAllPizzasAreReceived() {
        // When
        Page<Pizza> pizzaPage = pizzaRepository.findPageWithIngredients(null);

        // Then
        assertNotNull(pizzaPage);
        assertEquals(3, pizzaPage.getTotalElements());
        assertEquals(3, pizzaPage.getNumberOfElements());
        assertThat(pizzaPage.getContent(), containsInAnyOrder(carbonara, hawaiian, margherita));
    }


    @Test
    public void findPageWithIngredients_whenNotNullPageableIsGiven_thenDifferentPagesWillBeManaged() {
        // Given
        int size = 2;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        // When
        Page<Pizza> pizzaPage1 = pizzaRepository.findPageWithIngredients(PageRequest.of(0, size, sort));
        Page<Pizza> pizzaPage2 = pizzaRepository.findPageWithIngredients(PageRequest.of(1, size, sort));

        // Then
        assertNotNull(pizzaPage1);
        assertEquals(3, pizzaPage1.getTotalElements());
        assertEquals(2, pizzaPage1.getNumberOfElements());
        assertThat(pizzaPage1.getContent(), contains(carbonara, hawaiian));

        assertNotNull(pizzaPage2);
        assertEquals(3, pizzaPage2.getTotalElements());
        assertEquals(1, pizzaPage2.getNumberOfElements());
        assertThat(pizzaPage2.getContent(), contains(margherita));
    }


    @Test
    public void findWithIngredientsByName_whenNoNameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findWithIngredientsByName(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findWithIngredientsByName_whenANonExistentNameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String nonExistentName = carbonara.getName() + hawaiian.getName() + margherita.getName();

        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findWithIngredientsByName(nonExistentName);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findWithIngredientsByName_whenAnExistentNameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findWithIngredientsByName(carbonara.getName());

        // Then
        assertTrue(optionalPizza.isPresent());
        assertEquals(carbonara, optionalPizza.get());
        assertFalse(optionalPizza.get().getIngredients().isEmpty());
        assertEquals(carbonara.getIngredients().size(), optionalPizza.get().getIngredients().size());
        assertThat(optionalPizza.get().getIngredients(), containsInAnyOrder(carbonara.getIngredients().toArray()));
    }


    @Test
    public void findByName_whenNoNameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findByName(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findByName_whenANonExistentNameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String nonExistentName = carbonara.getName() + hawaiian.getName() + margherita.getName();

        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findByName(nonExistentName);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findByName_whenAnExistentNameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaRepository.findByName(hawaiian.getName());

        // Then
        assertTrue(optionalPizza.isPresent());
        assertEquals(hawaiian, optionalPizza.get());
    }

}
