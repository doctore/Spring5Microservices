package com.order.model.jooq;

import org.jooq.Sequence;
import org.jooq.impl.SequenceImpl;

public class Sequences {

    /**
     * The sequence <code>eat.order_id_seq</code>
     */
    public static final Sequence<Integer> ORDER_ID_SEQ = new SequenceImpl<Integer>("order_id_seq", Eat.EAT, org.jooq.impl.SQLDataType.INTEGER.nullable(false));

    /**
     * The sequence <code>eat.order_line_id_seq</code>
     */
    public static final Sequence<Integer> ORDER_LINE_ID_SEQ = new SequenceImpl<Integer>("order_line_id_seq", Eat.EAT, org.jooq.impl.SQLDataType.INTEGER.nullable(false));

    /**
     * The sequence <code>eat.pizza_id_seq</code>
     */
    public static final Sequence<Short> PIZZA_ID_SEQ = new SequenceImpl<Short>("pizza_id_seq", Eat.EAT, org.jooq.impl.SQLDataType.SMALLINT.nullable(false));

}
