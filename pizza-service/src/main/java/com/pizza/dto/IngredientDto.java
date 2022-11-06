package com.pizza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@EqualsAndHashCode(of = { "name" })
@Data
@NoArgsConstructor
public class IngredientDto {

    @Schema(description = "Internal unique identifier", required = true)
    private Integer id;

    @Schema(description = "Name", required = true)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;

}
