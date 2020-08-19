package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilTest {

    static Stream<Arguments> collectPropertyNoCollectionFactoryTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            collection,                                 keyExtractor,   expectedResult
                Arguments.of( null,                                       null,           asList() ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getName,        asList(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getCost,        asList(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyNoCollectionFactoryTestCases")
    @DisplayName("collectProperty: without collection factory test cases")
    public void collectPropertyNoCollectionFactory_testCases(List<PizzaDto> collection, Function<PizzaDto, String> keyExtractor,
                                                             Collection<String> expectedResult) {
        Collection<String> result = CollectionUtil.collectProperty(collection, keyExtractor);
        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> collectPropertyAllParametersTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            collection,                                 keyExtractor,   collectionFactory,   expectedResult
                Arguments.of( null,                                       null,           null,                asList() ),
                Arguments.of( null,                                       null,           setSupplier,         new HashSet<>() ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getName,        null,                asList(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getName,        setSupplier,         Set.of(carbonaraCheap.getName()) ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getCost,        null,                asList(carbonaraCheap.getCost(), carbonaraExpense.getCost()) ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getCost,        setSupplier,         Set.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyAllParametersTestCases")
    @DisplayName("collectProperty: with all parameters test cases")
    public void collectPropertyAllParameters_testCases(List<PizzaDto> collection, Function<PizzaDto, String> keyExtractor,
                                                       Supplier<Collection<String>> collectionFactory, Collection<String> expectedResult) {
        Collection<String> result = CollectionUtil.collectProperty(collection, keyExtractor, collectionFactory);
        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> concatUniqueElementsTestCases() {
        return Stream.of(
                //@formatter:off
                //            collection1ToConcat,   collection2ToConcat,   collection3ToConcat,   expectedResult
                Arguments.of( null,                  null,                  null,                  new LinkedHashSet<>() ),
                Arguments.of( null,                  asList(),              asList(),              new LinkedHashSet<>() ),
                Arguments.of( asList(1, 2),          null,                  asList(2, 3),          new LinkedHashSet<>(asList(1, 2, 3)) ),
                Arguments.of( asList(5, 6),          asList(),              asList(6, 7),          new LinkedHashSet<>(asList(5, 6, 7)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatUniqueElementsTestCases")
    @DisplayName("concatUniqueElements: test cases")
    public void concatUniqueElements_testCases(List<Integer> collection1ToConcat, List<Integer> collection2ToConcat,
                                               List<Integer> collection3ToConcat, LinkedHashSet<Integer> expectedResult) {
        Set<Integer> concatedValues = CollectionUtil.concatUniqueElements(collection1ToConcat, collection2ToConcat, collection3ToConcat);
        assertEquals(expectedResult, concatedValues);
    }

}
