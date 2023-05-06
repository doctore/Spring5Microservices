package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.FunctionUtil.fromKeyValueMapperToMapEntry;
import static com.spring5microservices.common.util.FunctionUtil.overwriteWithNew;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionUtilTest {

    static Stream<Arguments> fromKeyValueMapperToMapEntryTestCases() {
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Function<Integer, Integer> plus10 =
                i -> null == i
                        ? 0
                        : i + 10;
        return Stream.of(
                //@formatter:off
                //            t,      keyMapper,         valueMapper,       expectedException,                expectedResult
                Arguments.of( null,   null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( 11,     multiply2String,   null,              IllegalArgumentException.class,   null ),
                Arguments.of( 11,     null,              plus10,            IllegalArgumentException.class,   null ),
                Arguments.of( null,   multiply2String,   plus10,            null,                             Map.entry("", 0) ),
                Arguments.of( 11,     multiply2String,   plus10,            null,                             Map.entry("22", 21) ),
                Arguments.of( 11,     plus10,            multiply2String,   null,                             Map.entry(21, "22") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromKeyValueMapperToMapEntryTestCases")
    @DisplayName("fromKeyValueMapperToMapEntry: test cases")
    public <T, K, V> void fromKeyValueMapperToMapEntry_testCases(T t,
                                                                 Function<? super T, ? extends K> keyMapper,
                                                                 Function<? super T, ? extends V> valueMapper,
                                                                 Class<? extends Exception> expectedException,
                                                                 Map.Entry<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            fromKeyValueMapperToMapEntry(
                                    keyMapper,
                                    valueMapper
                            )
                            .apply(t)
            );
        } else {
            assertEquals(expectedResult,
                    fromKeyValueMapperToMapEntry(
                            keyMapper,
                            valueMapper
                    )
                    .apply(t)
            );
        }
    }


    static Stream<Arguments> overwriteWithNewTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto marguerita = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 15D);
        return Stream.of(
                //@formatter:off
                //            oldInstance,   newInstance,   expectedResult
                Arguments.of( null,          null,          null ),
                Arguments.of( 11,            null,          null ),
                Arguments.of( null,          11,            11 ),
                Arguments.of( carbonara,     marguerita,    marguerita )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("overwriteWithNewTestCases")
    @DisplayName("overwriteWithNew: test cases")
    public <T> void overwriteWithNew_testCases(T oldInstance,
                                               T newInstance,
                                               T expectedResult) {
        assertEquals(expectedResult, overwriteWithNew().apply(oldInstance, newInstance));
    }

}
