package com.spring5microservices.common.util.Try;

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

public class SuccessTest {

    @Test
    @DisplayName("empty: when is invoked then Success with no internal value is returned")
    public void empty_whenIsInvoked_thenSuccessWithNoInternalValueIsReturned() {
        Success<Integer> emptyInstance = Success.empty();

        assertNotNull(emptyInstance);
        assertNull(emptyInstance.get());
    }


    static Stream<Arguments> ofTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedException,                expectedResult
                Arguments.of( null,    IllegalArgumentException.class,   null ),
                Arguments.of( 1,       null,                             Success.of(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T> void of_testCases(T value,
                                 Class<? extends Exception> expectedException,
                                 Success<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Success.of(value));
        } else {
            assertEquals(expectedResult, Success.of(value));
        }
    }


    static Stream<Arguments> ofNullableTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Success.empty() ),
                Arguments.of( "12",    Success.of("12") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofNullableTestCases")
    @DisplayName("ofNullable: test cases")
    public <T> void ofNullable_testCases(T value,
                                         Success<T> expectedResult) {
        assertEquals(expectedResult, Success.ofNullable(value));
    }


    @Test
    @DisplayName("isSuccess: when is invoked then true is returned")
    public void isSuccess_whenIsInvoked_thenTrueIsReturned() {
        Success<Integer> instance = Success.of(33);

        assertTrue(instance.isSuccess());
    }


    static Stream<Arguments> getTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,             expectedResult
                Arguments.of( Success.empty(),   null ),
                Arguments.of( Success.of(1),     1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public <T> void get_testCases(Success<T> success,
                                 T expectedResult) {
        assertEquals(expectedResult, success.get());
    }


    @Test
    @DisplayName("getException: when is invoked then NoSuchElementException is thrown")
    public void getException_whenIsInvoked_thenNoSuchElementExceptionIsThrown() {
        Success<Integer> instance = Success.of(11);

        assertThrows(NoSuchElementException.class, instance::getException);
    }


    static Stream<Arguments> equalsTestCases() {
        Success<Integer> emptyInstance = Success.empty();
        Success<Integer> s1 = Success.of(12);
        Success<Long> s2 = Success.of(21L);
        Success<Integer> s3 = Success.of(12);
        return Stream.of(
                //@formatter:off
                //            success,   objectToCompare,   expectedResult
                Arguments.of( s1,        null,              false ),
                Arguments.of( s1,        emptyInstance,     false ),
                Arguments.of( s1,        12,                false ),
                Arguments.of( s2,        s2.get(),          false ),
                Arguments.of( s1,        s2,                false ),
                Arguments.of( s1,        s1,                true ),
                Arguments.of( s2,        s2,                true ),
                Arguments.of( s1,        s3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T> void equals_testCases(Success<T> success,
                                     Object objectToCompare,
                                     boolean expectedResult) {
        assertEquals(expectedResult, success.equals(objectToCompare));
    }

}
