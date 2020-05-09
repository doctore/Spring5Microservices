package com.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description="Information related with a pizza")
public class PizzaDto {

    @ApiModelProperty(value = "Internal unique identifier", required = true)
    private Short id;

    @ApiModelProperty(position = 1, value = "Name", required = true)
    @NotNull
    @Size(min=1, max=64)
    private String name;

    @ApiModelProperty(position = 2, value = "Cost", required = true)
    @NotNull
    @Positive
    private Double cost;

}