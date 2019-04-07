package com.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Order implements IModel, Serializable {

    private static final long serialVersionUID = -779236471;

    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String code;

    @NotNull
    private Timestamp created;


    @Override
    public boolean isNew() {
        return null == id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return null == id ? code.equals(order.code) : id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(code) : Objects.hash(id);
    }

}
