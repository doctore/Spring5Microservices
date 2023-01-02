package com.pizza.grpc.interceptor;

import io.grpc.ForwardingServerCallListener;
import org.slf4j.MDC;
import com.spring5microservices.grpc.configuration.GrpcHeader;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *    Includes the current request identifier in the context used by the logging functionality, allowing to trace what happened to every request
 * across all invoked microservices.
 * <p>
 * The current format using by Spring Sleuth is the following:
 * <p>
 *    [application name, traceId, spanId, export]
 * <p>
 * The main elements are:
 * <p>
 *  - Application name: the name we set in the properties file and can be used to aggregate logs from multiple instances of the same application.
 *  - TraceId: an id that is assigned to a single request, job, or action. Something like each unique user initiated web request will have its own traceId.
 *  - SpanId: Tracks a unit of work. Think of a request that consists of multiple steps. Each step could have its own spanId and be tracked individually.
 *
 * @see <a href="https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/">Spring Sleuth</a>
 */
@Log4j2
@Component
public class RequestIdInterceptor implements ServerInterceptor {

    private final String TRACE_ID = "traceId";
    private final String SPAN_ID = "spanId";


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
                                                                 final Metadata metadata,
                                                                 final ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String requestId = metadata.get(GrpcHeader.REQUEST_ID);
        MDC.put(TRACE_ID, requestId);
        MDC.put(SPAN_ID, buildSpanId());

        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(
                serverCall,
                metadata
        );
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onComplete() {
                try {
                    super.onComplete();
                } finally {
                    MDC.clear();
                }
            }
        };
    }


    /**
     * Calculates the new value for spanId used by logging functionality.
     *
     * @return {@link String}
     */
    private String buildSpanId() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 16);
    }

}
