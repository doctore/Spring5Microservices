package com.order.grpc.client;

import com.order.configuration.security.SecurityConfiguration;
import com.order.grpc.interceptor.RequestIdInterceptor;
import com.spring5microservices.grpc.IngredientServiceGrpc;
import com.order.grpc.configuration.GrpcConfiguration;
import com.spring5microservices.grpc.security.BasicCredential;
import io.grpc.CallCredentials;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * gRPC client used to communicate microservices.
 *
 * @see <a href="https://grpc.io/docs/what-is-grpc/introduction/">Introduction to gRPC</a>
 * @see <a href="https://grpc.io/docs/languages/java/">Java gRPC</a>
 */
@Component
@Log4j2
public class GrpcClient {

    @Lazy
    private final GrpcConfiguration grpcConfiguration;

    @Lazy
    private final SecurityConfiguration securityConfiguration;

    @Lazy
    private final RequestIdInterceptor requestIdInterceptor;

    private final ManagedChannel channel;

    private final IngredientServiceGrpc.IngredientServiceBlockingStub ingredientServiceGrpc;


    public GrpcClient(final GrpcConfiguration grpcConfiguration,
                      final SecurityConfiguration securityConfiguration,
                      final RequestIdInterceptor requestIdInterceptor) {
        this.grpcConfiguration = grpcConfiguration;
        this.securityConfiguration = securityConfiguration;
        this.requestIdInterceptor = requestIdInterceptor;
        channel = buildChannel(
                grpcConfiguration.getServerHost(),
                grpcConfiguration.getServerPort()
        );
        this.ingredientServiceGrpc = buildIngredientService();
    }


    public IngredientServiceGrpc.IngredientServiceBlockingStub getIngredientServiceGrpc() {
        int withDeadlineAfterInSeconds = grpcConfiguration.getClientAwaitTerminationInSeconds();
        if (0 < withDeadlineAfterInSeconds) {
            return ingredientServiceGrpc
                    .withDeadlineAfter(
                            withDeadlineAfterInSeconds,
                            TimeUnit.SECONDS
                    );
        } else {
            return ingredientServiceGrpc;
        }
    }


    /**
     * Start sending requests
     */
    public void start() {
        if (Objects.nonNull(channel)) {
            log.info(
                    format("gRPC client is starting. Configured server located on host: %s and port: %d",
                            grpcConfiguration.getServerHost(),
                            grpcConfiguration.getServerPort()
                    )
            );
            addShutdownHook();
        } else {
            log.error("gRPC client channel is null");
        }
    }


    /**
     * Stop sending requests and shutdown resources.
     *
     * @throws InterruptedException if there was a problem shutting down the channel
     */
    public void stop() throws InterruptedException {
        if (Objects.nonNull(channel)) {
            int awaitTerminationInSeconds = grpcConfiguration.getClientAwaitTerminationInSeconds();
            channel.shutdown()
                    .awaitTermination(
                            awaitTerminationInSeconds,
                            TimeUnit.SECONDS
                    );
        }
    }


    /**
     * Configures the default options used by every channel added in the gRPC client.
     *
     * @param host
     *    Host in which the gRPC server is running
     * @param port
     *    Port used by the gRPC server
     *
     * @return {@link ManagedChannel}
     */
    private ManagedChannel buildChannel(final String host,
                                        final int port) {
        String target = host + ":" + port;
        return Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .intercept(requestIdInterceptor)
                .build();
    }


    /**
     * Returns a synchronous stub to manage functionality related with ingredients.
     *
     * @return {@link IngredientServiceGrpc.IngredientServiceBlockingStub}
     */
    private IngredientServiceGrpc.IngredientServiceBlockingStub buildIngredientService() {
        return IngredientServiceGrpc
                .newBlockingStub(channel)
                .withCallCredentials(buildCallCredentials());
    }


    /**
     * Stops the gRPC client before JVM completes the shutdown.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Shutting down gRPC client");
                    try {
                        GrpcClient.this.stop();
                    } catch (Exception e) {
                        log.error("There was an error shutting down gRPC client", e);
                    }
                    log.info("gRPC client shut down");
                })
        );
    }


    /**
     * Builds the required {@link CallCredentials} used in the communication between gRPC client and server.
     *
     * @return {@link CallCredentials}
     */
    private CallCredentials buildCallCredentials() {
        final String username = securityConfiguration.getClientId();
        final String password = securityConfiguration.getClientPassword();
        return BasicCredential.of(
                username,
                password
        );
    }

}
