package com.order.grpc.client;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GrpcClientRunner implements ApplicationRunner {

    @Lazy
    private final GrpcClient grpcClient;


    @Override
    public void run(ApplicationArguments args) {
        grpcClient.start();
    }
}