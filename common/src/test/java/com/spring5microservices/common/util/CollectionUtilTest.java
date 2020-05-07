package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilTest {

    static Stream<Arguments> concatUniqueElementsTestCases() {
        return Stream.of(
                //@formatter:off
                //            collection1ToConcat,   collection2ToConcat,   collection3ToConcat,   expectedResult
                Arguments.of( null,                  null,                  null,                  new LinkedHashSet<>() ),
                Arguments.of( null,                  asList(),              asList(),              new LinkedHashSet<>() ),
                Arguments.of( asList(1, 2),          null,                  asList(2, 3),          new LinkedHashSet<>(asList(1, 2, 3)) ),
                Arguments.of( asList(5, 6),          asList(),              asList(6, 7),          new LinkedHashSet<>(asList(5, 6, 7)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatUniqueElementsTestCases")
    @DisplayName("concatUniqueElements: test cases")
    public void concatUniqueElements_testCases(List<Integer> collection1ToConcat, List<Integer> collection2ToConcat,
                                               List<Integer> collection3ToConcat, LinkedHashSet<Integer> expectedResult) {
        Set<Integer> concatedValues = CollectionUtil.concatUniqueElements(collection1ToConcat, collection2ToConcat, collection3ToConcat);
        assertEquals(expectedResult, concatedValues);
    }

}
