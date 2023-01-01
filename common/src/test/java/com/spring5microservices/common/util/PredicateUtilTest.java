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
import static com.spring5microservices.common.util.PredicateUtil.alwaysFalse;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysFalse;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.distinctByKey;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PredicateUtilTest {

    static Stream<Arguments> alwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,     expectedResult
                Arguments.of( "noMatterString",   false ),
                Arguments.of( 12,                 false ),
                Arguments.of( carbonara,          false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysFalseTestCases")
    @DisplayName("alwaysFalse: test cases")
    public <T> void alwaysFalse_testCases(T sourceInstance,
                                          boolean expectedResult) {
        assertEquals(expectedResult, alwaysFalse().test(sourceInstance));
    }


    static Stream<Arguments> alwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,     expectedResult
                Arguments.of( "noMatterString",   true ),
                Arguments.of( 12,                 true ),
                Arguments.of( carbonara,          true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysTrueTestCases")
    @DisplayName("alwaysTrue: test cases")
    public <T> void alwaysTrue_testCases(T sourceInstance,
                                         boolean expectedResult) {
        assertEquals(expectedResult, alwaysTrue().test(sourceInstance));
    }


    static Stream<Arguments> biAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            firstSourceInstance,   secondSourceInstance,   expectedResult
                Arguments.of( "noMatterString",      11,                     false ),
                Arguments.of( 12,                    54L,                    false ),
                Arguments.of( carbonara,             Boolean.TRUE,           false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAlwaysFalseTestCases")
    @DisplayName("biAlwaysFalse: test cases")
    public <T, E> void biAlwaysFalse_testCases(T firstSourceInstance,
                                               E secondSourceInstance,
                                               boolean expectedResult) {
        assertEquals(expectedResult, biAlwaysFalse().test(firstSourceInstance, secondSourceInstance));
    }


    static Stream<Arguments> biAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            firstSourceInstance,   secondSourceInstance,   expectedResult
                Arguments.of( "noMatterString",      11,                     true ),
                Arguments.of( 12,                    54L,                    true ),
                Arguments.of( carbonara,             Boolean.TRUE,           true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAlwaysTrueTestCases")
    @DisplayName("biAlwaysTrue: test cases")
    public <T, E> void biAlwaysTrue_testCases(T firstSourceInstance,
                                               E secondSourceInstance,
                                               boolean expectedResult) {
        assertEquals(expectedResult, biAlwaysTrue().test(firstSourceInstance, secondSourceInstance));
    }


    static Stream<Arguments> distinctByKeyTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto carbonaraExpense = new PizzaDto(CARBONARA.getInternalPropertyValue(), 10D);
        PizzaDto margheritaCheap = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 5D);
        PizzaDto margheritaExpense = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            initialCollection,                             keyExtractor,   expectedResult
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
    public void distinctByKey_testCases(List<PizzaDto> initialCollection,
                                        Function<PizzaDto, String> keyExtractor,
                                        List<PizzaDto> expectedResult) {
        List<PizzaDto> distinctCollection = initialCollection.stream()
                .filter(distinctByKey(keyExtractor))
                .collect(toList());
        assertEquals(expectedResult, distinctCollection);
    }

}
