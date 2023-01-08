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
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.PERMISSION_DENIED;
import static io.grpc.Status.UNAUTHENTICATED;
import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.UNIMPLEMENTED;
import static io.grpc.Status.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GrpcErrorHandlerUtilTest {

    static Stream<Arguments> getStatusFromThrowableTestCases() {
        ConnectException connectException = new ConnectException("Connection refused");

        Status unavailableStatus = UNAVAILABLE.withDescription("io exception").withCause(connectException);
        Status unauthenticatedStatus = UNAUTHENTICATED.withDescription("Provided authentication is not valid").withCause(null);

        StatusException unavailableException = new StatusException(unavailableStatus);
        StatusRuntimeException unauthenticatedException = new StatusRuntimeException(unauthenticatedStatus);

        Status resultNullProvidedThrowable = UNKNOWN.withDescription("No source information was provided to determine the related status value");
        return Stream.of(
                //@formatter:off
                //            throwable,                  expectedResult
                Arguments.of( null,                       resultNullProvidedThrowable ),
                Arguments.of( unavailableException,       unavailableStatus ),
                Arguments.of( unauthenticatedException,   unauthenticatedStatus )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getStatusFromThrowableTestCases")
    @DisplayName("getStatusFromThrowable: test cases")
    public void getStatusFromThrowable_testCases(Throwable throwable,
                                                 Status expectedResult) {
        Status result = getStatusFromThrowable(throwable);

        compareStatus(expectedResult, result);
    }


    static Stream<Arguments> getHttpCodeFromStatusTestCases() {
        return Stream.of(
                //@formatter:off
                //            status,              expectedResult
                Arguments.of( null,                500 ),
                Arguments.of( INTERNAL,            400 ),
                Arguments.of( INVALID_ARGUMENT,    400 ),
                Arguments.of( UNAUTHENTICATED,     401 ),
                Arguments.of( PERMISSION_DENIED,   403 ),
                Arguments.of( UNAVAILABLE,         404 ),
                Arguments.of( UNIMPLEMENTED,       404 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getHttpCodeFromStatusTestCases")
    @DisplayName("getHttpCodeFromStatus: test cases")
    public void getHttpCodeFromStatus_testCases(Status status,
                                                int expectedResult) {
        assertEquals(expectedResult, getHttpCodeFromStatus(status));
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
