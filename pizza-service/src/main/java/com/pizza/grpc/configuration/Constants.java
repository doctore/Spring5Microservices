package com.pizza.grpc.configuration;

import io.grpc.Context;

/**
 * Global values used in different part of the gRPC functionality.
 */
public class Constants {

    // gRPC client identifier that sent the request to the server
    public static final Context.Key<String> GRPC_CLIENT_ID = Context.key("grpcClientId");

}
