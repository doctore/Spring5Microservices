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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectsUtilTest {

    static Stream<Arguments> getOrElse_GenericDefaultValue_NoMapperTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getInternalPropertyValue(), null);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( null,              12L,                  12L ),
                Arguments.of( pizza.getName(),   null,                 pizza.getName() ),
                Arguments.of( pizza.getName(),   "testDefaultValue",   pizza.getName() ),
                Arguments.of( pizza.getCost(),   45.1D,                45.1D )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_GenericDefaultValue_NoMapperTestCases")
    @DisplayName("getOrElse: using a generic default value without mapper parameter test cases")
    public <T> void getOrElse_GenericDefaultValue_NoMapper_testCases(T sourceInstance,
                                                                     T defaultValue,
                                                                     T expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, defaultValue));
    }


    static Stream<Arguments> getOrElse_GenericDefaultValue_AllParametersTestCases() {
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,    defaultValue,         expectedResult
                Arguments.of( null,                     null,      null,                 null ),
                Arguments.of( null,                     null,      "testDefaultValue",   "testDefaultValue" ),
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
    @MethodSource("getOrElse_GenericDefaultValue_AllParametersTestCases")
    @DisplayName("getOrElse: using a generic default value with all parameters test cases")
    public <T, E> void getOrElse_GenericDefaultValue_AllParameters_testCases(T sourceInstance,
                                                                             Function<? super T, ? extends E> mapper,
                                                                             E defaultValue,
                                                                             E expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, mapper, defaultValue));
    }


    static Stream<Arguments> getOrElse_StringDefaultValue_NoMapperTestCases() {
        PizzaDto pizza = new PizzaDto(CARBONARA.getInternalPropertyValue(), null);
        return Stream.of(
                //@formatter:off
                //            sourceInstance,    defaultValue,         expectedResult
                Arguments.of( null,              null,                 null ),
                Arguments.of( null,              "testDefaultValue",   "testDefaultValue" ),
                Arguments.of( pizza.getName(),   null,                 pizza.getName() ),
                Arguments.of( pizza.getName(),   "testDefaultValue",   pizza.getName() ),
                Arguments.of( pizza,             null,                 "PizzaDto(name=Carbonara, cost=null)" ),
                Arguments.of( pizza,             "testDefaultValue",   "PizzaDto(name=Carbonara, cost=null)" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_StringDefaultValue_NoMapperTestCases")
    @DisplayName("getOrElse: using a string default value without mapper parameter test cases")
    public <T> void getOrElse_StringDefaultValue_NoMapper_testCases(T sourceInstance,
                                                                    String defaultValue,
                                                                    String expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, defaultValue));
    }


    static Stream<Arguments> getOrElse_StringDefaultValue_AllParametersTestCases() {
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 7D);
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,           mapper,    defaultValue,         expectedResult
                Arguments.of( null,                     null,      null,                 null ),
                Arguments.of( null,                     null,      "testDefaultValue",   "testDefaultValue" ),
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
    @MethodSource("getOrElse_StringDefaultValue_AllParametersTestCases")
    @DisplayName("getOrElse: using a string default value with all parameters test cases")
    public <T, E> void getOrElse_StringDefaultValue_AllParameters_testCases(T sourceInstance,
                                                                            Function<? super T, ? extends E> mapper,
                                                                            String defaultValue,
                                                                            String expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, mapper, defaultValue));
    }

}
