package com.spring5microservices.common.util.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidTest {

    @Test
    @DisplayName("empty: when is invoked then Invalid with empty list of errors is returned")
    public void empty_whenIsInvoked_thenInvalidWithEmptyListOfErrorsIsReturned() {
        Invalid<String, Integer> emptyInstance = Invalid.empty();

        assertNotNull(emptyInstance);
        assertNotNull(emptyInstance.getErrors());
        assertTrue(emptyInstance.getErrors().isEmpty());
    }


    static Stream<Arguments> ofTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,                expectedException,                expectedResult
                Arguments.of( null,                 IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,                             Invalid.of(List.of()) ),
                Arguments.of( List.of("problem"),   null,                             Invalid.of(List.of("problem")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <E, T> void of_testCases(List<String> value,
                                    Class<? extends Exception> expectedException,
                                    Invalid<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Invalid.of(value));
        }
        else {
            assertEquals(expectedResult, Invalid.of(value));
        }
    }


    static Stream<Arguments> ofNullableTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,                expectedResult
                Arguments.of( null,                 Invalid.empty() ),
                Arguments.of( List.of(),            Invalid.of(List.of()) ),
                Arguments.of( List.of("problem"),   Invalid.of(List.of("problem")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofNullableTestCases")
    @DisplayName("ofNullable: test cases")
    public <E, T> void ofNullable_testCases(List<String> value,
                                            Invalid<E, T> expectedResult) {
        assertEquals(expectedResult, Invalid.ofNullable(value));
    }


    @Test
    @DisplayName("isValid: when is invoked then false is returned")
    public void isValid_whenIsInvoked_thenFalseIsReturned() {
        Invalid<String, Integer> instance = Invalid.of(List.of("There was a problem"));

        assertFalse(instance.isValid());
    }


    @Test
    @DisplayName("get: when is invoked then NoSuchElementException is thrown")
    public void get_whenIsInvoked_thenNoSuchElementExceptionIsThrown() {
        Invalid<String, Integer> instance = Invalid.of(List.of("There was a problem"));

        assertThrows(NoSuchElementException.class, instance::get);
    }


    static Stream<Arguments> getErrorsTestCases() {
        List<String> errors = List.of("There was an error");
        return Stream.of(
                //@formatter:off
                //            value,                expectedResult
                Arguments.of( Invalid.empty(),      new ArrayList<>() ),
                Arguments.of( Invalid.of(errors),   errors )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getErrorsTestCases")
    @DisplayName("getErrors: test cases")
    public <E, T> void getErrors_testCases(Invalid<E, T> invalid,
                                           Collection<E> expectedResult) {
        assertEquals(expectedResult, invalid.getErrors());
    }


    static Stream<Arguments> equalsTestCases() {
        Invalid<String, Integer> emptyInstance = Invalid.empty();
        Invalid<String, Integer> v1 = Invalid.of(List.of("There was an error"));
        Invalid<Long, Integer> v2 = Invalid.of(List.of(21L));
        Invalid<String, Integer> v3 = Invalid.of(List.of("There was an error"));
        return Stream.of(
                //@formatter:off
                //            invalid,   objectToCompare,   expectedResult
                Arguments.of( v1,        null,              false ),
                Arguments.of( v1,        emptyInstance,     false ),
                Arguments.of( v1,        12,                false ),
                Arguments.of( v2,        v2.getErrors(),    false ),
                Arguments.of( v1,        v2,                false ),
                Arguments.of( v1,        v1,                true ),
                Arguments.of( v2,        v2,                true ),
                Arguments.of( v1,        v3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <E, T> void equals_testCases(Invalid<E, T> invalid,
                                        Object objectToCompare,
                                        boolean expectedResult) {
        assertEquals(expectedResult, invalid.equals(objectToCompare));
    }

}
