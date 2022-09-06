package com.spring5microservices.common.util.either;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LeftTest {

    @Test
    @DisplayName("empty: when is invoked then Left with no internal value is returned")
    public void empty_whenIsInvoked_thenLeftWithNoInternalValueIsReturned() {
        Left<String, Integer> emptyInstance = Left.empty();

        assertNotNull(emptyInstance);
        assertNull(emptyInstance.getLeft());
    }


    static Stream<Arguments> ofTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedException,                expectedResult
                Arguments.of( null,    IllegalArgumentException.class,   null ),
                Arguments.of( 1,       null,                             Left.of(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <E, T> void of_testCases(T value,
                                    Class<? extends Exception> expectedException,
                                    Left<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Left.of(value));
        } else {
            assertEquals(expectedResult, Left.of(value));
        }
    }


    static Stream<Arguments> ofNullableTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Left.empty() ),
                Arguments.of( "12",    Left.of("12") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofNullableTestCases")
    @DisplayName("ofNullable: test cases")
    public <E, T> void ofNullable_testCases(T value,
                                            Left<E, T> expectedResult) {
        assertEquals(expectedResult, Left.ofNullable(value));
    }


    @Test
    @DisplayName("isRight: when is invoked then false is returned")
    public void isRight_whenIsInvoked_thenFalseIsReturned() {
        Left<String, Integer> instance = Left.of("test value");

        assertFalse(instance.isRight());
    }


    @Test
    @DisplayName("get: when is invoked then NoSuchElementException is thrown")
    public void get_whenIsInvoked_thenNoSuchElementExceptionIsThrown() {
        Left<String, Integer> instance = Left.of("There was a problem");

        assertThrows(NoSuchElementException.class, instance::get);
    }


    static Stream<Arguments> getLeftTestCases() {
        String error = "There was an error";
        return Stream.of(
                //@formatter:off
                //            value,            expectedResult
                Arguments.of( Left.empty(),     null ),
                Arguments.of( Left.of(error),   error )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getLeftTestCases")
    @DisplayName("getLeft: test cases")
    public <E, T> void getLeft_testCases(Left<E, T> left,
                                         E expectedResult) {
        assertEquals(expectedResult, left.getLeft());
    }


    static Stream<Arguments> equalsTestCases() {
        Left<String, Integer> emptyInstance = Left.empty();
        Left<String, Integer> l1 = Left.of("There was an error");
        Left<Long, Integer> l2 = Left.of(21L);
        Left<String, Integer> l3 = Left.of("There was an error");
        return Stream.of(
                //@formatter:off
                //            left,   objectToCompare,   expectedResult
                Arguments.of( l1,     null,              false ),
                Arguments.of( l1,     emptyInstance,     false ),
                Arguments.of( l1,     12,                false ),
                Arguments.of( l2,     l2.getLeft(),      false ),
                Arguments.of( l1,     l2,                false ),
                Arguments.of( l1,     l1,                true ),
                Arguments.of( l2,     l2,                true ),
                Arguments.of( l1,     l3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <E, T> void equals_testCases(Left<E, T> left,
                                        Object objectToCompare,
                                        boolean expectedResult) {
        assertEquals(expectedResult, left.equals(objectToCompare));
    }

}
