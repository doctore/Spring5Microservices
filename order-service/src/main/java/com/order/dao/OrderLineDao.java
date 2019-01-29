package com.order.dao;

import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.model.Pizza;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.records.OrderLineRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderLineDao extends ParentDao<OrderLineRecord, OrderLine, Integer> {

    /**
     * Create a new OrderLineDao with an attached configuration
     */
    @Autowired
    public OrderLineDao(DSLContext dslContext) {
        super(OrderLineTable.ORDER_LINE_TABLE, OrderLine.class, dslContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(OrderLine orderLine) {
        return Optional.ofNullable(orderLine)
                       .map(OrderLine::getId)
                       .orElse(null);
    }


    /**
     *    Get the {@link List} of {@link OrderLine}s which identifiers match with the
     * given ones.
     *
     * @param ids
     *    {@link List} of {@link OrderLine#id} to find
     *
     * @return {@link List} of {@link OrderLine}s
     */
    public List<OrderLine> findByIds(Integer... ids) {
        return fetch(OrderLineTable.ORDER_LINE_TABLE.ID, ids);
    }


    /**
     * Get the {@link OrderLine}s which identifier matches with the given one.
     *
     * @param id
     *    {@link OrderLine#id} to find
     *
     * @return {@link Optional} with the {@link OrderLine} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<OrderLine> findOptionalById(Integer id) {
        return fetchOptional(OrderLineTable.ORDER_LINE_TABLE.ID, id);
    }


    /**
     * Get the {@link List} of {@link OrderLine}s belong to the given {@link Order#id}.
     *
     * @param orderIds
     *    Array of {@link Order#id}s to find
     *
     * @return {@link List} of {@link OrderLine}s
     */
    public List<OrderLine> findByOrderIds(Integer... orderIds) {
        return fetch(OrderLineTable.ORDER_LINE_TABLE.ORDER_ID, orderIds);
    }


    /**
     * Get the {@link List} of {@link OrderLine}s belong to the given {@link Pizza#id}.
     *
     * @param pizzaIds
     *    Array of {@link Pizza#id}s to find
     *
     * @return {@link List} of {@link OrderLine}s
     */
    public List<OrderLine> findByPizzaIds(Short... pizzaIds) {
        return fetch(OrderLineTable.ORDER_LINE_TABLE.PIZZA_ID, pizzaIds);
    }

}
