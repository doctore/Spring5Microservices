package com.pizza.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(schema = "eat")
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max=64, message="Name must not be more than 64 characters long")
    private String name;

    @NotNull
    private Double cost;

    @ManyToMany
    @JoinTable(schema = "eat",
               name = "pizza_ingredient",
               inverseJoinColumns = { @JoinColumn(name = "ingredient_id") })
    private Set<Ingredient> ingredients;


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
