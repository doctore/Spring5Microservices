package com.order.dao;

import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.model.Pizza;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.PizzaTable;
import com.order.model.jooq.tables.records.OrderLineRecord;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.util.Optional.ofNullable;

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
        return ofNullable(orderLine)
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


    /**
     * Return the {@link List} of {@link OrderLineDto}s and its {@link PizzaDto} information of the given {@link Order#id}
     *
     * @param orderId
     *    {@link Order#id} to find
     *
     * @return {@link List} of {@link OrderLineDto}s.
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public List<OrderLineDto> fetchToOrderLineDtoByOrderIdWithPizzaDto(Integer orderId) {
        OrderLineTable ORDER_LINE = OrderLineTable.ORDER_LINE_TABLE;
        PizzaTable PIZZA = PizzaTable.PIZZA_TABLE;

        try (ResultSet rs =
                     dsl.select(ORDER_LINE.ID, ORDER_LINE.ORDER_ID, ORDER_LINE.AMOUNT, ORDER_LINE.COST
                               ,PIZZA.ID.as("pizza_id"), PIZZA.NAME.as("pizza_name"), PIZZA.COST.as("pizza_cost"))
                        .from(ORDER_LINE)
                        .join(PIZZA).on(PIZZA.ID.eq(ORDER_LINE.PIZZA_ID))
                        .where(ORDER_LINE.ORDER_ID.eq(orderId))
                        .fetchResultSet()) {

            JdbcMapper<OrderLineDto> jdbcMapper = getJdbcMapper(OrderLineDto.class, "id", "pizza_id");
            return jdbcMapper.stream(rs).collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataAccessException(String.format("There was an error trying to find the order lines related with the order: %d", orderId), e);
        }
    }

}
