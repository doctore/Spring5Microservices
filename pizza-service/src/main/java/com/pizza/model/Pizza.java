package com.pizza.model;

import com.pizza.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
   name = Constants.SQL_RESULT_MAPPING.PIZZA_INGREDIENTS,
   entities = {
      @EntityResult(entityClass = Pizza.class),
      @EntityResult(
         entityClass = Ingredient.class,
         fields = {
            @FieldResult(name = "id", column = "ingredients_id"),
            @FieldResult(name = "name", column = "ingredients_name")})})
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator=Constants.DATABASE_SCHEMA + "pizza_id_seq")
    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String name;

    @NotNull
    @Positive
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
