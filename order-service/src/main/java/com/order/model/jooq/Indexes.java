package com.order.model.jooq;

import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.PizzaTable;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index ORDER_CODE_UINDEX = Indexes0.ORDER_CODE_UINDEX;
    public static final Index ORDER_PK = Indexes0.ORDER_PK;
    public static final Index ORDER_LINE_PK = Indexes0.ORDER_LINE_PK;
    public static final Index PIZZA_NAME_UINDEX = Indexes0.PIZZA_NAME_UINDEX;
    public static final Index PIZZA_PK = Indexes0.PIZZA_PK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index ORDER_CODE_UINDEX = Internal.createIndex("order_code_uindex", OrderTable.ORDER_TABLE, new OrderField[] { OrderTable.ORDER_TABLE.CODE }, true);
        public static Index ORDER_PK = Internal.createIndex("order_pk", OrderTable.ORDER_TABLE, new OrderField[] { OrderTable.ORDER_TABLE.ID }, true);
        public static Index ORDER_LINE_PK = Internal.createIndex("order_line_pk", OrderLineTable.ORDER_LINE_TABLE, new OrderField[] { OrderLineTable.ORDER_LINE_TABLE.ID }, true);
        public static Index PIZZA_NAME_UINDEX = Internal.createIndex("pizza_name_uindex", PizzaTable.PIZZA_TABLE, new OrderField[] { PizzaTable.PIZZA_TABLE.NAME }, true);
        public static Index PIZZA_PK = Internal.createIndex("pizza_pk", PizzaTable.PIZZA_TABLE, new OrderField[] { PizzaTable.PIZZA_TABLE.ID }, true);
    }
}
