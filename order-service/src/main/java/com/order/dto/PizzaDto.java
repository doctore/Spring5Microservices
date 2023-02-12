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

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"code"})
@Data
@NoArgsConstructor
@Schema(description = "Information related with a pizza")
public class PizzaDto {

    @Schema(description = "Internal unique identifier", requiredMode = RequiredMode.REQUIRED)
    private Short id;

    @Schema(description = "Name", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min=1, max=64)
    private String name;

    @Schema(description = "Cost", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Double cost;

}
