package com.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ApiModel(description="Information related with the lines of an order")
public class OrderLineDto {

    @ApiModelProperty(value = "Internal unique identifier", required = true)
    private Integer id;

    @ApiModelProperty(position = 1, value = "Order related", required = true)
    private Integer orderId;

    @ApiModelProperty(position = 2, value = "Pizza included in this line", required = true)
    @NotNull
    @Valid
    private PizzaDto pizza;

    @ApiModelProperty(position = 3, value = "Number of pizzas", required = true)
    @NotNull
    @Positive
    private Short amount;

    @ApiModelProperty(position = 4, value = "Cost", required = true)
    @NotNull
    @Positive
    private Double cost;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineDto orderLine = (OrderLineDto) o;
        return null == id ? pizza.equals(orderLine.pizza) : id.equals(orderLine.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(pizza) : Objects.hash(id);
    }

}
