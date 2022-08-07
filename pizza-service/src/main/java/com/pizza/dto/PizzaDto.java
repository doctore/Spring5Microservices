package com.pizza.dto;

import com.pizza.enums.PizzaEnum;
import com.spring5microservices.common.util.validator.enums.EnumHasInternalStringValue;
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

    private Integer id;

    @NotNull
    @EnumHasInternalStringValue(enumClass=PizzaEnum.class)
    private String name;

    @NotNull
    @Positive
    private Double cost;

    @Valid
    private Set<IngredientDto> ingredients;

}
