package com.pizza.service;

import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Service
public class IngredientService {

    private final IngredientRepository repository;


    @Autowired
    public IngredientService(@Lazy final IngredientRepository repository) {
        this.repository = repository;
    }


    /**
     * Return the {@link Ingredient}s contained in the {@link Pizza}'s identifier {@code pizzaId}
     *
     * @param pizzaId
     *   {@link Pizza#getId()}
     *
     * @return {@link Set} of {@link Ingredient}
     */
    public Set<Ingredient> findByPizzaId(final Integer pizzaId) {
        return ofNullable(pizzaId)
                .map(repository::findByPizzaId)
                .orElseGet(HashSet::new);
    }


    /**
     * Persist the information included in the given {@code ingredients}
     *
     * @param ingredients
     *    {@link List} of {@link Ingredient} to save
     *
     * @return updated {@link Ingredient}s
     */
    public List<Ingredient> saveAll(final Collection<Ingredient> ingredients) {
        return ofNullable(ingredients)
                .map(repository::saveAll)
                .orElseGet(ArrayList::new);
    }

}
