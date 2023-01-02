package com.pizza.grpc.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GrpcConfiguration {

    @Value("${grpc.server.port}")
    private int serverPort;

    @Value("${grpc.server.awaitTerminationInSeconds}")
    private int serverAwaitTerminationInSeconds;

}
