package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.PredicateUtil.allOf;
import static com.spring5microservices.common.util.PredicateUtil.alwaysFalse;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.anyOf;
import static com.spring5microservices.common.util.PredicateUtil.biAllOf;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysFalse;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.biAnyOf;
import static com.spring5microservices.common.util.PredicateUtil.distinctByKey;
import static com.spring5microservices.common.util.PredicateUtil.fromBiPredicateToMapEntryPredicate;
import static com.spring5microservices.common.util.PredicateUtil.getOrAlwaysFalse;
import static com.spring5microservices.common.util.PredicateUtil.getOrAlwaysTrue;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicateUtilTest {

    static Stream<Arguments> allOfTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto marguerita = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 15D);
        Predicate<PizzaDto> nameLongerThan5 = p -> 5 < p.getName().length();
        Predicate<PizzaDto> costMoreExpenseThan10 = p -> 0 < p.getCost().compareTo(10d);
        return Stream.of(
                //@formatter:off
                //            t,            predicate1,        predicate2,              expectedResult
                Arguments.of( carbonara,    null,              null,                    true ),
                Arguments.of( carbonara,    null,              costMoreExpenseThan10,   false ),
                Arguments.of( carbonara,    nameLongerThan5,   null,                    true ),
                Arguments.of( carbonara,    nameLongerThan5,   costMoreExpenseThan10,   false ),
                Arguments.of( marguerita,   null,              costMoreExpenseThan10,   true ),
                Arguments.of( marguerita,   nameLongerThan5,   null,                    true ),
                Arguments.of( marguerita,   nameLongerThan5,   costMoreExpenseThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("allOfTestCases")
    @DisplayName("allOf: test cases")
    public <T> void allOf_testCases(T t,
                                    Predicate<? super T> predicate1,
                                    Predicate<? super T> predicate2,
                                    boolean expectedResult) {
        Predicate<T> finalPredicate =
                isNull(predicate1) && isNull(predicate2)
                        ? allOf()
                        : allOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t));
    }


    static Stream<Arguments> anyOfTestCases() {
        Predicate<Integer> isGreaterThanTen = i -> 10 < i;
        Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
        return Stream.of(
                //@formatter:off
                //            t,    predicate1,         predicate2,            expectedResult
                Arguments.of( 1,    null,               null,                  false ),
                Arguments.of( 1,    null,               isGreaterThanTwenty,   false ),
                Arguments.of( 1,    isGreaterThanTen,   null,                  false ),
                Arguments.of( 1,    isGreaterThanTen,   isGreaterThanTwenty,   false ),
                Arguments.of( 11,   isGreaterThanTen,   isGreaterThanTwenty,   true ),
                Arguments.of( 21,   isGreaterThanTen,   isGreaterThanTwenty,   true )

        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("anyOfTestCases")
    @DisplayName("anyOf: test cases")
    public <T> void anyOf_testCases(T t,
                                    Predicate<? super T> predicate1,
                                    Predicate<? super T> predicate2,
                                    boolean expectedResult) {
        Predicate<T> finalPredicate =
                isNull(predicate1) && isNull(predicate2)
                        ? anyOf()
                        : anyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t));
    }


    static Stream<Arguments> alwaysFalseTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t
                Arguments.of( nullInt ),
                Arguments.of( "noMatterString" ),
                Arguments.of( 12 ),
                Arguments.of( carbonara )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysFalseTestCases")
    @DisplayName("alwaysFalse: test cases")
    public <T> void alwaysFalse_testCases(T t) {
        assertFalse(alwaysFalse().test(t));
    }


    static Stream<Arguments> alwaysTrueTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t
                Arguments.of( nullInt ),
                Arguments.of( "noMatterString" ),
                Arguments.of( 12 ),
                Arguments.of( carbonara )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysTrueTestCases")
    @DisplayName("alwaysTrue: test cases")
    public <T> void alwaysTrue_testCases(T t) {
        assertTrue(alwaysTrue().test(t));
    }


    static Stream<Arguments> biAllOfTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        BiPredicate<Integer, String> isGreaterThanTwentyAndStringLongerThan5 = (i, s) -> (20 < i) && (5 < s.length());
        return Stream.of(
                //@formatter:off
                //            t,    e,          predicate1,                                    predicate2,                                expectedResult
                Arguments.of( 1,    "s",        null,                                          null,                                      true ),
                Arguments.of( 1,    "s",        null,                                          isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   null,                                      false ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 11,   "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 21,   "abcdef",   isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAllOfTestCases")
    @DisplayName("biAllOf: test cases")
    public <T, E> void biAllOf_testCases(T t,
                                         E e,
                                         BiPredicate<? super T, ? super E> predicate1,
                                         BiPredicate<? super T, ? super E> predicate2,
                                         boolean expectedResult) {
        BiPredicate<? super T, ? super E> finalPredicate =
                isNull(predicate1) && isNull(predicate2)
                        ? biAllOf()
                        : biAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t, e));
    }


    static Stream<Arguments> biAnyOfTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        BiPredicate<Integer, String> isLowerThanTwentyAndStringShorterThan5 = (i, s) -> (20 > i) && (5 > s.length());
        return Stream.of(
                //@formatter:off
                //            t,    e,          predicate1,                                    predicate2,                               expectedResult
                Arguments.of( 1,    "s",        null,                                          null,                                     false ),
                Arguments.of( 1,    "s",        null,                                          isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   null,                                     false ),
                Arguments.of( 11,   "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 8,    "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 5,    "abcdef",   isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAnyOfTestCases")
    @DisplayName("biAnyOf: test cases")
    public <T, E> void biAnyOf_testCases(T t,
                                         E e,
                                         BiPredicate<? super T, ? super E> predicate1,
                                         BiPredicate<? super T, ? super E> predicate2,
                                         boolean expectedResult) {
        BiPredicate<? super T, ? super E> finalPredicate =
                isNull(predicate1) && isNull(predicate2)
                        ? biAnyOf()
                        : biAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t, e));
    }


    static Stream<Arguments> biAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            firstSourceInstance,   secondSourceInstance,   expectedResult
                Arguments.of( null,                  null,                   false ),
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
                Arguments.of( null,                  null,                   true ),
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
                .toList();
        assertEquals(expectedResult, distinctCollection);
    }


    static Stream<Arguments> fromBiPredicateToMapEntryPredicateTestCases() {
        Map.Entry<String, Integer> emptyEntry = new AbstractMap.SimpleEntry<>(
                null,
                null
        );
        BiPredicate<Integer, String> predicate =
                (i, s) ->
                        null != i &&
                                0 == i % 2;
        return Stream.of(
                //@formatter:off
                //            entry,              predicate,   expectedResult
                Arguments.of( null,               null,        true ),
                Arguments.of( null,               predicate,   true ),
                Arguments.of( emptyEntry,         null,        true ),
                Arguments.of( emptyEntry,         predicate,   false ),
                Arguments.of( Map.entry(1, ""),   predicate,   false ),
                Arguments.of( Map.entry(2, ""),   predicate,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromBiPredicateToMapEntryPredicateTestCases")
    @DisplayName("fromBiPredicateToMapEntryPredicate: test cases")
    public <K, V> void fromBiPredicateToMapEntryPredicate_testCases(Map.Entry<? super K, ? super V> entry,
                                                                    BiPredicate<? super K, ? super V> predicate,
                                                                    boolean expectedResult) {
        // Required because sometimes the Java compiler is stupid
        Predicate<Map.Entry<K, V>> predicateToApply = fromBiPredicateToMapEntryPredicate(
                predicate
        );
        assertEquals(expectedResult, predicateToApply.test((Map.Entry<K, V>) entry));
    }


    static Stream<Arguments> getOrAlwaysFalsePredicateTestCases() {
        Predicate<Integer> greaterThan10 = i -> 0 < i.compareTo(10);
        return Stream.of(
                //@formatter:off
                //            predicate,       t,      expectedResult
                Arguments.of( null,            null,   false ),
                Arguments.of( null,            12,     false ),
                Arguments.of( greaterThan10,   9,      false ),
                Arguments.of( greaterThan10,   12,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysFalsePredicateTestCases")
    @DisplayName("getOrAlwaysFalse: with Predicate test cases")
    public <T> void getOrAlwaysFalsePredicate_testCases(Predicate<? super T> predicate,
                                                        T t,
                                                        boolean expectedResult) {
        Predicate<T> predicateToApply = getOrAlwaysFalse(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t));
    }


    static Stream<Arguments> getOrAlwaysFalseBiPredicateTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        return Stream.of(
                //@formatter:off
                //            predicate,                                     t,      e,       expectedResult
                Arguments.of( null,                                          null,   null,    false ),
                Arguments.of( null,                                          11,     null,    false ),
                Arguments.of( null,                                          null,   "ab",    false ),
                Arguments.of( null,                                          9,      "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   9,      "abc",   false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "abc",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysFalseBiPredicateTestCases")
    @DisplayName("getOrAlwaysFalse: with BiPredicate test cases")
    public <T, E> void getOrAlwaysFalseBiPredicate_testCases(BiPredicate<? super T, ? super E> predicate,
                                                             T t,
                                                             E e,
                                                             boolean expectedResult) {
        BiPredicate<T, E> predicateToApply = getOrAlwaysFalse(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t, e));
    }


    static Stream<Arguments> getOrAlwaysTruePredicateTestCases() {
        Predicate<Integer> greaterThan10 = i -> 0 < i.compareTo(10);
        return Stream.of(
                //@formatter:off
                //            predicate,       t,      expectedResult
                Arguments.of( null,            null,   true ),
                Arguments.of( null,            12,     true ),
                Arguments.of( greaterThan10,   9,      false ),
                Arguments.of( greaterThan10,   12,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysTruePredicateTestCases")
    @DisplayName("getOrAlwaysTrue: with Predicate test cases")
    public <T> void getOrAlwaysTruePredicate_testCases(Predicate<? super T> predicate,
                                                       T t,
                                                       boolean expectedResult) {
        Predicate<T> predicateToApply = getOrAlwaysTrue(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t));
    }


    static Stream<Arguments> getOrAlwaysTrueBiPredicateTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        return Stream.of(
                //@formatter:off
                //            predicate,                                     t,      e,       expectedResult
                Arguments.of( null,                                          null,   null,    true ),
                Arguments.of( null,                                          11,     null,    true ),
                Arguments.of( null,                                          null,   "ab",    true ),
                Arguments.of( null,                                          9,      "ab",    true ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   9,      "abc",   false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "abc",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysTrueBiPredicateTestCases")
    @DisplayName("getOrAlwaysTrue: with BiPredicate test cases")
    public <T, E> void getOrAlwaysTrueBiPredicate_testCases(BiPredicate<? super T, ? super E> predicate,
                                                            T t,
                                                            E e,
                                                            boolean expectedResult) {
        BiPredicate<T, E> predicateToApply = getOrAlwaysTrue(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t, e));
    }

}
