package com.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@EqualsAndHashCode(of = { "name" })
@Data
@NoArgsConstructor
@Schema(description = "Used to group the ingredients included in every order")
public class IngredientAmountDto {

    @Schema(description = "Name", required = true)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;

    @Schema(description = "amount", required = true)
    @NotNull
    @Positive
    private Integer amount;

}