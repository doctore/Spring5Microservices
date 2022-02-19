package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.asSet;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectionUtilTest {

    @Test
    public void asSet_whenNullIsGiven_thenNewEmptySetIsReturned() {
        Set result = asSet(null);
        assertEquals(result, new LinkedHashSet<>());
    }


    @Test
    public void asSet_whenSeveralParametersAreGiven_thenNewSetWithGivenParametersIsReturned() {
        Set result = asSet(2, 5, 8, 9, 11);
        assertEquals(result, new LinkedHashSet<>(asList(2, 5, 8, 9, 11)));
    }


    static Stream<Arguments> collectNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Function<Integer, String> fromIntegerToString = i -> i.toString();
        return Stream.of(
                //@formatter:off
                //            collection,   filterPredicate,   mapFunction,           expectedException,                expectedResult
                Arguments.of( null,         null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(),     null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),    null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),    isEven,            null,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,         isEven,            fromIntegerToString,   null,                             asList()),
                Arguments.of( asList(),     isEven,            fromIntegerToString,   null,                             asList()),
                Arguments.of( ints,         isEven,            fromIntegerToString,   null,                             asList("2", "6"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectNoCollectionFactoryTestCases")
    @DisplayName("collect: without collection factory test cases")
    public <T, E> void collectNoCollectionFactory_testCases(Collection<T> collection, Predicate<? super T> filterPredicate,
                                                            Function<? super T, ? extends E> mapFunction,
                                                            Class<? extends Exception> expectedException,
                                                            List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.collect(collection, filterPredicate, mapFunction));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.collect(collection, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> collectAllParametersTestCases() {
        List<Integer> ints = asList(1, 2, 3, 6);
        Set<String> collectedInts = new HashSet<>(asList("1", "3"));
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        Function<Integer, String> fromIntegerToString = i -> i.toString();
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            collection,   filterPredicate,   mapFunction,           collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,         null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(),     null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),    null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),    isOdd,             null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,         isOdd,             fromIntegerToString,   null,                null,                             asList()),
                Arguments.of( asList(),     isOdd,             fromIntegerToString,   null,                null,                             asList()),
                Arguments.of( ints,         isOdd,             fromIntegerToString,   null,                null,                             asList("1", "3")),
                Arguments.of( ints,         isOdd,             fromIntegerToString,   setSupplier,         null,                             collectedInts)
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectAllParametersTestCases")
    @DisplayName("collect: with all parameters test cases")
    public <T, E> void collectAllParameters_testCases(Collection<T> collection, Predicate<? super T> filterPredicate,
                                                      Function<? super T, ? extends E> mapFunction,
                                                      Supplier<Collection<E>> collectionFactory,
                                                      Class<? extends Exception> expectedException,
                                                      Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.collect(collection, filterPredicate, mapFunction, collectionFactory));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.collect(collection, filterPredicate, mapFunction, collectionFactory));
        }
    }


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


    static Stream<Arguments> iterateTestCases() {
        UnaryOperator<Integer> divisionBy10 = a -> a / 10;
        Predicate<Integer> untilLowerOrEqualTo50 = a -> 50 >= a;
        Predicate<Integer> untilLowerOrEqualTo0 = a -> 0 >= a;
        return Stream.of(
                //@formatter:off
                //            initialValue,   applyFunction,   untilPredicate,          expectedException,                expectedResult
                Arguments.of( null,           null,            null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 1,              null,            null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 1,              divisionBy10,    null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 42,             divisionBy10,    untilLowerOrEqualTo50,   null,                             asList() ),
                Arguments.of( 42,             divisionBy10,    untilLowerOrEqualTo0,    null,                             asList(42, 4) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("iterateTestCases")
    @DisplayName("iterate: test cases")
    public <T> void iterate_testCases(T initialValue, UnaryOperator<T> applyFunction, Predicate<T> untilPredicate,
                                      Class<? extends Exception> expectedException, T expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.iterate(initialValue, applyFunction, untilPredicate));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.iterate(initialValue, applyFunction, untilPredicate));
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


    static Stream<Arguments> sliceTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(11, 12, 13, 14));
        List<String> letters = asList("a", "b", "c", "d", "f");
        return Stream.of(
                //@formatter:off
                //            collection,   from,   to,   expectedException,                expectedResult
                Arguments.of( null,         2,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( asList(),     3,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( ints,         1,      0,    IllegalArgumentException.class,   null ),
                Arguments.of( null,         0,      1,    null,                             asList() ),
                Arguments.of( asList(),     0,      1,    null,                             asList() ),
                Arguments.of( ints,        -1,      0,    null,                             asList() ),
                Arguments.of( ints,        -1,      3,    null,                             asList(11, 12, 13) ),
                Arguments.of( ints,         1,      3,    null,                             asList(12, 13) ),
                Arguments.of( ints,         2,      5,    null,                             asList(13, 14) ),
                Arguments.of( ints,         6,      8,    null,                             asList() ),
                Arguments.of( letters,     -1,      1,    null,                             asList("a") ),
                Arguments.of( letters,      2,      3,    null,                             asList("c") ),
                Arguments.of( letters,      4,      9,    null,                             asList("f") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sliceTestCases")
    @DisplayName("slice: test cases")
    public <T> void slice_testCases(Collection<T> collection, int from, int until,
                                    Class<? extends Exception> expectedException,
                                    List<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> CollectionUtil.slice(collection, from, until));
        }
        else {
            assertEquals(expectedResult, CollectionUtil.slice(collection, from, until));
        }
    }


    static Stream<Arguments> slidingTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        return Stream.of(
                //@formatter:off
                //            collectionToSlide,   size,                      expectedResult
                Arguments.of( null,                5,                         new ArrayList<>() ),
                Arguments.of( asList(),            0,                         new ArrayList<>() ),
                Arguments.of( integers,            integers.size() + 1,       asList(integers) ),
                Arguments.of( integers,            2,                         asList(asList(1, 3), asList(3, 5)) ),
                Arguments.of( strings,             2,                         asList(asList("A", "E"), asList("E", "G"), asList("G", "M")) ),
                Arguments.of( strings,             3,                         asList(asList("A", "E", "G"), asList("E", "G", "M")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T> void sliding_testCases(Collection<T> collectionToSlide, int size, List<List<T>> expectedResult) {
        List<List<T>> slidedList = CollectionUtil.sliding(collectionToSlide, size);
        assertEquals(expectedResult, slidedList);
    }


    static Stream<Arguments> splitTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        return Stream.of(
                //@formatter:off
                //            collectionToSplit,   size,                  expectedResult
                Arguments.of( null,                5,                     new ArrayList<>() ),
                Arguments.of( asList(),            0,                     new ArrayList<>() ),
                Arguments.of( integers,            integers.size() + 1,   asList(integers) ),
                Arguments.of( strings,             2,                     asList(asList("A", "E"), asList("G", "M")) ),
                Arguments.of( strings,             3,                     asList(asList("A", "E", "G"), asList("M")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T> void split_testCases(Collection<T> collectionToSplit, int size, List<List<T>> expectedResult) {
        List<List<T>> splittedList = CollectionUtil.split(collectionToSplit, size);
        assertEquals(expectedResult, splittedList);
    }


    static Stream<Arguments> transposeTestCases() {
        List<List<Integer>> emptyLists = asList(asList(), asList());
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
        List<List<Integer>> differentInnerListSizes = asList(asList(1, 2), asList(0), asList(7, 8, 9));

        List<List<Integer>> integersResult = asList(asList(1, 4), asList(2, 5), asList(3, 6));
        List<List<String>> stringsResult = asList(asList("a1", "b1", "c1"), asList("a2", "b2", "c2"));
        List<List<Integer>> differentInnerListSizesResult = asList(asList(1, 0, 7), asList(2, 8), asList(9));

        return Stream.of(
                //@formatter:off
                //            collectionsToTranspose,    expectedResult
                Arguments.of( null,                      new ArrayList<>() ),
                Arguments.of( new ArrayList<>(),         new ArrayList<>() ),
                Arguments.of( emptyLists,                new ArrayList<>() ),
                Arguments.of( integers,                  integersResult ),
                Arguments.of( strings,                   stringsResult ),
                Arguments.of( differentInnerListSizes,   differentInnerListSizesResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("transposeTestCases")
    @DisplayName("transpose: test cases")
    public <T> void transpose_testCases(Collection<Collection<T>> collectionsToTranspose, List<List<T>> expectedResult) {
        assertEquals(expectedResult, CollectionUtil.transpose(collectionsToTranspose));
    }

}
