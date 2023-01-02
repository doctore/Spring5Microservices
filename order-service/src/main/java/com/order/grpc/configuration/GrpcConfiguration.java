package com.order.grpc.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GrpcConfiguration {

    @Value("${grpc.client.awaitTerminationInSeconds}")
    private int clientAwaitTerminationInSeconds;

    @Value("${grpc.client.withDeadlineAfterInSeconds}")
    private int clientWithDeadlineAfterInSeconds;

    @Value("${grpc.server.host}")
    private String serverHost;

    @Value("${grpc.server.port}")
    private int serverPort;

}
