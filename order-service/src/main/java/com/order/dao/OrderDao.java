package com.order.dao;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.PizzaTable;
import com.order.model.jooq.tables.records.OrderRecord;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.denseRank;
import static org.jooq.impl.DSL.orderBy;

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
     *    Return the {@link OrderDto} and its {@link OrderLineDto} information (and related {@link PizzaDto})
     * of the given {@link OrderDto#id}.
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return {@link Optional} with the {@link OrderDto} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Optional<OrderDto> fetchToOrderDtoByIdWithOrderLineDto(Integer id) {
        try (ResultSet rs = getOrderWithLinesQuery().where(OrderTable.ORDER_TABLE.ID.eq(id))
                                                    .fetchResultSet()) {

            JdbcMapper<OrderDto> jdbcMapper = getJdbcMapper(OrderDto.class, "id", "order_lines_id", "pizza_id");
            return jdbcMapper.stream(rs).findFirst();
        } catch (Exception e) {
            throw new DataAccessException(String.format("There was an error trying to find the order: %d", id), e);
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


    /**
     *    Return a "page of {@link OrderDto}" (with its {@link OrderLineDto} and related {@link PizzaDto}), ordered by
     * {@link Order#created} desc.
     *
     * @param page
     *    Desired page to get (taking into account the value of the given size)
     * @param size
     *    Number of {@link OrderDto}s included in every page
     *
     * @return {@link List} of {@link OrderDto} ordered by {@link Order#created} desc
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Set<OrderDto> fetchPageToOrderDtoByIdWithOrderLineDto(int page, int size) {

        if (0 > page || 0 >= size)
            return new LinkedHashSet<>();

        int rankInitial = (page * size) + 1;
        int rankFinal = rankInitial + size - 1;

        // Build the table with required information about orders and their orderlines
        Table<Record10<Integer, String, Timestamp, Integer, Integer, Short, Double, Short, String, Double>> orderWithLines =
                getOrderWithLinesQuery().orderBy(OrderTable.ORDER_TABLE.CREATED.desc()).asTable("orderWithLines");

        // Use denseRank function to group the required results (and know the final number of rows to return)
        Table<Record> orderWithLinesAndRank = dsl.select(orderWithLines.asterisk()
                                                        ,denseRank().over(orderBy(orderWithLines.field("created").desc())).as("rank"))
                                                 .from(orderWithLines).asTable("orderWithLinesAndRank");

        try (ResultSet rs = dsl.select(orderWithLinesAndRank.field("id"), orderWithLinesAndRank.field("code")
                                      ,   orderWithLinesAndRank.field("created")
                                      ,orderWithLinesAndRank.field("order_lines_id"), orderWithLinesAndRank.field("order_lines_orderId")
                                      ,   orderWithLinesAndRank.field("order_lines_amount"), orderWithLinesAndRank.field("order_lines_cost")
                                      ,orderWithLinesAndRank.field("order_lines_pizza_id"), orderWithLinesAndRank.field("order_lines_pizza_name")
                                      ,   orderWithLinesAndRank.field("order_lines_pizza_cost"))
                               .from(orderWithLinesAndRank)
                               .where(orderWithLinesAndRank.field("rank").cast(Integer.TYPE).between(rankInitial, rankFinal))
                               .fetchResultSet()) {

            JdbcMapper<OrderDto> jdbcMapper = getJdbcMapper(OrderDto.class, "id", "order_lines_id", "pizza_id");
            return jdbcMapper.stream(rs).collect(Collectors.toCollection(LinkedHashSet::new));

        } catch (Exception e) {
            throw new DataAccessException(String.format("There was an error trying to find the orders "
                                                      + "using page: %d and size: %d", page, size), e);
        }
    }


    /**
     * Build the query used to get the information related with {@link Order}s and its {@link OrderLine}s
     *
     * @return {@link SelectOnConditionStep} with the "partial query"
     */
    private SelectOnConditionStep<Record10<Integer, String, Timestamp, Integer, Integer, Short, Double, Short, String, Double>> getOrderWithLinesQuery() {
        OrderTable ORDER = OrderTable.ORDER_TABLE;
        OrderLineTable ORDER_LINE = OrderLineTable.ORDER_LINE_TABLE;
        PizzaTable PIZZA = PizzaTable.PIZZA_TABLE;

        return dsl.select(ORDER.ID, ORDER.CODE, ORDER.CREATED
                         ,ORDER_LINE.ID.as("order_lines_id"), ORDER.ID.as("order_lines_orderId")
                         ,ORDER_LINE.AMOUNT.as("order_lines_amount"), ORDER_LINE.COST.as("order_lines_cost")
                         ,PIZZA.ID.as("order_lines_pizza_id"), PIZZA.NAME.as("order_lines_pizza_name"), PIZZA.COST.as("order_lines_pizza_cost"))
                  .from(ORDER)
                  .leftJoin(ORDER_LINE).on(ORDER_LINE.ORDER_ID.eq(ORDER.ID))
                  .join(PIZZA).on(PIZZA.ID.eq(ORDER_LINE.PIZZA_ID));
    }

}
