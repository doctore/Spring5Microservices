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

import static com.spring5microservices.common.util.CollectionUtil.toSet;
import static com.spring5microservices.common.util.StringUtil.abbreviate;
import static com.spring5microservices.common.util.StringUtil.abbreviateMiddle;
import static com.spring5microservices.common.util.StringUtil.containsIgnoreCase;
import static com.spring5microservices.common.util.StringUtil.getBeforeLastIndexOf;
import static com.spring5microservices.common.util.StringUtil.getDigits;
import static com.spring5microservices.common.util.StringUtil.getOrElse;
import static com.spring5microservices.common.util.StringUtil.getOrEmpty;
import static com.spring5microservices.common.util.StringUtil.hideMiddle;
import static com.spring5microservices.common.util.StringUtil.isBlank;
import static com.spring5microservices.common.util.StringUtil.isEmpty;
import static com.spring5microservices.common.util.StringUtil.join;
import static com.spring5microservices.common.util.StringUtil.leftPad;
import static com.spring5microservices.common.util.StringUtil.rightPad;
import static com.spring5microservices.common.util.StringUtil.sliding;
import static com.spring5microservices.common.util.StringUtil.split;
import static com.spring5microservices.common.util.StringUtil.splitMultilevel;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringUtilTest {

    static Stream<Arguments> abbreviateWithSourceStringAndMaxLengthTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                             "" ),
                Arguments.of( "abc",          0,           null,                             "" ),
                Arguments.of( "abc",          -1,          null,                             "" ),
                Arguments.of( "abc",          1,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       3,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           null,                             "ab" ),
                Arguments.of( "abc",          3,           null,                             "abc" ),
                Arguments.of( "abcdef",       4,           null,                             "a..." ),
                Arguments.of( "abcdef",       5,           null,                             "ab..." ),
                Arguments.of( "abcdef",       6,           null,                             "abcdef" ),
                Arguments.of( "abcdef",       7,           null,                             "abcdef" ),
                Arguments.of( "abcdefg",      6,           null,                             "abc..." )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateWithSourceStringAndMaxLengthTestCases")
    @DisplayName("abbreviate: with source string and max length parameters test cases")
    public void abbreviateWithSourceStringAndMaxLength_testCases(String sourceString,
                                                                 int maxLength,
                                                                 Class<? extends Exception> expectedException,
                                                                 String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviate(sourceString, maxLength));
        } else {
            assertEquals(expectedResult, abbreviate(sourceString, maxLength));
        }
    }


    static Stream<Arguments> abbreviateAllParametersTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                 null,                             "" ),
                Arguments.of( "abc",          0,           null,                 null,                             "" ),
                Arguments.of( "abc",          -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",          1,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       3,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",          3,           ".",                  null,                             "abc" ),
                Arguments.of( "abcdef",       4,           ".",                  null,                             "abc." ),
                Arguments.of( "abcdef",       5,           ".",                  null,                             "abcd." ),
                Arguments.of( "abcdef",       5,           null,                 null,                             "ab..." ),
                Arguments.of( "abcdef",       5,           "...",                null,                             "ab..." ),
                Arguments.of( "abcdef",       6,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdef",       7,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdefg",      6,           "...",                null,                             "abc..." ),
                Arguments.of( "abcdefg",      6,           "...",                null,                             "abc..." )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateAllParametersTestCases")
    @DisplayName("abbreviate: with all parameters test cases")
    public void abbreviateAllParameters_testCases(String sourceString,
                                                  int maxLength,
                                                  String abbreviationString,
                                                  Class<? extends Exception> expectedException,
                                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviate(sourceString, maxLength, abbreviationString));
        } else {
            assertEquals(expectedResult, abbreviate(sourceString, maxLength, abbreviationString));
        }
    }


    static Stream<Arguments> abbreviateMiddleWithSourceStringAndMaxLengthTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                             "" ),
                Arguments.of( "abc",          0,           null,                             "" ),
                Arguments.of( "abc",          -1,          null,                             "" ),
                Arguments.of( "abc",          1,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       4,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           null,                             "ab" ),
                Arguments.of( "abc",          3,           null,                             "abc" ),
                Arguments.of( "abcdef",       5,           null,                             "a...f" ),
                Arguments.of( "abcdef",       6,           null,                             "abcdef" ),
                Arguments.of( "abcdef",       7,           null,                             "abcdef" ),
                Arguments.of( "abcdefg",      6,           null,                             "ab...g" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateMiddleWithSourceStringAndMaxLengthTestCases")
    @DisplayName("abbreviateMiddle: with source string and max length parameters test cases")
    public void abbreviateMiddleWithSourceStringAndMaxLength_testCases(String sourceString,
                                                                       int maxLength,
                                                                       Class<? extends Exception> expectedException,
                                                                       String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviateMiddle(sourceString, maxLength));
        } else {
            assertEquals(expectedResult, abbreviateMiddle(sourceString, maxLength));
        }
    }


    static Stream<Arguments> abbreviateMiddleAllParametersTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                 null,                             "" ),
                Arguments.of( "abc",          0,           null,                 null,                             "" ),
                Arguments.of( "abc",          -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",          2,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",          3,           ".",                  null,                             "abc" ),
                Arguments.of( "abcdef",       4,           ".",                  null,                             "ab.f" ),
                Arguments.of( "abcdef",       5,           ".",                  null,                             "ab.ef" ),
                Arguments.of( "abcdef",       5,           null,                 null,                             "a...f" ),
                Arguments.of( "abcdef",       5,           "...",                null,                             "a...f" ),
                Arguments.of( "abcdef",       6,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdef",       7,           "...",                null,                             "abcdef" ),
                Arguments.of( "abcdefg",      6,           "...",                null,                             "ab...g" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("abbreviateMiddleAllParametersTestCases")
    @DisplayName("abbreviateMiddle: with all parameters test cases")
    public void abbreviateMiddleAllParameters_testCases(String sourceString,
                                                        int maxLength,
                                                        String abbreviationString,
                                                        Class<? extends Exception> expectedException,
                                                        String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> abbreviateMiddle(sourceString, maxLength, abbreviationString));
        } else {
            assertEquals(expectedResult, abbreviateMiddle(sourceString, maxLength, abbreviationString));
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


    static Stream<Arguments> getDigitsTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,     expectedResult
                Arguments.of( null,             "" ),
                Arguments.of( "",               "" ),
                Arguments.of( "  ",             "" ),
                Arguments.of( "123",            "123" ),
                Arguments.of( "373-030-9447",   "3730309447" ),
                Arguments.of( "aSf35~yt99Th",   "3599" ),
                Arguments.of( "12-34 56$",      "123456" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getDigitsTestCases")
    @DisplayName("getDigits: test cases")
    public void getDigits_testCases(String sourceString,
                                    String expectedResult) {
        assertEquals(expectedResult, getDigits(sourceString));
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


    static Stream<Arguments> hideMiddleWithSourceStringAndMaxLengthTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                             "" ),
                Arguments.of( "abc",          0,           null,                             "" ),
                Arguments.of( "abc",          -1,          null,                             "" ),
                Arguments.of( "abc",          3,           IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       4,           IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           null,                             "ab" ),
                Arguments.of( "abcdef",       5,           null,                             "a...f" ),
                Arguments.of( "abcdef",       6,           null,                             "ab...f" ),
                Arguments.of( "abcdef",       7,           null,                             "ab...f" ),
                Arguments.of( "abcdef",       10,          null,                             "ab...f" ),
                Arguments.of( "abcdefg",      6,           null,                             "ab...g" ),
                Arguments.of( "abcdefg",      10,          null,                             "ab...fg" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hideMiddleWithSourceStringAndMaxLengthTestCases")
    @DisplayName("hideMiddle: with source string and max length parameters test cases")
    public void hideMiddleWithSourceStringAndMaxLength_testCases(String sourceString,
                                                                 int maxLength,
                                                                 Class<? extends Exception> expectedException,
                                                                 String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> hideMiddle(sourceString, maxLength));
        } else {
            assertEquals(expectedResult, hideMiddle(sourceString, maxLength));
        }
    }


    static Stream<Arguments> hideMiddleAllParametersTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   maxLength,   abbreviationString,   expectedException,                expectedResult
                Arguments.of( null,           0,           null,                 null,                             "" ),
                Arguments.of( "abc",          0,           null,                 null,                             "" ),
                Arguments.of( "abc",          -1,          ".",                  null,                             "" ),
                Arguments.of( "abc",          2,           ".",                  IllegalArgumentException.class,   null ),
                Arguments.of( "abcdef",       4,           "...",                IllegalArgumentException.class,   null ),
                Arguments.of( "ab",           3,           "...",                null,                             "ab" ),
                Arguments.of( "abc",          3,           ".",                  null,                             "a.c" ),
                Arguments.of( "abcdef",       4,           ".",                  null,                             "ab.f" ),
                Arguments.of( "abcdef",       5,           ".",                  null,                             "ab.ef" ),
                Arguments.of( "abcdef",       5,           null,                 null,                             "a...f" ),
                Arguments.of( "abcdef",       5,           "...",                null,                             "a...f" ),
                Arguments.of( "abcdef",       6,           "...",                null,                             "ab...f" ),
                Arguments.of( "abcdef",       7,           "...",                null,                             "ab...f" ),
                Arguments.of( "abcdef",       10,          "..",                 null,                             "ab..ef" ),
                Arguments.of( "abcdef",       10,          "...",                null,                             "ab...f" ),
                Arguments.of( "abcdefg",      6,           "..",                 null,                             "ab..fg" ),
                Arguments.of( "abcdefg",      6,           "...",                null,                             "ab...g" ),
                Arguments.of( "abcdefg",      10,          "..",                 null,                             "abc..fg" ),
                Arguments.of( "abcdefg",      10,          "...",                null,                             "ab...fg" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hideMiddleAllParametersTestCases")
    @DisplayName("hideMiddle: with all parameters test cases")
    public void hideMiddleAllParameters_testCases(String sourceString,
                                                  int maxLength,
                                                  String abbreviationString,
                                                  Class<? extends Exception> expectedException,
                                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> hideMiddle(sourceString, maxLength, abbreviationString));
        } else {
            assertEquals(expectedResult, hideMiddle(sourceString, maxLength, abbreviationString));
        }
    }


    static Stream<Arguments> isBlankTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   expectedResult
                Arguments.of( null,           true ),
                Arguments.of( "",             true ),
                Arguments.of( "  ",           true ),
                Arguments.of( "  123 ",       false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isBlankTestCases")
    @DisplayName("isBlank: test cases")
    public void isBlank_testCases(String sourceString,
                                  boolean expectedResult) {
        assertEquals(expectedResult, isBlank(sourceString));
    }


    static Stream<Arguments> isEmptyTestCases() {
        return Stream.of(
                //@formatter:off
                //            sourceString,   expectedResult
                Arguments.of( null,           true ),
                Arguments.of( "",             true ),
                Arguments.of( "  ",           false ),
                Arguments.of( "  123 ",       false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public void isEmpty_testCases(String sourceString,
                                  boolean expectedResult) {
        assertEquals(expectedResult, isEmpty(sourceString));
    }


    static Stream<Arguments> joinOnlyWithCollectionTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String expectedIntsResult = "123368";
        String expectedStringsWithNullsResult = "242abH";
        return Stream.of(
                //@formatter:off
                //            sourceString,       expectedResult
                Arguments.of( null,               "" ),
                Arguments.of( List.of(),          "" ),
                Arguments.of( ints,               expectedIntsResult ),
                Arguments.of( stringsWithNulls,   expectedStringsWithNullsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinOnlyWithCollectionTestCases")
    @DisplayName("join: only with source collection test cases")
    public <T> void joinOnlyWithCollection_testCases(Collection<? extends T> sourceCollection,
                                                     String expectedResult) {
        assertEquals(expectedResult, join(sourceCollection));
    }


    static Stream<Arguments> joinOnlyElementsTestCases() {
        List<Integer> ints = List.of(33, 68, 99, 2);
        List<String> stringsWithNulls = asList(null, null, "242", "ab", "", "H");

        String expectedIntsResult = "3368992";
        String expectedStringsWithNullsResult = "242abH";
        return Stream.of(
                //@formatter:off
                //            elements,           expectedResult
                Arguments.of( null,               "" ),
                Arguments.of( List.of(),          "" ),
                Arguments.of( ints,               expectedIntsResult ),
                Arguments.of( stringsWithNulls,   expectedStringsWithNullsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinOnlyElementsTestCases")
    @DisplayName("join: only with array of elements test cases")
    @SuppressWarnings("unchecked")
    public <T> void joinOnlyElements_testCases(List<T> elements,
                                               String expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, join(finalElements));
    }


    static Stream<Arguments> joinWithCollectionAndFilterTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        Predicate<Integer> isEven = i -> null != i && 0 == i % 2;
        Predicate<String> longerThan2 = s -> null != s && 2 < s.length();

        String expectedIntsResultNoFilter = "123368";
        String expectedIntsResultWithFilter = "268";
        String expectedStringsWithNullsResultNoFilter = "242abH";
        String expectedStringsWithNullsResultWithFilter = "242";
        return Stream.of(
                //@formatter:off
                //            sourceString,       filterPredicate,   expectedResult
                Arguments.of( null,               null,              "" ),
                Arguments.of( List.of(),          null,              "" ),
                Arguments.of( null,               isEven,            "" ),
                Arguments.of( List.of(),          isEven,            "" ),
                Arguments.of( ints,               null,              expectedIntsResultNoFilter ),
                Arguments.of( ints,               isEven,            expectedIntsResultWithFilter ),
                Arguments.of( stringsWithNulls,   null,              expectedStringsWithNullsResultNoFilter ),
                Arguments.of( stringsWithNulls,   longerThan2,       expectedStringsWithNullsResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinWithCollectionAndFilterTestCases")
    @DisplayName("join: with source collection and filter test cases")
    public <T> void joinWithCollectionAndFilter_testCases(Collection<? extends T> sourceCollection,
                                                          Predicate<? super T> filterPredicate,
                                                          String expectedResult) {
        assertEquals(expectedResult, join(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> joinWithCollectionAndSeparatorTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        String separator = ";";

        String expectedIntsResultNoSeparator = "123368";
        String expectedIntsResultWithSeparator = "1;2;33;68";
        String expectedStringsWithNullsResultNoSeparator = "242abH";
        String expectedStringsWithNullsResultWithSeparator = ";;242;ab;;H";
        return Stream.of(
                //@formatter:off
                //            sourceString,       separator,   expectedResult
                Arguments.of( null,               null,        "" ),
                Arguments.of( List.of(),          null,        "" ),
                Arguments.of( null,               separator,   "" ),
                Arguments.of( List.of(),          separator,   "" ),
                Arguments.of( ints,               null,        expectedIntsResultNoSeparator ),
                Arguments.of( ints,               separator,   expectedIntsResultWithSeparator ),
                Arguments.of( stringsWithNulls,   null,        expectedStringsWithNullsResultNoSeparator ),
                Arguments.of( stringsWithNulls,   separator,   expectedStringsWithNullsResultWithSeparator )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("joinWithCollectionAndSeparatorTestCases")
    @DisplayName("join: with source collection and separator test cases")
    public <T> void joinWithCollectionAndSeparator_testCases(Collection<? extends T> sourceCollection,
                                                             String separator,
                                                             String expectedResult) {
        assertEquals(expectedResult, join(sourceCollection, separator));
    }


    static Stream<Arguments> joinAllParametersTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 33, 68));
        List<String> stringsWithNulls = asList("", null, "242", "ab", null, "H");

        Predicate<Integer> isEven = i -> null != i && 0 == i % 2;
        Predicate<String> longerThan2 = s -> null != s && 2 < s.length();
        String separator = ";";

        String expectedIntsResultNoFilterAndSeparator = "123368";
        String expectedIntsResultOnlyFilter = "268";
        String expectedIntsResultOnlySeparator = "1;2;33;68";
        String expectedIntsResultWithFilterAndSeparator = "2;68";

        String expectedStringsWithNullsResultNoFilterAndSeparator = "242abH";
        String expectedStringsWithNullsResultOnlyFilter = "242";
        String expectedStringsWithNullsResultOnlySeparator = ";;242;ab;;H";
        String expectedStringsWithNullsResultWithFilterAndSeparator = "242";
        return Stream.of(
                //@formatter:off
                //            sourceString,       filterPredicate,   separator,   expectedResult
                Arguments.of( null,               null,              null,        "" ),
                Arguments.of( null,               isEven,            null,        "" ),
                Arguments.of( null,               isEven,            separator,   "" ),
                Arguments.of( List.of(),          null,              null,        "" ),
                Arguments.of( List.of(),          isEven,            null,        "" ),
                Arguments.of( List.of(),          isEven,            separator,   "" ),
                Arguments.of( ints,               null,              null,        expectedIntsResultNoFilterAndSeparator ),
                Arguments.of( ints,               isEven,            null,        expectedIntsResultOnlyFilter ),
                Arguments.of( ints,               null,              separator,   expectedIntsResultOnlySeparator ),
                Arguments.of( ints,               isEven,            separator,   expectedIntsResultWithFilterAndSeparator ),
                Arguments.of( stringsWithNulls,   null,              null,        expectedStringsWithNullsResultNoFilterAndSeparator ),
                Arguments.of( stringsWithNulls,   longerThan2,       null,        expectedStringsWithNullsResultOnlyFilter ),
                Arguments.of( stringsWithNulls,   null,              separator,   expectedStringsWithNullsResultOnlySeparator ),
                Arguments.of( stringsWithNulls,   longerThan2,       separator,   expectedStringsWithNullsResultWithFilterAndSeparator )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("joinAllParametersTestCases")
    @DisplayName("join: with all parameters test cases")
    public <T> void joinAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                Predicate<? super T> filterPredicate,
                                                String separator,
                                                String expectedResult) {
        assertEquals(expectedResult, join(sourceCollection, filterPredicate, separator));
    }


    static Stream<Arguments> leftPadWithSourceStringAndSizeTestCases() {
        String sourceString = "abc";
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   expectedResult
                Arguments.of( null,           -1,                          "" ),
                Arguments.of( null,           0,                           "" ),
                Arguments.of( null,           2,                           "  " ),
                Arguments.of( "",             -1,                          "" ),
                Arguments.of( "",             0,                           "" ),
                Arguments.of( "",             3,                           "   " ),
                Arguments.of( sourceString,   -1,                          sourceString ),
                Arguments.of( sourceString,   0,                           sourceString ),
                Arguments.of( sourceString,   1,                           sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 2,   "  " + sourceString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("leftPadWithSourceStringAndSizeTestCases")
    @DisplayName("leftPad: with source string and size test cases")
    public void leftPadWithSourceStringAndSize_testCases(String sourceString,
                                                         int size,
                                                         String expectedResult) {
        assertEquals(expectedResult, leftPad(sourceString, size));
    }


    static Stream<Arguments> leftPadAllParametersTestCases() {
        String sourceString = "abc";
        String padString = "zz";
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   padString,                        expectedResult
                Arguments.of( null,           -1,                          null,        "" ),
                Arguments.of( null,           0,                           null,        "" ),
                Arguments.of( null,           -1,                          padString,   "" ),
                Arguments.of( null,            0,                          padString,   "" ),
                Arguments.of( null,            1,                          padString,   "z" ),
                Arguments.of( null,            2,                          padString,   "zz" ),
                Arguments.of( "",             -1,                          null,        "" ),
                Arguments.of( "",             0,                           null,        "" ),
                Arguments.of( "",             -1,                          padString,   "" ),
                Arguments.of( "",             0,                           padString,   "" ),
                Arguments.of( "",             1,                           padString,   "z" ),
                Arguments.of( "",             2,                           padString,   "zz" ),
                Arguments.of( sourceString,   -1,                          null,        sourceString ),
                Arguments.of( sourceString,   0,                           null,        sourceString ),
                Arguments.of( sourceString,   1,                           null,        sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       null,        sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 2,   null,        "  " + sourceString ),
                Arguments.of( sourceString,   -1,                          padString,   sourceString ),
                Arguments.of( sourceString,   0,                           padString,   sourceString ),
                Arguments.of( sourceString,   1,                           padString,   sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       padString,   sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 1,   padString,   "z" + sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 2,   padString,   "zz" + sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 3,   padString,   "zzz" + sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 4,   padString,   "zzzz" + sourceString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("leftPadAllParametersTestCases")
    @DisplayName("leftPad: with all parameters test cases")
    public void leftPadAllParameters_testCases(String sourceString,
                                               int size,
                                               String padString,
                                               String expectedResult) {
        assertEquals(expectedResult, leftPad(sourceString, size, padString));
    }


    static Stream<Arguments> rightPadWithSourceStringAndSizeTestCases() {
        String sourceString = "abc";
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   expectedResult
                Arguments.of( null,           -1,                          "" ),
                Arguments.of( null,           0,                           "" ),
                Arguments.of( null,           2,                           "  " ),
                Arguments.of( "",             -1,                          "" ),
                Arguments.of( "",             0,                           "" ),
                Arguments.of( "",             3,                           "   " ),
                Arguments.of( sourceString,   -1,                          sourceString ),
                Arguments.of( sourceString,   0,                           sourceString ),
                Arguments.of( sourceString,   1,                           sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 2,   sourceString + "  " )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("rightPadWithSourceStringAndSizeTestCases")
    @DisplayName("rightPad: with source string and size test cases")
    public void rightPadWithSourceStringAndSize_testCases(String sourceString,
                                                          int size,
                                                          String expectedResult) {
        assertEquals(expectedResult, rightPad(sourceString, size));
    }


    static Stream<Arguments> rightPadAllParametersTestCases() {
        String sourceString = "abc";
        String padString = "zz";
        return Stream.of(
                //@formatter:off
                //            sourceString,   size,   padString,                        expectedResult
                Arguments.of( null,           -1,                          null,        "" ),
                Arguments.of( null,           0,                           null,        "" ),
                Arguments.of( null,           -1,                          padString,   "" ),
                Arguments.of( null,            0,                          padString,   "" ),
                Arguments.of( null,            1,                          padString,   "z" ),
                Arguments.of( null,            2,                          padString,   "zz" ),
                Arguments.of( "",             -1,                          null,        "" ),
                Arguments.of( "",             0,                           null,        "" ),
                Arguments.of( "",             -1,                          padString,   "" ),
                Arguments.of( "",             0,                           padString,   "" ),
                Arguments.of( "",             1,                           padString,   "z" ),
                Arguments.of( "",             2,                           padString,   "zz" ),
                Arguments.of( sourceString,   -1,                          null,        sourceString ),
                Arguments.of( sourceString,   0,                           null,        sourceString ),
                Arguments.of( sourceString,   1,                           null,        sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       null,        sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 2,   null,        sourceString + "  " ),
                Arguments.of( sourceString,   -1,                          padString,   sourceString ),
                Arguments.of( sourceString,   0,                           padString,   sourceString ),
                Arguments.of( sourceString,   1,                           padString,   sourceString ),
                Arguments.of( sourceString,   sourceString.length(),       padString,   sourceString ),
                Arguments.of( sourceString,   sourceString.length() + 1,   padString,   sourceString + "z" ),
                Arguments.of( sourceString,   sourceString.length() + 2,   padString,   sourceString + "zz" ),
                Arguments.of( sourceString,   sourceString.length() + 3,   padString,   sourceString + "zzz" ),
                Arguments.of( sourceString,   sourceString.length() + 4,   padString,   sourceString + "zzzz" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("rightPadAllParametersTestCases")
    @DisplayName("rightPad: with all parameters test cases")
    public void rightPadAllParameters_testCases(String sourceString,
                                                int size,
                                                String padString,
                                                String expectedResult) {
        assertEquals(expectedResult, rightPad(sourceString, size, padString));
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
                //            sourceString,   valueExtractor,        expectedResult
                Arguments.of( null,           null,                  List.of() ),
                Arguments.of( integers,       null,                  List.of() ),
                Arguments.of( integers,       fromStringToInteger,   List.of(1,2,3) ),
                Arguments.of( characters,     fromStringToString,    List.of("A","B","3") )
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
                //            sourceString,   separator,   valueExtractor,         collectionFactory,         expectedResult
                Arguments.of( null,           null,        null,                   null,                      List.of() ),
                Arguments.of( integers,       null,        null,                   null,                      List.of() ),
                Arguments.of( integers,       ",",         null,                   null,                      List.of() ),
                Arguments.of( integers,       ",",         fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( roles,          ",",         fromToStringWithTrim,   null,                      List.of("R1", "R2", "R3", "R3")),
                Arguments.of( integers,       ",",         fromStringToInteger,    setSupplier,               toSet(1, 2, 3) ),
                Arguments.of( characters,     "-",         fromStringToString,     setSupplier,               toSet("A", "B", "3") ),
                Arguments.of( roles,          ",",         fromToStringWithTrim,   setSupplier,               toSet("R1", "R2", "R3"))
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
                //            sourceString,   separator,   chunkLimit,   valueExtractor,         collectionFactory,         expectedResult
                Arguments.of( null,           null,        -1,           null,                   null,                      List.of() ),
                Arguments.of( null,           null,         2,           null,                   null,                      List.of() ),
                Arguments.of( integers,       null,        -1,           null,                   null,                      List.of() ),
                Arguments.of( integers,       null,         2,           null,                   null,                      List.of() ),
                Arguments.of( integers,       ",",         -1,           null,                   null,                      List.of() ),
                Arguments.of( integers,       ",",          2,           null,                   null,                      List.of() ),
                Arguments.of( integers,       ",",         -1,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( integers,       ",",          4,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( integers,       ",",         10,           fromStringToInteger,    null,                      List.of(1, 2, 3, 2) ),
                Arguments.of( roles,          ",",          2,           fromToStringWithTrim,   null,                      List.of("R1", "R2, R3,R3")),
                Arguments.of( integers,       ",",         -1,           fromStringToInteger,    setSupplier,               toSet(1, 2, 3) ),
                Arguments.of( characters,     "-",         -1,           fromStringToString,     setSupplier,               toSet("A", "B", "3") ),
                Arguments.of( characters,     "-",          1,           fromStringToString,     setSupplier,               toSet("A-B-3-B") ),
                Arguments.of( roles,          ",",         -1,           fromToStringWithTrim,   setSupplier,               toSet("R1", "R2", "R3")),
                Arguments.of( roles,          ",",          4,           fromToStringWithTrim,   setSupplier,               toSet("R1", "R2", "R3"))
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitAllParametersTestCases")
    @DisplayName("split: with all parameters test cases")
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
                //            sourceString,   separators,       expectedResult
                Arguments.of( null,           null,             List.of() ),
                Arguments.of( null,           allSeparators,    List.of() ),
                Arguments.of( s1,             null,             List.of(s1) ),
                Arguments.of( s1,             commaSeparator,   expectedResultWithCommaSeparator ),
                Arguments.of( s2,             allSeparators,    expectedResultWithAllSeparators )
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
        String s3 = "1,13&%7,8,22&3";

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> commaSeparator = List.of(",");
        List<String> dotSeparator = List.of(".");
        List<String> ampersandPercentageSeparator = List.of("&%");
        List<String> commaAndDotSeparators = List.of(commaSeparator.get(0), dotSeparator.get(0));
        List<String> commaAndAmpersandPercentageSeparators = List.of(commaSeparator.get(0), ampersandPercentageSeparator.get(0));

        List<String> expectedResultWithCommaSeparator = List.of("ABC", "DEF");
        List<String> expectedResultWithCommaAndDotSeparators = List.of("1", "2", "3", "6", "7", "8", "9");
        Set<String> expectedResultWithCommaAndAmpersandPercentageAndSetSupplier = Set.of("1", "13", "7", "8", "22&3");
        return Stream.of(
                //@formatter:off
                //            sourceString,   collectionFactory,   separators,                              expectedResult
                Arguments.of( null,           null,                null,                                    List.of() ),
                Arguments.of( null,           null,                commaAndDotSeparators,                   List.of() ),
                Arguments.of( s1,             null,                null,                                    List.of(s1) ),
                Arguments.of( s1,             null,                commaSeparator,                          expectedResultWithCommaSeparator ),
                Arguments.of( s2,             null,                commaAndDotSeparators,                   expectedResultWithCommaAndDotSeparators ),
                Arguments.of( s3,             setSupplier,         commaAndAmpersandPercentageSeparators,   expectedResultWithCommaAndAmpersandPercentageAndSetSupplier )
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
