package com.order.grpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientRunner implements ApplicationRunner {

    private final GrpcClient grpcClient;


    @Autowired
    public GrpcClientRunner(@Lazy final GrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }


    @Override
    public void run(ApplicationArguments args) {
        grpcClient.start();
    }

}