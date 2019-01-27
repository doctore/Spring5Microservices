package com.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PizzaDto {

    private Integer id;
    private String name;
    private Double cost;
    private Set<IngredientDto> ingredients;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        PizzaDto pizza = (PizzaDto) o;
        return name.equals(pizza.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
