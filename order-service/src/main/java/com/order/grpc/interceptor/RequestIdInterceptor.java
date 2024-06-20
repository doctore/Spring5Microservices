package com.order.grpc.interceptor;

import com.spring5microservices.grpc.configuration.GrpcHeader;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Optional.ofNullable;

/**
 *    Includes the current request identifier in the one sent to gRPC server, allowing to trace what happened to every request
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
@Component
public class RequestIdInterceptor implements ClientInterceptor {

    private final Tracer tracer;


    @Autowired
    public RequestIdInterceptor(@Lazy final Tracer tracer) {
        this.tracer = tracer;
    }


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               final CallOptions callOptions,
                                                               final Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                channel.newCall(methodDescriptor, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                addRequestIdHeader(headers);
                super.start(
                        responseListener,
                        headers
                );
            }
        };
    }


    /**
     *    Adds the header {@link GrpcHeader#REQUEST_ID} to the existing ones. It will be used as unique identifier of
     * the current request across all invoked microservices.
     *
     * @param headers
     *    {@link Metadata} with the data to send to gRPC server
     */
    private void addRequestIdHeader(Metadata headers) {
        headers.put(
                GrpcHeader.REQUEST_ID,
                getOrBuildRequestId()
        );
    }


    /**
     *    Gets the current traceId from the logging functionality to use as the requestId, if it does not exist
     * uses {@link UUID} to create a new one.
     *
     * @return {@link String}
     */
    private String getOrBuildRequestId() {
        return ofNullable(tracer)
                .map(Tracer::currentSpan)
                .map(Span::context)
                .map(TraceContext::traceId)
                .orElseGet(() -> {
                    UUID uuid = UUID.randomUUID();
                    return uuid.toString();
                });
    }

}
