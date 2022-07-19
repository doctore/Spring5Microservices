package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LazyTest {

    static Stream<Arguments> ofTestCases() {
        Supplier<String> stringSupplier = () -> "ABC";
        Supplier<Integer> integerSupplier = () -> 11;
        return Stream.of(
                //@formatter:off
                //            supplier,          expectedException,              expectedResult
                Arguments.of( null,              NullPointerException.class,     null ),
                Arguments.of( stringSupplier,    null,                           Lazy.of(stringSupplier) ),
                Arguments.of( integerSupplier,   null,                           Lazy.of(integerSupplier) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T> void of_testCases(Supplier<? extends T> supplier,
                                 Class<? extends Exception> expectedException,
                                 Lazy<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Lazy.of(supplier));
        }
        else {
            Lazy<T> newLazy = Lazy.of(supplier);
            invokeInternalSupplier(newLazy);
            invokeInternalSupplier(expectedResult);
            assertEquals(expectedResult, newLazy);
        }
    }


    static Stream<Arguments> getTestCases() {
        Lazy<String> lazy1 = Lazy.of(() -> "ABC");
        Lazy<Integer> lazy2 = Lazy.of(() -> 11);
        return Stream.of(
                //@formatter:off
                //            lazy,    expectedResult
                Arguments.of( lazy1,   "ABC" ),
                Arguments.of( lazy2,   11 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public <T> void get_testCases(Lazy<? extends T> lazy,
                                  T expectedResult) {
        assertEquals(expectedResult, lazy.get());
    }


    static Stream<Arguments> getNoCachedTestCases() {
        Lazy<String> lazy1 = Lazy.of(() -> "zzD");
        Lazy<Integer> lazy2 = Lazy.of(() -> 23);
        return Stream.of(
                //@formatter:off
                //            lazy,    expectedResult
                Arguments.of( lazy1,   "zzD" ),
                Arguments.of( lazy2,   23 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getNoCachedTestCases")
    @DisplayName("getNoCached: test cases")
    public <T> void getNoCached_testCases(Lazy<? extends T> lazy,
                                          T expectedResult) {
        assertEquals(expectedResult, lazy.getNoCached());
    }


    static Stream<Arguments> equalsTestCases() {
        Lazy<String> lazy1 = Lazy.of(() -> "ABC");
        Lazy<String> lazySameSupplierThan1 = Lazy.of(() -> "ABC");
        Lazy<Integer> lazy2 = Lazy.of(() -> 11);
        lazy2.get();
        return Stream.of(
                //@formatter:off
                //            lazy,    objectToCompare,         expectedResult
                Arguments.of( lazy1,   "ABC",                   false ),
                Arguments.of( lazy1,   lazy1,                   true ),
                Arguments.of( lazy1,   lazy2,                   false ),
                Arguments.of( lazy1,   lazySameSupplierThan1,   false ),
                Arguments.of( lazy2,   lazy2,                   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T> void equals_testCases(Lazy<T> lazy,
                                     Object objectToCompare,
                                     boolean expectedResult) {
        assertEquals(expectedResult, lazy.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal supplier is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalSupplierIsReturned() {
        Supplier<String> stringSupplier = () -> "ABC";
        Lazy<String> lazy1 = Lazy.of(stringSupplier);
        int expectedHashCode = Objects.hashCode(stringSupplier);

        assertEquals(expectedHashCode, lazy1.hashCode());
    }


    static Stream<Arguments> filterTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Predicate<String> doesNotVerifyPredicate = s -> 2 == s.length();
        Predicate<String> verifyPredicate = s -> 3 == s.length();
        return Stream.of(
                //@formatter:off
                //            lazy,   predicate,                expectedException,            expectedResult
                Arguments.of( lazy,   null,                     NullPointerException.class,   null ),
                Arguments.of( lazy,   doesNotVerifyPredicate,   null,                         empty() ),
                Arguments.of( lazy,   verifyPredicate,          null,                         Optional.of("ABC") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <T> void filter_testCases(Lazy<? extends T> lazy,
                                     Predicate<? super T> predicate,
                                     Class<? extends Exception> expectedException,
                                     Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> lazy.filter(predicate));
        }
        else {
            assertEquals(expectedResult, lazy.filter(predicate));
        }
    }


    static Stream<Arguments> isEvaluatedTestCases() {
        Lazy<String> lazyNotEvalualed = Lazy.of(() -> "ABC");
        Lazy<Integer> lazyEvalualed = Lazy.of(() -> 123);
        lazyEvalualed.get();
        return Stream.of(
                //@formatter:off
                //            lazy,               expectedResult
                Arguments.of( lazyNotEvalualed,   false ),
                Arguments.of( lazyEvalualed,      true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEvaluatedTestCases")
    @DisplayName("isEvaluated: test cases")
    public <T> void isEvaluated_testCases(Lazy<T> lazy,
                                          boolean expectedResult) {
        assertEquals(expectedResult, lazy.isEvaluated());
    }


    static Stream<Arguments> mapTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Lazy<Long> mappedLazy = Lazy.of(() -> 6L);
        mappedLazy.get();
        return Stream.of(
                //@formatter:off
                //            lazy,   mapper,                expectedException,            expectedResult
                Arguments.of( lazy,   null,                  NullPointerException.class,   null ),
                Arguments.of( lazy,   Function.identity(),   null,                         lazy ),
                Arguments.of( lazy,   fromStringToLong,      null,                         mappedLazy )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public <T, U> void map_testCases(Lazy<? extends T> lazy,
                                     Function<? super T, ? extends U> mapper,
                                     Class<? extends Exception> expectedException,
                                     Lazy<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> lazy.map(mapper));
        }
        else {
            Lazy<U> mappedLazy = lazy.map(mapper);
            invokeInternalSupplier(mappedLazy);
            assertEquals(expectedResult, mappedLazy);
        }
    }


    static Stream<Arguments> peekTestCases() {
        Lazy<String> lazy = Lazy.of(() -> "ABC");
        Consumer<String> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            lazy,   action,   expectedException,            expectedResult
                Arguments.of( lazy,   null,     NullPointerException.class,   null ),
                Arguments.of( lazy,   action,   null,                         lazy )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekTestCases")
    @DisplayName("peek: test cases")
    public <T> void peek_testCases(Lazy<? extends T> lazy,
                                   Consumer<? super T> action,
                                   Class<? extends Exception> expectedException,
                                   Lazy<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> lazy.peek(action));
        }
        else {
            assertEquals(expectedResult, lazy.peek(action));
        }
    }


    private <T> void invokeInternalSupplier(Lazy<T> lazyToInvoke) {
        lazyToInvoke.get();
    }

}
