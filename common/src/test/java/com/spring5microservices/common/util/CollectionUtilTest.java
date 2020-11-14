package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    static Stream<Arguments> foldLeftTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("AB", "E", "GMT");
        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        return Stream.of(
                //@formatter:off
                //            collection,   initialValue,   accumulator,  expectedException,                expectedResult
                Arguments.of( null,         null,           null,         IllegalArgumentException.class,   null ),
                Arguments.of( asList(),     1,              multiply,     null,                             1 ),
                Arguments.of( integers,     0,              null,         null,                             0 ),
                Arguments.of( integers,     1,              multiply,     null,                             15 ),
                Arguments.of( strings,      0,              sumLength,    null,                             6 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <T, E> void foldLeft_testCases(Collection<T> collection, E initialValue, BiFunction<E, ? super T, E> accumulator,
                                          Class<? extends Exception> expectedException, E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.foldLeft(collection, initialValue, accumulator));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.foldLeft(collection, initialValue, accumulator));
        }
    }


    static Stream<Arguments> removeKeysTestCases() {
        Map<String, Integer> sourceMap = new HashMap<>() {{
            put("A", 1);
            put("B", 2);
        }};
        Map<String, Integer> sourceMapFiltered = new HashMap<>() {{
            put("A", 1);
        }};
        List<String> keysToExcludeIncluded = asList("B");
        List<String> keysToExcludeNotIncluded = asList("C");
        return Stream.of(
                //@formatter:off
                //            sourceMap,   keysToExclude,              expectedResult
                Arguments.of( null,        null,                       new HashMap<>() ),
                Arguments.of( null,        keysToExcludeIncluded,      new HashMap<>() ),
                Arguments.of( sourceMap,   keysToExcludeNotIncluded,   sourceMap ),
                Arguments.of( sourceMap,   keysToExcludeIncluded,      sourceMapFiltered )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeKeysTestCases")
    @DisplayName("removeKeys: test cases")
    public <T, E> void removeKeys_testCases(Map<T, E> sourceMap, Collection<T> keysToExclude, HashMap<T, E> expectedResult) {
        Map<T, E> filteredMap = CollectionUtil.removeKeys(sourceMap, keysToExclude);
        assertEquals(expectedResult, filteredMap);
    }


    static Stream<Arguments> slidingTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("A", "E", "G", "M");
        return Stream.of(
                //@formatter:off
                //            listToSlide,   size,                  expectedResult
                Arguments.of( null,          5,                     new ArrayList<>() ),
                Arguments.of( asList(),      0,                     new ArrayList<>() ),
                Arguments.of( integers,      integers.size() + 1,   asList(integers) ),
                Arguments.of( strings,       2,                     asList(asList("A", "E"), asList("E", "G"), asList("G", "M")) ),
                Arguments.of( strings,       3,                     asList(asList("A", "E", "G"), asList("E", "G", "M")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T> void sliding_testCases(List<T> listToSlide, int size, List<List<T>> expectedResult) {
        List<List<T>> slidedList = CollectionUtil.sliding(listToSlide, size);
        assertEquals(expectedResult, slidedList);
    }


    static Stream<Arguments> splitTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("A", "E", "G", "M");
        return Stream.of(
                //@formatter:off
                //            listToSplit,   size,                  expectedResult
                Arguments.of( null,          5,                     new ArrayList<>() ),
                Arguments.of( asList(),      0,                     new ArrayList<>() ),
                Arguments.of( integers,      integers.size() + 1,   asList(integers) ),
                Arguments.of( strings,       2,                     asList(asList("A", "E"), asList("G", "M")) ),
                Arguments.of( strings,       3,                     asList(asList("A", "E", "G"), asList("M")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T> void split_testCases(List<T> listToSplit, int size, List<List<T>> expectedResult) {
        List<List<T>> splittedList = CollectionUtil.split(listToSplit, size);
        assertEquals(expectedResult, splittedList);
    }


    static Stream<Arguments> transposeTestCases() {
        List<List<Integer>> invalidList = asList(asList(1), asList(2, 3));
        List<List<Integer>> integers = asList(asList(1, 2, 3), asList(4, 5, 6));
        Set<Set<String>> strings = new LinkedHashSet<>() {{
            add(new LinkedHashSet<>() {{
                add("a1");
                add("a2");
            }});
            add(new LinkedHashSet<>() {{
                add("b1");
                add("b2");
            }});
            add(new LinkedHashSet<>() {{
                add("c1");
                add("c2");
            }});
        }};
        List<List<Integer>> integersResult = asList(asList(1, 4), asList(2, 5), asList(3, 6));
        List<List<String>> stringsResult = asList(asList("a1", "b1", "c1"), asList("a2", "b2", "c2"));
        return Stream.of(
                //@formatter:off
                //            collectionsToTranspose,   expectedException,                expectedResult
                Arguments.of( null,                     null,                             new ArrayList<>() ),
                Arguments.of( new ArrayList<>(),        null,                             new ArrayList<>() ),
                Arguments.of( invalidList,              IllegalArgumentException.class,   null ),
                Arguments.of( integers,                 null,                             integersResult ),
                Arguments.of( strings,                  null,                             stringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("transposeTestCases")
    @DisplayName("transpose: test cases")
    public <T> void transpose_testCases(Collection<Collection<T>> collectionsToTranspose, Class<? extends Exception> expectedException,
                                        List<List<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.transpose(collectionsToTranspose));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.transpose(collectionsToTranspose));
        }
    }

}
