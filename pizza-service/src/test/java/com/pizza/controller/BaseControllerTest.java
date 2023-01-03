package com.pizza.controller;

import com.pizza.grpc.server.GrpcServerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

public abstract class BaseControllerTest {

    @Autowired
    protected ApplicationContext context;

    // To avoid Hazelcast instance creation
    @MockBean
    @Qualifier("cacheManager")
    private CacheManager mockCacheManager;

    // To avoid gRPC server initialization
    @MockBean
    private GrpcServerRunner grpcServerRunner;

}
