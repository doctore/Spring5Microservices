package com.pizza.service;

import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildPizza;
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
    private PizzaRepository mockPizzaRepository;

    @Mock
    private IngredientService mockIngredientService;

    private PizzaService service;


    @BeforeEach
    public void init() {
        service = new PizzaService(mockPizzaRepository, mockIngredientService);
    }


    static Stream<Arguments> findByNameTestCases() {
        Ingredient ingredient = buildIngredient(1, "Cheese");
        Pizza pizza = buildPizza(1, CARBONARA, 7D, Set.of(ingredient));
        return Stream.of(
                //@formatter:off
                //            name,                                   repositoryResult,    expectedResult
                Arguments.of( null,                                   empty(),             empty() ),
                Arguments.of( CARBONARA.getInternalPropertyValue(),   empty(),             empty() ),
                Arguments.of( CARBONARA.getInternalPropertyValue(),   of(pizza),           of(pizza) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByNameTestCases")
    @DisplayName("findByName: test cases")
    public void findByName_testCases(String name,
                                     Optional<Pizza> repositoryResult,
                                     Optional<Pizza> expectedResult) {
        if (null != name) {
            when(mockPizzaRepository.findWithIngredientsByName(PizzaEnum.getFromDatabaseValue(name).get()))
                    .thenReturn(repositoryResult);
        }
        Optional<Pizza> result = service.findByName(name);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> findPageWithIngredientsTestCases() {
        Ingredient ingredient1 = buildIngredient(1, "Cheese");
        Ingredient ingredient2 = buildIngredient(2, "Jam");
        Pizza pizza1 = buildPizza(1, CARBONARA, 7D, Set.of(ingredient1));
        Pizza pizza2 = buildPizza(2, MARGUERITA, 12D, Set.of(ingredient2));
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Page<Pizza> pizzaEmptyPage = new PageImpl<>(List.of());
        Page<Pizza> pizzaPage = new PageImpl<>(List.of(pizza1, pizza2));
        return Stream.of(
                //@formatter:off
                //            page,   size,   sort,   repositoryResult,   expectedResult
                Arguments.of( 0,      1,      null,   pizzaEmptyPage,     pizzaEmptyPage ),
                Arguments.of( 0,      1,      sort,   pizzaEmptyPage,     pizzaEmptyPage ),
                Arguments.of( 0,      1,      sort,   pizzaPage,          pizzaPage )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findPageWithIngredientsTestCases")
    @DisplayName("findPageWithIngredients: test cases")
    public void findPageWithIngredients_testCases(int page,
                                                  int size,
                                                  Sort sort,
                                                  Page<Pizza> repositoryResult,
                                                  Page<Pizza> expectedResult) {
        when(mockPizzaRepository.findPageWithIngredientsWithoutInMemoryPagination(
                PageUtil.buildPageRequest(page, size, sort))
        ).thenReturn(repositoryResult);

        Page<Pizza> result = service.findPageWithIngredients(page, size, sort);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> saveTestCases() {
        Ingredient ingredient = buildIngredient(1, "Cheese");
        Pizza pizza = buildPizza(1, CARBONARA, 7D, Set.of(ingredient));
        return Stream.of(
                //@formatter:off
                //            pizza,   repositoryResult,   expectedResult
                Arguments.of( null,    null,               empty() ),
                Arguments.of( pizza,   null,               empty() ),
                Arguments.of( pizza,   pizza,              of(pizza) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveTestCases")
    @DisplayName("save: test cases")
    public void save_testCases(Pizza pizza,
                               Pizza repositoryResult,
                               Optional<Pizza> expectedResult) {
        when(mockPizzaRepository.save(pizza)).thenReturn(repositoryResult);

        Optional<Pizza> result = service.save(pizza);

        assertEquals(expectedResult, result);
        if (Objects.nonNull(pizza)) {
            verify(mockIngredientService, times(1))
                    .saveAll(pizza.getIngredients());
        }
    }

}
