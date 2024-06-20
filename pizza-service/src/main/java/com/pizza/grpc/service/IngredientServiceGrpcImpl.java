package com.pizza.grpc.service;

import com.spring5microservices.grpc.IngredientResponse;
import com.spring5microservices.grpc.IngredientServiceGrpc;
import com.spring5microservices.grpc.PizzaRequest;
import com.pizza.grpc.converter.IngredientGrpcConverter;
import com.pizza.service.IngredientService;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class IngredientServiceGrpcImpl extends IngredientServiceGrpc.IngredientServiceImplBase {

    private final IngredientService ingredientService;

    private final IngredientGrpcConverter ingredientGrpcConverter;


    public IngredientServiceGrpcImpl(@Lazy final IngredientService ingredientService,
                                     @Lazy final IngredientGrpcConverter ingredientGrpcConverter) {
        this.ingredientService = ingredientService;
        this.ingredientGrpcConverter = ingredientGrpcConverter;
    }


    @Override
    public void getIngredients(final PizzaRequest pizzaRequest,
                               final StreamObserver<IngredientResponse> responseObserver) {
        log.info(
                format("Getting ingredients contained in the pizza's identifier: %s",
                        getOrElse(
                                pizzaRequest,
                                PizzaRequest::getId,
                                "null"
                        )
                )
        );
        ofNullable(pizzaRequest)
                .map(PizzaRequest::getId)
                .map(ingredientService::findByPizzaId)
                .ifPresent(ingredients ->
                        ingredients.stream()
                                .map(ingredientGrpcConverter::fromModelToDto)
                                .forEach(responseObserver::onNext)
                );
        responseObserver.onCompleted();
    }

}
