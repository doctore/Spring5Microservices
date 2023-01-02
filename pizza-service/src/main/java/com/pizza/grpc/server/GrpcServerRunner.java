package com.pizza.grpc.server;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GrpcServerRunner implements ApplicationRunner {

    @Lazy
    private final GrpcServer grpcServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcServer.start();
        grpcServer.blockUntilShutdown();
    }

}
