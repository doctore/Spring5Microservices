package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.collection.tuple.Tuple0.comparator;
import static com.spring5microservices.common.collection.tuple.Tuple0.instance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple0Test {

    @Test
    @DisplayName("instance: when is invoked then empty Tuple0 is returned")
    public void instance_whenIsInvoked_thenEmptyTuple0IsReturned() {
        Tuple0 result = instance();
        assertNotNull(result);
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = instance().arity();
        assertEquals(0, result);
    }


    @Test
    @DisplayName("comparator: when is invoked then 0 returned")
    public void comparator_whenIsInvoked_then0IsReturned() {
        int result = comparator().compare(instance(), instance());
        assertEquals(0, result);
    }


    static Stream<Arguments> applyTestCases() {
        Supplier<Integer> fInteger = () -> 1;
        Supplier<Tuple0> fIdentity = () -> instance();
        return Stream.of(
                //@formatter:off
                //            f,           expectedException,                expectedResult
                Arguments.of( null,        IllegalArgumentException.class,   null ),
                Arguments.of( fInteger,    null,                             1 ),
                Arguments.of( fIdentity,   null,                             instance() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <U> void apply_testCases(Supplier<? extends U> f,
                                    Class<? extends Exception> expectedException,
                                    U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> instance().apply(f));
        }
        else {
            assertEquals(expectedResult, instance().apply(f));
        }
    }

}
