package com.spring5microservices.grpc.configuration;

import io.grpc.Metadata;
import lombok.experimental.UtilityClass;

/**
 * Headers used in communication between gRPC server and client.
 */
@UtilityClass
public class GrpcHeader {

    // Unique request's identifier
    public static Metadata.Key<String> REQUEST_ID =
            Metadata.Key.of(
                    "x-request-id",
                    Metadata.ASCII_STRING_MARSHALLER
            );

    // To include authorization data
    public static Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of(
                    "Authorization",
                    Metadata.ASCII_STRING_MARSHALLER
            );

}
