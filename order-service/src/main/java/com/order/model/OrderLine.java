package com.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderLine implements IModel, Serializable {

    private static final long serialVersionUID = 1518934662;

    private Integer id;

    @NotNull
    @Positive
    private Integer orderId;

    @NotNull
    @Positive
    private Short pizzaId;

    @NotNull
    @Positive
    private Short amount;

    @NotNull
    @Positive
    private Double cost;


    @Override
    public boolean isNew() {
        return null == id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return null == id ? (orderId.equals(orderLine.orderId) &&  pizzaId.equals(orderLine.pizzaId))
                          : id.equals(orderLine.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(orderId) + Objects.hash(pizzaId) : Objects.hash(id);
    }

}
