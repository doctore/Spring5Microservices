package com.pizza.grpc.converter;

import com.spring5microservices.grpc.IngredientResponse;
import com.pizza.model.Ingredient;
import com.spring5microservices.common.converter.BaseFromModelToDtoConverter;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link Ingredient} to {@link IngredientResponse}.
 */
@Mapper
public interface IngredientGrpcConverter extends BaseFromModelToDtoConverter<Ingredient, IngredientResponse> {

    @Override
    default IngredientResponse fromModelToDto(final Ingredient model) {
        return ofNullable(model)
                .map(m ->
                        IngredientResponse.newBuilder()
                                .setId(model.getId())
                                .setName(model.getName())
                                .build()
                )
                .orElse(null);
    }

}
