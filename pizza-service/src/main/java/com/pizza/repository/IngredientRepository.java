package com.pizza.repository;

import com.pizza.dto.IngredientPizzaSummaryDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.model.QIngredient;
import com.pizza.model.QPizza;
import com.pizza.repository.base.ExtendedQueryDslJpaRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLExpressions;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public interface IngredientRepository extends ExtendedQueryDslJpaRepository<Ingredient, Integer>, QuerydslPredicateExecutor<Ingredient> {

    /**
     * Return the {@link Ingredient#getName()} with its more expensive {@link Pizza#getName()}
     *
     * @param ingredientNames
     *    {@link Collection} of {@link Ingredient#getName()} to search
     * <p>
     * Generated query will be similar to:
     *
     * <pre>
     *     select ingredient, pizza, cost
     *     from (
     *        select i.name as ingredient, p.name as pizza, p.cost
     *              ,row_number() over (partition by i.name order by p.cost desc) as rnk
     *        from eat.pizza p
     *        join eat.pizza_ingredient pi on pi.pizza_id = p.id
     *        join eat.ingredient i on i.id = pi.ingredient_id
     *        where i.name in ('Bacon', 'Cheese', 'Garlic', 'Seafood', 'Mozzarella', 'Tomato sauce')
     *     ) temp
     *     where rnk = 1
     * </pre>
     *
     * @return {@link List} of {@link IngredientPizzaSummaryDto}
     */
    default List<IngredientPizzaSummaryDto> getIngredientWithItsMoreExpensivePizza(Collection<String> ingredientNames) {
        QIngredient ingredient = QIngredient.ingredient;
        QPizza pizza = QPizza.pizza;

        RelationalPathBase<Object> pizzaIngredient = new RelationalPathBase<>(
                Object.class,
                "pi",
                "eat",
                "pizza_ingredient"
        );
        NumberPath<Integer> pizzaIngredient_PizzaId = Expressions.numberPath(
                Integer.class,
                pizzaIngredient,
                "pizza_id"
        );
        NumberPath<Integer> pizzaIngredient_IngredientId = Expressions.numberPath(
                Integer.class,
                pizzaIngredient,
                "ingredient_id"
        );

        StringPath ingredientPath = Expressions.stringPath("ingredient");
        StringPath pizzaPath = Expressions.stringPath( "pizza");
        NumberPath<Double> costPath = Expressions.numberPath(
                Double.class,
                "cost"
        );

        Expression<Long> rowNumber = SQLExpressions.rowNumber()
                .over()
                .partitionBy(ingredientPath)
                .orderBy(costPath.desc())
                .as("rnk");
        NumberPath<Long> rnk = Expressions.numberPath(Long.class, "rnk");

        SubQueryExpression<Tuple> subQuery = getJPASQLQuery()
                .select(
                        ingredient.name.as(ingredientPath),
                        pizza.name.as(String.valueOf(pizzaPath)),
                        pizza.cost.as(costPath),
                        rowNumber
                )
                .from(pizza)
                .innerJoin(pizzaIngredient).on(pizzaIngredient_PizzaId.eq(pizza.id))
                .innerJoin(ingredient).on(ingredient.id.eq(pizzaIngredient_IngredientId))
                .where(ingredient.name.in(ingredientNames))
                .orderBy(ingredient.name.asc());

        return getJPASQLQuery()
                .select(
                        ingredientPath,
                        pizzaPath,
                        costPath)
                .from(
                        subQuery,
                        Expressions.stringPath("temp")
                )
                .where(rnk.eq(1L))
                .fetch()
                .stream()
                .map(IngredientPizzaSummaryDto::new)
                .collect(toList());
    }

}

