package com.spring5microservices.common.util;

import com.spring5microservices.common.exception.JsonException;
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
import static com.spring5microservices.common.util.ExceptionUtil.throwableOfType;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionUtilTest {

    static Stream<Arguments> getRootCauseTestCases() {
        ConnectException connectException = new ConnectException("Connection refused");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Argument not valid", connectException);
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException);

        // Recursive cause hierarchy use case
        ExceptionWithCause exception1 = new ExceptionWithCause(null);
        ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
        exception1.setCause(exception2);
        ExceptionWithCause cyclicExceptionHierarchy = new ExceptionWithCause(exception1);
        return Stream.of(
                //@formatter:off
                //            sourceThrowable,   expectedResult
                Arguments.of( null,                       empty() ),
                Arguments.of( connectException,           of(connectException) ),
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
        ExceptionWithCause exception1 = new ExceptionWithCause(null);
        ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
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


    static Stream<Arguments> throwableOfTypeWithoutSubclassTestCases() {
        ConnectException connectException = new ConnectException("Connection refused");
        IllegalArgumentException illegalArgumentException1 = new IllegalArgumentException("Argument not valid 1", connectException);
        IllegalArgumentException illegalArgumentException2 = new IllegalArgumentException("Argument not valid 2", illegalArgumentException1);
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException2);

        // Recursive cause hierarchy use case
        ExceptionWithCause exception1 = new ExceptionWithCause(null);
        ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
        exception1.setCause(exception2);
        JsonException cyclicExceptionHierarchy = new JsonException(exception1);
        return Stream.of(
                //@formatter:off
                //            sourceThrowable,             type,                             expectedResult
                Arguments.of( null,                        null,                             empty() ),
                Arguments.of( null,                        IllegalArgumentException.class,   empty() ),
                Arguments.of( connectException,            null,                             empty() ),
                Arguments.of( illegalArgumentException2,   InterruptedException.class,       empty() ),
                Arguments.of( connectException,            ConnectException.class,           of(connectException) ),
                Arguments.of( illegalArgumentException2,   ConnectException.class,           of(connectException) ),
                Arguments.of( securityException,           IllegalArgumentException.class,   of(illegalArgumentException2) ),
                Arguments.of( cyclicExceptionHierarchy,    ExceptionWithCause.class,         of(exception1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("throwableOfTypeWithoutSubclassTestCases")
    @DisplayName("throwableOfType: without subclass parameter test cases")
    public <T extends Throwable> void throwableOfTypeWithoutSubclass_testCases(Throwable sourceThrowable,
                                                                               Class<T> type,
                                                                               Optional<T> expectedResult) {
        assertEquals(expectedResult, throwableOfType(sourceThrowable, type));
    }


    static Stream<Arguments> throwableOfTypeAllParametersTestCases() {
        NumberFormatException numberFormatException = new NumberFormatException("Number not valid");
        RuntimeException runtimeException = new RuntimeException("Severe error");
        IllegalArgumentException illegalArgumentException1 = new IllegalArgumentException("Argument not valid 1", runtimeException);
        IllegalArgumentException illegalArgumentException2 = new IllegalArgumentException("Argument not valid 2", illegalArgumentException1);

        // Recursive cause hierarchy use case
        ExceptionWithCause exception1 = new ExceptionWithCause(null);
        ExceptionWithCause exception2 = new ExceptionWithCause(exception1);
        exception1.setCause(exception2);
        JsonException cyclicExceptionHierarchy = new JsonException(exception1);
        return Stream.of(
                //@formatter:off
                //            sourceThrowable,             type,                             subclass,   expectedResult
                Arguments.of( null,                        null,                             true,       empty() ),
                Arguments.of( null,                        null,                             false,      empty() ),
                Arguments.of( null,                        IllegalArgumentException.class,   true,       empty() ),
                Arguments.of( null,                        IllegalArgumentException.class,   false,      empty() ),
                Arguments.of( numberFormatException,       null,                             true,       empty() ),
                Arguments.of( numberFormatException,       null,                             false,      empty() ),
                Arguments.of( illegalArgumentException2,   InterruptedException.class,       true,       empty() ),
                Arguments.of( illegalArgumentException2,   InterruptedException.class,       false,      empty() ),
                Arguments.of( numberFormatException,       NumberFormatException.class,      true,       of(numberFormatException) ),
                Arguments.of( numberFormatException,       NumberFormatException.class,      false,      of(numberFormatException) ),
                Arguments.of( illegalArgumentException2,   IllegalArgumentException.class,   true,       of(illegalArgumentException2) ),
                Arguments.of( illegalArgumentException2,   IllegalArgumentException.class,   false,      of(illegalArgumentException2) ),
                Arguments.of( illegalArgumentException2,   RuntimeException.class,           true,       of(illegalArgumentException2) ),
                Arguments.of( illegalArgumentException2,   RuntimeException.class,           false,      of(runtimeException) ),
                Arguments.of( cyclicExceptionHierarchy,    ExceptionWithCause.class,         true,       of(exception1) ),
                Arguments.of( cyclicExceptionHierarchy,    ExceptionWithCause.class,         false,      of(exception1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("throwableOfTypeAllParametersTestCases")
    @DisplayName("throwableOfType: with all parameters test cases")
    public <T extends Throwable> void throwableOfTypeAllParameters_testCases(Throwable sourceThrowable,
                                                                             Class<T> type,
                                                                             boolean subclass,
                                                                             Optional<T> expectedResult) {
        assertEquals(expectedResult, throwableOfType(sourceThrowable, type, subclass));
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
