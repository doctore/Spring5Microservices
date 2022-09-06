package com.spring5microservices.common.util.Try;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FailureTest {

    static Stream<Arguments> ofTestCases() {
        IOException exception = new IOException();
        InterruptedException fatalException = new InterruptedException();
        return Stream.of(
                //@formatter:off
                //            exception,        expectedException,                expectedResult
                Arguments.of( null,             IllegalArgumentException.class,   null ),
                Arguments.of( fatalException,   InterruptedException.class,       null ),
                Arguments.of( exception,        null,                             Failure.of(exception) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T> void of_testCases(Throwable exception,
                                 Class<? extends Exception> expectedException,
                                 Failure<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Failure.of(exception));
        } else {
            assertEquals(expectedResult, Failure.of(exception));
        }
    }


    @Test
    @DisplayName("isSuccess: when is invoked then false is returned")
    public void isSuccess_whenIsInvoked_thenFalseIsReturned() {
        Failure<Integer> instance = Failure.of(new IOException());

        assertFalse(instance.isSuccess());
    }


    @Test
    @DisplayName("get: when is invoked then stored exception is thrown")
    public void get_whenIsInvoked_thenStoredExceptionIsThrown() {
        Failure<Integer> instance = Failure.of(new IOException());

        assertThrows(IOException.class, instance::get);
    }


    @Test
    @DisplayName("getException: when is invoked then stored exception is thrown")
    public void getException_whenIsInvoked_thenStoredExceptionIsReturned() {
        NoSuchElementException exception = new NoSuchElementException();
        Failure<Integer> instance = Failure.of(exception);

        assertEquals(exception, instance.getException());
    }


    static Stream<Arguments> equalsTestCases() {
        IndexOutOfBoundsException exception = new IndexOutOfBoundsException();

        Failure<Integer> f1 = Failure.of(exception);
        Failure<String> f2 = Failure.of(new IndexOutOfBoundsException());
        Failure<Integer> f3 = Failure.of(exception);
        return Stream.of(
                //@formatter:off
                //            failure,   objectToCompare,              expectedResult
                Arguments.of( f1,        null,                         false ),
                Arguments.of( f1,        new NullPointerException(),   false ),
                Arguments.of( f2,        f2.getException(),            false ),
                Arguments.of( f1,        f2,                           false ),
                Arguments.of( f1,        f1,                           true ),
                Arguments.of( f2,        f2,                           true ),
                Arguments.of( f1,        f3,                           true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T> void equals_testCases(Failure<T> failure,
                                     Object objectToCompare,
                                     boolean expectedResult) {
        assertEquals(expectedResult, failure.equals(objectToCompare));
    }

}
