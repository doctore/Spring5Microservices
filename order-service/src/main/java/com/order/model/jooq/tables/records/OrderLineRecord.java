package com.order.model.jooq.tables.records;

import com.order.model.jooq.tables.OrderLineTable;

import javax.validation.constraints.NotNull;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;

public class OrderLineRecord extends UpdatableRecordImpl<OrderLineRecord> implements Record5<Integer, Integer, Short, Double, Short> {

    private static final long serialVersionUID = -445302359;

    /**
     * Setter for <code>eat.order_line.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>eat.order_line.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>eat.order_line.order_id</code>.
     */
    public void setOrderId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>eat.order_line.order_id</code>.
     */
    @NotNull
    public Integer getOrderId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>eat.order_line.pizza_id</code>.
     */
    public void setPizzaId(Short value) {
        set(2, value);
    }

    /**
     * Getter for <code>eat.order_line.pizza_id</code>.
     */
    @NotNull
    public Short getPizzaId() {
        return (Short) get(2);
    }

    /**
     * Setter for <code>eat.order_line.cost</code>.
     */
    public void setCost(Double value) {
        set(3, value);
    }

    /**
     * Getter for <code>eat.order_line.cost</code>.
     */
    @NotNull
    public Double getCost() {
        return (Double) get(3);
    }

    /**
     * Setter for <code>eat.order_line.amount</code>.
     */
    public void setAmount(Short value) {
        set(4, value);
    }

    /**
     * Getter for <code>eat.order_line.amount</code>.
     */
    @NotNull
    public Short getAmount() {
        return (Short) get(4);
    }


    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, Short, Double, Short> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, Short, Double, Short> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return OrderLineTable.ORDER_LINE_TABLE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return OrderLineTable.ORDER_LINE_TABLE.ORDER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field3() {
        return OrderLineTable.ORDER_LINE_TABLE.PIZZA_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Double> field4() {
        return OrderLineTable.ORDER_LINE_TABLE.COST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Short> field5() {
        return OrderLineTable.ORDER_LINE_TABLE.AMOUNT;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getOrderId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component3() {
        return getPizzaId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double component4() {
        return getCost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short component5() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getOrderId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value3() {
        return getPizzaId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double value4() {
        return getCost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Short value5() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord value2(Integer value) {
        setOrderId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord value3(Short value) {
        setPizzaId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord value4(Double value) {
        setCost(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord value5(Short value) {
        setAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderLineRecord values(Integer value1, Integer value2, Short value3, Double value4, Short value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OrderLineRecord
     */
    public OrderLineRecord() {
        super(OrderLineTable.ORDER_LINE_TABLE);
    }

    /**
     * Create a detached, initialised OrderLineRecord
     */
    public OrderLineRecord(Integer id, Integer orderId, Short pizzaId, Double cost, Short amount) {
        super(OrderLineTable.ORDER_LINE_TABLE);

        set(0, id);
        set(1, orderId);
        set(2, pizzaId);
        set(3, cost);
        set(4, amount);
    }
}
