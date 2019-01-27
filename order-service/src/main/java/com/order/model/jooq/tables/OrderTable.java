package com.order.model.jooq.tables;

import com.order.model.jooq.Eat;
import com.order.model.jooq.Indexes;
import com.order.model.jooq.Keys;
import com.order.model.jooq.tables.records.OrderRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

public class OrderTable extends TableImpl<OrderRecord> {

    private static final long serialVersionUID = -914836592;

    /**
     * The reference instance of <code>eat.order</code>
     */
    public static final OrderTable ORDER_TABLE = new OrderTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OrderRecord> getRecordType() {
        return OrderRecord.class;
    }

    /**
     * The column <code>eat.order.id</code>.
     */
    public final TableField<OrderRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('eat.order_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>eat.order.code</code>.
     */
    public final TableField<OrderRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>eat.order.created</code>.
     */
    public final TableField<OrderRecord, Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>eat.order</code> table reference
     */
    public OrderTable() {
        this(DSL.name("order"), null);
    }

    /**
     * Create an aliased <code>eat.order</code> table reference
     */
    public OrderTable(String alias) {
        this(DSL.name(alias), ORDER_TABLE);
    }

    /**
     * Create an aliased <code>eat.order</code> table reference
     */
    public OrderTable(Name alias) {
        this(alias, ORDER_TABLE);
    }

    private OrderTable(Name alias, Table<OrderRecord> aliased) {
        this(alias, aliased, null);
    }

    private OrderTable(Name alias, Table<OrderRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> OrderTable(Table<O> child, ForeignKey<O, OrderRecord> key) {
        super(child, key, ORDER_TABLE);
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
        return Arrays.<Index>asList(Indexes.ORDER_CODE_UINDEX, Indexes.ORDER_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<OrderRecord, Integer> getIdentity() {
        return Keys.IDENTITY_ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OrderRecord> getPrimaryKey() {
        return Keys.ORDER_PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OrderRecord>> getKeys() {
        return Arrays.<UniqueKey<OrderRecord>>asList(Keys.ORDER_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderTable as(String alias) {
        return new OrderTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderTable as(Name alias) {
        return new OrderTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderTable rename(String name) {
        return new OrderTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public OrderTable rename(Name name) {
        return new OrderTable(name, null);
    }
}
