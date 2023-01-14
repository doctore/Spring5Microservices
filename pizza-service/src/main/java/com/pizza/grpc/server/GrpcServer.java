package com.pizza.grpc.server;

import com.pizza.grpc.configuration.GrpcConfiguration;
import com.pizza.grpc.interceptor.AuthenticationInterceptor;
import com.pizza.grpc.interceptor.ExceptionHandlerInterceptor;
import com.pizza.grpc.interceptor.RequestIdInterceptor;
import com.pizza.grpc.service.IngredientServiceGrpcImpl;
import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerInterceptor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * gRPC server used to communicate microservices.
 *
 * @see <a href="https://grpc.io/docs/what-is-grpc/introduction/">Introduction to gRPC</a>
 * @see <a href="https://grpc.io/docs/languages/java/">Java gRPC</a>
 */
@Component
@Log4j2
public class GrpcServer {

    @Lazy
    private final GrpcConfiguration grpcConfiguration;

    @Lazy
    private final RequestIdInterceptor requestIdInterceptor;

    @Lazy
    private final AuthenticationInterceptor authenticationInterceptor;

    @Lazy
    private final ExceptionHandlerInterceptor exceptionHandlerInterceptor;

    @Lazy
    private final IngredientServiceGrpcImpl ingredientServiceGrpc;

    private final Server server;


    public GrpcServer(final GrpcConfiguration grpcConfiguration,
                      final RequestIdInterceptor requestIdInterceptor,
                      final AuthenticationInterceptor authenticationInterceptor,
                      final ExceptionHandlerInterceptor exceptionHandlerInterceptor,
                      final IngredientServiceGrpcImpl ingredientServiceGrpc) {
        this.grpcConfiguration = grpcConfiguration;
        this.requestIdInterceptor = requestIdInterceptor;
        this.authenticationInterceptor = authenticationInterceptor;
        this.exceptionHandlerInterceptor = exceptionHandlerInterceptor;
        this.ingredientServiceGrpc = ingredientServiceGrpc;
        this.server = buildServer(
                grpcConfiguration.getServerPort()
        );
    }


    /**
     * Start serving requests
     */
    public void start() {
        if (nonNull(server)) {
            log.info("gRPC server is starting");
            try {
                server.start();
                log.info(
                        format("gRPC server started and listening on port: %d",
                                grpcConfiguration.getServerPort())
                );
                displayAvailableServices();
                addShutdownHook();
            } catch (Throwable e) {
                log.error("There was an error starting gRPC server", e);
            }
        } else {
            log.error("gRPC server is null");
        }
    }


    /**
     * Stop serving requests and shutdown resources.
     *
     * @throws InterruptedException if there was a problem shutting down the server
     */
    public void stop() throws InterruptedException {
        if (nonNull(server)) {
            int awaitTerminationInSeconds = grpcConfiguration.getServerAwaitTerminationInSeconds();
            if (0 < awaitTerminationInSeconds) {
                server.shutdown()
                        .awaitTermination(
                                awaitTerminationInSeconds,
                                TimeUnit.SECONDS
                        );
            } else {
                server.shutdown()
                        .awaitTermination();
            }
        }
    }


    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     *
     * @throws InterruptedException if there was a problem shutting down the server
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (nonNull(server)) {
            server.awaitTermination();
        }
    }


    /**
     * Configures the gRPC server including: {@link BindableService} and {@link ServerInterceptor}.
     *
     * @param port
     *    Port used by the gRPC server
     *
     * @return {@link Server}
     */
    private Server buildServer(final int port) {
        return Grpc.newServerBuilderForPort(
                port,
                InsecureServerCredentials.create()
        )
        .addService(ingredientServiceGrpc)
        .intercept(exceptionHandlerInterceptor)
        .intercept(authenticationInterceptor)
        .intercept(requestIdInterceptor)
        .build();
    }


    /**
     * Adds into the logs the available services provided by the gRPC server.
     */
    private void displayAvailableServices() {
        server.getServices()
                .forEach(s ->
                        log.info(
                                format("Available the gRPC service: %s", s.getServiceDescriptor().getName())
                        )
                );
    }


    /**
     * Stops the gRPC server before JVM completes the shutdown.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutting down gRPC server");
                    try {
                        GrpcServer.this.stop();
                    } catch (Exception e) {
                        log.error("There was an error shutting down gRPC server", e);
                    }
                    log.info("gRPC server shut down");
                })
        );
    }

}
