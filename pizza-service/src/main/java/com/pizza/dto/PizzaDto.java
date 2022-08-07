package com.pizza.dto;

import com.pizza.enums.PizzaEnum;
import com.spring5microservices.common.util.validator.enums.EnumHasInternalStringValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Data
@NoArgsConstructor
public class PizzaDto {

    @Schema(description = "Internal unique identifier", required = true)
    private Integer id;

    @Schema(description = "Name", required = true)
    @NotNull
    @EnumHasInternalStringValue(enumClass=PizzaEnum.class)
    private String name;

    @Schema(description = "Cost", required = true)
    @NotNull
    @Positive
    private Double cost;

    @Schema(description = "List of ingredients")
    @Valid
    private Set<IngredientDto> ingredients;

}
