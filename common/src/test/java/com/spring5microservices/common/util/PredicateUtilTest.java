package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.PizzaEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class PredicateUtilTest {

    static Stream<Arguments> distinctByKeyTestCases() {
        PizzaDto carbonaraCheap = PizzaDto.builder().name(PizzaEnum.CARBONARA.getInternalPropertyValue()).cost(5D).build();
        PizzaDto carbonaraExpense = PizzaDto.builder().name(PizzaEnum.CARBONARA.getInternalPropertyValue()).cost(10D).build();
        PizzaDto margheritaCheap = PizzaDto.builder().name(PizzaEnum.MARGUERITA.getInternalPropertyValue()).cost(5D).build();
        PizzaDto margheritaExpense = PizzaDto.builder().name(PizzaEnum.MARGUERITA.getInternalPropertyValue()).cost(10D).build();
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            initialCollection,                            keyExtractor,   expectedResult
                Arguments.of( asList(),                                     getName,        asList() ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),     getName,        asList(carbonaraCheap) ),
                Arguments.of( asList(carbonaraCheap, margheritaCheap),      getName,        asList(carbonaraCheap, margheritaCheap) ),
                Arguments.of( asList(margheritaCheap, margheritaExpense),   getCost,        asList(margheritaCheap, margheritaExpense) ),
                Arguments.of( asList(carbonaraCheap, margheritaCheap),      getCost,        asList(carbonaraCheap) )
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
