package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.dto.PairDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.applyOrElse;
import static com.spring5microservices.common.util.CollectionUtil.asSet;
import static com.spring5microservices.common.util.CollectionUtil.collect;
import static com.spring5microservices.common.util.CollectionUtil.collectProperty;
import static com.spring5microservices.common.util.CollectionUtil.concatUniqueElements;
import static com.spring5microservices.common.util.CollectionUtil.find;
import static com.spring5microservices.common.util.CollectionUtil.findLast;
import static com.spring5microservices.common.util.CollectionUtil.foldLeft;
import static com.spring5microservices.common.util.CollectionUtil.iterate;
import static com.spring5microservices.common.util.CollectionUtil.reverseList;
import static com.spring5microservices.common.util.CollectionUtil.slice;
import static com.spring5microservices.common.util.CollectionUtil.sliding;
import static com.spring5microservices.common.util.CollectionUtil.split;
import static com.spring5microservices.common.util.CollectionUtil.transpose;
import static com.spring5microservices.common.util.CollectionUtil.unzip;
import static com.spring5microservices.common.util.CollectionUtil.zip;
import static com.spring5microservices.common.util.CollectionUtil.zipAll;
import static com.spring5microservices.common.util.CollectionUtil.zipWithIndex;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectionUtilTest {

    static Stream<Arguments> applyOrElseNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);
        List<String> expectedIntsResult = asList("2", "3", "6", "7");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultFunction,   orElseFunction,    expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           null,              null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isEven,            null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isEven,            plus1String,       null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,               isEven,            plus1String,       multiply2String,   null,                             asList() ),
                Arguments.of( asList(),           isEven,            plus1String,       multiply2String,   null,                             asList() ),
                Arguments.of( ints,               isEven,            plus1String,       multiply2String,   null,                             expectedIntsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseNoCollectionFactoryTestCases")
    @DisplayName("applyOrElse: without collection factory test cases")
    public <T, E> void applyOrElseNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                                Predicate<? super T> filterPredicate,
                                                                Function<? super T, ? extends E> defaultFunction,
                                                                Function<? super T, ? extends E> orElseFunction,
                                                                Class<? extends Exception> expectedException,
                                                                List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
        else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseAllParametersTestCases() {
        List<Integer> ints = asList(1, 2, 3, 6);
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        List<String> expectedIntsResultList = asList("2", "4", "4", "12");
        Set<String> expectedIntsResultSet = new HashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultFunction,   orElseFunction,    collectionFactory,  expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           null,              null,              null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isOdd,             null,              null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isOdd,             plus1String,       null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( null,               isOdd,             plus1String,       multiply2String,   null,               null,                             asList() ),
                Arguments.of( asList(),           isOdd,             plus1String,       multiply2String,   null,               null,                             asList() ),
                Arguments.of( ints,               isOdd,             plus1String,       multiply2String,   null,               null,                             expectedIntsResultList ),
                Arguments.of( ints,               isOdd,             plus1String,       multiply2String,   setSupplier,        null,                             expectedIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseAllParametersTestCases")
    @DisplayName("applyOrElse: with all parameters test cases")
    public <T, E> void applyOrElseAllParameters_testCases(Collection<T> sourceCollection,
                                                          Predicate<? super T> filterPredicate,
                                                          Function<? super T, ? extends E> defaultFunction,
                                                          Function<? super T, ? extends E> orElseFunction,
                                                          Supplier<Collection<E>> collectionFactory,
                                                          Class<? extends Exception> expectedException,
                                                          Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction, collectionFactory
                    )
            );
        }
        else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction, collectionFactory
                    )
            );
        }
    }


    @Test
    public void asSet_whenNullIsGiven_thenNewEmptySetIsReturned() {
        Set result = asSet(null);
        assertEquals(result, new LinkedHashSet<>());
    }


    @Test
    public void asSet_whenSeveralParametersAreGiven_thenNewSetWithGivenParametersIsReturned() {
        Set<Integer> result = asSet(2, 5, 8, 9, 11);
        assertEquals(result, new LinkedHashSet<>(asList(2, 5, 8, 9, 11)));
    }


    static Stream<Arguments> collectNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Function<Integer, String> fromIntegerToString = Objects::toString;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   mapFunction,           expectedException,                expectedResult
                Arguments.of( null,               null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isEven,            null,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,               isEven,            fromIntegerToString,   null,                             asList()),
                Arguments.of( asList(),           isEven,            fromIntegerToString,   null,                             asList()),
                Arguments.of( ints,               isEven,            fromIntegerToString,   null,                             asList("2", "6"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectNoCollectionFactoryTestCases")
    @DisplayName("collect: without collection factory test cases")
    public <T, E> void collectNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                            Predicate<? super T> filterPredicate,
                                                            Function<? super T, ? extends E> mapFunction,
                                                            Class<? extends Exception> expectedException,
                                                            List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, filterPredicate, mapFunction));
        }
        else {
            assertEquals(expectedResult, collect(sourceCollection, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> collectAllParametersTestCases() {
        List<Integer> ints = asList(1, 2, 3, 6);
        Set<String> collectedInts = new HashSet<>(asList("1", "3"));
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        Function<Integer, String> fromIntegerToString = Object::toString;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   mapFunction,           collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( asList(1),          isOdd,             null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,               isOdd,             fromIntegerToString,   null,                null,                             asList() ),
                Arguments.of( asList(),           isOdd,             fromIntegerToString,   null,                null,                             asList() ),
                Arguments.of( ints,               isOdd,             fromIntegerToString,   null,                null,                             asList("1", "3") ),
                Arguments.of( ints,               isOdd,             fromIntegerToString,   setSupplier,         null,                             collectedInts )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectAllParametersTestCases")
    @DisplayName("collect: with all parameters test cases")
    public <T, E> void collectAllParameters_testCases(Collection<T> sourceCollection,
                                                      Predicate<? super T> filterPredicate,
                                                      Function<? super T, ? extends E> mapFunction,
                                                      Supplier<Collection<E>> collectionFactory,
                                                      Class<? extends Exception> expectedException,
                                                      Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, filterPredicate, mapFunction, collectionFactory));
        }
        else {
            assertEquals(expectedResult, collect(sourceCollection, filterPredicate, mapFunction, collectionFactory));
        }
    }


    static Stream<Arguments> collectPropertyNoCollectionFactoryTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,                           keyExtractor,   expectedResult
                Arguments.of( null,                                       null,           asList() ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getName,        asList(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( asList(carbonaraCheap, carbonaraExpense),   getCost,        asList(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyNoCollectionFactoryTestCases")
    @DisplayName("collectProperty: without collection factory test cases")
    public void collectPropertyNoCollectionFactory_testCases(List<PizzaDto> sourceCollection,
                                                             Function<PizzaDto, String> keyExtractor,
                                                             Collection<String> expectedResult) {
        Collection<String> result = collectProperty(sourceCollection, keyExtractor);
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
                //            sourceCollection,                           keyExtractor,   collectionFactory,   expectedResult
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
    public void collectPropertyAllParameters_testCases(List<PizzaDto> sourceCollection,
                                                       Function<PizzaDto, String> keyExtractor,
                                                       Supplier<Collection<String>> collectionFactory,
                                                       Collection<String> expectedResult) {
        Collection<String> result = collectProperty(sourceCollection, keyExtractor, collectionFactory);
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
    public void concatUniqueElements_testCases(List<Integer> collection1ToConcat,
                                               List<Integer> collection2ToConcat,
                                               List<Integer> collection3ToConcat,
                                               LinkedHashSet<Integer> expectedResult) {
        Set<Integer> concatedValues = concatUniqueElements(collection1ToConcat, collection2ToConcat, collection3ToConcat);
        assertEquals(expectedResult, concatedValues);
    }


    static Stream<Arguments> findTestCases() {
        List<Integer> integers = asList(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));

        Predicate<Integer> upperThan10 = i -> 10 < i;
        Predicate<Integer> upperThan20 = i -> 20 < i;
        Predicate<Long> upperThan50 = l -> 50 < l;
        Predicate<String> moreThan2Characters = s -> 2 < s.length();
        Predicate<String> moreThan5Characters = s -> 5 < s.length();
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,       expectedResult
                Arguments.of( null,               null,                  empty() ),
                Arguments.of( null,               upperThan10,           empty() ),
                Arguments.of( asList(),           null,                  empty() ),
                Arguments.of( integers,           null,                  empty() ),
                Arguments.of( integers,           upperThan20,           empty() ),
                Arguments.of( strings,            moreThan5Characters,   empty() ),
                Arguments.of( integers,           upperThan10,           of(11) ),
                Arguments.of( strings,            moreThan2Characters,   of("YTGH") ),
                Arguments.of( longs,              upperThan50,           of(54L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    @DisplayName("find: test cases")
    public <T> void find_testCases(Collection<T> sourceCollection,
                                   Predicate<? super T> filterPredicate,
                                   Optional<T> expectedResult) {
        assertEquals(expectedResult, find(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> findLastTestCases() {
        List<Integer> integers = asList(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));

        Predicate<Integer> upperThan10 = i -> 10 < i;
        Predicate<Integer> upperThan20 = i -> 20 < i;
        Predicate<Long> upperThan50 = l -> 50 < l;
        Predicate<String> moreThan2Characters = s -> 2 < s.length();
        Predicate<String> moreThan5Characters = s -> 5 < s.length();
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,       expectedResult
                Arguments.of( null,               null,                  empty() ),
                Arguments.of( null,               upperThan10,           empty() ),
                Arguments.of( asList(),           null,                  empty() ),
                Arguments.of( integers,           null,                  empty() ),
                Arguments.of( integers,           upperThan20,           empty() ),
                Arguments.of( strings,            moreThan5Characters,   empty() ),
                Arguments.of( integers,           upperThan10,           of(15) ),
                Arguments.of( strings,            moreThan2Characters,   of("IOP") ),
                Arguments.of( longs,              upperThan50,           of(78L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findLastTestCases")
    @DisplayName("findLast: test cases")
    public <T> void findLast_testCases(Collection<T> sourceCollection,
                                       Predicate<? super T> filterPredicate,
                                       Optional<T> expectedResult) {
        assertEquals(expectedResult, findLast(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> foldLeftTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 75L, 12L));

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        BiFunction<Long, Long, Long> subtract = (a, b) -> a - b;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   initialValue,   accumulator,   expectedException,                expectedResult
                Arguments.of( null,               null,           null,          IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           2,              null,          null,                             2 ),
                Arguments.of( asList(),           1,              multiply,      null,                             1 ),
                Arguments.of( integers,           0,              null,          null,                             0 ),
                Arguments.of( integers,           1,              multiply,      null,                             15 ),
                Arguments.of( strings,            0,              sumLength,     null,                             6 ),
                Arguments.of( longs,              10L,            subtract,      null,                             -131L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <T, E> void foldLeft_testCases(Collection<T> sourceCollection,
                                          E initialValue,
                                          BiFunction<E, ? super T, E> accumulator,
                                          Class<? extends Exception> expectedException,
                                          E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> foldLeft(sourceCollection, initialValue, accumulator));
        }
        else {
            assertEquals(expectedResult, foldLeft(sourceCollection, initialValue, accumulator));
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
    public <T> void iterate_testCases(T initialValue,
                                      UnaryOperator<T> applyFunction,
                                      Predicate<T> untilPredicate,
                                      Class<? extends Exception> expectedException,
                                      T expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> iterate(initialValue, applyFunction, untilPredicate));
        }
        else {
            assertEquals(expectedResult, iterate(initialValue, applyFunction, untilPredicate));
        }
    }


    static Stream<Arguments> reverseListTestCases() {
        List<Integer> integersList = asList(3, 7, 9, 11, 15);
        Set<String> stringsLinkedSet = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));

        TreeSet<Integer> integersTreeSet = new TreeSet<>(Collections.reverseOrder());
        integersTreeSet.addAll(asList(45, 71, 9, 11, 35));

        PriorityQueue<Long> longsPriorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
        longsPriorityQueue.addAll(asList(54L, 78L, 12L));

        List<Integer> reverseIntegersList = asList(15, 11, 9, 7, 3);
        List<String> reverseStringsLinkedSet = asList("IOP", "YTGH", "BT", "A");
        List<Integer> reverseIntegersTreeSet = asList(9, 11, 35, 45, 71);
        List<Long> reverseLongsPriorityQueue = asList(78L, 54L, 12L);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,     expectedResult
                Arguments.of( null,                 asList() ),
                Arguments.of( asList(),             asList() ),
                Arguments.of( integersList,         reverseIntegersList ),
                Arguments.of( integersTreeSet,      reverseIntegersTreeSet ),
                Arguments.of( stringsLinkedSet,     reverseStringsLinkedSet ),
                Arguments.of( longsPriorityQueue,   reverseLongsPriorityQueue )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("reverseListTestCases")
    @DisplayName("reverseList: test cases")
    public <T> void reverseList_testCases(Collection<T> sourceCollection,
                                          List<T> expectedResult) {
        assertEquals(expectedResult, reverseList(sourceCollection));
    }


    static Stream<Arguments> sliceTestCases() {
        Set<Integer> integers = new LinkedHashSet<>(asList(11, 12, 13, 14));
        List<String> strings = asList("a", "b", "c", "d", "f");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   from,   to,   expectedException,                expectedResult
                Arguments.of( null,               2,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( asList(),           3,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( integers,           1,      0,    IllegalArgumentException.class,   null ),
                Arguments.of( null,               0,      1,    null,                             asList() ),
                Arguments.of( asList(),           0,      1,    null,                             asList() ),
                Arguments.of( integers,          -1,      0,    null,                             asList() ),
                Arguments.of( integers,          -1,      3,    null,                             asList(11, 12, 13) ),
                Arguments.of( integers,           1,      3,    null,                             asList(12, 13) ),
                Arguments.of( integers,           2,      5,    null,                             asList(13, 14) ),
                Arguments.of( integers,           6,      8,    null,                             asList() ),
                Arguments.of( strings,           -1,      1,    null,                             asList("a") ),
                Arguments.of( strings,            2,      3,    null,                             asList("c") ),
                Arguments.of( strings,            4,      9,    null,                             asList("f") ),
                Arguments.of( longs,              0,      1,    null,                             asList(12L) ),
                Arguments.of( longs,              1,      2,    null,                             asList(54L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sliceTestCases")
    @DisplayName("slice: test cases")
    public <T> void slice_testCases(Collection<T> sourceCollection,
                                    int from, int until,
                                    Class<? extends Exception> expectedException,
                                    List<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> slice(sourceCollection, from, until));
        }
        else {
            assertEquals(expectedResult, slice(sourceCollection, from, until));
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
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,                      expectedResult
                Arguments.of( null,               5,                         new ArrayList<>() ),
                Arguments.of( asList(),           0,                         new ArrayList<>() ),
                Arguments.of( integers,           integers.size() + 1,       asList(integers) ),
                Arguments.of( integers,           2,                         asList(asList(1, 3), asList(3, 5)) ),
                Arguments.of( strings,            2,                         asList(asList("A", "E"), asList("E", "G"), asList("G", "M")) ),
                Arguments.of( strings,            3,                         asList(asList("A", "E", "G"), asList("E", "G", "M")) ),
                Arguments.of( longs,              2,                         asList(asList(12L, 54L), asList(54L, 78L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T> void sliding_testCases(Collection<T> sourceCollection, int size,
                                      List<List<T>> expectedResult) {
        List<List<T>> slidedList = sliding(sourceCollection, size);
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
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,                  expectedResult
                Arguments.of( null,               5,                     new ArrayList<>() ),
                Arguments.of( asList(),           0,                     new ArrayList<>() ),
                Arguments.of( integers,           integers.size() + 1,   asList(integers) ),
                Arguments.of( strings,            2,                     asList(asList("A", "E"), asList("G", "M")) ),
                Arguments.of( strings,            3,                     asList(asList("A", "E", "G"), asList("M")) ),
                Arguments.of( longs,              2,                     asList(asList(12L, 54L), asList(78L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T> void split_testCases(Collection<T> sourceCollection, int size,
                                    List<List<T>> expectedResult) {
        List<List<T>> splittedList = split(sourceCollection, size);
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

        List<List<Integer>> integersResult = asList(
                asList(1, 4),
                asList(2, 5),
                asList(3, 6)
        );
        List<List<String>> stringsResult = asList(
                asList("a1", "b1", "c1"),
                asList("a2", "b2", "c2")
        );
        List<List<Integer>> differentInnerListSizesResult = asList(
                asList(1, 0, 7),
                asList(2, 8),
                asList(9)
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,          expectedResult
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
    public <T> void transpose_testCases(Collection<Collection<T>> sourceCollection,
                                        List<List<T>> expectedResult) {
        assertEquals(expectedResult, transpose(sourceCollection));
    }


    static Stream<Arguments> unzipTestCases() {
        List<PairDto<String, Integer>> pairList = asList(PairDto.of("a", 1), PairDto.of("b", 2), PairDto.of("c", 3));
        Set<PairDto<String, Boolean>> pairSet = new LinkedHashSet<>() {{
            add(PairDto.of("true", true));
            add(PairDto.of("false", false));
        }};

        PairDto emptyPairResult = PairDto.of(asList(), asList());
        PairDto<List<String>, List<Integer>> pairListResult = PairDto.of(
                asList("a", "b", "c"),
                asList(1, 2, 3)
        );
        PairDto<List<String>, List<Boolean>> pairSetResult = PairDto.of(
                asList("true", "false"),
                asList(true, false)
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,    expectedResult
                Arguments.of( null,                emptyPairResult ),
                Arguments.of( new ArrayList<>(),   emptyPairResult ),
                Arguments.of( pairList,            pairListResult ),
                Arguments.of( pairSet,             pairSetResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("unzipTestCases")
    @DisplayName("unzip: test cases")
    public <T, E> void unzip_testCases(Collection<PairDto<T, E>> sourceCollection,
                                       PairDto<List<T>, List<E>> expectedResult) {
        assertEquals(expectedResult, unzip(sourceCollection));
    }


    static Stream<Arguments> zipTestCases() {
        List<Integer> integers = asList(11, 31, 55);
        List<Boolean> booleans = asList(true, false);
        List<String> strings = asList("h", "o", "p");

        List<PairDto<Integer, Boolean>> integersBooleansResult = asList(
                PairDto.of(11, true),
                PairDto.of(31, false)
        );
        List<PairDto<Integer, String>> integersStringsResult = asList(
                PairDto.of(11, "h"),
                PairDto.of(31, "o"),
                PairDto.of(55, "p")
        );
        List<PairDto<Boolean, String>> booleansStringsResult = asList(
                PairDto.of(true, "h"),
                PairDto.of(false, "o")
        );
        return Stream.of(
                //@formatter:off
                //            sourceLeftCollection,   sourceRightCollection,   expectedResult
                Arguments.of( null,                   null,                    asList() ),
                Arguments.of( null,                   integers,                asList() ),
                Arguments.of( integers,               null,                    asList() ),
                Arguments.of( asList(),               integers,                asList() ),
                Arguments.of( integers,               asList(),                asList() ),
                Arguments.of( integers,               booleans,                integersBooleansResult ),
                Arguments.of( integers,               strings,                 integersStringsResult ),
                Arguments.of( booleans,               strings,                 booleansStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipTestCases")
    @DisplayName("zip: test cases")
    public <T, E> void zip_testCases(Collection<T> sourceLeftCollection, Collection<E> sourceRightCollection,
                                     List<PairDto<T, E>> expectedResult) {
        assertEquals(expectedResult, zip(sourceLeftCollection, sourceRightCollection));
    }


    static Stream<Arguments> zipAllTestCases() {
        List<Integer> integers = asList(11, 31, 55);
        List<Boolean> booleans = asList(true, false);
        List<String> strings = asList("h", "o", "p");
        Integer defaultIntegerValue = 99;
        Boolean defaultBooleanValue = true;
        String defaultStringValue = "x";

        List<PairDto<Object, Boolean>> booleansWithNullResult = asList(
                PairDto.of(null, true),
                PairDto.of(null, false)
        );
        List<PairDto<Integer, Object>> integersWithNullResult = asList(
                PairDto.of(11, null),
                PairDto.of(31, null),
                PairDto.of(55, null)
        );
        List<PairDto<Integer, Boolean>> integersBooleansResult = asList(
                PairDto.of(11, true),
                PairDto.of(31, false),
                PairDto.of(55, defaultBooleanValue)
        );
        List<PairDto<Integer, String>> integersStringsResult = asList(
                PairDto.of(11, "h"),
                PairDto.of(31, "o"),
                PairDto.of(55, "p")
        );
        List<PairDto<Boolean, String>> booleansStringsResult = asList(
                PairDto.of(true, "h"),
                PairDto.of(false, "o"),
                PairDto.of(defaultBooleanValue, "p")
        );
        return Stream.of(
                //@formatter:off
                //            sourceLeftCollection,   sourceRightCollection,   defaultLeftElement,    defaultRightElement,   expectedResult
                Arguments.of( null,                   null,                    null,                  null,                  asList() ),
                Arguments.of( null,                   booleans,                null,                  99,                    booleansWithNullResult ),
                Arguments.of( integers,               null,                    91,                    null,                  integersWithNullResult ),
                Arguments.of( asList(),               booleans,                null,                  75,                    booleansWithNullResult ),
                Arguments.of( integers,               asList(),                49,                    null,                  integersWithNullResult ),
                Arguments.of( integers,               booleans,                defaultIntegerValue,   defaultBooleanValue,   integersBooleansResult ),
                Arguments.of( integers,               strings,                 defaultIntegerValue,   defaultStringValue,    integersStringsResult ),
                Arguments.of( booleans,               strings,                 defaultBooleanValue,   defaultStringValue,    booleansStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipAllTestCases")
    @DisplayName("zipAll: test cases")
    public <T, E> void zipAll_testCases(Collection<T> sourceLeftCollection, Collection<E> sourceRightCollection,
                                        T defaultLeftElement,
                                        E defaultRightElement,
                                        List<PairDto<T, E>> expectedResult) {
        assertEquals(expectedResult,
                zipAll(
                        sourceLeftCollection, sourceRightCollection, defaultLeftElement, defaultRightElement
                )
        );
    }


    static Stream<Arguments> zipWithIndexTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        List<PairDto<Integer, Integer>> integersResult = asList(
                PairDto.of(0, 1),
                PairDto.of(1, 3),
                PairDto.of(2, 5)
        );
        List<PairDto<Integer, String>> stringsResult = asList(
                PairDto.of(0, "A"),
                PairDto.of(1, "E"),
                PairDto.of(2, "G"),
                PairDto.of(3, "M")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               new ArrayList<>() ),
                Arguments.of( asList(),           new ArrayList<>() ),
                Arguments.of( integers,           integersResult ),
                Arguments.of( strings,            stringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipWithIndexTestCases")
    @DisplayName("zipWithIndex: test cases")
    public <T> void zipWithIndex_testCases(Collection<T> sourceCollection,
                                           List<PairDto<Integer, T>> expectedResult) {
        List<PairDto<Integer, T>> zippedList = zipWithIndex(sourceCollection);
        assertEquals(expectedResult, zippedList);
    }

}
