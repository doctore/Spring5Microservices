package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    static Stream<Arguments> splitFromStringNoSeparatorAndCollectionFactoryTestCases() {
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
    @MethodSource("splitFromStringNoSeparatorAndCollectionFactoryTestCases")
    @DisplayName("splitFromString: without separator nor collection factory test cases")
    public <T> void splitFromStringNoSeparatorAndCollectionFactory_testCases(String source, Function<String, T> valueExtractor,
                                                                             Collection<T> expectedResult) {
        Collection<T> splittedValues = StringUtil.splitFromString(source, valueExtractor);
        assertEquals(expectedResult, splittedValues);
    }


    static Stream<Arguments> splitFromStringNoCollectionFactoryTestCases() {
        String integers = "1,2,3";
        String characters = "A- B-  3";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::trim;
        return Stream.of(
                //@formatter:off
                //            source,       valueExtractor,        separator,   expectedResult
                Arguments.of( null,         null,                  null,        asList() ),
                Arguments.of( integers,     null,                  null,        asList() ),
                Arguments.of( integers,     fromStringToInteger,   null,        asList(1,2,3) ),
                Arguments.of( integers,     fromStringToInteger,   ",",         asList(1,2,3) ),
                Arguments.of( characters,   fromStringToString,    "-",         asList("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringNoCollectionFactoryTestCases")
    @DisplayName("splitFromString: without collection factory test cases")
    public <T> void splitFromStringNoCollectionFactory_testCases(String source, Function<String, T> valueExtractor,
                                                                 String separator, Collection<T> expectedResult) {
        Collection<T> splittedValues = StringUtil.splitFromString(source, valueExtractor, separator);
        assertEquals(expectedResult, splittedValues);
    }


    static Stream<Arguments> splitFromStringAllParametersTestCases() {
        String integers = "1,2,3,3";
        String characters = "A  -B -3- B";
        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = String::trim;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            source,       valueExtractor,        separator,   collectionFactory,   expectedResult
                Arguments.of( null,         null,                  null,        null,                asList() ),
                Arguments.of( integers,     null,                  null,        null,                asList() ),
                Arguments.of( integers,     fromStringToInteger,   null,        null,                asList(1,2,3,3) ),
                Arguments.of( integers,     fromStringToInteger,   ",",         null,                asList(1,2,3,3) ),
                Arguments.of( characters,   fromStringToString,    "-",         null,                asList("A","B","3","B") ),
                Arguments.of( integers,     fromStringToInteger,   ",",         setSupplier,         Set.of(1,2,3) ),
                Arguments.of( characters,   fromStringToString,    "-",         setSupplier,         Set.of("A","B","3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitFromStringAllParametersTestCases")
    @DisplayName("splitFromString: with all parameters test cases")
    public <T> void splitFromStringAllParameters_testCases(String source, Function<String, T> valueExtractor, String separator,
                                                           Supplier<Collection<T>> collectionFactory, Collection<T> expectedResult) {
        Collection<T> splittedValues = StringUtil.splitFromString(source, valueExtractor, separator, collectionFactory);
        assertEquals(expectedResult, splittedValues);
    }

}
