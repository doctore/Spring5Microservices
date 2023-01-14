package com.spring5microservices.grpc.util;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import lombok.experimental.UtilityClass;

import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.UNKNOWN;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@UtilityClass
public class GrpcErrorHandlerUtil {

    /**
     *    Returns the equivalent Http code from provided {@link Status}. If no status can be found, then 500 will be the
     * returned Http code.
     *
     * @param status
     *    gRPC {@link Status} used as source
     *
     * @return Http code related with given {@code status}
     */
    public static int getHttpCodeFromStatus(final Status status) {
        return ofNullable(status)
                .map(s ->
                        switch (s.getCode()) {
                            case OK -> 200;
                            case FAILED_PRECONDITION, INVALID_ARGUMENT, OUT_OF_RANGE -> 400;
                            case UNAUTHENTICATED -> 401;
                            case PERMISSION_DENIED -> 403;
                            case NOT_FOUND -> 404;
                            case ABORTED, ALREADY_EXISTS -> 409;
                            case RESOURCE_EXHAUSTED -> 429;
                            case CANCELLED -> 499;
                            case UNIMPLEMENTED -> 501;
                            case UNAVAILABLE -> 503;
                            case DEADLINE_EXCEEDED -> 504;

                            // DATA_LOSS, INTERNAL, UNKNOWN
                            default -> 500;
                        }
                )
                .orElse(500);
    }


    /**
     *    Extracts an error {@link Status} from the causal chain of a {@link Throwable}. If no status can be found,
     * a {@link Status} is created with {@link Status.Code#UNKNOWN} as its code and {@code throwable} as its cause.
     *
     * @param throwable
     *    {@link Throwable} used to determine the right {@link Status} to return
     *
     * @return non-{@code null} {@link Status}
     */
    public static Status getStatusFromThrowable(final Throwable throwable) {
        return getStatusRuntimeExceptionFromThrowable(throwable)
                .getStatus();
    }


    /**
     *    Returns the equivalent {@link StatusRuntimeException} from provided {@link Throwable}. In no equivalence can be
     * found, a {@link StatusRuntimeException} is created with {@link Status.Code#UNKNOWN} as its code and {@code throwable}
     * as its cause.
     *
     * @param throwable
     *    {@link Throwable} used to determine the right {@link StatusRuntimeException} to return
     *
     * @return non-{@code null} {@link StatusRuntimeException}
     */
    public static StatusRuntimeException getStatusRuntimeExceptionFromThrowable(final Throwable throwable) {
        return ofNullable(throwable)
                .map(t -> {
                    Throwable lastNotNullCause = t;
                    for(Throwable cause = t; null != cause; cause = cause.getCause()) {
                        if (cause instanceof StatusException) {
                            return ((StatusException)cause).getStatus()
                                    .asRuntimeException();
                        }
                        if (cause instanceof StatusRuntimeException) {
                            return (StatusRuntimeException) cause;
                        }
                        lastNotNullCause = cause;
                    }
                    // The provided exception hierarchy does not contain an instance of StatusException or StatusRuntimeException
                    if (lastNotNullCause instanceof IllegalArgumentException ||
                            lastNotNullCause instanceof NullPointerException) {
                        return INTERNAL
                                .withDescription(
                                        format("There was an error caused by an instance of: %s",
                                                lastNotNullCause.getClass().getName())
                                )
                                .withCause(throwable)
                                .asRuntimeException();
                    }
                    return UNKNOWN
                            .withDescription(
                                    format("Not found the right status equivalence using an instance of: %s and last not null cause: %s",
                                            t.getClass().getName(),
                                            lastNotNullCause.getClass().getName()
                                    )
                            )
                            .withCause(throwable)
                            .asRuntimeException();
                })
                .orElseGet(() ->
                        UNKNOWN
                          .withDescription("No source exception was provided")
                          .asRuntimeException()
                );
    }

}
