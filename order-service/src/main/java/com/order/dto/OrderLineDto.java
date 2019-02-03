package com.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderLineDto {

    private Integer id;
    private Integer orderId;
    private PizzaDto pizza;
    private Short amount;
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
