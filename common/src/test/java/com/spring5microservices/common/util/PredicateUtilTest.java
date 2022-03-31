package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PredicateUtilTest {

    static Stream<Arguments> distinctByKeyTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto carbonaraExpense = new PizzaDto(CARBONARA.getInternalPropertyValue(), 10D);
        PizzaDto margheritaCheap = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 5D);
        PizzaDto margheritaExpense = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            initialCollection,                            keyExtractor,   expectedResult
                Arguments.of( List.of(),                                     getName,        List.of() ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),     getName,        List.of(carbonaraCheap) ),
                Arguments.of( List.of(carbonaraCheap, margheritaCheap),      getName,        List.of(carbonaraCheap, margheritaCheap) ),
                Arguments.of( List.of(margheritaCheap, margheritaExpense),   getCost,        List.of(margheritaCheap, margheritaExpense) ),
                Arguments.of( List.of(carbonaraCheap, margheritaCheap),      getCost,        List.of(carbonaraCheap) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("distinctByKeyTestCases")
    @DisplayName("distinctByKey: test cases")
    public void distinctByKey_testCases(List<PizzaDto> initialCollection, Function<PizzaDto, String> keyExtractor, List<PizzaDto> expectedResult) {
        List<PizzaDto> distinctCollection = initialCollection.stream().filter(PredicateUtil.distinctByKey(keyExtractor)).collect(toList());
        assertEquals(expectedResult, distinctCollection);
    }

}
