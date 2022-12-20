package com.pizza.repository;

import com.pizza.configuration.persistence.PersistenceConfiguration;
import com.pizza.dto.IngredientPizzaSummaryDto;
import com.pizza.model.Ingredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildIngredientPizzaSummaryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceConfiguration.class)
public class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository repository;


    static Stream<Arguments> findByPizzaIdTestCases() {
        Integer carbonaraId = 1;
        Set<Ingredient> carbonaraIngredients = Set.of(
                buildIngredient(1, "Bacon"),
                buildIngredient(3, "Egg"),
                buildIngredient(5, "Mozzarella"),
                buildIngredient(7, "Parmesan")
        );
        return Stream.of(
                //@formatter:off
                //            pizzaId,       expectedResult
                Arguments.of( null,          Set.of() ),
                Arguments.of( carbonaraId,   carbonaraIngredients )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByPizzaIdTestCases")
    @DisplayName("findByPizzaId: test cases")
    public void findByPizzaId_testCases(Integer pizzaId,
                                        Set<Ingredient> expectedResult) {
        Set<Ingredient> result = repository.findByPizzaId(pizzaId);
        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> getIngredientWithItsMoreExpensivePizzaTestCases() {
        IngredientPizzaSummaryDto dto1 = buildIngredientPizzaSummaryDto("Tomato sauce", "Margherita", 7d);
        IngredientPizzaSummaryDto dto2 = buildIngredientPizzaSummaryDto("Cheese", "Hawaiian", 8d);
        return Stream.of(
                //@formatter:off
                //            ingredientNames,                                       expectedResult
                Arguments.of( List.of(),                                             List.of() ),
                Arguments.of( List.of("NotExistingName"),                            List.of() ),
                Arguments.of( List.of(dto1.getIngredient()),                         List.of(dto1) ),
                Arguments.of( List.of(dto1.getIngredient(), dto2.getIngredient()),   List.of(dto2, dto1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getIngredientWithItsMoreExpensivePizzaTestCases")
    @DisplayName("getIngredientWithItsMoreExpensivePizza: test cases")
    public void getIngredientWithItsMoreExpensivePizza_testCases(Collection<String> ingredientNames,
                                                                 List<IngredientPizzaSummaryDto> expectedResult) {
        List<IngredientPizzaSummaryDto> result = repository.getIngredientWithItsMoreExpensivePizza(ingredientNames);
        assertEquals(expectedResult, result);
    }

}
