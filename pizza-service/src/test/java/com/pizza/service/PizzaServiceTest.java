package com.pizza.service;

import com.pizza.dto.IngredientDto;
import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.IngredientRepository;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
import com.pizza.util.converter.PizzaConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PizzaServiceTest {

    @Mock
    private IngredientRepository mockIngredientRepository;

    @Mock
    private PageUtil mockPageUtil;

    @Mock
    private PizzaConverter mockPizzaConverter;

    @Mock
    private PizzaRepository mockPizzaRepository;

    private PizzaService pizzaService;


    @Before
    public void init() {
        pizzaService = new PizzaService(mockIngredientRepository, mockPageUtil, mockPizzaConverter, mockPizzaRepository);
    }


    @Test
    public void findByName_whenNullNameIsGiven_thenEmptyOptionalIsReturned() {
        // When
        Optional<PizzaDto> pizzaDto = pizzaService.findByName(null);

        // Then
        assertFalse(pizzaDto.isPresent());
        verify(mockPizzaConverter, times(0)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(0)).findByName(anyString());
        verify(mockPizzaRepository, times(0)).findWithIngredientsByName(anyString());
    }


    @Test
    public void findByName_whenEmptyOptionalPizzaIsReturnedByRepository_thenEmptyOptionalIsReturned() {
        // When
        when(mockPizzaRepository.findWithIngredientsByName(anyString())).thenReturn(Optional.empty());
        Optional<PizzaDto> pizzaDto = pizzaService.findByName("Carbonara");

        // Then
        assertFalse(pizzaDto.isPresent());
        verify(mockPizzaConverter, times(0)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(1)).findWithIngredientsByName(anyString());
    }


    @Test
    public void findByName_whenAPizzaIsReturnedByRepository_thenOptionalOfEquivalentPizzaDtoIsReturned() {
        // Given
        Pizza carbonara = Pizza.builder().id(1).name("Carbonara").cost(7.50D).build();
        PizzaDto carbonaraDto = PizzaDto.builder().id(1).name("Carbonara").cost(7.50D).build();

        // When
        when(mockPizzaRepository.findWithIngredientsByName(anyString())).thenReturn(Optional.of(carbonara));
        when(mockPizzaConverter.fromEntityToOptionalDto(carbonara)).thenReturn(Optional.of(carbonaraDto));
        Optional<PizzaDto> pizzaDto = pizzaService.findByName("Carbonara");

        // Then
        assertTrue(pizzaDto.isPresent());
        assertThat(pizzaDto.get(), samePropertyValuesAs(carbonaraDto));

        verify(mockPizzaConverter, times(1)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(1)).findWithIngredientsByName(anyString());
    }


    @Test
    public void findPageWithIngredients_whenNullPageIsGivenByRepository_thenEmptyPageIsReturned() {
        // When
        when(mockPizzaRepository.findPageWithIngredients(any())).thenReturn(null);
        Page<PizzaDto> pizzaDtoPage = pizzaService.findPageWithIngredients(0, 2, Sort.unsorted());

        // Then
        assertNotNull(pizzaDtoPage);
        assertEquals(0, pizzaDtoPage.getTotalElements());
        assertTrue(pizzaDtoPage.getContent().isEmpty());
        verify(mockPizzaConverter, times(0)).fromEntitiesToDtos(any());
    }


    @Test
    public void findPageWithIngredients_whenEmptyPageIsGivenByRepository_thenEmptyPageIsReturned() {
        // When
        when(mockPizzaRepository.findPageWithIngredients(any())).thenReturn(Page.empty());
        Page<PizzaDto> pizzaDtoPage = pizzaService.findPageWithIngredients(0, 2, Sort.unsorted());

        // Then
        assertNotNull(pizzaDtoPage);
        assertEquals(0, pizzaDtoPage.getTotalElements());
        assertTrue(pizzaDtoPage.getContent().isEmpty());
        verify(mockPizzaConverter, times(1)).fromEntitiesToDtos(any());
    }


    @Test
    public void findPageWithIngredients_whenResultsAreGivenByRepository_thenEquivalentDtosAreReturned() {
        // Given
        Pizza carbonara = Pizza.builder().id(1).name("Carbonara").cost(7.50D).build();
        Pizza hawaiian = Pizza.builder().id(2).name("Hawaiian").cost(8D).build();
        Page<Pizza> pizzaPage = new PageImpl<>(Arrays.asList(carbonara, hawaiian));

        PizzaDto carbonaraDto = PizzaDto.builder().id(carbonara.getId()).name(carbonara.getName()).cost(carbonara.getCost()).build();
        PizzaDto hawaiianDto = PizzaDto.builder().id(hawaiian.getId()).name(hawaiian.getName()).cost(hawaiian.getCost()).build();

        int size = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "name");

        // When
        when(mockPizzaConverter.fromEntitiesToDtos(any())).thenReturn(Arrays.asList(hawaiianDto, carbonaraDto));
        when(mockPizzaRepository.findPageWithIngredients(any())).thenReturn(pizzaPage);
        Page<PizzaDto> pizzaDtoPage = pizzaService.findPageWithIngredients(0, size, sort);

        // Then
        assertNotNull(pizzaDtoPage);
        assertEquals(size, pizzaDtoPage.getTotalElements());
        assertThat(pizzaDtoPage.getContent(), contains(hawaiianDto, carbonaraDto));
        verify(mockPizzaConverter, times(1)).fromEntitiesToDtos(any());
    }


    @Test
    public void save_whenNullPizzaIsGiven_thenNoOneIsSavedAndEmptyOptionalIsReturned() {
        // When
        Optional<PizzaDto> optionalPizzaDto = pizzaService.save(null);

        // Then
        assertFalse(optionalPizzaDto.isPresent());
        verify(mockIngredientRepository, times(0)).saveAll(any());
        verify(mockPizzaConverter, times(0)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(0)).save(any());
    }


    @Test
    public void save_whenNonExistingPizzaIsGiven_thenSaveRepositoryMethodIsInvokedAndAPizzaObjectIsReturned() {
        // Given
        Ingredient mozzarella = Ingredient.builder().id(1).name("Mozzarella").build();
        Ingredient oregano = Ingredient.builder().id(2).name("Oregano").build();
        Set<Ingredient> ingredients = new LinkedHashSet<>(Arrays.asList(mozzarella, oregano));

        IngredientDto mozzarellaDto = IngredientDto.builder().id(mozzarella.getId()).name(mozzarella.getName()).build();
        IngredientDto oreganoDto = IngredientDto.builder().id(oregano.getId()).name(oregano.getName()).build();
        Set<IngredientDto> ingredientDtos = new LinkedHashSet<>(Arrays.asList(mozzarellaDto, oreganoDto));

        Pizza pizza = Pizza.builder().name("carbonara").cost(7D).ingredients(ingredients).build();
        PizzaDto pizzaDto = PizzaDto.builder().name(pizza.getName()).cost(pizza.getCost()).ingredients(ingredientDtos).build();

        // When
        when(mockIngredientRepository.saveAll(any(Collection.class))).thenReturn(new ArrayList(ingredients));
        when(mockPizzaConverter.fromDtoToOptionalEntity(any(PizzaDto.class))).thenReturn(Optional.of(pizza));
        when(mockPizzaConverter.fromEntityToOptionalDto(any(Pizza.class))).thenReturn(Optional.of(pizzaDto));
        when(mockPizzaRepository.save(any(Pizza.class))).thenReturn(pizza);
        Optional<PizzaDto> optionalPizzaDto = pizzaService.save(pizzaDto);

        // Then
        assertTrue(optionalPizzaDto.isPresent());
        assertEquals(pizzaDto, optionalPizzaDto.get());
        assertThat(pizzaDto.getIngredients(), containsInAnyOrder(optionalPizzaDto.get().getIngredients().toArray()));

        verify(mockIngredientRepository, times(1)).saveAll(any());
        verify(mockPizzaConverter, times(1)).fromDtoToOptionalEntity(any());
        verify(mockPizzaConverter, times(1)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(1)).save(any());
    }


    @Test
    public void save_whenExistingPizzaIsGiven_thenSaveRepositoryMethodIsInvokedAndAPizzaObjectIsReturned() {
        // Given
        Ingredient mozzarella = Ingredient.builder().id(1).name("Mozzarella").build();
        Ingredient oregano = Ingredient.builder().id(2).name("Oregano").build();
        Set<Ingredient> ingredients = new LinkedHashSet<>(Arrays.asList(mozzarella, oregano));

        IngredientDto mozzarellaDto = IngredientDto.builder().id(mozzarella.getId()).name(mozzarella.getName()).build();
        IngredientDto oreganoDto = IngredientDto.builder().id(oregano.getId()).name(oregano.getName()).build();
        Set<IngredientDto> ingredientDtos = new LinkedHashSet<>(Arrays.asList(mozzarellaDto, oreganoDto));

        Pizza pizza = Pizza.builder().id(1).name("carbonara").cost(7D).ingredients(ingredients).build();
        PizzaDto pizzaDto = PizzaDto.builder().id(pizza.getId()).name(pizza.getName())
                                              .cost(pizza.getCost()).ingredients(ingredientDtos).build();
        // When
        when(mockIngredientRepository.saveAll(any(Collection.class))).thenReturn(new ArrayList(ingredients));
        when(mockPizzaConverter.fromDtoToOptionalEntity(any(PizzaDto.class))).thenReturn(Optional.of(pizza));
        when(mockPizzaConverter.fromEntityToOptionalDto(any(Pizza.class))).thenReturn(Optional.of(pizzaDto));
        when(mockPizzaRepository.save(any(Pizza.class))).thenReturn(pizza);
        Optional<PizzaDto> optionalPizzaDto = pizzaService.save(pizzaDto);

        // Then
        assertTrue(optionalPizzaDto.isPresent());
        assertEquals(pizzaDto, optionalPizzaDto.get());
        assertThat(pizzaDto.getIngredients(), containsInAnyOrder(optionalPizzaDto.get().getIngredients().toArray()));

        verify(mockIngredientRepository, times(1)).saveAll(any());
        verify(mockPizzaConverter, times(1)).fromDtoToOptionalEntity(any());
        verify(mockPizzaConverter, times(1)).fromEntityToOptionalDto(any());
        verify(mockPizzaRepository, times(1)).save(any());
    }

}
