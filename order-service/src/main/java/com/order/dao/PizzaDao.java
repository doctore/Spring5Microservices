package com.order.dao;

import com.order.model.Pizza;
import com.order.model.jooq.tables.PizzaTable;
import com.order.model.jooq.tables.records.PizzaRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.util.Optional.ofNullable;

@Repository
public class PizzaDao extends ParentDao<PizzaRecord, Pizza, Short> {

    /**
     * Create a new PizzaDao with an attached configuration
     */
    @Autowired
    public PizzaDao(DSLContext dslContext) {
        super(
                PizzaTable.PIZZA_TABLE,
                Pizza.class,
                dslContext
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getId(final Pizza pizza) {
        return ofNullable(pizza)
                .map(Pizza::getId)
                .orElse(null);
    }


    /**
     *    Get the {@link List} of {@link Pizza}s which identifiers match with the
     * given ones.
     *
     * @param ids
     *    {@link List} of {@link Pizza#getId()} to find
     *
     * @return {@link List} of {@link Pizza}s
     */
    public List<Pizza> findByIds(Short... ids) {
        return fetch(
                PizzaTable.PIZZA_TABLE.ID,
                ids
        );
    }


    /**
     * Get the {@link Pizza}s which identifier matches with the given one.
     *
     * @param id
     *    {@link Pizza#getId()} to find
     *
     * @return {@link Optional} with the {@link Pizza} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<Pizza> findOptionalById(final Short id) {
        return fetchOptional(
                PizzaTable.PIZZA_TABLE.ID,
                id
        );
    }


    /**
     *    Get the {@link List} of {@link Pizza}s which names match with the
     * given ones.
     *
     * @param names
     *    {@link List} of {@link Pizza#getName()} to find
     *
     * @return {@link List} of {@link Pizza}s
     */
    public List<Pizza> findByNames(String... names) {
        return fetch(
                PizzaTable.PIZZA_TABLE.NAME,
                names
        );
    }


    /**
     * Get the {@link Pizza}s which name matches with the given one.
     *
     * @param name
     *    {@link Pizza#getName()} to find
     *
     * @return {@link Optional} with the {@link Pizza} which name matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<Pizza> findByName(final String name) {
        return fetchOptional(
                PizzaTable.PIZZA_TABLE.NAME,
                name
        );
    }

}
