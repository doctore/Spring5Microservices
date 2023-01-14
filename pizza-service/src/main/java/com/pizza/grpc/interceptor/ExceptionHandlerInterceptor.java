package com.pizza.grpc.interceptor;

import com.spring5microservices.grpc.util.GrpcErrorHandlerUtil;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

/**
 * Implementation of {@link ServerInterceptor} that translates and arguments gRPC exceptions.
 */
@Log4j2
@Component
public class ExceptionHandlerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata,
                                                                 final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(
                serverCall,
                metadata
        );
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    serverCall.close(
                            logExceptionAndGetStatus(e),
                            new Metadata()
                    );
                }
            }
        };
    }


    /**
     * Logs the current {@link Exception} and transforms it into the equivalent {@link Status}.
     *
     * @param sourceException
     *    {@link Throwable} used to determine the right {@link Status} to return
     *
     * @return {@link Status} equivalent to provided {@code sourceException}
     */
    private Status logExceptionAndGetStatus(final Exception sourceException) {
        Status returnedStatus = GrpcErrorHandlerUtil.getStatusFromThrowable(sourceException);
        log.error(
                format("There was an error in current request and the returned response will be status: %s and description: %s",
                        returnedStatus.getCode(),
                        returnedStatus.getDescription()
                ),
                sourceException
        );
        return returnedStatus;
    }

}
