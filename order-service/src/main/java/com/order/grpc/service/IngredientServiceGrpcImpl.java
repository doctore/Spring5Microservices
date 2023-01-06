package com.order.grpc.service;

import com.spring5microservices.grpc.IngredientResponse;
import com.spring5microservices.grpc.IngredientServiceGrpc;
import com.spring5microservices.grpc.PizzaRequest;
import com.order.grpc.client.GrpcClient;
import com.spring5microservices.common.util.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Log4j2
@Service
public class IngredientServiceGrpcImpl {

    @Lazy
    private final GrpcClient grpcClient;


    public List<IngredientResponse> findByPizzaId(Short pizzaId) {
        log.info(
                format("Sending a request to get the ingredients contained in the pizza's identifier: %s",
                        pizzaId)
        );
        return ofNullable(pizzaId)
                .map(id ->
                        PizzaRequest.newBuilder()
                                .setId(id)
                                .build()
                )
                .map(request ->
                        CollectionUtil.fromIterator(
                                getIngredientServiceGrpc()
                                        .getIngredients(request)
                        )
                )
                .orElseGet(ArrayList::new);
    }


    private IngredientServiceGrpc.IngredientServiceBlockingStub getIngredientServiceGrpc() {
        return grpcClient.getIngredientServiceGrpc();
    }

}
