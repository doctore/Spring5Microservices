package com.pizza.model;

import com.pizza.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(schema = Constants.DATABASE_SCHEMA)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator=Constants.DATABASE_SCHEMA + "ingredient_id_seq")
    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient ingredient = (Ingredient) o;
        return null == id ? name.equals(ingredient.name) : id.equals(ingredient.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(name) : Objects.hash(id);
    }

}