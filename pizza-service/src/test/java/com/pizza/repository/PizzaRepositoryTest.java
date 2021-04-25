package com.pizza.repository;

import com.pizza.configuration.persistence.PersistenceConfiguration;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Pizza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceConfiguration.class)
public class PizzaRepositoryTest {

    @Autowired
    private PizzaRepository repository;

    // Elements used to test the functionality
    private Pizza carbonara;
    private Pizza hawaiian;
    private Pizza margherita;


    @BeforeEach
    public void init() {
        carbonara = repository.findWithIngredientsByName(PizzaEnum.CARBONARA).get();
        hawaiian = repository.findWithIngredientsByName(PizzaEnum.HAWAIIAN).get();
        margherita = repository.findWithIngredientsByName(PizzaEnum.MARGUERITA).get();
    }


    @Test
    public void findPageWithIngredients_whenNullPageableIsGiven_thenAllPizzasAreReceived() {
        // When
        Page<Pizza> pizzaPage = repository.findPageWithIngredients(null);

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
        Page<Pizza> pizzaPage1 = repository.findPageWithIngredients(PageRequest.of(0, size, sort));
        Page<Pizza> pizzaPage2 = repository.findPageWithIngredients(PageRequest.of(1, size, sort));

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
    public void findPageWithIngredientsWithoutInMemoryPagination_whenNullPageableIsGiven_thenAllPizzasAreReceived() {
        // When
        Page<Pizza> pizzaPage = repository.findPageWithIngredientsWithoutInMemoryPagination(null);

        // Then
        assertNotNull(pizzaPage);
        assertEquals(3, pizzaPage.getTotalElements());
        assertEquals(3, pizzaPage.getNumberOfElements());
        assertThat(pizzaPage.getContent(), containsInAnyOrder(carbonara, hawaiian, margherita));
    }


    @Test
    public void findPageWithIngredientsWithoutInMemoryPagination_whenNotNullPageableIsGiven_thenDifferentPagesWillBeManaged() {
        // Given
        int size = 2;
        Sort sort = Sort.by(Sort.Direction.ASC, "cost");

        // When
        Page<Pizza> pizzaPage1 = repository.findPageWithIngredientsWithoutInMemoryPagination(PageRequest.of(0, size, sort));
        Page<Pizza> pizzaPage2 = repository.findPageWithIngredientsWithoutInMemoryPagination(PageRequest.of(1, size, sort));

        // Then
        assertNotNull(pizzaPage1);
        assertEquals(3, pizzaPage1.getTotalElements());
        assertEquals(2, pizzaPage1.getNumberOfElements());
        assertThat(pizzaPage1.getContent(), contains(margherita, carbonara));

        assertNotNull(pizzaPage2);
        assertEquals(3, pizzaPage2.getTotalElements());
        assertEquals(1, pizzaPage2.getNumberOfElements());
        assertThat(pizzaPage2.getContent(), contains(hawaiian));
    }


    @Test
    public void findWithIngredientsByName_whenNoNameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Pizza> optionalPizza = repository.findWithIngredientsByName(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findWithIngredientsByName_whenAnExistentNameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // When
        Optional<Pizza> optionalPizza = repository.findWithIngredientsByName(carbonara.getName());

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
        Optional<Pizza> optionalPizza = repository.findByName(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findByName_whenAnExistentNameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // When
        Optional<Pizza> optionalPizza = repository.findByName(hawaiian.getName());

        // Then
        assertTrue(optionalPizza.isPresent());
        assertEquals(hawaiian, optionalPizza.get());
    }

}
