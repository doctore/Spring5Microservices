package com.order.model.jooq.tables;

import com.order.model.jooq.Eat;
import com.order.model.jooq.Indexes;
import com.order.model.jooq.Keys;
import com.order.model.jooq.tables.records.OrderLineRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

public class OrderLineTable extends TableImpl<OrderLineRecord> {

    private static final long serialVersionUID = 1621182688;

    /**
     * The reference instance of <code>eat.order_line</code>
     */
    public static final OrderLineTable ORDER_LINE_TABLE = new OrderLineTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrderLineRecord> getRecordType() {
        return OrderLineRecord.class;
    }

    /**
     * The column <code>eat.order_line.id</code>.
     */
    public final TableField<OrderLineRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('eat.order_line_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>eat.order_line.order_id</code>.
     */
    public final TableField<OrderLineRecord, Integer> ORDER_ID = createField("order_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>eat.order_line.pizza_id</code>.
     */
    public final TableField<OrderLineRecord, Short> PIZZA_ID = createField("pizza_id", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>eat.order_line.cost</code>.
     */
    public final TableField<OrderLineRecord, Double> COST = createField("cost", org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

    /**
     * The column <code>eat.order_line.amount</code>.
     */
    public final TableField<OrderLineRecord, Short> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * Create a <code>eat.order_line</code> table reference
     */
    public OrderLineTable() {
        this(DSL.name("order_line"), null);
    }

    /**
     * Create an aliased <code>eat.order_line</code> table reference
     */
    public OrderLineTable(String alias) {
        this(DSL.name(alias), ORDER_LINE_TABLE);
    }

    /**
     * Create an aliased <code>eat.order_line</code> table reference
     */
    public OrderLineTable(Name alias) {
        this(alias, ORDER_LINE_TABLE);
    }

    private OrderLineTable(Name alias, Table<OrderLineRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrderLineTable(Name alias, Table<OrderLineRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Eat.EAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ORDER_LINE_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<OrderLineRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ORDER_LINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrderLineRecord> getPrimaryKey() {
        return Keys.ORDER_LINE_PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrderLineRecord>> getKeys() {
        return Arrays.<UniqueKey<OrderLineRecord>>asList(Keys.ORDER_LINE_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<OrderLineRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<OrderLineRecord, ?>>asList(Keys.ORDER_LINE__ORDER_LINE_ORDER_ID_FK, Keys.ORDER_LINE__ORDER_LINE_PIZZA_ID_FK);
    }

    public OrderTable order() {
        return new OrderTable(this, Keys.ORDER_LINE__ORDER_LINE_ORDER_ID_FK);
    }

    public PizzaTable pizza() {
        return new PizzaTable(this, Keys.ORDER_LINE__ORDER_LINE_PIZZA_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineTable as(String alias) {
        return new OrderLineTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineTable as(Name alias) {
        return new OrderLineTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderLineTable rename(String name) {
        return new OrderLineTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderLineTable rename(Name name) {
        return new OrderLineTable(name, null);
    }
}
