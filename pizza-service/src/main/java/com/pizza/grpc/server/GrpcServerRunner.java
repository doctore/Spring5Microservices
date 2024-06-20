package com.pizza.grpc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class GrpcServerRunner implements ApplicationRunner {

    private final GrpcServer grpcServer;


    @Autowired
    public GrpcServerRunner(@Lazy final GrpcServer grpcServer) {
        this.grpcServer = grpcServer;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        grpcServer.start();
        grpcServer.blockUntilShutdown();
    }

}
