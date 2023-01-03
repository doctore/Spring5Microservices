package com.spring5microservices.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.exception.JsonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.JsonUtil.fromJson;
import static com.spring5microservices.common.util.JsonUtil.fromJsonCollection;
import static com.spring5microservices.common.util.JsonUtil.toJson;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonUtilTest {

    static Stream<Arguments> fromJsonDefaultMapperTestCases() {
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";
        String jsonOfEmptyInstance = "{\"name\":null,\"cost\":null}";
        String jsonOfNotEmptyInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";

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
                                                    Class<? extends T> clazzToConvert,
                                                    Class<? extends Exception> expectedException,
                                                    Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJson(sourceJson, clazzToConvert));
        } else {
            assertEquals(expectedResult, fromJson(sourceJson, clazzToConvert));
        }
    }


    static Stream<Arguments> fromJsonAllParametersTestCases() {
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";
        String jsonOfEmptyInstance = "{\"name\":null,\"cost\":null}";
        String jsonOfNotEmptyInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";

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
                                                    Class<? extends T> clazzToConvert,
                                                    ObjectMapper objectMapper,
                                                    Class<? extends Exception> expectedException,
                                                    Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJson(sourceJson, clazzToConvert, objectMapper));
        } else {
            assertEquals(expectedResult, fromJson(sourceJson, clazzToConvert, objectMapper));
        }
    }


    static Stream<Arguments> fromJsonCollectionDefaultMapperTestCases() {
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";
        String jsonOfNotCollectionInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";
        String jsonOfEmptyInstance = "[{\"name\":null,\"cost\":null}]";
        String jsonOfNotEmptyInstance = "[{\"name\":\"Carbonara\",\"cost\":5.0}]";
        String jsonOfNotEmptyInstances = "[{\"name\":\"Carbonara\",\"cost\":5.0},{\"name\":\"Margherita\",\"cost\":6.0}]";

        List<PizzaDto> expectedResultEmptyInstance = List.of(new PizzaDto());
        List<PizzaDto> expectedResultNotEmptyInstance = List.of(new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D));
        Set<PizzaDto> expectedResultNotEmptyInstances = Set.of(
                new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D),
                new PizzaDto(MARGUERITA.getInternalPropertyValue(), 6D)
        );
        return Stream.of(
                //@formatter:off
                //            sourceJson,                    clazzOfElements,   clazzOfCollection,   expectedException,     expectedResult
                Arguments.of( notValidJson,                  null,              null,                JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,           null,              null,                JsonException.class,   null ),
                Arguments.of( notValidJson,                  PizzaDto.class,    null,                JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,              PizzaDto.class,    null,                JsonException.class,   null ),
                Arguments.of( jsonOfNotCollectionInstance,   PizzaDto.class,    null,                JsonException.class,   null ),
                Arguments.of( null,                          null,              null,                null,                  List.of() ),
                Arguments.of( null,                          PizzaDto.class,    null,                null,                  List.of() ),
                Arguments.of( "  ",                          null,              HashSet.class,       null,                  Set.of() ),
                Arguments.of( "  ",                          PizzaDto.class,    HashSet.class,       null,                  Set.of() ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    null,                null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    ArrayList.class,     null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    null,                null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    ArrayList.class,     null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstances,       PizzaDto.class,    HashSet.class,       null,                  expectedResultNotEmptyInstances )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromJsonCollectionDefaultMapperTestCases")
    @DisplayName("fromJsonCollection: with default mapper test cases")
    public <T> void fromJsonCollectionDefaultMapper_testCases(String sourceJson,
                                                              Class<? extends T> clazzOfElements,
                                                              Class<? extends Collection> clazzOfCollection,
                                                              Class<? extends Exception> expectedException,
                                                              Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJsonCollection(sourceJson, clazzOfElements, clazzOfCollection));
        } else {
            assertEquals(expectedResult, fromJsonCollection(sourceJson, clazzOfElements, clazzOfCollection));
        }
    }


    static Stream<Arguments> fromJsonCollectionAllParametersTestCases() {
        String notValidJson = "{$}";
        String jsonDoesNotMatch = "{\"id\":null}";
        String jsonOfNotCollectionInstance = "{\"name\":\"Carbonara\",\"cost\":5.0}";
        String jsonOfEmptyInstance = "[{\"name\":null,\"cost\":null}]";
        String jsonOfNotEmptyInstance = "[{\"name\":\"Carbonara\",\"cost\":5.0}]";
        String jsonOfNotEmptyInstances = "[{\"name\":\"Carbonara\",\"cost\":5.0},{\"name\":\"Margherita\",\"cost\":6.0}]";

        ObjectMapper objectMapper = new ObjectMapper();

        List<PizzaDto> expectedResultEmptyInstance = List.of(new PizzaDto());
        List<PizzaDto> expectedResultNotEmptyInstance = List.of(new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D));
        Set<PizzaDto> expectedResultNotEmptyInstances = Set.of(
                new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D),
                new PizzaDto(MARGUERITA.getInternalPropertyValue(), 6D)
        );
        return Stream.of(
                //@formatter:off
                //            sourceJson,                    clazzOfElements,   clazzOfCollection,   objectMapper,   expectedException,     expectedResult
                Arguments.of( notValidJson,                  null,              null,                null,           JsonException.class,   null ),
                Arguments.of( notValidJson,                  null,              null,                objectMapper,   JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,           null,              null,                null,           JsonException.class,   null ),
                Arguments.of( jsonOfEmptyInstance,           null,              null,                objectMapper,   JsonException.class,   null ),
                Arguments.of( notValidJson,                  PizzaDto.class,    null,                null,           JsonException.class,   null ),
                Arguments.of( notValidJson,                  PizzaDto.class,    null,                objectMapper,   JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,              PizzaDto.class,    null,                null,           JsonException.class,   null ),
                Arguments.of( jsonDoesNotMatch,              PizzaDto.class,    null,                objectMapper,   JsonException.class,   null ),
                Arguments.of( jsonOfNotCollectionInstance,   PizzaDto.class,    null,                null,           JsonException.class,   null ),
                Arguments.of( jsonOfNotCollectionInstance,   PizzaDto.class,    null,                objectMapper,   JsonException.class,   null ),
                Arguments.of( null,                          null,              null,                null,           null,                  List.of() ),
                Arguments.of( null,                          null,              null,                objectMapper,   null,                  List.of() ),
                Arguments.of( null,                          PizzaDto.class,    null,                null,           null,                  List.of() ),
                Arguments.of( null,                          PizzaDto.class,    null,                objectMapper,   null,                  List.of() ),
                Arguments.of( "  ",                          null,              HashSet.class,       null,           null,                  Set.of() ),
                Arguments.of( "  ",                          null,              HashSet.class,       objectMapper,   null,                  Set.of() ),
                Arguments.of( "  ",                          PizzaDto.class,    HashSet.class,       null,           null,                  Set.of() ),
                Arguments.of( "  ",                          PizzaDto.class,    HashSet.class,       objectMapper,   null,                  Set.of() ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    null,                null,           null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    null,                objectMapper,   null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    ArrayList.class,     null,           null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfEmptyInstance,           PizzaDto.class,    ArrayList.class,     objectMapper,   null,                  expectedResultEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    null,                null,           null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    null,                objectMapper,   null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    ArrayList.class,     null,           null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstance,        PizzaDto.class,    ArrayList.class,     objectMapper,   null,                  expectedResultNotEmptyInstance ),
                Arguments.of( jsonOfNotEmptyInstances,       PizzaDto.class,    HashSet.class,       null,           null,                  expectedResultNotEmptyInstances ),
                Arguments.of( jsonOfNotEmptyInstances,       PizzaDto.class,    HashSet.class,       objectMapper,   null,                  expectedResultNotEmptyInstances )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromJsonCollectionAllParametersTestCases")
    @DisplayName("fromJsonCollection: with all parameters test cases")
    public <T> void fromJsonCollectionAllParameters_testCases(String sourceJson,
                                                              Class<? extends T> clazzOfElements,
                                                              Class<? extends Collection> clazzOfCollection,
                                                              ObjectMapper objectMapper,
                                                              Class<? extends Exception> expectedException,
                                                              Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromJsonCollection(sourceJson, clazzOfElements, clazzOfCollection, objectMapper));
        } else {
            assertEquals(expectedResult, fromJsonCollection(sourceJson, clazzOfElements, clazzOfCollection, objectMapper));
        }
    }


    static Stream<Arguments> toJsonDefaultMapperTestCases() {
        PizzaDto emptyInstance = new PizzaDto();
        PizzaDto notEmptyInstance = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);

        Optional<String> expectedResultEmptyInstance = of("{\"name\":null,\"cost\":null}");
        Optional<String> expectedResultNotEmptyInstance = of("{\"name\":\"Carbonara\",\"cost\":5.0}");
        return Stream.of(
                //@formatter:off
                //            sourceObject,       expectedResult
                Arguments.of( null,               empty() ),
                Arguments.of( emptyInstance,      expectedResultEmptyInstance ),
                Arguments.of( notEmptyInstance,   expectedResultNotEmptyInstance )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonDefaultMapperTestCases")
    @DisplayName("toJson: with default mapper test cases")
    public void toJsonDefaultMapper_testCases(PizzaDto sourceObject,
                                              Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(sourceObject));
    }


    static Stream<Arguments> toJsonAllParameters_IndividualObjectAsSource_TestCases() {
        PizzaDto emptyInstance = new PizzaDto();
        PizzaDto notEmptyInstance = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        ObjectMapper objectMapper = new ObjectMapper();

        Optional<String> expectedResultEmptyInstance = of("{\"name\":null,\"cost\":null}");
        Optional<String> expectedResultNotEmptyInstance = of("{\"name\":\"Carbonara\",\"cost\":5.0}");

        return Stream.of(
                //@formatter:off
                //            sourceObject,       objectMapper,   expectedResult
                Arguments.of( null,               null,           empty() ),
                Arguments.of( null,               objectMapper,   empty() ),
                Arguments.of( emptyInstance,      null,           expectedResultEmptyInstance ),
                Arguments.of( emptyInstance,      objectMapper,   expectedResultEmptyInstance ),
                Arguments.of( notEmptyInstance,   null,           expectedResultNotEmptyInstance ),
                Arguments.of( notEmptyInstance,   objectMapper,   expectedResultNotEmptyInstance )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonAllParameters_IndividualObjectAsSource_TestCases")
    @DisplayName("toJson: with all parameters but no collection used as sourceObject test cases")
    public void toJsonAllParameters_IndividualObjectAsSource_testCases(PizzaDto sourceObject,
                                                                       ObjectMapper objectMapper,
                                                                       Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(sourceObject, objectMapper));
    }


    static Stream<Arguments> toJsonAllParameters_CollectionAsSource_TestCases() {
        PizzaDto emptyInstance = new PizzaDto();
        PizzaDto notEmptyInstance1 = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto notEmptyInstance2 = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 6D);
        List<PizzaDto> allNotEmptyInstances = List.of(notEmptyInstance1, notEmptyInstance2);
        ObjectMapper objectMapper = new ObjectMapper();

        Optional<String> expectedResultEmptyInstance = of("[{\"name\":null,\"cost\":null}]");
        Optional<String> expectedResultNotEmptyInstance1 = of("[{\"name\":\"Carbonara\",\"cost\":5.0}]");
        Optional<String> expectedResultNotEmptyInstances = of("[{\"name\":\"Carbonara\",\"cost\":5.0},{\"name\":\"Margherita\",\"cost\":6.0}]");
        return Stream.of(
                //@formatter:off
                //            sourceObject,                 objectMapper,   expectedResult
                Arguments.of( null,                         null,           empty() ),
                Arguments.of( null,                         objectMapper,   empty() ),
                Arguments.of( List.of(emptyInstance),       null,           expectedResultEmptyInstance ),
                Arguments.of( Set.of(emptyInstance),        objectMapper,   expectedResultEmptyInstance ),
                Arguments.of( Set.of(notEmptyInstance1),    null,           expectedResultNotEmptyInstance1 ),
                Arguments.of( List.of(notEmptyInstance1),   objectMapper,   expectedResultNotEmptyInstance1 ),
                Arguments.of( allNotEmptyInstances,         null,           expectedResultNotEmptyInstances ),
                Arguments.of( allNotEmptyInstances,         objectMapper,   expectedResultNotEmptyInstances )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toJsonAllParameters_CollectionAsSource_TestCases")
    @DisplayName("toJson: with all parameters and collection used as sourceObject test cases")
    public void toJsonAllParameters_CollectionAsSource_testCases(Collection<PizzaDto> sourceObject,
                                                                 ObjectMapper objectMapper,
                                                                 Optional<String> expectedResult) {
        assertEquals(expectedResult, toJson(sourceObject, objectMapper));
    }

}
