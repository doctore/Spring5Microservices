package com.pizza.model;

import com.pizza.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(schema = Constants.DATABASE_SCHEMA)
@SqlResultSetMapping(
   name="PizzaIngredientsMapping",
   entities = {
      @EntityResult(entityClass = Pizza.class),
      @EntityResult(
         entityClass = Ingredient.class,
         fields = {
            @FieldResult(name = "id", column = "ingredients_id"),
            @FieldResult(name = "name", column = "ingredients_name")})})
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
    @JoinTable(schema = Constants.DATABASE_SCHEMA,
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
