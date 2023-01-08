package com.spring5microservices.grpc.util;

import io.grpc.Status;
import lombok.experimental.UtilityClass;

import static io.grpc.Status.UNKNOWN;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@UtilityClass
public class GrpcErrorHandlerUtil {

    /**
     *    Extract an error {@link Status} from the causal chain of a {@link Throwable}. If no status can be found,
     * a {@link Status} is created with {@link Status.Code#UNKNOWN} as its code and {@code throwable} as its cause.
     *
     * @param throwable
     *    {@link Throwable} used to determine the right {@link Status} to return
     *
     * @return non-{@code null} {@link Status}
     */
    public static Status getStatusFromThrowable(Throwable throwable) {
        return ofNullable(throwable)
                .map(t -> {
                    try {
                        return Status.fromThrowable(t);
                    } catch (Exception e) {
                        return UNKNOWN
                                .withDescription(
                                        format("There was an error trying to determine the suitable status using an instance of: %s",
                                                t.getClass().getName())
                                )
                                .withCause(e);
                    }
                })
                .orElseGet(() ->
                        UNKNOWN
                                .withDescription("No source information was provided to determine the related status value"));
    }

}
