package com.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Data
@NoArgsConstructor
public class IngredientDto {

    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String name;

}
