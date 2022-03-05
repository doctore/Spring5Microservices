package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.asSet;
import static com.spring5microservices.common.util.StringUtil.abbreviateMiddle;
import static com.spring5microservices.common.util.StringUtil.containsIgnoreCase;
import static com.spring5microservices.common.util.StringUtil.sliding;
import static com.spring5microservices.common.util.StringUtil.splitBySize;
import static com.spring5microservices.common.util.StringUtil.splitFromString;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilTest {

    static Stream<Arguments> abbreviateMiddleTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   putInTheMiddle,   sizeOfEveryChunk,   expectedException,                expectedResult
                Arguments.of( null,           null,             -2,                 IllegalArgumentException.class,   null ),
                Arguments.of( "ABCDE",        null,             -1,                 IllegalArgumentException.class,   null ),
                Arguments.of( "ABCDE",        "..",             0,                  IllegalArgumentException.class,   null ),
                Arguments.of( "AB",           "..",             1,                  null,                             of("AB") ),
                Arguments.of( "ABC",          "..",             1,                  null,                             of("A..C") ),
                Arguments.of( "ABC",          null,             2,                  null,                             of("ABC") ),
                Arguments.of( "ABCDE",        null,             2,                  null,                             of("AB...DE") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateMiddleTestCases")
    @DisplayName("abbreviateMiddle: test cases")
    public void compareToBigDecimal_testCases(String sourceString, String putInTheMiddle, int sizeOfEveryChunk,
                                              Class<? extends Exception> expectedException, Optional<String> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviateMiddle(sourceString, putInTheMiddle, sizeOfEveryChunk));
        }
        else {
            Optional<String> result = abbreviateMiddle(sourceString, putInTheMiddle, sizeOfEveryChunk);
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> containsIgnoreCaseTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   stringToSearch,  expectedResult
                Arguments.of( null,           null,            false ),
                Arguments.of( null,           "test",          false ),
                Arguments.of( "test",         null,            false ),
                Arguments.of( "abc",          "ac",            false ),
                Arguments.of( "ABC",          "AC",            false ),
                Arguments.of( "ac",           "abc",           false ),
                Arguments.of( "AC",           "ABC",           false ),
                Arguments.of( "abcd",         "bc",            true ),
                Arguments.of( "ABcD",         "bC",            true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsIgnoreCaseTestCases")
    @DisplayName("containsIgnoreCase: test cases")
    public void containsIgnoreCase_testCases(String sourceString, String stringToSearch, boolean expectedResult) {
        boolean result = containsIgnoreCase(sourceString, stringToSearch);
        assertEquals(expectedResult, result);
    }


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


    static Stream<Arguments> splitBySizeTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   expectedResult
                Arguments.of( null,           -1,     asList() ),
                Arguments.of( null,            3,     asList() ),
                Arguments.of( "",             -1,     asList("") ),
                Arguments.of( "",              3,     asList("") ),
                Arguments.of( "12345",        -1,     asList("12345") ),
                Arguments.of( "12345",         8,     asList("12345") ),
                Arguments.of( "12345",         3,     asList("123", "45") ),
                Arguments.of( "1234",          2,     asList("12", "34") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitBySizeTestCases")
    @DisplayName("splitBySize: test cases")
    public void splitBySize_testCases(String sourceString, int size,
                                      List<String> expectedResult) {
        List<String> result = splitBySize(sourceString, size);
        assertEquals(expectedResult, result);
    }


    static Stream<Arguments> slidingTestCases() {
        String emptyString = "";
        String stringValue1 = "abcdefg";
        String stringValue2 = "1234";

        List<String> stringValue1Size8Result = asList("abcdefg");
        List<String> stringValue1Size3Result = asList("abc", "bcd", "cde", "def", "efg");
        List<String> stringValue2Size2Result = asList("12", "23", "34");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,   expectedResult
                Arguments.of( null,               5,      new ArrayList<>() ),
                Arguments.of( emptyString,        0,      asList("") ),
                Arguments.of( emptyString,        5,      asList("") ),
                Arguments.of( stringValue1,       8,      stringValue1Size8Result ),
                Arguments.of( stringValue1,       3,      stringValue1Size3Result ),
                Arguments.of( stringValue2,       2,      stringValue2Size2Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public void sliding_testCases(String sourceString, int size, List<String> expectedResult) {
        List<String> result = sliding(sourceString, size);
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
