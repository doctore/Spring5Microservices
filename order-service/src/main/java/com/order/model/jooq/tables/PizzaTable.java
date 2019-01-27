package com.order.model.jooq.tables;

import com.order.model.jooq.Eat;
import com.order.model.jooq.Indexes;
import com.order.model.jooq.Keys;
import com.order.model.jooq.tables.records.PizzaRecord;

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

public class PizzaTable extends TableImpl<PizzaRecord> {

    private static final long serialVersionUID = 695709838;

    /**
     * The reference instance of <code>eat.pizza</code>
     */
    public static final PizzaTable PIZZA_TABLE = new PizzaTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PizzaRecord> getRecordType() {
        return PizzaRecord.class;
    }

    /**
     * The column <code>eat.pizza.id</code>.
     */
    public final TableField<PizzaRecord, Short> ID = createField("id", org.jooq.impl.SQLDataType.SMALLINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('eat.pizza_id_seq'::regclass)", org.jooq.impl.SQLDataType.SMALLINT)), this, "");

    /**
     * The column <code>eat.pizza.name</code>.
     */
    public final TableField<PizzaRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(64), this, "");

    /**
     * The column <code>eat.pizza.cost</code>.
     */
    public final TableField<PizzaRecord, Double> COST = createField("cost", org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

    /**
     * Create a <code>eat.pizza</code> table reference
     */
    public PizzaTable() {
        this(DSL.name("pizza"), null);
    }

    /**
     * Create an aliased <code>eat.pizza</code> table reference
     */
    public PizzaTable(String alias) {
        this(DSL.name(alias), PIZZA_TABLE);
    }

    /**
     * Create an aliased <code>eat.pizza</code> table reference
     */
    public PizzaTable(Name alias) {
        this(alias, PIZZA_TABLE);
    }

    private PizzaTable(Name alias, Table<PizzaRecord> aliased) {
        this(alias, aliased, null);
    }

    private PizzaTable(Name alias, Table<PizzaRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> PizzaTable(Table<O> child, ForeignKey<O, PizzaRecord> key) {
        super(child, key, PIZZA_TABLE);
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
        return Arrays.<Index>asList(Indexes.PIZZA_NAME_UINDEX, Indexes.PIZZA_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PizzaRecord, Short> getIdentity() {
        return Keys.IDENTITY_PIZZA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PizzaRecord> getPrimaryKey() {
        return Keys.PIZZA_PK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PizzaRecord>> getKeys() {
        return Arrays.<UniqueKey<PizzaRecord>>asList(Keys.PIZZA_PK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaTable as(String alias) {
        return new PizzaTable(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaTable as(Name alias) {
        return new PizzaTable(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PizzaTable rename(String name) {
        return new PizzaTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public PizzaTable rename(Name name) {
        return new PizzaTable(name, null);
    }
}
