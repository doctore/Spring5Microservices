package com.order.dao;

import com.order.model.Pizza;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@JooqTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class PizzaDaoTest {

    @Autowired
    private DSLContext dslContext;

    private PizzaDao pizzaDao;

    // Elements used to test the functionality
    private Pizza carbonara;
    private Pizza hawaiian;


    @Before
    public void init() {
        pizzaDao = new PizzaDao(this.dslContext);

        carbonara = pizzaDao.findById((short)1);
        hawaiian = pizzaDao.findById((short)2);
    }


    @Test
    public void getId_whenNullModelIsGiven_thenNullIdIsReturned() {
        // When
        Short id = pizzaDao.getId(null);

        // Then
        assertNull(id);
    }


    @Test
    public void getId_whenNotNullModelIsGiven_thenItsIdIsReturned() {
        // When
        Short id = pizzaDao.getId(carbonara);

        // Then
        assertNotNull(id);
        assertEquals(carbonara.getId(), id);
    }


    @Test
    public void findByIds_whenNullIdsAreGiven_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByIds(null);

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void findByIds_whenANonExistentIdsIsGiven_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByIds((short)-2, (short)-1);

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void findByIds_whenAnExistentIdIsGiven_thenRelatedModelIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByIds(carbonara.getId());

        // Then
        assertNotNull(pizzas);
        assertEquals(1, pizzas.size());
        assertThat(pizzas.get(0), samePropertyValuesAs(carbonara));
    }


    @Test
    public void findByIds_whenExistentIdsAreGiven_thenRelatedModelsAreReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByIds(carbonara.getId(), hawaiian.getId());

        // Then
        assertNotNull(pizzas);
        assertEquals(2, pizzas.size());
        assertThat(pizzas, contains(carbonara, hawaiian));
    }


    @Test
    public void findOptionalById_whenNoIdIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaDao.findOptionalById(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findOptionalById_whenANonExistentIdIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        Short nonExistentId = (short)-2;

        // When
        Optional<Pizza> optionalPizza = pizzaDao.findOptionalById(nonExistentId);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findOptionalById_whenAnExistentIdIsGiven_thenOptionalWithRelatedModelIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaDao.findOptionalById(carbonara.getId());

        // Then
        assertTrue(optionalPizza.isPresent());
        assertThat(optionalPizza.get(), samePropertyValuesAs(carbonara));
    }


    @Test
    public void findByNames_whenNullNamesAreGiven_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByNames(null);

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void findByNames_whenANonExistentNameIsGiven_thenEmptyListIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByNames(carbonara.getName() + "V2");

        // Then
        assertNotNull(pizzas);
        assertTrue(pizzas.isEmpty());
    }


    @Test
    public void findByNames_whenAnExistentNameIsGiven_thenRelatedModelIsReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByNames(carbonara.getName());

        // Then
        assertNotNull(pizzas);
        assertEquals(1, pizzas.size());
        assertThat(pizzas.get(0), samePropertyValuesAs(carbonara));
    }


    @Test
    public void findByNames_whenExistentNamesAreGiven_thenRelatedModelsAreReturned() {
        // When
        List<Pizza> pizzas = pizzaDao.findByNames(carbonara.getName(), hawaiian.getName());

        // Then
        assertNotNull(pizzas);
        assertEquals(2, pizzas.size());
        assertThat(pizzas, contains(carbonara, hawaiian));
    }


    @Test
    public void findByName_whenNoNameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaDao.findByName(null);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findByName_whenANonExistentNameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String nonExistentName = carbonara.getName() + hawaiian.getName();

        // When
        Optional<Pizza> optionalPizza = pizzaDao.findByName(nonExistentName);

        // Then
        assertFalse(optionalPizza.isPresent());
    }


    @Test
    public void findByName_whenAnExistentNameIsGiven_thenOptionalWithRelatedModelIsReturned() {
        // When
        Optional<Pizza> optionalPizza = pizzaDao.findByName(carbonara.getName());

        // Then
        assertTrue(optionalPizza.isPresent());
        assertThat(optionalPizza.get(), samePropertyValuesAs(carbonara));
    }

}
