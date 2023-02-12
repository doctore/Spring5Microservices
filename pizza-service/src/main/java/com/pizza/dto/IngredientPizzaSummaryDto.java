package com.pizza.dto;

import com.pizza.enums.PizzaEnum;
import com.querydsl.core.Tuple;
import com.spring5microservices.common.util.validator.enums.EnumHasInternalStringValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import static java.util.Optional.ofNullable;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@EqualsAndHashCode(of = { "ingredient", "pizza" })
@Data
@NoArgsConstructor
@Schema(description = "Used to provide a relation between pizza, ingredient and cost")
public class IngredientPizzaSummaryDto {

    // Specific for QueryDSL
    public IngredientPizzaSummaryDto(final Tuple tuple) {
        Object[] tupleValues = ofNullable(tuple)
                .map(Tuple::toArray)
                .orElseGet(() -> new Object[] {});

        this.ingredient = ofNullable(tupleValues[0])
                .map(v -> tuple.get(0, String.class))
                .orElse(null);
        this.pizza = ofNullable(tupleValues[1])
                .map(v -> tuple.get(1, String.class))
                .orElse(null);
        this.cost = ofNullable(tupleValues[2])
                .map(v -> tuple.get(2, Double.class))
                .orElse(null);
    }

    @Schema(description = "Ingredient name", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 64)
    private String ingredient;

    @Schema(description = "Pizza name", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @EnumHasInternalStringValue(enumClass = PizzaEnum.class)
    private String pizza;

    @Schema(description = "Cost of the pizza", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Double cost;

}
