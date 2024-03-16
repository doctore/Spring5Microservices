package com.spring5microservices.common.interfaces.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuadConsumerTest {

    @Test
    @DisplayName("accept: then the defined operation is performed based on provided arguments")
    public void accept_thenTheDefinedOperationIsPerformedBasedOnProvidedArguments() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15
        );

        QuadConsumer<Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s, b, i2) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s);
            list.set(2, b);
            list.set(3, (Integer)list.get(3) + i2);
        };

        plusNAndAddSAndChangeB.accept(
                5,
                "V2",
                false,
                7
        );

        assertEquals(15, list.get(0));
        assertEquals("abcV2", list.get(1));
        assertEquals(false, list.get(2));
        assertEquals(22, list.get(3));
    }


    @Test
    @DisplayName("andThen: when after is null then NullPointerException is thrown")
    public void andThen_whenAfterIsNull_thenNullPointerExceptionIsThrown() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15
        );

        QuadConsumer<Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s, b, i2) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s);
            list.set(2, b);
            list.set(3, (Integer)list.get(3) + i2);
        };

        assertThrows(
                NullPointerException.class,
                () -> plusNAndAddSAndChangeB.andThen(null)
        );
    }


    @Test
    @DisplayName("andThen: when after is not null then after is applied after current consumer")
    public void andThen_whenAfterIsNotNull_thenAfterIsAppliedAfterCurrentConsumer() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15
        );

        QuadConsumer<Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s, b, i2) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s);
            list.set(2, b);
            list.set(3, (Integer)list.get(3) + i2);
        };

        plusNAndAddSAndChangeB.andThen(
                plusNAndAddSAndChangeB
        ).accept(
                5,
                "V2",
                false,
                7
        );

        assertEquals(20, list.get(0));
        assertEquals("abcV2V2", list.get(1));
        assertEquals(false, list.get(2));
        assertEquals(29, list.get(3));
    }

}
