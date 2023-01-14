package com.spring5microservice.grpc.util;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.ConnectException;
import java.util.stream.Stream;

import static com.spring5microservices.grpc.util.GrpcErrorHandlerUtil.getHttpCodeFromStatus;
import static com.spring5microservices.grpc.util.GrpcErrorHandlerUtil.getStatusFromThrowable;
import static com.spring5microservices.grpc.util.GrpcErrorHandlerUtil.getStatusRuntimeExceptionFromThrowable;
import static io.grpc.Status.ABORTED;
import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.CANCELLED;
import static io.grpc.Status.DATA_LOSS;
import static io.grpc.Status.DEADLINE_EXCEEDED;
import static io.grpc.Status.FAILED_PRECONDITION;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.OK;
import static io.grpc.Status.OUT_OF_RANGE;
import static io.grpc.Status.PERMISSION_DENIED;
import static io.grpc.Status.RESOURCE_EXHAUSTED;
import static io.grpc.Status.UNAUTHENTICATED;
import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNIMPLEMENTED;
import static io.grpc.Status.UNKNOWN;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GrpcErrorHandlerUtilTest {

    static Stream<Arguments> getHttpCodeFromStatusTestCases() {
        return Stream.of(
                //@formatter:off
                //            status,                expectedResult
                Arguments.of( null,                  500 ),
                Arguments.of( OK,                    200 ),
                Arguments.of( FAILED_PRECONDITION,   400 ),
                Arguments.of( INVALID_ARGUMENT,      400 ),
                Arguments.of( OUT_OF_RANGE,          400 ),
                Arguments.of( UNAUTHENTICATED,       401 ),
                Arguments.of( PERMISSION_DENIED,     403 ),
                Arguments.of( NOT_FOUND,             404 ),
                Arguments.of( ALREADY_EXISTS,        409 ),
                Arguments.of( ABORTED,               409 ),
                Arguments.of( RESOURCE_EXHAUSTED,    429 ),
                Arguments.of( CANCELLED,             499 ),
                Arguments.of( DATA_LOSS,             500 ),
                Arguments.of( INTERNAL,              500 ),
                Arguments.of( UNKNOWN,               500 ),
                Arguments.of( UNIMPLEMENTED,         501 ),
                Arguments.of( UNAVAILABLE,           503 ),
                Arguments.of( DEADLINE_EXCEEDED,     504 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getHttpCodeFromStatusTestCases")
    @DisplayName("getHttpCodeFromStatus: test cases")
    public void getHttpCodeFromStatus_testCases(Status status,
                                                int expectedResult) {
        assertEquals(expectedResult, getHttpCodeFromStatus(status));
    }


    static Stream<Arguments> getStatusFromThrowableTestCases() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Argument not valid");
        ConnectException connectException = new ConnectException("Connection refused");
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException);

        Status unavailableStatus = UNAVAILABLE.withDescription("io exception").withCause(connectException);
        Status unauthenticatedStatus = UNAUTHENTICATED.withDescription("Provided authentication is not valid").withCause(null);

        StatusException unavailableException = new StatusException(unavailableStatus);
        StatusRuntimeException unauthenticatedException = new StatusRuntimeException(unauthenticatedStatus);
        IllegalStateException illegalStateException = new IllegalStateException("State not valid", unauthenticatedException);

        Status resultNullProvidedThrowable = UNKNOWN.withDescription("No source exception was provided");
        Status resultNoStatusExceptionInHierarchy =
                INTERNAL
                  .withDescription(
                          format("There was an error caused by an instance of: %s",
                                  securityException.getCause().getClass().getName())
                  )
                  .withCause(securityException);
        Status resultNoStatusEquivalenceFound =
                UNKNOWN
                  .withDescription(
                          format("Not found the right status equivalence using an instance of: %s and last not null cause: %s",
                                  connectException.getClass().getName(),
                                  connectException.getClass().getName()
                          )
                  )
                  .withCause(connectException);

        return Stream.of(
                //@formatter:off
                //            throwable,                  expectedResult
                Arguments.of( null,                       resultNullProvidedThrowable ),
                Arguments.of( unavailableException,       unavailableStatus ),
                Arguments.of( unauthenticatedException,   unauthenticatedStatus ),
                Arguments.of( illegalStateException,      unauthenticatedStatus ),
                Arguments.of( securityException,          resultNoStatusExceptionInHierarchy ),
                Arguments.of( connectException,           resultNoStatusEquivalenceFound )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getStatusFromThrowableTestCases")
    @DisplayName("getStatusFromThrowable: test cases")
    public void getStatusFromThrowable_testCases(Throwable throwable,
                                                 Status expectedResult) {
        Status result = getStatusFromThrowable(throwable);

        assertNotNull(result);
        compareStatus(expectedResult, result);
    }


    static Stream<Arguments> getStatusRuntimeExceptionFromThrowableTestCases() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Argument not valid");
        ConnectException connectException = new ConnectException("Connection refused");
        SecurityException securityException = new SecurityException("Operation not allowed", illegalArgumentException);

        Status unavailableStatus = UNAVAILABLE.withDescription("io exception").withCause(connectException);
        Status unauthenticatedStatus = UNAUTHENTICATED.withDescription("Provided authentication is not valid").withCause(null);

        StatusException unavailableStatusException = new StatusException(unavailableStatus);
        StatusRuntimeException unauthenticatedStatusRuntimeException = new StatusRuntimeException(unauthenticatedStatus);
        IllegalStateException illegalStateException = new IllegalStateException("State not valid", unauthenticatedStatusRuntimeException);

        StatusRuntimeException resultNullProvidedThrowable =
                UNKNOWN
                  .withDescription("No source exception was provided")
                  .asRuntimeException();
        StatusRuntimeException resultStatusExceptionProvided = new StatusRuntimeException(unavailableStatusException.getStatus());
        StatusRuntimeException resultStatusRuntimeExceptionProvided = unauthenticatedStatusRuntimeException;
        StatusRuntimeException resultStatusRuntimeExceptionProvidedAsCause = unauthenticatedStatusRuntimeException;
        StatusRuntimeException resultNoStatusExceptionProvidedButManagedCause =
                INTERNAL
                  .withDescription(
                        format("There was an error caused by an instance of: %s",
                                securityException.getCause().getClass().getName())
                )
                .withCause(securityException)
                .asRuntimeException();
        StatusRuntimeException resultNoStatusExceptionProvidedAndNotManagedCause =
                UNKNOWN
                  .withDescription(
                          format("Not found the right status equivalence using an instance of: %s and last not null cause: %s",
                                  connectException.getClass().getName(),
                                  connectException.getClass().getName()
                          )
                  )
                  .withCause(connectException)
                  .asRuntimeException();

        return Stream.of(
                //@formatter:off
                //            throwable,                               expectedResult
                Arguments.of( null,                                    resultNullProvidedThrowable ),
                Arguments.of( unavailableStatusException,              resultStatusExceptionProvided ),
                Arguments.of( unauthenticatedStatusRuntimeException,   resultStatusRuntimeExceptionProvided ),
                Arguments.of( illegalStateException,                   resultStatusRuntimeExceptionProvidedAsCause ),
                Arguments.of( securityException,                       resultNoStatusExceptionProvidedButManagedCause ),
                Arguments.of( connectException,                        resultNoStatusExceptionProvidedAndNotManagedCause )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getStatusRuntimeExceptionFromThrowableTestCases")
    @DisplayName("getStatusRuntimeExceptionFromThrowable: test cases")
    public void getStatusRuntimeExceptionFromThrowable_testCases(Throwable throwable,
                                                                 StatusRuntimeException expectedResult) {
        StatusRuntimeException result = getStatusRuntimeExceptionFromThrowable(throwable);

        assertNotNull(result);
        compareStatus(expectedResult.getStatus(), result.getStatus());
    }


    private void compareStatus(final Status s1,
                               final Status s2) {
        assertNotNull(s1);
        assertNotNull(s2);
        assertEquals(s1.getCode(), s2.getCode());
        assertEquals(s1.getDescription(), s2.getDescription());
        assertEquals(s1.getCause(), s2.getCause());
    }

}
