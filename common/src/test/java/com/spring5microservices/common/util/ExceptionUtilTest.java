package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.ExceptionUtil.getRootCause;
import static com.spring5microservices.common.util.ExceptionUtil.getThrowableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionUtilTest {

    static Stream<Arguments> getRootCauseTestCases() {
        ConnectException connectException = new ConnectException("Connection refused");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Argument not valid", connectException);
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException);

        // Recursive cause hierarchy use case
        final ExceptionWithCause exception1 = new ExceptionWithCause(null);
        final ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
        exception1.setCause(exception2);
        ExceptionWithCause cyclicExceptionHierarchy = new ExceptionWithCause(exception1);
        return Stream.of(
                //@formatter:off
                //            sourceThrowable,   expectedResult
                Arguments.of( null,                       empty() ),
                Arguments.of( connectException,           empty() ),
                Arguments.of( illegalArgumentException,   of(connectException) ),
                Arguments.of( securityException,          of(connectException) ),
                Arguments.of( cyclicExceptionHierarchy,   of(exception2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRootCauseTestCases")
    @DisplayName("getRootCause: test cases")
    public void getRootCause_testCases(Throwable sourceThrowable,
                                       Optional<Throwable> expectedResult) {
        assertEquals(expectedResult, getRootCause(sourceThrowable));
    }


    static Stream<Arguments> getThrowableListTestCases() {
        ConnectException connectException = new ConnectException("Connection refused");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Argument not valid", connectException);
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException);

        // Recursive cause hierarchy use case
        final ExceptionWithCause exception1 = new ExceptionWithCause(null);
        final ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
        exception1.setCause(exception2);
        ExceptionWithCause cyclicExceptionHierarchy = new ExceptionWithCause(exception1);
        return Stream.of(
                //@formatter:off
                //            sourceThrowable,            expectedResult
                Arguments.of( null,                       List.of() ),
                Arguments.of( connectException,           List.of(connectException) ),
                Arguments.of( illegalArgumentException,   List.of(illegalArgumentException, connectException) ),
                Arguments.of( securityException,          List.of(securityException, illegalArgumentException, connectException) ),
                Arguments.of( cyclicExceptionHierarchy,   List.of(cyclicExceptionHierarchy, exception1, exception2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getThrowableListTestCases")
    @DisplayName("getThrowableList: test cases")
    public void getThrowableList_testCases(Throwable sourceThrowable,
                                           List<Throwable> expectedResult) {
        assertEquals(expectedResult, getThrowableList(sourceThrowable));
    }



    /**
     * Exception used only for testing purpose to allow verifying recursive cause hierarchies.
     */
    private static class ExceptionWithCause extends Exception {

        private Throwable cause;

        ExceptionWithCause(final Throwable cause) {
            setCause(cause);
        }

        @Override
        public synchronized Throwable getCause() {
            return cause;
        }

        public void setCause(final Throwable cause) {
            this.cause = cause;
        }
    }

}
