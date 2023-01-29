package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.MapUtil.applyOrElse;
import static com.spring5microservices.common.util.MapUtil.collect;
import static com.spring5microservices.common.util.MapUtil.count;
import static com.spring5microservices.common.util.MapUtil.dropWhile;
import static com.spring5microservices.common.util.MapUtil.find;
import static com.spring5microservices.common.util.MapUtil.flatten;
import static com.spring5microservices.common.util.MapUtil.foldLeft;
import static com.spring5microservices.common.util.MapUtil.getOrElse;
import static com.spring5microservices.common.util.MapUtil.groupBy;
import static com.spring5microservices.common.util.MapUtil.groupMap;
import static com.spring5microservices.common.util.MapUtil.groupMapReduce;
import static com.spring5microservices.common.util.MapUtil.map;
import static com.spring5microservices.common.util.MapUtil.mapValues;
import static com.spring5microservices.common.util.MapUtil.max;
import static com.spring5microservices.common.util.MapUtil.maxValue;
import static com.spring5microservices.common.util.MapUtil.min;
import static com.spring5microservices.common.util.MapUtil.minValue;
import static com.spring5microservices.common.util.MapUtil.partition;
import static com.spring5microservices.common.util.MapUtil.slice;
import static com.spring5microservices.common.util.MapUtil.sliding;
import static com.spring5microservices.common.util.MapUtil.split;
import static com.spring5microservices.common.util.MapUtil.takeWhile;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapUtilTest {

    static Stream<Arguments> applyOrElseNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) -> k % 2 == 1 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());
        BiFunction<Integer, String, Long> sumKeyPlusValueLength = (k, v) -> (long) (k + v.length());

        Map<Integer, Long> intsAndStringsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(3, 7L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 4L);
            put(4, 5L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultFunction,               orElseFunction,          expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                    multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseNoMapFactoryTestCases")
    @DisplayName("applyOrElse: without map factory test cases")
    public <T, E, R> void applyOrElseNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                            BiPredicate<? super T, ? super E> filterPredicate,
                                                            BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                            BiFunction<? super T, ? super E, ? extends R> orElseFunction,
                                                            Class<? extends Exception> expectedException,
                                                            Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
        }};
        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) -> k % 2 == 1 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());
        BiFunction<Integer, String, Long> sumKeyPlusValueLength = (k, v) -> (long) (k + v.length());

        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Long> intsAndStringsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(3, 7L);
        }};
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 4L);
        }};
        LinkedHashMap<Integer, Long> intsAndStringsLinkedMapResult = new LinkedHashMap<>(intsAndStringsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultFunction,               orElseFunction,          mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                    null,                          null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                    multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             intsAndStringsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             intsAndStringsResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseAllParametersTestCases")
    @DisplayName("applyOrElse: with all parameters test cases")
    public <T, E, R> void applyOrElseAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                             BiPredicate<? super T, ? super E> filterPredicate,
                                                             BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                             BiFunction<? super T, ? super E, ? extends R> orElseFunction,
                                                             Supplier<Map<T, R>> mapFactory,
                                                             Class<? extends Exception> expectedException,
                                                             Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction, mapFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> collectNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());

        Map<Integer, Long> intsAndLongsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                          null,                             Map.of() ),
                Arguments.of( null,             null,                     multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                          null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                          null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                     multiply2KeyPlusValueLength,   null,                             intsAndLongsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectNoMapFactoryTestCases")
    @DisplayName("collect: without map factory test cases")
    public <T, E, R> void collectNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                        Class<? extends Exception> expectedException,
                                                        Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction));
        } else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> collectAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());

        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Long> intsAndLongsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        LinkedHashMap<Integer, Long> intsAndLongsLinkedMapResult = new LinkedHashMap<>(intsAndLongsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                          null,                null,                             Map.of() ),
                Arguments.of( null,             null,                     multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                          null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   null,                          null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                     multiply2KeyPlusValueLength,   null,                null,                             intsAndLongsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             intsAndLongsResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndLongsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectAllParametersTestCases")
    @DisplayName("collect: with all parameters test cases")
    public <T, E, R> void collectAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                         BiPredicate<? super T, ? super E> filterPredicate,
                                                         BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                         Supplier<Map<T, R>> mapFactory,
                                                         Class<? extends Exception> expectedException,
                                                         Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> countTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "i");
            put(4, "o");
            put(8, "E");
        }};
        Map<String, Long> stringsAndLongs = new TreeMap<>() {{
            put("HY", 23L);
            put("ZW", 62L);
            put("ZZ", 63L);
            put("TZ", 69L);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel =
                (k, v) ->
                        k % 2 == 0 && "AEIOUaeiou".contains(v);

        BiPredicate<String, Long> isKeyContainsZAndValueIsOdd =
                (k, v) ->
                        k.toUpperCase().contains("Z") && v % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            sourceMap,         filterPredicate,               expectedResult
                Arguments.of( null,              null,                          0 ),
                Arguments.of( Map.of(),          null,                          0 ),
                Arguments.of( null,              isKeyEvenAndValueVowel,        0 ),
                Arguments.of( Map.of(),          isKeyEvenAndValueVowel,        0 ),
                Arguments.of( intsAndStrings,    isKeyEvenAndValueVowel,        3 ),
                Arguments.of( stringsAndLongs,   isKeyContainsZAndValueIsOdd,   2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("countTestCases")
    @DisplayName("count: test cases")
    public <T, E> void count_testCases(Map<? extends T, ? extends E> sourceMap,
                                       BiPredicate<? super T, ? super E> filterPredicate,
                                       int expectedResult) {
        assertEquals(expectedResult, count(sourceMap, filterPredicate));
    }


    static Stream<Arguments> dropWhileNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileNoMapFactoryTestCases")
    @DisplayName("dropWhile: without map factory test cases")
    public <T, E> void dropWhileNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> filterPredicate,
                                                       Map<T, E> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceMap, filterPredicate));
    }


    static Stream<Arguments> dropWhileAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileAllParametersTestCases")
    @DisplayName("dropWhile: with all parameters test cases")
    public <T, E> void dropWhileAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<T, E> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceMap, filterPredicate, mapFactory));
    }


    static Stream<Arguments> findTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(8, "E");
        }};
        Map<String, Long> stringsAndLongs = new TreeMap<>() {{
            put("HY", 23L);
            put("ZW", 62L);
            put("ZZ", 63L);
            put("TZ", 69L);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel =
                (k, v) ->
                        k % 2 == 0 && "AEIOUaeiou".contains(v);

        BiPredicate<Integer, String> isValueContainsZ =
                (k, v) ->
                        v.toUpperCase().contains("Z");

        BiPredicate<String, Long> isKeyContainsZAndValueIsOdd =
                (k, v) ->
                        k.toUpperCase().contains("Z") && v % 2 == 1;

        return Stream.of(
                //@formatter:off
                //            sourceMap,         filterPredicate,               expectedResult
                Arguments.of( null,              null,                          empty() ),
                Arguments.of( Map.of(),          null,                          empty() ),
                Arguments.of( null,              isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( Map.of(),          isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( intsAndStrings,    isValueContainsZ,              empty() ),
                Arguments.of( intsAndStrings,    isKeyEvenAndValueVowel,        of(Tuple.of(4, "o")) ),
                Arguments.of( stringsAndLongs,   isKeyContainsZAndValueIsOdd,   of(Tuple.of("TZ", 69L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    @DisplayName("find: test cases")
    public <T, E> void find_testCases(Map<? extends T, ? extends E> sourceMap,
                                      BiPredicate<? super T, ? super E> filterPredicate,
                                      Optional<Tuple2<T, E>> expectedResult) {
        assertEquals(expectedResult, find(sourceMap, filterPredicate));
    }


    static Stream<Arguments> flattenNoCollectionFactoryTestCases() {
        Map<Integer, String> intsAndStringsRaw = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, List<String>> intsAndStringsList = new LinkedHashMap<>() {{
            put(1, List.of("A"));
            put(8, List.of("E", "H"));
        }};

        BiFunction<Integer, String, Tuple2<Integer, String>> flattenerRaw =
                Tuple2::of;

        BiFunction<Integer, List<String>, List<Tuple2<Integer, String>>> flattenerList =
                (i, l) ->
                        l.stream()
                                .map(elto -> Tuple2.of(i, elto))
                                .collect(Collectors.toList());

        List<Tuple2<Integer, String>> resultRaw = List.of(Tuple2.of(1, "A"), Tuple2.of(2, "B"), Tuple2.of(4, "o"));
        List<Tuple2<Integer, String>> resultList = List.of(Tuple2.of(1, "A"), Tuple2.of(8, "E"), Tuple2.of(8, "H"));
        return Stream.of(
                //@formatter:off
                //            sourceMap,            flattener,       expectedException,                expectedResult
                Arguments.of( intsAndStringsRaw,    null,            IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,            null,                             List.of() ),
                Arguments.of( Map.of(),             null,            null,                             List.of() ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    null,                             resultRaw ),
                Arguments.of( intsAndStringsList,   flattenerList,   null,                             resultList )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenNoCollectionFactoryTestCases")
    @DisplayName("flatten: without collection factory test cases")
    public <T, E, R, U> void flattenNoCollectionFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                  BiFunction<? super T, ? super E, ? extends R> flattener,
                                                                  Class<? extends Exception> expectedException,
                                                                  List<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> flatten(sourceMap, flattener));
        } else {
            assertEquals(expectedResult, flatten(sourceMap, flattener));
        }
    }


    static Stream<Arguments> flattenAllParametersTestCases() {
        Map<Integer, String> intsAndStringsRaw = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, List<String>> intsAndStringsList = new LinkedHashMap<>() {{
            put(1, List.of("A"));
            put(8, List.of("E", "H"));
        }};
        Supplier<Set<Tuple2<Integer, String>>> setSupplier = LinkedHashSet::new;

        BiFunction<Integer, String, Tuple2<Integer, String>> flattenerRaw =
                Tuple2::of;

        BiFunction<Integer, List<String>, List<Tuple2<Integer, String>>> flattenerList =
                (i, l) ->
                        l.stream()
                                .map(elto -> Tuple2.of(i, elto))
                                .collect(Collectors.toList());

        List<Tuple2<Integer, String>> resultRaw = List.of(Tuple2.of(1, "A"), Tuple2.of(2, "B"), Tuple2.of(4, "o"));
        List<Tuple2<Integer, String>> resultList = List.of(Tuple2.of(1, "A"), Tuple2.of(8, "E"), Tuple2.of(8, "H"));
        return Stream.of(
                //@formatter:off
                //            sourceMap,            flattener,       collectionFactory,   expectedException,                expectedResult
                Arguments.of( intsAndStringsRaw,    null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStringsRaw,    null,            setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,            null,                null,                             List.of() ),
                Arguments.of( null,                 null,            setSupplier,         null,                             Set.of() ),
                Arguments.of( Map.of(),             null,            null,                null,                             List.of() ),
                Arguments.of( Map.of(),             null,            setSupplier,         null,                             Set.of() ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    null,                null,                             resultRaw ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    setSupplier,         null,                             new LinkedHashSet<>(resultRaw) ),
                Arguments.of( intsAndStringsList,   flattenerList,   null,                null,                             resultList ),
                Arguments.of( intsAndStringsList,   flattenerList,   setSupplier,         null,                             new LinkedHashSet<>(resultList) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenAllParametersTestCases")
    @DisplayName("flatten: with call parameters test cases")
    public <T, E, R, U> void flattenAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                            BiFunction<? super T, ? super E, ? extends R> flattener,
                                                            Supplier<Collection<U>> collectionFactory,
                                                            Class<? extends Exception> expectedException,
                                                            Collection<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> flatten(sourceMap, flattener, collectionFactory));
        } else {
            assertEquals(expectedResult, flatten(sourceMap, flattener, collectionFactory));
        }
    }


    static Stream<Arguments> foldLeftTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};
        TriFunction<Integer, Integer, String, Integer> multiply = (a, b, c) -> a * b * c.length();
        TriFunction<Long, Integer, Long, Long> sum = (a, b, c) -> a + (long)b + c;
        return Stream.of(
                //@formatter:off
                //            sourceMap,         initialValue,   accumulator,   expectedException,                expectedResult
                Arguments.of( null,              null,           null,          IllegalArgumentException.class,   null ),
                Arguments.of( new HashMap<>(),   2,              null,          null,                             2 ),
                Arguments.of( new HashMap<>(),   1,              multiply,      null,                             1 ),
                Arguments.of( intsAndStrings,    0,              null,          null,                             0 ),
                Arguments.of( intsAndStrings,    1,              multiply,      null,                             8 ),
                Arguments.of( intsAndLongs,      5L,             sum,           null,                             62L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <T, E, R> void foldLeft_testCases(Map<T, E> sourceMap,
                                             R initialValue,
                                             TriFunction<R, ? super T, ?super E, R> accumulator,
                                             Class<? extends Exception> expectedException,
                                             R expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> foldLeft(sourceMap, initialValue, accumulator));
        } else {
            assertEquals(expectedResult, foldLeft(sourceMap, initialValue, accumulator));
        }
    }


    static Stream<Arguments> groupByNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        BiFunction<Integer, Long, Long> keyPlusValueMod3 = (k, v) -> (k + v) % 3;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(0,
                new HashMap<>() {{
                    put(2, "B");
                    put(4, "o");
                }}
            );
            put(1,
                new HashMap<>() {{
                    put(1, "A");
                }}
            );
        }};
        Map<Long, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(0L,
                new HashMap<>() {{
                    put(21, 9L);
                }}
            );
            put(1L,
                new HashMap<>() {{
                    put(7, 3L);
                }}
            );
            put(2L,
                new HashMap<>() {{
                    put(11, 6L);
                }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,      expectedResult
                Arguments.of( null,              null,               Map.of() ),
                Arguments.of( null,              keyMod2,            Map.of() ),
                Arguments.of( Map.of(),          null,               Map.of() ),
                Arguments.of( Map.of(),          keyMod2,            Map.of() ),
                Arguments.of( intsAndStrings,    keyMod2,            intsAndStringsResult ),
                Arguments.of( intsAndLongs,      keyPlusValueMod3,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByNoMapFactoryTestCases")
    @DisplayName("groupBy: without map factory test cases")
    public <T, E, R> void groupByNoMapFactory_testCases(Map<T, E> sourceMap,
                                                        BiFunction<? super T, ? super E, ? extends R> discriminator,
                                                        Map<R, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, groupBy(sourceMap, discriminator));
    }


    static Stream<Arguments> groupByAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(6, "Aa");
            put(8, "Bb");
            put(9, "Oo");
        }};
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(0,
                    new HashMap<>() {{
                        put(6, "Aa");
                        put(8, "Bb");
                    }}
            );
            put(1,
                    new HashMap<>() {{
                        put(9, "Oo");
                    }}
            );
        }};
        Map<Integer, Map<Integer, String>> intsAndStringsLinkedMapResult = new LinkedHashMap<>() {{
            put(0,
                    new LinkedHashMap<>() {{
                        put(6, "Aa");
                        put(8, "Bb");
                    }}
            );
            put(1,
                    new LinkedHashMap<>() {{
                        put(9, "Oo");
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,    mapResultFactory,    mapValuesFactory,    expectedResult
                Arguments.of( null,              null,             null,                null,                Map.of() ),
                Arguments.of( null,              keyMod2,          null,                null,                Map.of() ),
                Arguments.of( null,              keyMod2,          linkedMapSupplier,   null,                new LinkedHashMap<>() ),
                Arguments.of( null,              keyMod2,          null,                linkedMapSupplier,   Map.of() ),
                Arguments.of( null,              keyMod2,          linkedMapSupplier,   linkedMapSupplier,   new LinkedHashMap<>() ),
                Arguments.of( Map.of(),          null,             null,                null,                Map.of() ),
                Arguments.of( Map.of(),          keyMod2,          null,                null,                Map.of() ),
                Arguments.of( Map.of(),          keyMod2,          linkedMapSupplier,   null,                new LinkedHashMap<>() ),
                Arguments.of( Map.of(),          keyMod2,          null,                linkedMapSupplier,   Map.of() ),
                Arguments.of( Map.of(),          keyMod2,          linkedMapSupplier,   linkedMapSupplier,   new LinkedHashMap<>() ),
                Arguments.of( intsAndStrings,    keyMod2,          null,                null,                intsAndStringsResult ),
                Arguments.of( intsAndStrings,    keyMod2,          linkedMapSupplier,   linkedMapSupplier,   intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }


    static Stream<Arguments> getOrElseTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        Supplier<Integer> always25 = () -> 25;
        return Stream.of(
                //@formatter:off
                //            sourceMap,     key,    defaultValue,   expectedException,                expectedResult
                Arguments.of( null,          null,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( null,          2,      null,           IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),      2,      null,           IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),      null,   always25,       null,                             always25.get() ),
                Arguments.of( Map.of(),      1,      always25,       null,                             always25.get() ),
                Arguments.of( integersMap,   1,      always25,       null,                             21 ),
                Arguments.of( integersMap,   null,   always25,       null,                             always25.get() ),
                Arguments.of( integersMap,   2,      always25,       null,                             always25.get() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseTestCases")
    @DisplayName("getOrElse: test cases")
    public <T, E> void getOrElse_testCases(Map<? extends T, ? extends E> sourceMap,
                                           T key,
                                           Supplier<E> defaultValue,
                                           Class<? extends Exception> expectedException,
                                           E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> getOrElse(sourceMap, key, defaultValue));
        } else {
            assertEquals(expectedResult, getOrElse(sourceMap, key, defaultValue));
        }
    }


    @ParameterizedTest
    @MethodSource("groupByAllParametersTestCases")
    @DisplayName("groupBy: with all parameters test cases")
    public <T, E, R> void groupByAllParameters_testCases(Map<T, E> sourceMap,
                                                         BiFunction<? super T, ? super E, ? extends R> discriminator,
                                                         Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                         Supplier<Map<T, E>> mapValuesFactory,
                                                         Map<R, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, groupBy(sourceMap, discriminator, mapResultFactory, mapValuesFactory));
    }


    static Stream<Arguments> groupMapNoCollectionFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "Hi");
            put(2, "Hello");
            put(5, "World");
            put(6, "!");
        }};
        BiFunction<Integer, String, Integer> keyMod3 = (k, v) -> k % 3;
        BiFunction<Integer, String, Integer> valueLength = (k, v) -> v.length();
        Map<Integer, List<Integer>> usingKeyMod3AsDiscriminatorKey = new HashMap<>() {{
            put(0, List.of(1));
            put(1, List.of(2));
            put(2, List.of(5, 5));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminatorKey,   valueMapper,   expectedException,                expectedResult
                Arguments.of( null,             null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod3,            null,          IllegalArgumentException.class,   null ),
                Arguments.of( null,             keyMod3,            valueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod3,            valueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   keyMod3,            valueLength,   null,                             usingKeyMod3AsDiscriminatorKey )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapNoCollectionFactoryTestCases")
    @DisplayName("groupMap: without collection factory test cases")
    public <T, E, R, V> void groupMapNoCollectionFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                   BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                                   BiFunction<? super T, ? super E, ? extends V> valueMapper,
                                                                   Class<? extends Exception> expectedException,
                                                                   Map<R, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapAllParametersTestCases() {
        Map<String, Integer> stringsAndIntegers = new LinkedHashMap<>() {{
            put("A", 10);
            put("BY", 20);
            put("C", 30);
            put("DH", 40);
        }};
        BiFunction<String, Integer, Integer> keyLength = (k, v) -> k.length();
        BiFunction<String, Integer, Integer> valuePlus5 = (k, v) -> v + 5;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<Integer>> resultWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
        }};
        Map<Integer, Set<Integer>> resultWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of(15, 35)));
            put(2, new LinkedHashSet<>(List.of(25, 45)));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,            discriminatorKey,   valueMapper,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,                 null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),             null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keyLength,          null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,                 keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   keyLength,          valuePlus5,    null,                null,                             resultWithDefaultCollectionFactory ),
                Arguments.of( stringsAndIntegers,   keyLength,          valuePlus5,    setSupplier,         null,                             resultWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapAllParametersTestCases")
    @DisplayName("groupMap: with all parameters test cases")
    public <T, E, R, V> void groupMapAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                             BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                             BiFunction<? super T, ? super E, ? extends V> valueMapper,
                                                             Supplier<Collection<V>> collectionFactory,
                                                             Class<? extends Exception> expectedException,
                                                             Map<R, Collection<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, discriminatorKey, valueMapper, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, discriminatorKey, valueMapper, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapReduceTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "AB");
            put(2, "BCD");
            put(3, "Z3");
            put(4, "oPQRT");
        }};
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        BiFunction<Integer, String, Integer> valueLength = (k, v) -> v.length();
        BinaryOperator<Integer> multiplyAll = (i1, i2) -> i1 * i2;
        Map<Integer, Integer> expectedResult = new HashMap<>() {{
            put(0, 15);
            put(1, 4);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminatorKey,   valueMapper,   reduceValues,   expectedException,                expectedResult
                Arguments.of( null,             null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod2,            null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod2,            valueLength,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( null,             keyMod2,            valueLength,   multiplyAll,    null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod2,            valueLength,   multiplyAll,    null,                             Map.of() ),
                Arguments.of( intsAndStrings,   keyMod2,            valueLength,   multiplyAll,    null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceTestCases")
    @DisplayName("groupMapReduce: test cases")
    public <T, E, R, V> void groupMapReduce_testCases(Map<? extends T, ? extends E> sourceMap,
                                                      BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                      BiFunction<? super T, ? super E, V> valueMapper,
                                                      BinaryOperator<V> reduceValues,
                                                      Class<? extends Exception> expectedException,
                                                      Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapReduce(sourceMap, discriminatorKey, valueMapper, reduceValues));
        } else {
            assertEquals(expectedResult, groupMapReduce(sourceMap, discriminatorKey, valueMapper, reduceValues));
        }
    }


    static Stream<Arguments> mapNoMapFactoryTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, Tuple2<String, String>> add1AndMultiply2AndConvertToString =
                (k, v) -> Tuple.of(String.valueOf(k + 1), String.valueOf(v * 2));

        Map<String, String> stringsMapResult = new HashMap<>() {{
            put("2", "42");
            put("5", "86");
            put("10", "202");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                                 IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                                 null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                                 null,                             Map.of() ),
                Arguments.of( null,              add1AndMultiply2AndConvertToString,   null,                             Map.of() ),
                Arguments.of( Map.of(),          add1AndMultiply2AndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   null,                             stringsMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapNoMapFactoryTestCases")
    @DisplayName("map: without map factory test cases")
    public <T, E, R, V> void mapNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction,
                                                       Class<? extends Exception> expectedException,
                                                       Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceMap, mapFunction));
        } else {
            assertEquals(expectedResult, map(sourceMap, mapFunction));
        }
    }


    static Stream<Arguments> mapAllParametersTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, Tuple2<String, String>> add1AndMultiply2AndConvertToString =
                (k, v) -> Tuple.of(String.valueOf(k + 1), String.valueOf(v * 2));

        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;
        Map<String, String> stringsMapResult = new HashMap<>() {{
            put("2", "42");
            put("5", "86");
            put("10", "202");
        }};
        LinkedHashMap<String, String> stringsLinkedMapResult = new LinkedHashMap<>(stringsMapResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                          mapFactory,          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                                 null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                                 null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                                 null,                null,                             Map.of() ),
                Arguments.of( null,              add1AndMultiply2AndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          add1AndMultiply2AndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   null,                null,                             stringsMapResult ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   linkedMapSupplier,   null,                             stringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapAllParametersTestCases")
    @DisplayName("map: with all parameters test cases")
    public <T, E, R, V> void mapAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction,
                                                        Supplier<Map<R, V>> mapFactory,
                                                        Class<? extends Exception> expectedException,
                                                        Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceMap, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, map(sourceMap, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> mapValuesNoMapFactoryTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, String> add1ToValueAndConvertToString = (k, v) -> String.valueOf(v + 1);

        Map<Integer, String> integersMapResult = new HashMap<>() {{
            put(1, "22");
            put(4, "44");
            put(9, "102");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                     expectedException,                expectedResult
                Arguments.of( integersMap,       null,                            IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                            null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                            null,                             Map.of() ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                             integersMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapValuesNoMapFactoryTestCases")
    @DisplayName("mapValues: without map factory test cases")
    public <T, E, R> void mapValuesNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                          BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                          Class<? extends Exception> expectedException,
                                                          Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> mapValues(sourceMap, mapFunction));
        } else {
            assertEquals(expectedResult, mapValues(sourceMap, mapFunction));
        }
    }


    static Stream<Arguments> mapValuesAllParametersTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, String> add1ToValueAndConvertToString = (k, v) -> String.valueOf(v + 1);

        Supplier<Map<Integer, String>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> integersMapResult = new HashMap<>() {{
            put(1, "22");
            put(4, "44");
            put(9, "102");
        }};
        LinkedHashMap<Integer, String> integersLinkedMapResult = new LinkedHashMap<>(integersMapResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                     mapFactory,          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                            null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                            null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                            null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                null,                             integersMapResult ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   linkedMapSupplier,   null,                             integersLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapValuesAllParametersTestCases")
    @DisplayName("mapValues: with all parameters test cases")
    public <T, E, R> void mapValuesAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                           BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                           Supplier<Map<T, R>> mapFactory,
                                                           Class<? extends Exception> expectedException,
                                                           Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> mapValues(sourceMap, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, mapValues(sourceMap, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> maxTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<Tuple2<Integer, String>> comparatorOnlyKeys = Comparator.comparing(t -> t._1);
        Comparator<Tuple2<Integer, String>> comparatorOnlyValues = Comparator.comparing(t -> t._2);
        Comparator<Tuple2<Integer, String>> comparatorBoth = (t1, t2) -> {
            int valueComparison = t1._2.compareTo(t2._2);
            return 0 != valueComparison
                   ? t1._1.compareTo(t2._1)
                   : valueComparison;
        };
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,             expectedException,                expectedResult
                Arguments.of( null,              null,                   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                   IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorOnlyKeys,     null,                             of(Tuple2.of(4, "UTf")) ),
                Arguments.of( intsAndStrings,    comparatorOnlyValues,   null,                             of(Tuple2.of(2, "Z3")) ),
                Arguments.of( intsAndStrings,    comparatorBoth,         null,                             of(Tuple2.of(4, "UTf")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxTestCases")
    @DisplayName("max: test cases")
    public <T, E> void max_testCases(Map<? extends T, ? extends E> sourceMap,
                                     Comparator<Tuple2<? extends T, ? extends E>> comparator,
                                     Class<? extends Exception> expectedException,
                                     Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> max(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, max(sourceMap, comparator));
        }
    }


    static Stream<Arguments> maxValueTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<String> comparatorNatural = String::compareTo;
        Comparator<String> comparatorReverse = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,          expectedException,                expectedResult
                Arguments.of( null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorNatural,   null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorNatural,   null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorNatural,   null,                             of("Z3") ),
                Arguments.of( intsAndStrings,    comparatorReverse,   null,                             of("AB") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxValueTestCases")
    @DisplayName("maxValue: test cases")
    public <T, E> void maxValue_testCases(Map<? extends T, ? extends E> sourceMap,
                                          Comparator<? super E> comparator,
                                          Class<? extends Exception> expectedException,
                                          Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> maxValue(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, maxValue(sourceMap, comparator));
        }
    }


    static Stream<Arguments> minTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "TT");
            put(2, "BA");
            put(3, "Urf");
            put(4, "BA");
        }};
        Comparator<Tuple2<Integer, String>> comparatorOnlyKeys = Comparator.comparing(t -> t._1);
        Comparator<Tuple2<Integer, String>> comparatorOnlyValues = Comparator.comparing(t -> t._2);
        Comparator<Tuple2<Integer, String>> comparatorBoth = (t1, t2) -> {
            int valueComparison = t1._2.compareTo(t2._2);
            return 0 != valueComparison
                    ? t1._1.compareTo(t2._1)
                    : valueComparison;
        };
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,             expectedException,                expectedResult
                Arguments.of( null,              null,                   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                   IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorOnlyKeys,     null,                             of(Tuple2.of(1, "TT")) ),
                Arguments.of( intsAndStrings,    comparatorOnlyValues,   null,                             of(Tuple2.of(2, "BA")) ),
                Arguments.of( intsAndStrings,    comparatorBoth,         null,                             of(Tuple2.of(1, "TT")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minTestCases")
    @DisplayName("min: test cases")
    public <T, E> void min_testCases(Map<? extends T, ? extends E> sourceMap,
                                     Comparator<Tuple2<? extends T, ? extends E>> comparator,
                                     Class<? extends Exception> expectedException,
                                     Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> min(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, min(sourceMap, comparator));
        }
    }


    static Stream<Arguments> minValueTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<String> comparatorNatural = String::compareTo;
        Comparator<String> comparatorReverse = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,          expectedException,                expectedResult
                Arguments.of( null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorNatural,   null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorNatural,   null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorNatural,   null,                             of("AB") ),
                Arguments.of( intsAndStrings,    comparatorReverse,   null,                             of("Z3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minValueTestCases")
    @DisplayName("mainValue: test cases")
    public <T, E> void minValue_testCases(Map<? extends T, ? extends E> sourceMap,
                                          Comparator<? super E> comparator,
                                          Class<? extends Exception> expectedException,
                                          Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> minValue(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, minValue(sourceMap, comparator));
        }
    }


    static Stream<Arguments> partitionNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};
        BiPredicate<Integer, String> isKeyEven = (k, v) -> k % 2 == 0;
        BiPredicate<Integer, Long> isKeyPlusValueOdd = (k, v) -> (k + v) % 2 == 1;

        Map<Boolean, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(7, 3L);
                        put(21, 9L);
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> emptyMap = new HashMap<>() {{
            put(Boolean.TRUE, new HashMap<>());
            put(Boolean.FALSE, new HashMap<>());
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,       expectedResult
                Arguments.of( null,              null,                emptyMap ),
                Arguments.of( null,              isKeyEven,           emptyMap ),
                Arguments.of( Map.of(),          null,                emptyMap ),
                Arguments.of( Map.of(),          isKeyEven,           emptyMap ),
                Arguments.of( intsAndStrings,    isKeyEven,           intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueOdd,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("partitionNoMapFactoryTestCases")
    @DisplayName("partition: without map factory test cases")
    public <T, E> void partitionNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> discriminator,
                                                       Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator));
    }


    static Stream<Arguments> partitionAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "FG");
            put(3, "HY");
            put(5, "ou");
        }};
        BiPredicate<Integer, String> isKeyMod3 = (k, v) -> k % 3 == 0;
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;

        Map<Boolean, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(3, "HY");
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "FG");
                        put(5, "ou");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, String>> intsAndStringsLinkedMapResult = new LinkedHashMap<>() {{
            put(Boolean.TRUE,
                    new LinkedHashMap<>() {{
                        put(3, "HY");
                    }}
            );
            put(Boolean.FALSE,
                    new LinkedHashMap<>() {{
                        put(1, "FG");
                        put(5, "ou");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> emptyHashMap = new HashMap<>() {{
            put(Boolean.TRUE, new HashMap<>());
            put(Boolean.FALSE, new HashMap<>());
        }};
        Map<Boolean, Map<Integer, Long>> emptyLinkedHashMap = new LinkedHashMap<>() {{
            put(Boolean.TRUE, new LinkedHashMap<>());
            put(Boolean.FALSE, new LinkedHashMap<>());
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminator,   mapFactory,          expectedResult
                Arguments.of( null,             null,            null,                emptyHashMap ),
                Arguments.of( null,             isKeyMod3,       null,                emptyHashMap ),
                Arguments.of( Map.of(),         null,            null,                emptyHashMap ),
                Arguments.of( Map.of(),         isKeyMod3,       null,                emptyHashMap ),
                Arguments.of( null,             null,            linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( null,             isKeyMod3,       linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( Map.of(),         null,            linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( Map.of(),         isKeyMod3,       linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( intsAndStrings,   isKeyMod3,       null,                intsAndStringsResult ),
                Arguments.of( intsAndStrings,   isKeyMod3,       linkedMapSupplier,   intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("partitionAllParametersTestCases")
    @DisplayName("partition: with all parameters test cases")
    public <T, E> void partitionAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> discriminator,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator, mapFactory));
    }


    static Stream<Arguments> sliceTestCases() {
        Map<String, Integer> sourceMap = new LinkedHashMap<>() {{
            put("A", 1);
            put("B", 2);
            put("C", 3);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   from,   to,                 expectedException,                expectedResult
                Arguments.of( null,        2,      1,                  IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),    3,      1,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceMap,   1,      0,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,        0,      1,                  null,                             Map.of() ),
                Arguments.of( Map.of(),    0,      1,                  null,                             Map.of() ),
                Arguments.of( Map.of(),   -1,      0,                  null,                             Map.of() ),
                Arguments.of( Map.of(),    3,      4,                  null,                             Map.of() ),
                Arguments.of( sourceMap,  -1,      2,                  null,                             Map.of("A", 1, "B", 2) ),
                Arguments.of( sourceMap,   1,      3,                  null,                             Map.of("B", 2, "C", 3) ),
                Arguments.of( sourceMap,   0,      sourceMap.size(),   null,                             sourceMap )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sliceTestCases")
    @DisplayName("slice: test cases")
    public <T, E> void slice_testCases(Map<T, E> sourceMap,
                                       int from,
                                       int until,
                                       Class<? extends Exception> expectedException,
                                       Map<T, E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> slice(sourceMap, from, until));
        } else {
            assertEquals(expectedResult, slice(sourceMap, from, until));
        }
    }


    static Stream<Arguments> slidingTestCases() {
        Map<Integer, String> sourceMap = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "D");
            put(5, "F");
        }};
        List<Map<Integer, String>> size1Result = List.of(
                Map.of(1, "A"),
                Map.of(2, "B"),
                Map.of(3, "C"),
                Map.of(4, "D"),
                Map.of(5, "F")
        );
        List<Map<Integer, String>> size2Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                }},
                new LinkedHashMap<>() {{
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(4, "D");
                    put(5, "F");
                }}
        );
        List<Map<Integer, String>> size3Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                    put(3, "C");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(3, "C");
                    put(4, "D");
                    put(5, "F");
                }}
        );
        List<Map<Integer, String>> size4Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                    put(5, "F");
                }}
        );
        return Stream.of(
                //@formatter:off
                //            sourceMap,   size,                   expectedException,                expectedResult
                Arguments.of( null,       -1,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,        5,                      null,                             List.of() ),
                Arguments.of( sourceMap,   0,                      null,                             List.of() ),
                Arguments.of( sourceMap,   sourceMap.size(),       null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   sourceMap.size() + 1,   null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   1,                      null,                             size1Result ),
                Arguments.of( sourceMap,   2,                      null,                             size2Result ),
                Arguments.of( sourceMap,   3,                      null,                             size3Result ),
                Arguments.of( sourceMap,   4,                      null,                             size4Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T, E> void sliding_testCases(Map<T, E> sourceMap,
                                         int size,
                                         Class<? extends Exception> expectedException,
                                         List<Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> sliding(sourceMap, size));
        }
        else {
            assertEquals(expectedResult, sliding(sourceMap, size));
        }
    }


    static Stream<Arguments> splitTestCases() {
        Map<String, Integer> sourceMap = new LinkedHashMap<>() {{
            put("A", 1);
            put("B", 2);
            put("C", 3);
            put("D", 4);
        }};
        List<Map<String, Integer>> size2Result = List.of(
                new LinkedHashMap<>() {{
                    put("A", 1);
                    put("B", 2);
                }},
                new LinkedHashMap<>() {{
                    put("C", 3);
                    put("D", 4);
                }}
        );
        List<Map<String, Integer>> size3Result = List.of(
                new LinkedHashMap<>() {{
                    put("A", 1);
                    put("B", 2);
                    put("C", 3);
                }},
                new LinkedHashMap<>() {{
                    put("D", 4);
                }}
        );
        return Stream.of(
                //@formatter:off
                //            sourceMap,   size,                   expectedException,                expectedResult
                Arguments.of( null,       -1,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,        5,                      null,                             List.of() ),
                Arguments.of( sourceMap,   0,                      null,                             List.of() ),
                Arguments.of( sourceMap,   sourceMap.size(),       null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   sourceMap.size() + 1,   null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   2,                      null,                             size2Result ),
                Arguments.of( sourceMap,   3,                      null,                             size3Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T, E> void split_testCases(Map<T, E> sourceMap,
                                       int size,
                                       Class<? extends Exception> expectedException,
                                       List<Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> split(sourceMap, size));
        }
        else {
            assertEquals(expectedResult, split(sourceMap, size));
        }
    }


    static Stream<Arguments> takeWhileNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(4, "o");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileNoMapFactoryTestCases")
    @DisplayName("takeWhile: without map factory test cases")
    public <T, E> void takeWhileNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> filterPredicate,
                                                       Map<T, E> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceMap, filterPredicate));
    }


    static Stream<Arguments> takeWhileAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(4, "o");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileAllParametersTestCases")
    @DisplayName("takeWhile: with all parameters test cases")
    public <T, E> void takeWhileAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<T, E> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceMap, filterPredicate, mapFactory));
    }

}
