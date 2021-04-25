package com.pizza;

import com.pizza.dto.IngredientDto;
import com.pizza.dto.IngredientPizzaSummaryDto;
import com.pizza.dto.PizzaDto;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class TestDataFactory {

    public static Ingredient buildIngredient(Integer id, String name) {
        return new Ingredient(id, name);
    }

    public static Pizza buildPizza(Integer id, PizzaEnum name, Double cost, Set<Ingredient> ingredients) {
        return new Pizza(id, name, cost, ingredients);
    }

    public static IngredientDto buildIngredientDto(Integer id, String name) {
        return new IngredientDto(id, name);
    }

    public static PizzaDto buildPizzaDto(Integer id, String name, Double cost, Set<IngredientDto> ingredients) {
        return new PizzaDto(id, name, cost, ingredients);
    }

    public static IngredientPizzaSummaryDto buildIngredientPizzaSummaryDto(String ingredient, String pizza, Double cost) {
        return new IngredientPizzaSummaryDto(ingredient, pizza, cost);
    }

}
