package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static com.spring5microservices.common.util.ObjectUtil.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectUtilTest {

    static Stream<Arguments> isEmpty_ArrayAsParameterTestCases() {
        Object[] emptyArray = {};
        Integer[] notEmptyArray = { 1 };
        return Stream.of(
                //@formatter:off
                //            sourceArray,     expectedResult
                Arguments.of( null,            true ),
                Arguments.of( emptyArray,      true ),
                Arguments.of( notEmptyArray,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmpty_ArrayAsParameterTestCases")
    @DisplayName("isEmpty: with array as parameter test cases")
    public void isEmpty_ArrayAsParameter_testCases(Object[] array,
                                                   boolean expectedResult) {
        assertEquals(expectedResult, isEmpty(array));
    }


    static Stream<Arguments> getOrElse_GenericDefaultValue_SourceDefaultParametersTestCases() {
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
    @MethodSource("getOrElse_GenericDefaultValue_SourceDefaultParametersTestCases")
    @DisplayName("getOrElse: with source and default value as parameters test cases")
    public <T> void getOrElse_GenericDefaultValue_SourceDefaultParameters_testCases(T sourceInstance,
                                                                                    T defaultValue,
                                                                                    T expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, defaultValue));
    }


    static Stream<Arguments> getOrElse_GenericDefaultValue_SourcePredicateDefaultParametersTestCases() {
        String emptyString = "   ";
        String notEmptyString = "Test string";
        double nine = 9D;
        double eleven = 11D;

        Predicate<String> notEmptyStringPredicate = StringUtils::hasText;
        Predicate<Double> higherThan10Predicate = d -> 10 < d;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,   predicateToMatch,          defaultValue,     expectedResult
                Arguments.of( null,             null,                      null,             null ),
                Arguments.of( null,             null,                      notEmptyString,   notEmptyString ),
                Arguments.of( null,             notEmptyStringPredicate,   null,             null ),
                Arguments.of( null,             notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( emptyString,      null,                      notEmptyString,   emptyString ),
                Arguments.of( emptyString,      notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( notEmptyString,   notEmptyStringPredicate,   emptyString,      notEmptyString ),
                Arguments.of( nine,             null,                      eleven,           nine ),
                Arguments.of( nine,             higherThan10Predicate,     eleven,           eleven ),
                Arguments.of( eleven,           higherThan10Predicate,     eleven,           eleven )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_GenericDefaultValue_SourcePredicateDefaultParametersTestCases")
    @DisplayName("getOrElse: with source, predicate and default value parameters test cases")
    public <T> void getOrElse_GenericDefaultValue_SourcePredicateDefaultParameters_testCases(T sourceInstance,
                                                                                             Predicate<? super T> predicateToMatch,
                                                                                             T defaultValue,
                                                                                             T expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, predicateToMatch, defaultValue));
    }


    static Stream<Arguments> getOrElse_GenericDefaultValue_SourceMapperDefaultParametersTestCases() {
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
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
    @MethodSource("getOrElse_GenericDefaultValue_SourceMapperDefaultParametersTestCases")
    @DisplayName("getOrElse: using source, mapper and default value parameters test cases")
    public <T, E> void getOrElse_GenericDefaultValue_SourceMapperDefaultParameters_testCases(T sourceInstance,
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
        PizzaDto pizzaWithoutProperties = new PizzaDto(null, null);
        PizzaDto pizzaWithAllProperties = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 7D);
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
