package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.asSet;
import static com.spring5microservices.common.util.StringUtil.splitFromString;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    static Stream<Arguments> keepOnlyDigitsTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,     expectedResult
                Arguments.of( null,             empty() ),
                Arguments.of( "",               of("") ),
                Arguments.of( "  ",             of("") ),
                Arguments.of( "123",            of("123") ),
                Arguments.of( "373-030-9447",   of("3730309447") ),
                Arguments.of( "12-34 56",       of("123456") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("keepOnlyDigitsTestCases")
    @DisplayName("keepOnlyDigits: test cases")
    public void keepOnlyDigits_testCases(String sourceString, Optional<String> expectedResult) {
        Optional<String> result = StringUtil.keepOnlyDigits(sourceString);
        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> splitFromStringWithSourceAndValueExtractorTestCases() {
        String integers = "1,2,3";
        String characters = "A,B,  3";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::trim;
        return Stream.of(
                //@formatter:off
                //            source,       valueExtractor,        expectedResult
                Arguments.of( null,         null,                  asList() ),
                Arguments.of( integers,     null,                  asList() ),
                Arguments.of( integers,     fromStringToInteger,   asList(1,2,3) ),
                Arguments.of( characters,   fromStringToString,    asList("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringWithSourceAndValueExtractorTestCases")
    @DisplayName("splitFromString: with source and value extractor test cases")
    public <T> void splitFromStringWithSourceAndValueExtractor_testCases(String source, Function<String, T> valueExtractor,
                                                                         Collection<T> expectedResult) {
        Collection<T> splittedValues = splitFromString(source, valueExtractor);
        assertEquals(expectedResult, splittedValues);
    }


    static Stream<Arguments> splitFromStringWithSourceAndSeparatorAndChunkLimitTestCases() {
        String integers = "1,2,3";
        String characters = "A,B,  3";
        return Stream.of(
                //@formatter:off
                //            source,       separator,   chunkLimit,        expectedResult
                Arguments.of( null,         null,        -1,                asList() ),
                Arguments.of( null,         null,         2,                asList() ),
                Arguments.of( integers,     null,        -1,                asList("1", "2", "3") ),
                Arguments.of( integers,     null,         3,                asList("1", "2", "3") ),
                Arguments.of( integers,     ",",         -1,                asList("1", "2", "3") ),
                Arguments.of( integers,     ",",          3,                asList("1", "2", "3") ),
                Arguments.of( characters,   null,        -1,                asList("A","B","3") ),
                Arguments.of( characters,   null,         2,                asList("A","B,  3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringWithSourceAndSeparatorAndChunkLimitTestCases")
    @DisplayName("splitFromString: with source, separator and chunkLimit test cases")
    public <T> void splitFromStringWithSourceAndSeparatorAndChunkLimit_testCases(String source, String separator, int chunkLimit,
                                                                                 Collection<T> expectedResult) {
        Collection<T> splittedValues = splitFromString(source, separator, chunkLimit);
        assertEquals(expectedResult, splittedValues);
    }


    static Stream<Arguments> splitFromStringWithSourceAndSeparatorAndValueExtractorAndCollectionFactoryTestCases() {
        String integers = "1,2,3,2";
        String characters = "A-B-3-B";
        String roles = "R1,  R2, R3,R3";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::toString;
        Function<String, String> fromToStringWithTrim = String::trim;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            source,       separator,   valueExtractor,         collectionFactory,         expectedResult
                Arguments.of( null,         null,        null,                   null,                      asList() ),
                Arguments.of( integers,     null,        null,                   null,                      asList() ),
                Arguments.of( integers,     ",",         null,                   null,                      asList() ),
                Arguments.of( integers,     ",",         fromStringToInteger,    null,                      asList(1, 2, 3, 2) ),
                Arguments.of( roles,        ",",         fromToStringWithTrim,   null,                      asList("R1", "R2", "R3", "R3")),
                Arguments.of( integers,     ",",         fromStringToInteger,    setSupplier,               asSet(1, 2, 3) ),
                Arguments.of( characters,   "-",         fromStringToString,     setSupplier,               asSet("A", "B", "3") ),
                Arguments.of( roles,        ",",         fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringWithSourceAndSeparatorAndValueExtractorAndCollectionFactoryTestCases")
    @DisplayName("splitFromString: with source, separator, valueExtractor and collectionFactory test cases")
    public <T> void splitFromStringWithSourceAndSeparatorAndValueExtractorAndCollectionFactory_testCases(String source,
                                                                                                         String separator,
                                                                                                         Function<String, T> valueExtractor,
                                                                                                         Supplier<Collection<T>> collectionFactory,
                                                                                                         Collection<T> expectedResult) {
        Collection<T> splittedValues = splitFromString(source, valueExtractor, separator, collectionFactory);
        assertEquals(expectedResult, splittedValues);
    }


    static Stream<Arguments> splitFromStringAllParametersTestCases() {
        String integers = "1,2,3,2";
        String characters = "A-B-3-B";
        String roles = "R1,  R2, R3,R3";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::toString;
        Function<String, String> fromToStringWithTrim = String::trim;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            source,       separator,   chunkLimit,   valueExtractor,         collectionFactory,         expectedResult
                Arguments.of( null,         null,        -1,           null,                   null,                      asList() ),
                Arguments.of( null,         null,         2,           null,                   null,                      asList() ),
                Arguments.of( integers,     null,        -1,           null,                   null,                      asList() ),
                Arguments.of( integers,     null,         2,           null,                   null,                      asList() ),
                Arguments.of( integers,     ",",         -1,           null,                   null,                      asList() ),
                Arguments.of( integers,     ",",          2,           null,                   null,                      asList() ),
                Arguments.of( integers,     ",",         -1,           fromStringToInteger,    null,                      asList(1, 2, 3, 2) ),
                Arguments.of( integers,     ",",          4,           fromStringToInteger,    null,                      asList(1, 2, 3, 2) ),
                Arguments.of( integers,     ",",         10,           fromStringToInteger,    null,                      asList(1, 2, 3, 2) ),
                Arguments.of( roles,        ",",          2,           fromToStringWithTrim,   null,                      asList("R1", "R2, R3,R3")),
                Arguments.of( integers,     ",",         -1,           fromStringToInteger,    setSupplier,               asSet(1, 2, 3) ),
                Arguments.of( characters,   "-",         -1,           fromStringToString,     setSupplier,               asSet("A", "B", "3") ),
                Arguments.of( characters,   "-",          1,           fromStringToString,     setSupplier,               asSet("A-B-3-B") ),
                Arguments.of( roles,        ",",         -1,           fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3")),
                Arguments.of( roles,        ",",          4,           fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringAllParametersTestCases")
    @DisplayName("splitFromString: all parameters test cases")
    public <T> void splitFromStringAllParameters_testCases(String source, String separator, int chunkLimit,
                                                           Function<String, T> valueExtractor, Supplier<Collection<T>> collectionFactory,
                                                           Collection<T> expectedResult) {
        Collection<T> splittedValues = splitFromString(source, separator, chunkLimit, valueExtractor, collectionFactory);
        assertEquals(expectedResult, splittedValues);
    }

}
