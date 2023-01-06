package com.pizza.grpc.service;

import com.pizza.grpc.converter.IngredientGrpcConverter;
import com.pizza.model.Ingredient;
import com.pizza.service.IngredientService;
import com.spring5microservices.grpc.IngredientResponse;
import com.spring5microservices.grpc.PizzaRequest;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildIngredientResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class IngredientServiceGrpcImplTest {

    @Mock
    private IngredientService mockIngredientService;

    @Mock
    private IngredientGrpcConverter mockIngredientGrpcConverter;

    private IngredientServiceGrpcImpl service;


    @BeforeEach
    public void init() {
        service = new IngredientServiceGrpcImpl(mockIngredientService, mockIngredientGrpcConverter);
    }


    static Stream<Arguments> getIngredientsTestCases() {
        PizzaRequest emptyRequest = PizzaRequest.newBuilder().build();
        PizzaRequest notEmptyRequest = PizzaRequest.newBuilder().setId(1).build();

        Ingredient ingredient1 = buildIngredient(1, "ingredient1");
        Ingredient ingredient2 = buildIngredient(2, "ingredient2");
        IngredientResponse ingredientResponse1 = buildIngredientResponse(ingredient1.getId(), ingredient1.getName());
        IngredientResponse ingredientResponse2 = buildIngredientResponse(ingredient2.getId(), ingredient2.getName());
        List<Ingredient> allIngredients = List.of(ingredient1, ingredient2);
        List<IngredientResponse> allIngredientsResponse = List.of(ingredientResponse1, ingredientResponse2);
        return Stream.of(
                //@formatter:off
                //            pizzaRequest,      serviceResult,          converterResult,                expectedResult
                Arguments.of( null,              List.of(),              List.of(),                      List.of() ),
                Arguments.of( emptyRequest,      List.of(),              List.of(),                      List.of() ),
                Arguments.of( notEmptyRequest,   List.of(),              List.of(),                      List.of() ),
                Arguments.of( notEmptyRequest,   List.of(ingredient1),   List.of(ingredientResponse1),   List.of(ingredientResponse1) ),
                Arguments.of( notEmptyRequest,   allIngredients,         allIngredientsResponse,         allIngredientsResponse )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getIngredientsTestCases")
    @DisplayName("getIngredients: test cases")
    public void getIngredients_testCases(PizzaRequest pizzaRequest,
                                         List<Ingredient> serviceResult,
                                         List<IngredientResponse> converterResult,
                                         List<IngredientResponse> expectedResult) throws InterruptedException {
        final List<IngredientResponse> result = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        final StreamObserver<IngredientResponse> streamObserver = buildStreamObserverForTesting(
                result,
                latch
        );

        if (Objects.nonNull(pizzaRequest)) {
            when(mockIngredientService.findByPizzaId(pizzaRequest.getId()))
                    .thenReturn(new HashSet<>(serviceResult));
        }
        for (int i = 0; i < serviceResult.size(); i++) {
            when(mockIngredientGrpcConverter.fromModelToDto(serviceResult.get(i)))
                    .thenReturn(converterResult.get(i));
        }

        service.getIngredients(pizzaRequest, streamObserver);
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        assertEquals(expectedResult, result);
    }


    private StreamObserver<IngredientResponse> buildStreamObserverForTesting(final Collection<IngredientResponse> ingredientResponse,
                                                                             final CountDownLatch latch) {
        return new StreamObserver<>() {
            @Override
            public void onNext(IngredientResponse value) {
                ingredientResponse.add(value);
            }

            @Override
            public void onError(Throwable t) {
                fail("There was an error in the StreamObserver used as response", t);
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };
    }

}
