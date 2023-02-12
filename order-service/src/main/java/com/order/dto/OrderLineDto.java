package com.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Schema(description = "Information related with the lines of an order")
public class OrderLineDto {

    @Schema(description = "Internal unique identifier", requiredMode = RequiredMode.REQUIRED)
    private Integer id;

    @Schema(description = "Order related", requiredMode = RequiredMode.REQUIRED)
    private Integer orderId;

    @Schema(description = "Pizza included in this line", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Valid
    private PizzaDto pizza;

    @Schema(description = "Number of pizzas", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Short amount;

    @Schema(description = "Cost", requiredMode = RequiredMode.REQUIRED)
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
