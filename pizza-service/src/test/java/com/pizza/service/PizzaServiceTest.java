package com.pizza.service;

import com.pizza.dto.IngredientDto;
import com.pizza.dto.PizzaDto;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.IngredientRepository;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
import com.pizza.util.converter.PizzaConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildIngredientDto;
import static com.pizza.TestDataFactory.buildPizza;
import static com.pizza.TestDataFactory.buildPizzaDto;
import static com.pizza.enums.PizzaEnum.CARBONARA;
import static com.pizza.enums.PizzaEnum.MARGUERITA;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PizzaServiceTest {

    @Mock
    private IngredientRepository mockIngredientRepository;

    @Mock
    private PizzaConverter mockPizzaConverter;

    @Mock
    private PizzaRepository mockPizzaRepository;

    private PizzaService service;

    @BeforeEach
    public void init() {
        service = new PizzaService(mockIngredientRepository, mockPizzaConverter, mockPizzaRepository);
    }


    static Stream<Arguments> findByNameTestCases() {
        Ingredient ingredient = buildIngredient(1, "Cheese");
        IngredientDto ingredientDto = buildIngredientDto(ingredient.getId(), ingredient.getName());
        Pizza pizza = buildPizza(1, CARBONARA, 7D, Set.of(ingredient));
        PizzaDto pizzaDto = buildPizzaDto(pizza.getId(), pizza.getName().name(), pizza.getCost(), Set.of(ingredientDto));
        return Stream.of(
                //@formatter:off
                //            name,                                   repositoryResult,    converterResult,    expectedResult
                Arguments.of( null,                                   empty(),             empty(),            empty() ),
                Arguments.of( CARBONARA.getInternalPropertyValue(),   empty(),             empty(),            empty() ),
                Arguments.of( CARBONARA.getInternalPropertyValue(),   of(pizza),           empty(),            empty() ),
                Arguments.of( CARBONARA.getInternalPropertyValue(),   of(pizza),           of(pizzaDto),       of(pizzaDto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByNameTestCases")
    @DisplayName("findByName: test cases")
    public void findByName_testCases(String name,
                                     Optional<Pizza> repositoryResult,
                                     Optional<PizzaDto> converterResult,
                                     Optional<PizzaDto> expectedResult) {
        if (null != name) {
            when(mockPizzaRepository.findWithIngredientsByName(PizzaEnum.getFromDatabaseValue(name).get())).thenReturn(repositoryResult);
        }
        if (repositoryResult.isPresent()) {
            when(mockPizzaConverter.fromModelToOptionalDto(repositoryResult.get())).thenReturn(converterResult);
        }
        Optional<PizzaDto> result = service.findByName(name);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> findPageWithIngredientsTestCases() {
        Ingredient ingredient1 = buildIngredient(1, "Cheese");
        Ingredient ingredient2 = buildIngredient(2, "Jam");
        IngredientDto ingredientDto1 = buildIngredientDto(ingredient1.getId(), ingredient1.getName());
        IngredientDto ingredientDto2 = buildIngredientDto(ingredient2.getId(), ingredient2.getName());
        Pizza pizza1 = buildPizza(1, CARBONARA, 7D, Set.of(ingredient1));
        Pizza pizza2 = buildPizza(2, MARGUERITA, 12D, Set.of(ingredient2));
        PizzaDto pizzaDto1 = buildPizzaDto(pizza1.getId(), pizza1.getName().name(), pizza1.getCost(), Set.of(ingredientDto1));
        PizzaDto pizzaDto2 = buildPizzaDto(pizza2.getId(), pizza2.getName().name(), pizza2.getCost(), Set.of(ingredientDto2));
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Page<Pizza> pizzaEmptyPage = new PageImpl<>(List.of());
        Page<Pizza> pizzaPage = new PageImpl<>(List.of(pizza1, pizza2));
        Page<PizzaDto> pizzaDtoEmptyPage = new PageImpl<>(List.of());
        Page<PizzaDto> pizzaDtoPage = new PageImpl<>(List.of(pizzaDto1, pizzaDto2));
        return Stream.of(
                //@formatter:off
                //            page,   size,   sort,   repositoryResult,   converterResult,             expectedResult
                Arguments.of( 0,      1,      null,   pizzaEmptyPage,     List.of(),                   pizzaDtoEmptyPage ),
                Arguments.of( 0,      1,      sort,   pizzaEmptyPage,     List.of(),                   pizzaDtoEmptyPage ),
                Arguments.of( 0,      1,      sort,   pizzaPage,          pizzaDtoPage.getContent(),   pizzaDtoPage )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findPageWithIngredientsTestCases")
    @DisplayName("findPageWithIngredients: test cases")
    public void findPageWithIngredients_testCases(int page,
                                                  int size,
                                                  Sort sort,
                                                  Page<Pizza> repositoryResult,
                                                  List<PizzaDto> converterResult,
                                                  Page<PizzaDto> expectedResult) {
        when(mockPizzaRepository.findPageWithIngredientsWithoutInMemoryPagination(PageUtil.buildPageRequest(page, size, sort))).thenReturn(repositoryResult);
        when(mockPizzaConverter.fromModelsToDtos(repositoryResult.getContent())).thenReturn(converterResult);

        Page<PizzaDto> result = service.findPageWithIngredients(page, size, sort);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> saveTestCases() {
        Ingredient ingredient = buildIngredient(1, "Cheese");
        IngredientDto ingredientDto = buildIngredientDto(ingredient.getId(), ingredient.getName());
        Pizza pizza = buildPizza(1, CARBONARA, 7D, Set.of(ingredient));
        PizzaDto pizzaDto = buildPizzaDto(pizza.getId(), pizza.getName().name(), pizza.getCost(), Set.of(ingredientDto));
        return Stream.of(
                //@formatter:off
                //            pizzaDto,   converterToModelResult,   repositoryResult,   converterToDtoResult,   expectedResult
                Arguments.of( null,       empty(),                  null,               empty(),                empty() ),
                Arguments.of( pizzaDto,   empty(),                  null,               empty(),                empty() ),
                Arguments.of( pizzaDto,   of(pizza),                null,               empty(),                empty() ),
                Arguments.of( pizzaDto,   of(pizza),                pizza,              empty(),                empty() ),
                Arguments.of( pizzaDto,   of(pizza),                pizza,              of(pizzaDto),           of(pizzaDto) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(PizzaDto pizzaDto,
                               Optional<Pizza> converterToModelResult,
                               Pizza repositoryResult,
                               Optional<PizzaDto> converterToDtoResult,
                               Optional<PizzaDto> expectedResult) {
        when(mockPizzaConverter.fromDtoToOptionalModel(pizzaDto)).thenReturn(converterToModelResult);
        when(mockPizzaConverter.fromModelToOptionalDto(repositoryResult)).thenReturn(converterToDtoResult);
        if (converterToModelResult.isPresent()) {
            when(mockPizzaRepository.save(converterToModelResult.get())).thenReturn(repositoryResult);
        }

        Optional<PizzaDto> result = service.save(pizzaDto);

        assertEquals(expectedResult, result);
        if (converterToModelResult.isPresent()) {
            verify(mockIngredientRepository, times(1)).saveAll(converterToModelResult.get().getIngredients());
        }
    }

}
