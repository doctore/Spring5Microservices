package com.order.model.jooq;

import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.PizzaTable;

public class Tables {

    /**
     * The table <code>eat.order</code>.
     */
    public static final OrderTable ORDER = OrderTable.ORDER_TABLE;

    /**
     * The table <code>eat.order_line</code>.
     */
    public static final OrderLineTable ORDER_LINE = OrderLineTable.ORDER_LINE_TABLE;

    /**
     * The table <code>eat.pizza</code>.
     */
    public static final PizzaTable PIZZA = PizzaTable.PIZZA_TABLE;

}
