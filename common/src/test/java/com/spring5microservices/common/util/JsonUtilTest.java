package com.spring5microservices.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.exception.JsonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.util.JsonUtil.fromJson;
import static com.spring5microservices.common.util.JsonUtil.toJson;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonUtilTest {

    static Stream<Arguments> fromJsonDefaultMapperTestCases() {
        String jsonOfEmptyInstance = "{\"name\":null,\"cost\":null}";
        String jsonOfNotEmptyInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";

        Optional<PizzaDto> expectedResultEmptyInstance = of(new PizzaDto());
        Optional<PizzaDto> expectedResultNotEmptyInstance = of(new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D));
        return Stream.of(
                //@formatter:off
                //            sourceJson,               clazzToConvert,   expectedException,     expectedResult
                Arguments.of( notValidJson,             null,             JsonException.class,   null ),
                Arguments.of( notValidJson,             PizzaDto.class,   JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,         PizzaDto.class,   JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,      null,             JsonException.class,   null ),
                Arguments.of( null,                     null,             null,                  empty() ),
                Arguments.of( null,                     PizzaDto.class,   null,                  empty() ),
                Arguments.of( "  ",                     null,             null,                  empty() ),
                Arguments.of( "  ",                     PizzaDto.class,   null,                  empty() ),
                Arguments.of( jsonOfEmptyInstance,      PizzaDto.class,   null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,   PizzaDto.class,   null,                  expectedResultNotEmptyInstance )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromJsonDefaultMapperTestCases")
    @DisplayName("fromJson: with default mapper test cases")
    public <T> void fromJsonDefaultMapper_testCases(String sourceJson,
                                                    Class<T> clazzToConvert,
                                                    Class<? extends Exception> expectedException,
                                                    Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJson(sourceJson, clazzToConvert));
        } else {
            assertEquals(expectedResult, fromJson(sourceJson, clazzToConvert));
        }
    }


    static Stream<Arguments> fromJsonAllParametersTestCases() {
        String jsonOfEmptyInstance = "{\"name\":null,\"cost\":null}";
        String jsonOfNotEmptyInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";
        ObjectMapper objectMapper = new ObjectMapper();

        Optional<PizzaDto> expectedResultEmptyInstance = of(new PizzaDto());
        Optional<PizzaDto> expectedResultNotEmptyInstance = of(new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D));
        return Stream.of(
                //@formatter:off
                //            sourceJson,               clazzToConvert,       objectMapper,   expectedException,     expectedResult
                Arguments.of( notValidJson,             null,             null,               JsonException.class,   null ),
                Arguments.of( notValidJson,             null,             objectMapper,       JsonException.class,   null ),
                Arguments.of( notValidJson,             PizzaDto.class,   null,               JsonException.class,   null ),
                Arguments.of( notValidJson,             PizzaDto.class,   objectMapper,       JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,         PizzaDto.class,   null,               JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,         PizzaDto.class,   objectMapper,       JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,      null,             null,               JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,      null,             objectMapper,       JsonException.class,   null ),
                Arguments.of( null,                     null,             null,               null,                  empty() ),
                Arguments.of( null,                     null,             objectMapper,       null,                  empty() ),
                Arguments.of( null,                     PizzaDto.class,   null,               null,                  empty() ),
                Arguments.of( null,                     PizzaDto.class,   objectMapper,       null,                  empty() ),
                Arguments.of( "  ",                     null,             null,               null,                  empty() ),
                Arguments.of( "  ",                     null,             objectMapper,       null,                  empty() ),
                Arguments.of( "  ",                     PizzaDto.class,   null,               null,                  empty() ),
                Arguments.of( "  ",                     PizzaDto.class,   objectMapper,       null,                  empty() ),
                Arguments.of( jsonOfEmptyInstance,      PizzaDto.class,   null,               null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfEmptyInstance,      PizzaDto.class,   objectMapper,       null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,   PizzaDto.class,   null,               null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,   PizzaDto.class,   objectMapper,       null,                  expectedResultNotEmptyInstance )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromJsonAllParametersTestCases")
    @DisplayName("fromJson: with all parameters test cases")
    public <T> void fromJsonAllParameters_testCases(String sourceJson,
                                                    Class<T> clazzToConvert,
                                                    ObjectMapper objectMapper,
                                                    Class<? extends Exception> expectedException,
                                                    Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJson(sourceJson, clazzToConvert, objectMapper));
        } else {
            assertEquals(expectedResult, fromJson(sourceJson, clazzToConvert, objectMapper));
        }
    }


    static Stream<Arguments> toJsonDefaultMapperTestCases() {
        PizzaDto emptyInstance = new PizzaDto();
        PizzaDto notEmptyInstance = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);

        Optional<String> expectedResultEmptyInstance = of("{\"name\":null,\"cost\":null}");
        Optional<String> expectedResultNotEmptyInstance = of("{\"name\":\"Carbonara\",\"cost\":5.0}");
        return Stream.of(
                //@formatter:off
                //            objectToConvert,    expectedResult
                Arguments.of( null,               empty() ),
                Arguments.of( emptyInstance,      expectedResultEmptyInstance ),
                Arguments.of( notEmptyInstance,   expectedResultNotEmptyInstance )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonDefaultMapperTestCases")
    @DisplayName("toJson: with default mapper test cases")
    public void toJsonDefaultMapper_testCases(PizzaDto objectToConvert,
                                              Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(objectToConvert));
    }


    static Stream<Arguments> toJsonAllParametersTestCases() {
        PizzaDto emptyInstance = new PizzaDto();
        PizzaDto notEmptyInstance = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        ObjectMapper objectMapper = new ObjectMapper();

        Optional<String> expectedResultEmptyInstance = of("{\"name\":null,\"cost\":null}");
        Optional<String> expectedResultNotEmptyInstance = of("{\"name\":\"Carbonara\",\"cost\":5.0}");
        return Stream.of(
                //@formatter:off
                //            objectToConvert,    objectMapper,   expectedResult
                Arguments.of( null,               null,           empty() ),
                Arguments.of( null,               objectMapper,   empty() ),
                Arguments.of( emptyInstance,      null,           expectedResultEmptyInstance ),
                Arguments.of( emptyInstance,      objectMapper,   expectedResultEmptyInstance ),
                Arguments.of( notEmptyInstance,   null,           expectedResultNotEmptyInstance ),
                Arguments.of( notEmptyInstance,   objectMapper,   expectedResultNotEmptyInstance )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonAllParametersTestCases")
    @DisplayName("toJson: with all parameters test cases")
    public void toJsonallParameters_testCases(PizzaDto objectToConvert,
                                              ObjectMapper objectMapper,
                                              Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(objectToConvert, objectMapper));
    }

}
