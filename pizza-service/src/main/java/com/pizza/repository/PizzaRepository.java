package com.pizza.repository;

import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.model.QPizza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Integer>, QuerydslPredicateExecutor<Pizza> {

    /**
     *    Gets paged all the {@link Pizza}s with their {@link Ingredient}s using the given {@link Pageable}
     * to configure the required one.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Pizza}
     */
    @Query(value = "SELECT DISTINCT p FROM Pizza p LEFT JOIN FETCH p.ingredients",
           countQuery = "SELECT COUNT(p) FROM Pizza p")
    Page<Pizza> findPageWithIngredients(@Nullable Pageable pageable);


    /**
     * Gets the {@link Pizza} (including its {@link Ingredient}s) which name matches with the given one.
     *
     * @param name
     *    Name to search a coincidence in {@link Pizza#name}
     *
     * @return {@link Optional} with the {@link Pizza} which name matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    @EntityGraph(attributePaths = "ingredients")
    Optional<Pizza> findWithIngredientsByName(@Nullable String name);


    /**
     * Gets the {@link Pizza} which name matches with the given one.
     *
     * @param name
     *    Name to search a coincidence in {@link Pizza#name}
     *
     * @return {@link Optional} with the {@link Pizza} which name matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<Pizza> findByName(@Nullable String name) {
        return Optional.ofNullable(name)
                       .flatMap(n -> findOne(QPizza.pizza.name.eq(n)));
    }

}
