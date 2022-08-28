package com.spring5microservices.common.util.validation;

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

public class ValidTest {

    @Test
    @DisplayName("empty: when is invoked then Valid with no internal value is returned")
    public void empty_whenIsInvoked_thenValidWithNoInternalValueIsReturned() {
        Valid<String, Integer> emptyInstance = Valid.empty();

        assertNotNull(emptyInstance);
        assertNull(emptyInstance.get());
    }


    static Stream<Arguments> ofTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedException,                expectedResult
                Arguments.of( null,    IllegalArgumentException.class,   null ),
                Arguments.of( 1,       null,                             Valid.of(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <E, T> void of_testCases(T value,
                                    Class<? extends Exception> expectedException,
                                    Valid<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Valid.of(value));
        }
        else {
            assertEquals(expectedResult, Valid.of(value));
        }
    }


    static Stream<Arguments> ofNullableTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Valid.empty() ),
                Arguments.of( "12",    Valid.of("12") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofNullableTestCases")
    @DisplayName("ofNullable: test cases")
    public <E, T> void ofNullable_testCases(T value,
                                            Valid<E, T> expectedResult) {
        assertEquals(expectedResult, Valid.ofNullable(value));
    }


    @Test
    @DisplayName("isValid: when is invoked then true is returned")
    public void isValid_whenIsInvoked_thenTrueIsReturned() {
        Valid<String, Integer> instance = Valid.of(11);

        assertTrue(instance.isValid());
    }


    static Stream<Arguments> getTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,           expectedResult
                Arguments.of( Valid.empty(),   null ),
                Arguments.of( Valid.of(1),     1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public <E, T> void get_testCases(Valid<E, T> valid,
                                     T expectedResult) {
        assertEquals(expectedResult, valid.get());
    }


    @Test
    @DisplayName("getErrors: when is invoked then NoSuchElementException is thrown")
    public void getErrors_whenIsInvoked_thenNoSuchElementExceptionIsThrown() {
        Valid<String, Integer> instance = Valid.of(11);

        assertThrows(NoSuchElementException.class, instance::getErrors);
    }


    static Stream<Arguments> equalsTestCases() {
        Valid<String, Integer> emptyInstance = Valid.empty();
        Valid<String, Integer> v1 = Valid.of(12);
        Valid<String, Long> v2 = Valid.of(21L);
        Valid<String, Integer> v3 = Valid.of(12);
        return Stream.of(
                //@formatter:off
                //            valid,   objectToCompare,   expectedResult
                Arguments.of( v1,      null,              false ),
                Arguments.of( v1,      emptyInstance,     false ),
                Arguments.of( v1,      12,                false ),
                Arguments.of( v2,      v2.get(),          false ),
                Arguments.of( v1,      v2,                false ),
                Arguments.of( v1,      v1,                true ),
                Arguments.of( v2,      v2,                true ),
                Arguments.of( v1,      v3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <E, T> void equals_testCases(Valid<E, T> valid,
                                        Object objectToCompare,
                                        boolean expectedResult) {
        assertEquals(expectedResult, valid.equals(objectToCompare));
    }

}
