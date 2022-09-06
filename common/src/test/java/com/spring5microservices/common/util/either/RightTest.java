package com.spring5microservices.common.util.either;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RightTest {

    @Test
    @DisplayName("empty: when is invoked then Right with no internal value is returned")
    public void empty_whenIsInvoked_thenRightWithNoInternalValueIsReturned() {
        Right<String, Integer> emptyInstance = Right.empty();

        assertNotNull(emptyInstance);
        assertNull(emptyInstance.get());
    }


    static Stream<Arguments> ofTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedException,                expectedResult
                Arguments.of( null,    IllegalArgumentException.class,   null ),
                Arguments.of( 1,       null,                             Right.of(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <E, T> void of_testCases(T value,
                                    Class<? extends Exception> expectedException,
                                    Right<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Right.of(value));
        } else {
            assertEquals(expectedResult, Right.of(value));
        }
    }


    static Stream<Arguments> ofNullableTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Right.empty() ),
                Arguments.of( "12",    Right.of("12") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofNullableTestCases")
    @DisplayName("ofNullable: test cases")
    public <E, T> void ofNullable_testCases(T value,
                                            Right<E, T> expectedResult) {
        assertEquals(expectedResult, Right.ofNullable(value));
    }


    @Test
    @DisplayName("isRight: when is invoked then true is returned")
    public void isRight_whenIsInvoked_thenTrueIsReturned() {
        Right<String, Integer> instance = Right.of(33);

        assertTrue(instance.isRight());
    }


    static Stream<Arguments> getTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,           expectedResult
                Arguments.of( Right.empty(),   null ),
                Arguments.of( Right.of(1),     1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public <E, T> void get_testCases(Right<E, T> right,
                                     T expectedResult) {
        assertEquals(expectedResult, right.get());
    }


    @Test
    @DisplayName("getLeft: when is invoked then NoSuchElementException is thrown")
    public void getLeft_whenIsInvoked_thenNoSuchElementExceptionIsThrown() {
        Right<String, Integer> instance = Right.of(11);

        assertThrows(NoSuchElementException.class, instance::getLeft);
    }


    static Stream<Arguments> equalsTestCases() {
        Right<String, Integer> emptyInstance = Right.empty();
        Right<String, Integer> r1 = Right.of(12);
        Right<String, Long> r2 = Right.of(21L);
        Right<String, Integer> r3 = Right.of(12);
        return Stream.of(
                //@formatter:off
                //            right,   objectToCompare,   expectedResult
                Arguments.of( r1,      null,              false ),
                Arguments.of( r1,      emptyInstance,     false ),
                Arguments.of( r1,      12,                false ),
                Arguments.of( r2,      r2.get(),          false ),
                Arguments.of( r1,      r2,                false ),
                Arguments.of( r1,      r1,                true ),
                Arguments.of( r2,      r2,                true ),
                Arguments.of( r1,      r3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <E, T> void equals_testCases(Right<E, T> right,
                                        Object objectToCompare,
                                        boolean expectedResult) {
        assertEquals(expectedResult, right.equals(objectToCompare));
    }

}
