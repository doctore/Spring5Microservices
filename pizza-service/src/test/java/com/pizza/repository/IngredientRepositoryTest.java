package com.pizza.repository;

import com.pizza.configuration.persistence.PersistenceConfiguration;
import com.pizza.dto.IngredientPizzaSummaryDto;
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
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredientPizzaSummaryDto;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Import(PersistenceConfiguration.class)
public class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository repository;


    static Stream<Arguments> getIngredientWithItsMoreExpensivePizzaTestCases() {
        IngredientPizzaSummaryDto dto1 = buildIngredientPizzaSummaryDto("Tomato sauce", "Margherita", 7d);
        IngredientPizzaSummaryDto dto2 = buildIngredientPizzaSummaryDto("Cheese", "Hawaiian", 8d);
        return Stream.of(
                //@formatter:off
                //            ingredientNames,                                      expectedResult
                Arguments.of( asList(),                                             asList() ),
                Arguments.of( asList("NotExistingName"),                            asList() ),
                Arguments.of( asList(dto1.getIngredient()),                         asList(dto1) ),
                Arguments.of( asList(dto1.getIngredient(), dto2.getIngredient()),   asList(dto2, dto1) )
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
