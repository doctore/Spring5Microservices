package com.order.dao;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.PizzaTable;
import com.order.model.jooq.tables.records.OrderRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao extends ParentDao<OrderRecord, Order, Integer> {

    /**
     * Create a new OrderDao with an attached configuration
     */
    @Autowired
    public OrderDao(DSLContext dslContext) {
        super(OrderTable.ORDER_TABLE, Order.class, dslContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(Order order) {
        return Optional.ofNullable(order)
                       .map(Order::getId)
                       .orElse(null);
    }


    /**
     *    Get the {@link List} of {@link Order}s which identifiers match with the
     * given ones.
     *
     * @param ids
     *    {@link List} of {@link Order#id} to find
     *
     * @return {@link List} of {@link Order}s
     */
    public List<Order> findByIds(Integer... ids) {
        return fetch(OrderTable.ORDER_TABLE.ID, ids);
    }


    /**
     * Get the {@link Order}s which identifier matches with the given one.
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return {@link Optional} with the {@link Order} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Order> findOptionalById(Integer id) {
        return fetchOptional(OrderTable.ORDER_TABLE.ID, id);
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#id}
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return {@link Optional} with the {@link OrderDto} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     *
     * @throws SQLException if there is an error executing the query
     */
    public Optional<OrderDto> fetchToOrderDtoByIdWithOrderLineDto(Integer id) throws SQLException {
        OrderTable ORDER = OrderTable.ORDER_TABLE;
        OrderLineTable ORDER_LINE = OrderLineTable.ORDER_LINE_TABLE;
        PizzaTable PIZZA = PizzaTable.PIZZA_TABLE;

        try (ResultSet rs =
                     dsl.select(ORDER.ID, ORDER.CODE, ORDER.CREATED
                               ,ORDER_LINE.ID.as("order_lines_id"), ORDER_LINE.AMOUNT.as("order_lines_amount"), ORDER_LINE.COST.as("order_lines_cost")
                               ,PIZZA.ID.as("order_lines_pizza_id"), PIZZA.NAME.as("order_lines_pizza_name"))
                        .from(ORDER)
                        .leftJoin(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(ORDER.ID))
                        .join(PIZZA).on(PIZZA.ID.eq(ORDER_LINE.PIZZA_ID))
                        .where(ORDER.ID.eq(id))
                        .fetchResultSet()) {

            JdbcMapper<OrderDto> jdbcMapper = getJdbcMapper(OrderDto.class, "id", "order_lines_id", "pizza_id");
            return jdbcMapper.stream(rs).findFirst();
        }
    }


    /**
     * Get the {@link List} of {@link Order}s which codes match with the given ones.
     *
     * @param codes
     *    {@link List} of {@link Order#code} to find
     *
     * @return {@link List} of {@link Order}s
     */
    public List<Order> findByCodes(String... codes) {
        return fetch(OrderTable.ORDER_TABLE.CODE, codes);
    }


    /**
     * Get the {@link Order}s which code matches with the given one.
     *
     * @param code
     *    {@link Order#code} to find
     *
     * @return {@link Optional} with the {@link Order} which code matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<Order> findByCode(String code) {
        return fetchOptional(OrderTable.ORDER_TABLE.CODE, code);
    }

}
