package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.exception.JsonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonUtilTest {

    static Stream<Arguments> toJsonTestCases() {
        return Stream.of(
                //@formatter:off
                //            objectToConvert,                                               expectedResult
                Arguments.of( null,                                                          empty() ),
                Arguments.of( new PizzaDto(),                                                of("{\"name\":null,\"cost\":null}") ),
                Arguments.of( new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D),   of("{\"name\":\"Carbonara\",\"cost\":5.0}") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonTestCases")
    @DisplayName("toJson: test cases")
    public void toJsonTestCases_testCases(PizzaDto objectToConvert, Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(objectToConvert));
    }


    @Test
    @DisplayName("fromJson: when given string is not a well format json or do not match with result class then JsonException is thrown")
    public void fromJson_whenGivenStringIsNotAWellFormatJsonOneOrDoNotMatchWithResultClass_thenRJsonExceptionIsThrown() {
        assertAll(
                () -> assertThrows(JsonException.class, () -> fromJson("{$}", PizzaDto.class)),
                () -> assertThrows(JsonException.class, () -> fromJson("{\"id\":null}", PizzaDto.class))
        );
    }


    static Stream<Arguments> fromJsonTestCases() {
        return Stream.of(
                //@formatter:off
                //            stringToConvert,                           classOfReturnObject,   expectedResult
                Arguments.of( null,                                      null,                  empty() ),
                Arguments.of( null,                                      PizzaDto.class,        empty() ),
                Arguments.of( "",                                        null,                  empty() ),
                Arguments.of( "",                                        PizzaDto.class,        empty() ),
                Arguments.of( "{\"name\":null,\"cost\":null}",           PizzaDto.class,        of(new PizzaDto()) ),
                Arguments.of( "{\"name\":\"Carbonara\",\"cost\":5.0}",   PizzaDto.class,        of(new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromJsonTestCases")
    @DisplayName("fromJson: test cases")
    public <T> void toJsonTestCases_testCases(String stringToConvert, Class<T> clazz, Optional<PizzaDto> expectedResult) {
        assertEquals(expectedResult, fromJson(stringToConvert, clazz));
    }

}