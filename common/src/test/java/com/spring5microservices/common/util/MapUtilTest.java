package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.MapUtil.applyOrElse;
import static com.spring5microservices.common.util.MapUtil.collect;
import static com.spring5microservices.common.util.MapUtil.count;
import static com.spring5microservices.common.util.MapUtil.find;
import static com.spring5microservices.common.util.MapUtil.foldLeft;
import static com.spring5microservices.common.util.MapUtil.groupBy;
import static com.spring5microservices.common.util.MapUtil.map;
import static com.spring5microservices.common.util.MapUtil.mapValues;
import static com.spring5microservices.common.util.MapUtil.partition;
import static com.spring5microservices.common.util.MapUtil.removeKeys;
import static com.spring5microservices.common.util.MapUtil.slice;
import static com.spring5microservices.common.util.MapUtil.sliding;
import static com.spring5microservices.common.util.MapUtil.split;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapUtilTest {

    static Stream<Arguments> applyOrElseTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) -> k % 2 == 1 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());
        BiFunction<Integer, String, Long> sumKeyPlusValueLength = (k, v) -> (long) (k + v.length());

        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 4L);
            put(4, 5L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultFunction,               orElseFunction,          expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseTestCases")
    @DisplayName("applyOrElse: test cases")
    public <T, E, R> void applyOrElse_testCases(Map<? extends T, ? extends E> sourceMap,
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
        }
        else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
    }


    static Stream<Arguments> collectTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());

        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectTestCases")
    @DisplayName("collect: test cases")
    public <T, E, R> void collect_testCases(Map<? extends T, ? extends E> sourceMap,
                                            BiPredicate<? super T, ? super E> filterPredicate,
                                            BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                            Class<? extends Exception> expectedException,
                                            Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction));
        }
        else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction));
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
        }
        else {
            assertEquals(expectedResult, foldLeft(sourceMap, initialValue, accumulator));
        }
    }


    static Stream<Arguments> groupByTestCases() {
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
        BiFunction<Integer, String, Integer> isKeyEven = (k, v) -> k % 2;
        BiFunction<Integer, Long, Long> isKeyPlusValueModThree = (k, v) -> (k + v) % 3;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(
                    0,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(
                    1,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Long, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(
                    0L,
                    new HashMap<>() {{
                        put(21, 9L);
                    }}
            );
            put(
                    1L,
                    new HashMap<>() {{
                        put(7, 3L);
                    }}
            );
            put(
                    2L,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,            expectedResult
                Arguments.of( null,              null,                     Map.of() ),
                Arguments.of( null,              isKeyEven,                Map.of() ),
                Arguments.of( Map.of(),          null,                     Map.of() ),
                Arguments.of( Map.of(),          isKeyEven,                Map.of() ),
                Arguments.of( intsAndStrings,    isKeyEven,                intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueModThree,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByTestCases")
    @DisplayName("groupBy: test cases")
    public <T, E, R> void groupBy_testCases(Map<T, E> sourceMap,
                                            BiFunction<? super T, ? super E, ? extends R> discriminator,
                                            Map<R, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, groupBy(sourceMap, discriminator));
    }


    static Stream<Arguments> partitionTestCases() {
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
            put(
                    Boolean.TRUE,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(
                    Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(
                    Boolean.TRUE,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
            put(
                    Boolean.FALSE,
                    new HashMap<>() {{
                        put(7, 3L);
                        put(21, 9L);
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,       expectedResult
                Arguments.of( null,              null,                Map.of() ),
                Arguments.of( null,              isKeyEven,           Map.of() ),
                Arguments.of( Map.of(),          null,                Map.of() ),
                Arguments.of( Map.of(),          isKeyEven,           Map.of() ),
                Arguments.of( intsAndStrings,    isKeyEven,           intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueOdd,   intsAndLongsResult )
        ); //@formatter:on
    }


    static Stream<Arguments> mapTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, Tuple2<String, String>> add1ToValueAndConvertToString =
                (k, v) -> Tuple.of(String.valueOf(k + 1), String.valueOf(v * 2));

        Map<String, String> integersMapResult = new HashMap<>() {{
            put("2", "42");
            put("5", "86");
            put("10", "202");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                     expectedException,                expectedResult
                Arguments.of( null,              null,                            IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),          null,                            IllegalArgumentException.class,   null ),
                Arguments.of( null,              add1ToValueAndConvertToString,   null,                             Map.of() ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                             integersMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public <T, E, R, V> void map_testCases(Map<? extends T, ? extends E> sourceMap,
                                           BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction,
                                           Class<? extends Exception> expectedException,
                                           Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceMap, mapFunction));
        }
        else {
            assertEquals(expectedResult, map(sourceMap, mapFunction));
        }
    }


    static Stream<Arguments> mapValuesTestCases() {
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
                Arguments.of( null,              null,                            IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),          null,                            IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                             integersMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapValuesTestCases")
    @DisplayName("mapValues: test cases")
    public <T, E, R> void mapValues_testCases(Map<? extends T, ? extends E> sourceMap,
                                              BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                              Class<? extends Exception> expectedException,
                                              Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> mapValues(sourceMap, mapFunction));
        }
        else {
            assertEquals(expectedResult, mapValues(sourceMap, mapFunction));
        }
    }


    @ParameterizedTest
    @MethodSource("partitionTestCases")
    @DisplayName("partition: test cases")
    public <T, E> void partition_testCases(Map<? extends T, ? extends E> sourceMap,
                                           BiPredicate<? super T, ? super E> discriminator,
                                           Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator));
    }


    static Stream<Arguments> removeKeysTestCases() {
        Map<String, Integer> sourceMap = new HashMap<>() {{
            put("A", 1);
            put("B", 2);
        }};
        Map<String, Integer> sourceMapFiltered = new HashMap<>() {{
            put("A", 1);
        }};
        List<String> keysToExcludeIncluded = List.of("B");
        List<String> keysToExcludeNotIncluded = List.of("C");
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
    public <T, E> void removeKeys_testCases(Map<T, E> sourceMap,
                                            Collection<T> keysToExclude,
                                            HashMap<T, E> expectedResult) {
        assertEquals(expectedResult, removeKeys(sourceMap, keysToExclude));
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
        }
        else {
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

}
