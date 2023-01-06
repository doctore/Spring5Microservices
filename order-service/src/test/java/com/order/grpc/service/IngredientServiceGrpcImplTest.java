package com.order.grpc.service;

import com.order.grpc.client.GrpcClient;
import com.spring5microservices.grpc.IngredientResponse;
import com.spring5microservices.grpc.IngredientServiceGrpc;
import com.spring5microservices.grpc.PizzaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class IngredientServiceGrpcImplTest {

    @Mock
    private GrpcClient mockGrpcClient;

    @Mock
    private IngredientServiceGrpc.IngredientServiceBlockingStub mockIngredientServiceGrpc;

    private IngredientServiceGrpcImpl service;


    @BeforeEach
    public void init() {
        service = new IngredientServiceGrpcImpl(mockGrpcClient);
        when(mockGrpcClient.getIngredientServiceGrpc()).thenReturn(mockIngredientServiceGrpc);
    }


    static Stream<Arguments> findByPizzaIdTestCases() {
        Short pizzaId = (short)11;
        IngredientResponse ingredient1 = IngredientResponse.newBuilder().setId(1).setName("ingredient 1").build();
        IngredientResponse ingredient2 = IngredientResponse.newBuilder().setId(2).setName("ingredient 2").build();

        Iterator<IngredientResponse> emptyGrpcResultIterator = Collections.emptyIterator();
        Iterator<IngredientResponse> notEmptyGrpcResultIterator = List.of(ingredient1, ingredient2).iterator();

        List<IngredientResponse> expectedResultNotEmptyIterator = List.of(ingredient1, ingredient2);
        return Stream.of(
                //@formatter:off
                //            pizzaId,   grpcInvocationResult,         expectedResult
                Arguments.of( null,      null,                         List.of() ),
                Arguments.of( pizzaId,   null,                         List.of() ),
                Arguments.of( pizzaId,   emptyGrpcResultIterator,      List.of() ),
                Arguments.of( pizzaId,   notEmptyGrpcResultIterator,   expectedResultNotEmptyIterator )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("findByPizzaIdTestCases")
    @DisplayName("findByPizzaId: test cases")
    public void findByPizzaId_testCases(Short pizzaId,
                                        Iterator<IngredientResponse> grpcInvocationResult,
                                        List<IngredientResponse> expectedResult) {
        when(mockIngredientServiceGrpc.getIngredients(any(PizzaRequest.class))).thenReturn(grpcInvocationResult);

        List<IngredientResponse> result = service.findByPizzaId(pizzaId);

        assertEquals(expectedResult, result);
    }

}
