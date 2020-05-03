package com.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"code"})
@Data
@NoArgsConstructor
public class PizzaDto {

    @Schema(description = "Internal unique identifier", required = true)
    private Short id;

    @Schema(description = "Name", required = true)
    @NotNull
    @Size(min=1, max=64)
    private String name;

    @Schema(description = "Cost", required = true)
    @NotNull
    @Positive
    private Double cost;

}
