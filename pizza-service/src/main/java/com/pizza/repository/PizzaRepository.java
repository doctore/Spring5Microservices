package com.pizza.repository;

import com.pizza.configuration.Constants;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.model.QPizza;
import com.pizza.repository.base.ExtendedJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Repository
public interface PizzaRepository extends ExtendedJpaRepository<Pizza, Integer>, QuerydslPredicateExecutor<Pizza> {

    /**
     *    Gets paged all the {@link Pizza}s with their {@link Ingredient}s using the given {@link Pageable}
     * to configure the required one.
     *
     * @apiNote To avoid the pagination in memory executed by this method, a better alternative is
     *          findPageWithIngredientsWithoutInMemoryPagination.
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
     *    Gets paged all the {@link Pizza}s with their {@link Ingredient}s using the given {@link Pageable}
     * to configure the required one.
     *
     * @apiNote In this case, the pagination will do in database, not in memory like in findPageWithIngredients
     *          method.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Pizza}
     */
    default Page<Pizza> findPageWithIngredientsWithoutInMemoryPagination(@Nullable final Pageable pageable) {
        if (null == pageable) {
            return findPageWithIngredients(pageable);
        }
        int rankInitial = (pageable.getPageNumber() * pageable.getPageSize()) + 1;
        int rankFinal = rankInitial + pageable.getPageSize() - 1;

        String orderByClause = (null == pageable.getSort() || pageable.getSort().isUnsorted())
                ? "id desc "
                : String.join(",",
                              pageable.getSort().stream()
                                      .map(s -> s.getProperty() + " " + s.getDirection().name())
                                      .collect(Collectors.toList()));

        List<Object[]> rawResults = getEntityManager().createNativeQuery("select p_i_r.id, p_i_r.name, p_i_r.cost, p_i_r.ingredients_id, p_i_r.ingredients_name "
                                                                       + "from (select *, dense_rank() over (order by " + orderByClause + ") rank "
                                                                       + "      from (select p.id, p.name, p.cost, i.id ingredients_id, i.name ingredients_name "
                                                                       + "            from eat.pizza p "
                                                                       + "            left join eat.pizza_ingredient pi on pi.pizza_id = p.id "
                                                                       + "            left join eat.ingredient i on i.id = pi.ingredient_id "
                                                                       + "            order by " + orderByClause
                                                                       + "           ) p_i "
                                                                       + "     ) p_i_r "
                                                                       + "where p_i_r.rank between :rankInitial and :rankFinal"
                                                                      ,Constants.SQL_RESULT_MAPPING.PIZZA_INGREDIENTS)
                                                      .setParameter("rankInitial", rankInitial)
                                                      .setParameter("rankFinal", rankFinal)
                                                      .getResultList();
        // Group by every Pizza and its ingredients
        Map<Pizza, Set<Ingredient>> mapPizzaIngredient = new LinkedHashMap<>();
        rawResults.forEach(object -> mapPizzaIngredient.computeIfAbsent(
                (Pizza)object[0], v ->
                        new LinkedHashSet<>()).add((Ingredient)object[1])
        );
        List<Pizza> pizzas = new ArrayList<>();
        mapPizzaIngredient.forEach((pizza, ingredients) -> {
            pizza.setIngredients(ingredients);
            pizzas.add(pizza);
        });
        return new PageImpl<>(pizzas, pageable, this.count());
    }


    /**
     * Gets the {@link Pizza} (including its {@link Ingredient}s) which name matches with the given one.
     *
     * @param name
     *    Name to search a coincidence in {@link Pizza#getName()}
     *
     * @return {@link Optional} with the {@link Pizza} which name matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    @EntityGraph(attributePaths = "ingredients")
    Optional<Pizza> findWithIngredientsByName(@Nullable PizzaEnum name);


    /**
     * Gets the {@link Pizza} which name matches with the given one.
     *
     * @param name
     *    Name to search a coincidence in {@link Pizza#getName()}
     *
     * @return {@link Optional} with the {@link Pizza} which name matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<Pizza> findByName(@Nullable final PizzaEnum name) {
        return ofNullable(name)
                .flatMap(n -> findOne(QPizza.pizza.name.eq(n)));
    }

}
