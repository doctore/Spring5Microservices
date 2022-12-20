package com.pizza.service;

import com.pizza.model.Ingredient;
import com.pizza.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class IngredientServiceTest {

    @Mock
    private IngredientRepository mockIngredientRepository;

    private IngredientService service;


    @BeforeEach
    public void init() {
        service = new IngredientService(mockIngredientRepository);
    }


    static Stream<Arguments> findByPizzaIdTestCases() {
        Integer pizzaId = 11;
        Ingredient ingredient1 = buildIngredient(1, "Cheese");
        Ingredient ingredient2 = buildIngredient(2, "Bacon");
        Set<Ingredient> allIngredients = Set.of(ingredient1, ingredient2);
        return Stream.of(
                //@formatter:off
                //            pizzaId,              repositoryResult,   expectedResult
                Arguments.of( null,      null,                          Set.of() ),
                Arguments.of( null,      Set.of(),                      Set.of() ),
                Arguments.of( pizzaId,   Set.of(),                      Set.of() ),
                Arguments.of( pizzaId,   Set.of(ingredient1),           Set.of(ingredient1) ),
                Arguments.of( pizzaId,   allIngredients,                allIngredients )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByPizzaIdTestCases")
    @DisplayName("findByPizzaId: test cases")
    public void findByPizzaId_testCases(Integer pizzaId,
                                        Set<Ingredient> repositoryResult,
                                        Set<Ingredient> expectedResult) {
        when(mockIngredientRepository.findByPizzaId(pizzaId)).thenReturn(repositoryResult);

        Set<Ingredient> result = service.findByPizzaId(pizzaId);

        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> saveAllTestCases() {
        Ingredient ingredient1 = buildIngredient(1, "Cheese");
        Ingredient ingredient2 = buildIngredient(2, "Bacon");
        List<Ingredient> allIngredients = List.of(ingredient1, ingredient2);
        return Stream.of(
                //@formatter:off
                //            ingredients,            repositoryResult,       expectedResult
                Arguments.of( null,                   null,                   List.of() ),
                Arguments.of( null,                   List.of(),              List.of() ),
                Arguments.of( List.of(ingredient1),   List.of(),              List.of() ),
                Arguments.of( List.of(ingredient1),   List.of(ingredient1),   List.of(ingredient1) ),
                Arguments.of( allIngredients,         allIngredients,         allIngredients )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("saveAllTestCases")
    @DisplayName("saveAll: test cases")
    public void saveAll_testCases(Collection<Ingredient> ingredients,
                                  List<Ingredient> repositoryResult,
                                  List<Ingredient> expectedResult) {
        when(mockIngredientRepository.saveAll(ingredients)).thenReturn(repositoryResult);

        List<Ingredient> result = service.saveAll(ingredients);

        assertEquals(expectedResult, result);
    }

}
