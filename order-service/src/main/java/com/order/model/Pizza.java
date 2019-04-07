package com.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Pizza implements IModel, Serializable {

    private static final long serialVersionUID = -1229319586;

    private Short  id;

    @NotNull
    @Size(min=1, max=64)
    private String name;

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
        Pizza pizza = (Pizza) o;
        return null == id ? name.equals(pizza.name) : id.equals(pizza.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(name) : Objects.hash(id);
    }

}
