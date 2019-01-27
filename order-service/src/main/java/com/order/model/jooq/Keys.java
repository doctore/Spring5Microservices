package com.order.model.jooq;

import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.PizzaTable;
import com.order.model.jooq.tables.records.OrderLineRecord;
import com.order.model.jooq.tables.records.OrderRecord;
import com.order.model.jooq.tables.records.PizzaRecord;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<OrderRecord, Integer> IDENTITY_ORDER = Identities0.IDENTITY_ORDER;
    public static final Identity<OrderLineRecord, Integer> IDENTITY_ORDER_LINE = Identities0.IDENTITY_ORDER_LINE;
    public static final Identity<PizzaRecord, Short> IDENTITY_PIZZA = Identities0.IDENTITY_PIZZA;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<OrderRecord> ORDER_PK = UniqueKeys0.ORDER_PK;
    public static final UniqueKey<OrderLineRecord> ORDER_LINE_PK = UniqueKeys0.ORDER_LINE_PK;
    public static final UniqueKey<PizzaRecord> PIZZA_PK = UniqueKeys0.PIZZA_PK;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<OrderLineRecord, OrderRecord> ORDER_LINE__ORDER_LINE_ORDER_ID_FK = ForeignKeys0.ORDER_LINE__ORDER_LINE_ORDER_ID_FK;
    public static final ForeignKey<OrderLineRecord, PizzaRecord> ORDER_LINE__ORDER_LINE_PIZZA_ID_FK = ForeignKeys0.ORDER_LINE__ORDER_LINE_PIZZA_ID_FK;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<OrderRecord, Integer> IDENTITY_ORDER = Internal.createIdentity(OrderTable.ORDER_TABLE, OrderTable.ORDER_TABLE.ID);
        public static Identity<OrderLineRecord, Integer> IDENTITY_ORDER_LINE = Internal.createIdentity(OrderLineTable.ORDER_LINE_TABLE, OrderLineTable.ORDER_LINE_TABLE.ID);
        public static Identity<PizzaRecord, Short> IDENTITY_PIZZA = Internal.createIdentity(PizzaTable.PIZZA_TABLE, PizzaTable.PIZZA_TABLE.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<OrderRecord> ORDER_PK = Internal.createUniqueKey(OrderTable.ORDER_TABLE, "order_pk", OrderTable.ORDER_TABLE.ID);
        public static final UniqueKey<OrderLineRecord> ORDER_LINE_PK = Internal.createUniqueKey(OrderLineTable.ORDER_LINE_TABLE, "order_line_pk", OrderLineTable.ORDER_LINE_TABLE.ID);
        public static final UniqueKey<PizzaRecord> PIZZA_PK = Internal.createUniqueKey(PizzaTable.PIZZA_TABLE, "pizza_pk", PizzaTable.PIZZA_TABLE.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<OrderLineRecord, OrderRecord> ORDER_LINE__ORDER_LINE_ORDER_ID_FK = Internal.createForeignKey(com.order.model.jooq.Keys.ORDER_PK, OrderLineTable.ORDER_LINE_TABLE, "order_line__order_line_order_id_fk", OrderLineTable.ORDER_LINE_TABLE.ORDER_ID);
        public static final ForeignKey<OrderLineRecord, PizzaRecord> ORDER_LINE__ORDER_LINE_PIZZA_ID_FK = Internal.createForeignKey(com.order.model.jooq.Keys.PIZZA_PK, OrderLineTable.ORDER_LINE_TABLE, "order_line__order_line_pizza_id_fk", OrderLineTable.ORDER_LINE_TABLE.PIZZA_ID);
    }

}
