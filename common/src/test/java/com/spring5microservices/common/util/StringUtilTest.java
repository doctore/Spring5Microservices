package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.asSet;
import static com.spring5microservices.common.util.StringUtil.abbreviateMiddle;
import static com.spring5microservices.common.util.StringUtil.containsIgnoreCase;
import static com.spring5microservices.common.util.StringUtil.getBeforeLastIndexOf;
import static com.spring5microservices.common.util.StringUtil.getOrElse;
import static com.spring5microservices.common.util.StringUtil.getOrEmpty;
import static com.spring5microservices.common.util.StringUtil.keepOnlyDigits;
import static com.spring5microservices.common.util.StringUtil.sliding;
import static com.spring5microservices.common.util.StringUtil.split;
import static com.spring5microservices.common.util.StringUtil.splitMultilevel;
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
    public void compareToBigDecimal_testCases(String sourceString,
                                              String putInTheMiddle,
                                              int sizeOfEveryChunk,
                                              Class<? extends Exception> expectedException,
                                              Optional<String> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviateMiddle(sourceString, putInTheMiddle, sizeOfEveryChunk));
        } else {
            assertEquals(expectedResult, abbreviateMiddle(sourceString, putInTheMiddle, sizeOfEveryChunk));
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
    public void containsIgnoreCase_testCases(String sourceString,
                                             String stringToSearch,
                                             boolean expectedResult) {
        assertEquals(expectedResult, containsIgnoreCase(sourceString, stringToSearch));
    }


    static Stream<Arguments> getBeforeLastIndexOf_NoDefaultValue_TestCases() {
        String str = "1234-34-5678";
        return Stream.of(
                //@formatter:off
                //            sourceString,   stringToSearch,  expectedResult
                Arguments.of( null,           null,            empty() ),
                Arguments.of( "",             null,            of("") ),
                Arguments.of( "",             "",              of("") ),
                Arguments.of( str,            null,            of(str) ),
                Arguments.of( str,            "",              of(str) ),
                Arguments.of( str,            "666",           of(str) ),
                Arguments.of( str,            "34",            of("1234-") ),
                Arguments.of( str,            "-",             of("1234-34") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getBeforeLastIndexOf_NoDefaultValue_TestCases")
    @DisplayName("getBeforeLastIndexOf: no default value provided test cases")
    public void getBeforeLastIndexOf_NoDefaultValue_testCases(String sourceString,
                                                              String stringToSearch,
                                                              Optional<String> expectedResult) {
        assertEquals(expectedResult, getBeforeLastIndexOf(sourceString, stringToSearch));
    }


    static Stream<Arguments> getBeforeLastIndexOf_DefaultValue_TestCases() {
        String str = "1234-34-5678";
        String defaultStr = "abc";
        return Stream.of(
                //@formatter:off
                //            sourceString,   stringToFind,   defaultValue,   expectedResult
                Arguments.of( null,           null,           null,           null ),
                Arguments.of( null,           null,           defaultStr,     defaultStr ),
                Arguments.of( "",             null,           null,           null ),
                Arguments.of( "",             null,           defaultStr,     defaultStr ),
                Arguments.of( "",             "",             null,           null ),
                Arguments.of( "",             "",             defaultStr,     defaultStr ),
                Arguments.of( str,            null,           null,           str ),
                Arguments.of( str,            null,           defaultStr,     str ),
                Arguments.of( str,            "666",          null,           str ),
                Arguments.of( str,            "666",          defaultStr,     str ),
                Arguments.of( str,            "34",           null,           "1234-" ),
                Arguments.of( str,            "34",           defaultStr,     "1234-" ),
                Arguments.of( str,            str,            defaultStr,     defaultStr )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getBeforeLastIndexOf_DefaultValue_TestCases")
    @DisplayName("getBeforeLastIndexOf: with default value test cases")
    public void getBeforeLastIndexOf_DefaultValue_testCases(String sourceString,
                                                            String stringToSearch,
                                                            String defaultValue,
                                                            String expectedResult) {
        assertEquals(expectedResult, getBeforeLastIndexOf(sourceString, stringToSearch, defaultValue));
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


    static Stream<Arguments> getOrEmptyTestCases() {
        String emptyString = "";
        String notEmptyString = "Test string";
        return Stream.of(
                //@formatter:off
                //            sourceInstance,   expectedResult
                Arguments.of( null,             emptyString ),
                Arguments.of( notEmptyString,   notEmptyString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrEmptyTestCases")
    @DisplayName("getOrEmpty: test cases")
    public void getOrEmpty_testCases(String sourceInstance,
                                     String expectedResult) {
        assertEquals(expectedResult, getOrEmpty(sourceInstance));
    }


    static Stream<Arguments> getOrElse_SourceDefaultParametersTestCases() {
        String notEmptyString = "Test string";
        return Stream.of(
                //@formatter:off
                //            sourceInstance,   defaultValue,         expectedResult
                Arguments.of( null,             null,                 null ),
                Arguments.of( null,             notEmptyString,       notEmptyString ),
                Arguments.of( notEmptyString,   null,                 notEmptyString ),
                Arguments.of( notEmptyString,   "testDefaultValue",   notEmptyString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_SourceDefaultParametersTestCases")
    @DisplayName("getOrElse: with source and default value as parameters test cases")
    public void getOrElse_SourceDefaultParameters_testCases(String sourceInstance,
                                                            String defaultValue,
                                                            String expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, defaultValue));
    }


    static Stream<Arguments> getOrElse_SourcePredicateDefaultParametersTestCases() {
        String emptyString = "   ";
        String notEmptyString = "Test string";
        Predicate<String> notEmptyStringPredicate = StringUtils::hasText;
        return Stream.of(
                //@formatter:off
                //            sourceInstance,   predicateToMatch,          defaultValue,     expectedResult
                Arguments.of( null,             null,                      null,             null ),
                Arguments.of( null,             null,                      notEmptyString,   notEmptyString ),
                Arguments.of( null,             notEmptyStringPredicate,   null,             null ),
                Arguments.of( null,             notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( emptyString,      null,                      notEmptyString,   emptyString ),
                Arguments.of( emptyString,      notEmptyStringPredicate,   notEmptyString,   notEmptyString ),
                Arguments.of( notEmptyString,   notEmptyStringPredicate,   emptyString,      notEmptyString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElse_SourcePredicateDefaultParametersTestCases")
    @DisplayName("getOrElse: using source, predicate and default value parameters test cases")
    public void getOrElse_GenericDefaultValue_SourcePredicateDefaultParameters_testCases(String sourceInstance,
                                                                                         Predicate<String> predicateToMatch,
                                                                                         String defaultValue,
                                                                                         String expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceInstance, predicateToMatch, defaultValue));
    }


    @ParameterizedTest
    @MethodSource("keepOnlyDigitsTestCases")
    @DisplayName("keepOnlyDigits: test cases")
    public void keepOnlyDigits_testCases(String sourceString,
                                         Optional<String> expectedResult) {
        assertEquals(expectedResult, keepOnlyDigits(sourceString));
    }


    static Stream<Arguments> slidingTestCases() {
        String emptyString = "";
        String stringValue1 = "abcdefg";
        String stringValue2 = "1234";

        List<String> stringValue1Size8Result = List.of("abcdefg");
        List<String> stringValue1Size3Result = List.of("abc", "bcd", "cde", "def", "efg");
        List<String> stringValue2Size2Result = List.of("12", "23", "34");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,   expectedResult
                Arguments.of( null,               5,      List.of() ),
                Arguments.of( emptyString,        0,      List.of("") ),
                Arguments.of( emptyString,        5,      List.of("") ),
                Arguments.of( stringValue1,       8,      stringValue1Size8Result ),
                Arguments.of( stringValue1,       3,      stringValue1Size3Result ),
                Arguments.of( stringValue2,       2,      stringValue2Size2Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public void sliding_testCases(String sourceString,
                                  int size,
                                  List<String> expectedResult) {
        assertEquals(expectedResult, sliding(sourceString, size));
    }


    static Stream<Arguments> splitBySizeTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   expectedResult
                Arguments.of( null,           -1,     List.of() ),
                Arguments.of( null,            3,     List.of() ),
                Arguments.of( "",             -1,     List.of("") ),
                Arguments.of( "",              3,     List.of("") ),
                Arguments.of( "12345",        -1,     List.of("12345") ),
                Arguments.of( "12345",         8,     List.of("12345") ),
                Arguments.of( "12345",         3,     List.of("123", "45") ),
                Arguments.of( "1234",          2,     List.of("12", "34") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitBySizeTestCases")
    @DisplayName("split: giving maximum size of every part test cases")
    public void splitBySize_testCases(String sourceString,
                                      int size,
                                      List<String> expectedResult) {
        assertEquals(expectedResult, split(sourceString, size));
    }


    static Stream<Arguments> splitWithSourceAndValueExtractorTestCases() {
        String integers = "1,2,3";
        String characters = "A,B,  3";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::trim;
        return Stream.of(
                //@formatter:off
                //            source,       valueExtractor,        expectedResult
                Arguments.of( null,         null,                  List.of() ),
                Arguments.of( integers,     null,                  List.of() ),
                Arguments.of( integers,     fromStringToInteger,   List.of(1,2,3) ),
                Arguments.of( characters,   fromStringToString,    List.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceAndValueExtractorTestCases")
    @DisplayName("split: with source and value extractor test cases")
    public <T> void splitWithSourceAndValueExtractor_testCases(String source,
                                                                         Function<String, T> valueExtractor,
                                                                         Collection<T> expectedResult) {
        assertEquals(expectedResult, split(source, valueExtractor));
    }


    static Stream<Arguments> splitWithSourceAndSeparatorAndValueExtractorAndCollectionFactoryTestCases() {
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
                Arguments.of( null,         null,        null,                   null,                      List.of() ),
                Arguments.of( integers,     null,        null,                   null,                      List.of() ),
                Arguments.of( integers,     ",",         null,                   null,                      List.of() ),
                Arguments.of( integers,     ",",         fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( roles,        ",",         fromToStringWithTrim,   null,                      List.of("R1", "R2", "R3", "R3")),
                Arguments.of( integers,     ",",         fromStringToInteger,    setSupplier,               asSet(1, 2, 3) ),
                Arguments.of( characters,   "-",         fromStringToString,     setSupplier,               asSet("A", "B", "3") ),
                Arguments.of( roles,        ",",         fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitWithSourceAndSeparatorAndValueExtractorAndCollectionFactoryTestCases")
    @DisplayName("split: with source, separator, valueExtractor and collectionFactory test cases")
    public <T> void splitWithSourceAndSeparatorAndValueExtractorAndCollectionFactory_testCases(String source,
                                                                                               String separator,
                                                                                               Function<String, T> valueExtractor,
                                                                                               Supplier<Collection<T>> collectionFactory,
                                                                                               Collection<T> expectedResult) {
        assertEquals(expectedResult, split(source, valueExtractor, separator, collectionFactory));
    }


    static Stream<Arguments> splitAllParametersTestCases() {
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
                Arguments.of( null,         null,        -1,           null,                   null,                      List.of() ),
                Arguments.of( null,         null,         2,           null,                   null,                      List.of() ),
                Arguments.of( integers,     null,        -1,           null,                   null,                      List.of() ),
                Arguments.of( integers,     null,         2,           null,                   null,                      List.of() ),
                Arguments.of( integers,     ",",         -1,           null,                   null,                      List.of() ),
                Arguments.of( integers,     ",",          2,           null,                   null,                      List.of() ),
                Arguments.of( integers,     ",",         -1,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( integers,     ",",          4,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( integers,     ",",         10,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( roles,        ",",          2,           fromToStringWithTrim,   null,                      List.of("R1", "R2, R3,R3")),
                Arguments.of( integers,     ",",         -1,           fromStringToInteger,    setSupplier,               asSet(1, 2, 3) ),
                Arguments.of( characters,   "-",         -1,           fromStringToString,     setSupplier,               asSet("A", "B", "3") ),
                Arguments.of( characters,   "-",          1,           fromStringToString,     setSupplier,               asSet("A-B-3-B") ),
                Arguments.of( roles,        ",",         -1,           fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3")),
                Arguments.of( roles,        ",",          4,           fromToStringWithTrim,   setSupplier,               asSet("R1", "R2", "R3"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitAllParametersTestCases")
    @DisplayName("split: all parameters test cases")
    public <T> void splitAllParameters_testCases(String source,
                                                 String separator,
                                                 int chunkLimit,
                                                 Function<String, T> valueExtractor,
                                                 Supplier<Collection<T>> collectionFactory,
                                                 Collection<T> expectedResult) {
        assertEquals(expectedResult, split(source, separator, chunkLimit, valueExtractor, collectionFactory));
    }


    static Stream<Arguments> splitMultilevelNoCollectionFactoryTestCases() {
        String s1 = "ABC,DEF";
        String s2 = "1,2.3,6,7.8.9";

        List<String> commaSeparator = List.of(",");
        List<String> dotSeparator = List.of(".");
        List<String> allSeparators = List.of(commaSeparator.get(0), dotSeparator.get(0));

        List<String> expectedResultWithCommaSeparator = List.of("ABC", "DEF");
        List<String> expectedResultWithAllSeparators = List.of("1", "2", "3", "6", "7", "8", "9");
        return Stream.of(
                //@formatter:off
                //            source,   separators,       expectedResult
                Arguments.of( null,     null,             List.of() ),
                Arguments.of( null,     allSeparators,    List.of() ),
                Arguments.of( s1,       null,             List.of(s1) ),
                Arguments.of( s1,       commaSeparator,   expectedResultWithCommaSeparator ),
                Arguments.of( s2,       allSeparators,    expectedResultWithAllSeparators )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitMultilevelNoCollectionFactoryTestCases")
    @DisplayName("splitMultilevel: without collection factory test cases")
    public void splitMultilevelNoCollectionFactory_testCases(String source,
                                                             List<String> separators,
                                                             List<String> expectedResult) {
        String[] finalSeparators =
                null == separators
                        ? null
                        : separators.toArray(new String[0]);

        assertEquals(expectedResult, splitMultilevel(source, finalSeparators));
    }


    static Stream<Arguments> splitMultilevelAllParametersTestCases() {
        String s1 = "ABC,DEF";
        String s2 = "1,2.3,6,7.8.9";
        String s3 = "1,1.3,6,7.8.9,7.2";

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> commaSeparator = List.of(",");
        List<String> dotSeparator = List.of(".");
        List<String> allSeparators = List.of(commaSeparator.get(0), dotSeparator.get(0));

        List<String> expectedResultWithCommaSeparator = List.of("ABC", "DEF");
        List<String> expectedResultWithAllSeparators = List.of("1", "2", "3", "6", "7", "8", "9");
        Set<String> expectedResultAllSeparatorsAndSetSupplier = Set.of("1", "3", "6", "7", "8", "9", "2");
        return Stream.of(
                //@formatter:off
                //            source,   collectionFactory,   separators,       expectedResult
                Arguments.of( null,     null,                null,             List.of() ),
                Arguments.of( null,     null,                allSeparators,    List.of() ),
                Arguments.of( s1,       null,                null,             List.of(s1) ),
                Arguments.of( s1,       null,                commaSeparator,   expectedResultWithCommaSeparator ),
                Arguments.of( s2,       null,                allSeparators,    expectedResultWithAllSeparators ),
                Arguments.of( s3,       setSupplier,         allSeparators,    expectedResultAllSeparatorsAndSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitMultilevelAllParametersTestCases")
    @DisplayName("splitMultilevel: with all parameters test cases")
    public void splitMultilevelAllParameters_testCases(String source,
                                                       Supplier<Collection<String>> collectionFactory,
                                                       List<String> separators,
                                                       Collection<String> expectedResult) {
        String[] finalSeparators =
                null == separators
                        ? null
                        : separators.toArray(new String[0]);

        assertEquals(expectedResult, splitMultilevel(source, collectionFactory, finalSeparators));
    }

}
