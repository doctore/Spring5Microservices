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

@AllArgsConstructor
@EqualsAndHashCode(of = {"ingredient", "pizza"})
@Data
@NoArgsConstructor
public class IngredientPizzaSummaryDto {

    // Specific for QueryDSL
    public IngredientPizzaSummaryDto(final Tuple tuple) {
        ofNullable(tuple)
                .ifPresent(t -> {
                    Object[] tupleValues = t.toArray();
                    this.ingredient = ofNullable(tupleValues[0]).map(v -> t.get(0, String.class)).orElse(null);
                    this.pizza = ofNullable(tupleValues[1]).map(v -> t.get(1, String.class)).orElse(null);
                    this.cost = ofNullable(tupleValues[2]).map(v -> t.get(2, Double.class)).orElse(null);
                });
    }

    @Schema(description = "Ingredient name", required = true)
    @NotNull
    @Size(min=1, max=64)
    private String ingredient;

    @Schema(description = "Pizza name", required = true)
    @NotNull
    @EnumHasInternalStringValue(enumClass= PizzaEnum.class)
    private String pizza;

    @Schema(description = "Cost of the pizza", required = true)
    @NotNull
    @Positive
    private Double cost;

}
