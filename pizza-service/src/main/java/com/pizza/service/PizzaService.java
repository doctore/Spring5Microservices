package com.pizza.service;

import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class PizzaService {

    @Lazy
    private final PizzaRepository repository;

    @Lazy
    private final IngredientService ingredientService;


    /**
     * Returns the {@link Pizza} which name matches with the given one.
     *
     * @param name
     *    Name to search in the current {@link Pizza#getName()}s
     *
     * @return {@link Optional} of {@link Pizza}
     */
    public Optional<Pizza> findByName(final String name) {
        return ofNullable(name)
                .flatMap(PizzaEnum::getFromDatabaseValue)
                .flatMap(repository::findWithIngredientsByName);
    }


    /**
     * Returns the required page information about {@link Pizza}s with their {@link Ingredient}s
     *
     * @param page
     *    Number of page to get
     * @param size
     *    Number of elements in every page
     * @param sort
     *    {@link Sort} with how we want to sort the returned results
     *
     * @return {@link Page} of {@link Pizza}
     */
    public Page<Pizza> findPageWithIngredients(final int page,
                                               final int size,
                                               final Sort sort) {
        return ofNullable(
                repository.findPageWithIngredientsWithoutInMemoryPagination(
                        PageUtil.buildPageRequest(
                                page,
                                size,
                                sort
                        )
                )
        )
        .orElseGet(() ->
                new PageImpl<>(new ArrayList<>())
        );
    }


    /**
     * Persist the information included in the given {@link Pizza}
     *
     * @param pizza
     *    {@link Pizza} to save
     *
     * @return {@link Optional} of {@link Pizza} with its "final information" after this action
     */
    public Optional<Pizza> save(final Pizza pizza) {
        return ofNullable(pizza)
                .map(p -> {
                    ingredientService.saveAll(p.getIngredients());
                    return repository.save(p);
                });
    }

}
