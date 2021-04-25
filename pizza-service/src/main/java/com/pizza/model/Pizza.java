package com.pizza.model;

import com.pizza.configuration.Constants;
import com.pizza.enums.PizzaEnum;
import com.pizza.util.converter.enums.PizzaEnumDatabaseConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Data
@NoArgsConstructor
@Entity
@Table(name = "pizza", schema = Constants.DATABASE_SCHEMA)
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
    @Convert(converter=PizzaEnumDatabaseConverter.class)
    private PizzaEnum name;

    @NotNull
    @Positive
    private Double cost;

    @ManyToMany
    @JoinTable(schema = Constants.DATABASE_SCHEMA,
               name = "pizza_ingredient",
               inverseJoinColumns = { @JoinColumn(name = "ingredient_id") })
    private Set<Ingredient> ingredients;

}
