package com.order.model.jooq.tables.records;

import com.order.model.jooq.tables.PizzaTable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

public class PizzaRecord extends UpdatableRecordImpl<PizzaRecord> implements Record3<Short, String, Double> {

    private static final long serialVersionUID = 1518305045;

    /**
     * Setter for <code>eat.pizza.id</code>.
     */
    public void setId(Short value) {
        set(0, value);
    }

    /**
     * Getter for <code>eat.pizza.id</code>.
     */
    public Short getId() {
        return (Short) get(0);
    }

    /**
     * Setter for <code>eat.pizza.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>eat.pizza.name</code>.
     */
    @Size(max = 64)
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>eat.pizza.cost</code>.
     */
    public void setCost(Double value) {
        set(2, value);
    }

    /**
     * Getter for <code>eat.pizza.cost</code>.
     */
    @NotNull
    public Double getCost() {
        return (Double) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Short> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Short, String, Double> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Short, String, Double> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field1() {
        return PizzaTable.PIZZA_TABLE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return PizzaTable.PIZZA_TABLE.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field3() {
        return PizzaTable.PIZZA_TABLE.COST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double component3() {
        return getCost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value3() {
        return getCost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaRecord value1(Short value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaRecord value3(Double value) {
        setCost(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaRecord values(Short value1, String value2, Double value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PizzaRecord
     */
    public PizzaRecord() {
        super(PizzaTable.PIZZA_TABLE);
    }

    /**
     * Create a detached, initialised PizzaRecord
     */
    public PizzaRecord(Short id, String name, Double cost) {
        super(PizzaTable.PIZZA_TABLE);

        set(0, id);
        set(1, name);
        set(2, cost);
    }
}
