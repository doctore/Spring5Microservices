package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.ObjectsUtil.getOrElse;
import static com.spring5microservices.common.util.ObjectsUtil.getOrElseString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsUtilTest {

    static Stream<Arguments> getOrElseTestCases() {
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,    defaultValue,         expectedResult
                Arguments.of( null,                     getName,   null,                 null ),
                Arguments.of( null,                     getName,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   getName,   null,                 pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   getName,   "testDefaultValue",   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithoutProperties,   getName,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   getName,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   getCost,   null,                 pizzaWithAllProperties.getCost() ),
                Arguments.of( pizzaWithAllProperties,   getCost,   1111D,                pizzaWithAllProperties.getCost() ),
                Arguments.of( pizzaWithoutProperties,   getCost,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   getCost,   9999D,                9999D )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseTestCases")
    @DisplayName("getOrElse: test cases")
    public <T, E> void getOrElse_testCases(T sourceInstance,
                                           Function<? super T, ? extends E> mapper,
                                           E defaultValue,
                                           E expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, mapper, defaultValue));
    }


    static Stream<Arguments> getOrElseStringTestCases() {
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 7D);
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,    defaultValue,         expectedResult
                Arguments.of( null,                     getName,   null,                 null ),
                Arguments.of( null,                     getName,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   getName,   null,                 pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithAllProperties,   getName,   "testDefaultValue",   pizzaWithAllProperties.getName() ),
                Arguments.of( pizzaWithoutProperties,   getName,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   getName,   "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizzaWithAllProperties,   getCost,   null,                 pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithAllProperties,   getCost,   "1111",               pizzaWithAllProperties.getCost().toString() ),
                Arguments.of( pizzaWithoutProperties,   getCost,   null,                 null ),
                Arguments.of( pizzaWithoutProperties,   getCost,   "9999",               "9999" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseStringTestCases")
    @DisplayName("getOrElseString: test cases")
    public <T, E> void getOrElseString_testCases(T sourceInstance,
                                                 Function<? super T, ? extends E> mapper,
                                                 String defaultValue,
                                                 String expectedResult) {
        assertEquals(expectedResult, getOrElseString(sourceInstance, mapper, defaultValue));
    }

}
