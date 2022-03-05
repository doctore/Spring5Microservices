package com.spring5microservices.common.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeightedCollectionTest {

    private WeightedCollection<String> weightedCollection;

    @BeforeEach
    public void setUp() {
        weightedCollection = WeightedCollection.of(new SecureRandom());
    }


    static Stream<Arguments> addFailedTestCases() {
        return Stream.of(
                //@formatter:off
                //            weight,   toInsert,        expectedException
                Arguments.of( 0,        null,            IllegalArgumentException.class ),
                Arguments.of( 0,        "DoesNotCare",   IllegalArgumentException.class ),
                Arguments.of( 1,        null,            IllegalArgumentException.class )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("addFailedTestCases")
    @DisplayName("add: failed test cases")
    public void add_failedTestCases(int weight, String toInsert, Class<? extends Exception> expectedException) {
        assertThrows(expectedException, () -> weightedCollection.add(weight, toInsert));
    }


    @Test
    @DisplayName("add: when weight and element to insert are valid ones then a new entry will be added to the collection")
    public void add_whenWeightAndElementToAddAreValid_thenAnEntryInTheCollectionIsIncluded() {
        int upperLimit = 5;
        IntStream.rangeClosed(1, upperLimit).forEach(it -> weightedCollection.add(it, String.valueOf(it)));

        assertEquals(upperLimit, weightedCollection.size());
    }


    @ParameterizedTest
    @MethodSource("addFailedTestCases")
    @DisplayName("addAndThen: failed test cases")
    public void addAndThen_failedTestCases(int weight, String toInsert, Class<? extends Exception> expectedException) {
        assertThrows(expectedException, () -> weightedCollection.addAndThen(weight, toInsert));
    }


    @Test
    @DisplayName("addAndThen: when weight and element to insert are valid ones then a new entry will be added into a new collection")
    public void addAndThen_whenWeightAndElementToAddAreValid_thenAnEntryInANewCollectionIsIncluded() {
        WeightedCollection<String> weightedCollectionNew = weightedCollection.addAndThen(10, "ForTesting");

        assertTrue(weightedCollection.isEmpty());
        assertEquals(1, weightedCollectionNew.size());
    }


    static Stream<Arguments> equals_TestCases() {
        WeightedCollection<String> emptyWeightedCollection = WeightedCollection.of(new SecureRandom());
        WeightedCollection<String> nonEmptyWeightedCollection = emptyWeightedCollection.addAndThen(10, "1").addAndThen(20, "2");
        WeightedCollection<String> nonEmptyWeightedCollectionSameElements = emptyWeightedCollection.addAndThen(10, "1").addAndThen(20, "2");
        WeightedCollection<Integer> differentTypeRandomCollection = WeightedCollection.of(new SecureRandom()).addAndThen(10, 1).addAndThen(20, 2);
        return Stream.of(
                //@formatter:off
                //            currentWeightedCollection,                   weightedCollectionToCompare,                 expectedResult
                Arguments.of( emptyWeightedCollection,                     null,                                        false ),
                Arguments.of( WeightedCollection.of(new SecureRandom()),   WeightedCollection.of(new SecureRandom()),   true ),
                Arguments.of( emptyWeightedCollection,                     emptyWeightedCollection,                     true ),
                Arguments.of( nonEmptyWeightedCollection,                  emptyWeightedCollection,                     false ),
                Arguments.of( nonEmptyWeightedCollection,                  nonEmptyWeightedCollectionSameElements,      true ),
                Arguments.of( emptyWeightedCollection,                     differentTypeRandomCollection,               false ),
                Arguments.of( nonEmptyWeightedCollection,                  differentTypeRandomCollection,               false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equals_TestCases")
    @DisplayName("equals: test cases")
    public <T, E> void equals_TestCases(WeightedCollection<T> currentWeightedCollection, WeightedCollection<E> weightedCollectionToCompare,
                                        boolean expectedResult) {
        assertEquals(expectedResult, currentWeightedCollection.equals(weightedCollectionToCompare));
    }


    @Test
    @DisplayName("isEmpty: when a collection is given then true is returned if the collection has no elements, false otherwise")
    public void isEmpty_whenACollectionIsGiven_thenTrueIsReturnedIfCollectionHasNoElementsAndFalseOtherwise() {
        assertAll(
                () -> assertTrue(weightedCollection.isEmpty()),
                () -> assertFalse(weightedCollection.addAndThen(10, "ForTesting").isEmpty())
        );
    }


    @Test
    @DisplayName("toSet: when WeightedCollection is empty then the returned Set is empty too")
    public void toSet_whenCollectionIsEmpty_thenAnEmptySetIsReturned() {
        Set<String> weightedResult = weightedCollection.toSet();

        assertTrue(weightedCollection.isEmpty());
        assertTrue(weightedResult.isEmpty());
    }


    @Test
    @DisplayName("toSet: when WeightedCollection contains elements then the returned Set will contains the same ones")
    public void getWeightedContent_whenCollectionIsNotEmpty_thenAnEquivalentSetIsReturned() {
        List<String> content = Arrays.asList("Elto 1", "Elto 2", "Elto 3");
        IntStream.rangeClosed(0, content.size()-1).forEach(it -> weightedCollection.add(it+10, content.get(it)));

        Set<String> weightedResult = weightedCollection.toSet();

        assertEquals(content.size(), weightedResult.size());
        assertEquals(weightedCollection.size(), weightedResult.size());
        assertThat(content, containsInAnyOrder(weightedResult.toArray()));
    }


    @Test
    @DisplayName("next: when WeightedCollection is empty then an empty Optional is returned")
    public void next_whenCollectionIsEmpty_thenAnEmptyOptionalIsReturned() {
        Optional<String> nextValue = weightedCollection.next();

        assertNotNull(nextValue);
        assertFalse(nextValue.isPresent());
    }


    @Test
    @DisplayName("next: when WeightedCollection is not empty then an non empty Optional is returned every time next is invoked ")
    public void next_whenCollectionIsNotEmpty_thenAnOptionalWithAValueIsReturnedEveryTime() {
        List<String> content = Arrays.asList("Elto 1", "Elto 2", "Elto 3");
        IntStream.rangeClosed(0, content.size()-1).forEach(it -> weightedCollection.add(it+10, content.get(it)));

        // The amount of items included in the RandomCollection does not matter
        for (int i = 1; i <= (weightedCollection.size() * 5); i++) {
            Optional<String> nextValue = weightedCollection.next();

            assertTrue(nextValue.isPresent());
            assertTrue(content.contains(nextValue.get()));
        }
    }


    static Stream<Arguments> removeAndThenTestCases() {
        WeightedCollection<String> emptyCollection = WeightedCollection.of(new SecureRandom());
        WeightedCollection<String> notEmptyCollection = emptyCollection.addAndThen(10, "Elto");
        return Stream.of(
                //@formatter:off
                //            initialCollection,    elementToRemove,   expectedCollection
                Arguments.of( emptyCollection,      null,              emptyCollection ),
                Arguments.of( emptyCollection,      "NotFound",        emptyCollection ),
                Arguments.of( notEmptyCollection,   null,              notEmptyCollection ),
                Arguments.of( notEmptyCollection,   "NotFound",        notEmptyCollection ),
                Arguments.of( notEmptyCollection,   "Elto",            emptyCollection )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeAndThenTestCases")
    @DisplayName("removeAndThen: use cases")
    public void removeAndThen_useCases(WeightedCollection<String> initialCollection, String elementToRemove,
                                       WeightedCollection<String> expectedCollection) {
        WeightedCollection<String> resultCollection = initialCollection.removeAndThen(elementToRemove);

        assertEquals(expectedCollection.size(), resultCollection.size());
        assertEquals(expectedCollection.toSet(), resultCollection.toSet());
    }


    @Test
    @DisplayName("size: when a collection is given then its number of elements is returned")
    public void size_whenACollectionIsGiven_thenItsNumberOfElementsIsReturned() {
        assertAll(
                () -> assertEquals(0, weightedCollection.size()),
                () -> assertEquals(1, weightedCollection.addAndThen(10, "Elto 1").size()),
                () -> assertEquals(2, weightedCollection.addAndThen(10, "Elto 1").addAndThen(20, "Elto 2").size())
        );
    }

}
