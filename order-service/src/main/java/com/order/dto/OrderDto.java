package com.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"code"})
@Data
@NoArgsConstructor
@ApiModel(description="Information related with an order")
public class OrderDto {

    @ApiModelProperty(value = "Internal unique identifier", required = true)
    private Integer id;

    @ApiModelProperty(position = 1, value = "Unique identifier of the order", required = true)
    @NotNull
    @Size(min=1, max=64)
    private String code;

    @ApiModelProperty(position = 1, value = "When was created", required = true)
    @NotNull
    private Date created;

    @ApiModelProperty(position = 1, value = "List of order lines", required = true)
    @Valid
    List<OrderLineDto> orderLines;

}