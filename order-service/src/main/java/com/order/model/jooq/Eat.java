package com.order.model.jooq;

import com.order.model.jooq.tables.OrderTable;
import com.order.model.jooq.tables.OrderLineTable;
import com.order.model.jooq.tables.PizzaTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

public class Eat extends SchemaImpl {

    private static final long serialVersionUID = -1485920652;

    /**
     * The reference instance of <code>eat</code>
     */
    public static final Eat EAT = new Eat();

    /**
     * The table <code>eat.order</code>.
     */
    public final OrderTable ORDER = OrderTable.ORDER_TABLE;

    /**
     * The table <code>eat.order_line</code>.
     */
    public final OrderLineTable ORDER_LINE = OrderLineTable.ORDER_LINE_TABLE;

    /**
     * The table <code>eat.pizza</code>.
     */
    public final PizzaTable PIZZA = PizzaTable.PIZZA_TABLE;

    /**
     * No further instances allowed
     */
    private Eat() {
        super("eat", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.ORDER_ID_SEQ,
            Sequences.ORDER_LINE_ID_SEQ,
            Sequences.PIZZA_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            OrderTable.ORDER_TABLE,
            OrderLineTable.ORDER_LINE_TABLE,
            PizzaTable.PIZZA_TABLE);
    }
}
