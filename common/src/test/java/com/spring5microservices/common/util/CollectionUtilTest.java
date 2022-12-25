package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.UserDto;
import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple1;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.collection.tuple.Tuple3;
import com.spring5microservices.common.collection.tuple.Tuple4;
import com.spring5microservices.common.collection.tuple.Tuple5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.*;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectionUtilTest {

    static Stream<Arguments> applyOrElseNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;

        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);

        List<String> expectedIntsNoFilterResult = asList("2", "3", "4", "7");
        List<String> expectedIntsResult = asList("2", "3", "6", "7");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultFunction,   orElseFunction,    expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,              null,                             List.of() ),
                Arguments.of( null,               isEven,            null,              null,              null,                             List.of() ),
                Arguments.of( null,               null,              plus1String,       null,              null,                             List.of() ),
                Arguments.of( null,               isEven,            plus1String,       null,              null,                             List.of() ),
                Arguments.of( null,               isEven,            plus1String,       multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,              null,              null,                             List.of() ),
                Arguments.of( List.of(),          isEven,            null,              null,              null,                             List.of() ),
                Arguments.of( List.of(),          null,              plus1String,       null,              null,                             List.of() ),
                Arguments.of( List.of(),          isEven,            plus1String,       null,              null,                             List.of() ),
                Arguments.of( List.of(),          isEven,            plus1String,       multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(1),         null,              null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isEven,            null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isEven,            plus1String,       null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isEven,            null,              multiply2String,   IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus1String,       multiply2String,   null,                             expectedIntsNoFilterResult ),
                Arguments.of( ints,               isEven,            plus1String,       multiply2String,   null,                             expectedIntsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseNoCollectionFactoryTestCases")
    @DisplayName("applyOrElse: without collection factory test cases")
    public <T, E> void applyOrElseNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                                Predicate<? super T> filterPredicate,
                                                                Function<? super T, ? extends E> defaultFunction,
                                                                Function<? super T, ? extends E> orElseFunction,
                                                                Class<? extends Exception> expectedException,
                                                                List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseAllParametersTestCases() {
        List<Integer> ints = asList(1, 2, 3, 6);
        Predicate<Integer> isOdd = i -> i % 2 == 1;

        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedIntsResultNoFilterList = asList("2", "3", "4", "7");
        List<String> expectedIntsResultList = asList("2", "4", "4", "12");
        Set<String> expectedIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultFunction,   orElseFunction,    collectionFactory,  expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,              null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             null,              null,              null,               null,                             List.of() ),
                Arguments.of( null,               null,              plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             plus1String,       multiply2String,   null,               null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,              null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             null,              null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          null,              plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       multiply2String,   null,               null,                             List.of() ),
                Arguments.of( List.of(1),         null,              null,              null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isOdd,             null,              null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isOdd,             plus1String,       null,              null,               IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isOdd,             null,              multiply2String,   null,               IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus1String,       multiply2String,   null,               null,                             expectedIntsResultNoFilterList ),
                Arguments.of( ints,               isOdd,             plus1String,       multiply2String,   null,               null,                             expectedIntsResultList ),
                Arguments.of( ints,               isOdd,             plus1String,       multiply2String,   setSupplier,        null,                             expectedIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseAllParametersTestCases")
    @DisplayName("applyOrElse: with all parameters test cases")
    public <T, E> void applyOrElseAllParameters_testCases(Collection<T> sourceCollection,
                                                          Predicate<? super T> filterPredicate,
                                                          Function<? super T, ? extends E> defaultFunction,
                                                          Function<? super T, ? extends E> orElseFunction,
                                                          Supplier<Collection<E>> collectionFactory,
                                                          Class<? extends Exception> expectedException,
                                                          Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction, collectionFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultFunction, orElseFunction, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> asSetNoSetFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6, 6, 2);
        Set<Integer> emptySet = new LinkedHashSet<>();
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(intsList);
        return Stream.of(
                //@formatter:off
                //            elements,    expectedResult
                Arguments.of( null,        emptySet ),
                Arguments.of( List.of(),   emptySet ),
                Arguments.of( intsList,    expectedAllIntsResultSet )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("asSetNoSetFactoryTestCases")
    @DisplayName("asSet: without set factory test cases")
    public void asSetNoSetFactory_testCases(List<Integer> elements,
                                            Set<Integer> expectedResult) {
        Integer[] finalElements =
                null == elements
                        ? null
                        : elements.toArray(new Integer[0]);

        assertEquals(expectedResult, asSet(finalElements));
    }


    static Stream<Arguments> asSetAllParametersTestCases() {
        List<String> stringList = List.of("A", "A", "B", "C", "D", "C");
        Supplier<Set<String>> setFactory = HashSet::new;

        Set<String> emptySet = new HashSet<>();
        Set<String> expectedAllStringResultSet = new LinkedHashSet<>(stringList);
        return Stream.of(
                //@formatter:off
                //            setFactory,   elements,     expectedResult
                Arguments.of( null,         null,         emptySet ),
                Arguments.of( setFactory,   null,         emptySet ),
                Arguments.of( null,         List.of(),    emptySet ),
                Arguments.of( setFactory,   stringList,   expectedAllStringResultSet )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("asSetAllParametersTestCases")
    @DisplayName("asSet: with all parameters test cases")
    public void asSetAllParameters_testCases(Supplier<Set<String>> setFactory,
                                             List<String> elements,
                                             Set<String> expectedResult) {
        String[] finalElements =
                null == elements
                        ? null
                        : elements.toArray(new String[0]);

        assertEquals(expectedResult, asSet(setFactory, finalElements));
    }


    static Stream<Arguments> collectNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Function<Integer, String> fromIntegerToString = Objects::toString;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   mapFunction,           expectedException,                expectedResult
                Arguments.of( null,               null,              null,                  null,                             List.of() ),
                Arguments.of( null,               isEven,            null,                  null,                             List.of() ),
                Arguments.of( null,               null,              fromIntegerToString,   null,                             List.of() ),
                Arguments.of( null,               isEven,            fromIntegerToString,   null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,                  null,                             List.of() ),
                Arguments.of( List.of(),          isEven,            null,                  null,                             List.of() ),
                Arguments.of( List.of(),          null,              fromIntegerToString,   null,                             List.of() ),
                Arguments.of( List.of(),          isEven,            fromIntegerToString,   null,                             List.of() ),
                Arguments.of( List.of(1),         null,              null,                  IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isEven,            null,                  IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              fromIntegerToString,   null,                             List.of("1", "2", "3", "6") ),
                Arguments.of( ints,               isEven,            fromIntegerToString,   null,                             List.of("2", "6") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectNoCollectionFactoryTestCases")
    @DisplayName("collect: without collection factory test cases")
    public <T, E> void collectNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                            Predicate<? super T> filterPredicate,
                                                            Function<? super T, ? extends E> mapFunction,
                                                            Class<? extends Exception> expectedException,
                                                            List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, filterPredicate, mapFunction));
        } else {
            assertEquals(expectedResult, collect(sourceCollection, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> collectAllParametersTestCases() {
        List<Integer> ints = asList(1, 2, 3, 6);
        Set<String> collectedInts = new LinkedHashSet<>(asList("1", "3"));

        Predicate<Integer> isOdd = i -> i % 2 == 1;
        Function<Integer, String> fromIntegerToString = Object::toString;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   mapFunction,           collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                  null,                null,                             List.of() ),
                Arguments.of( null,               isOdd,             null,                  null,                null,                             List.of() ),
                Arguments.of( null,               null,              fromIntegerToString,   null,                null,                             List.of() ),
                Arguments.of( null,               isOdd,             fromIntegerToString,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,                  null,                null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             null,                  null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,              fromIntegerToString,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             fromIntegerToString,   null,                null,                             List.of() ),
                Arguments.of( List.of(1),         null,              null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         isOdd,             null,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              fromIntegerToString,   null,                null,                             List.of("1", "2", "3", "6") ),
                Arguments.of( ints,               isOdd,             fromIntegerToString,   null,                null,                             List.of("1", "3") ),
                Arguments.of( ints,               isOdd,             fromIntegerToString,   setSupplier,         null,                             collectedInts )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectAllParametersTestCases")
    @DisplayName("collect: with all parameters test cases")
    public <T, E> void collectAllParameters_testCases(Collection<T> sourceCollection,
                                                      Predicate<? super T> filterPredicate,
                                                      Function<? super T, ? extends E> mapFunction,
                                                      Supplier<Collection<E>> collectionFactory,
                                                      Class<? extends Exception> expectedException,
                                                      Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, filterPredicate, mapFunction, collectionFactory));
        } else {
            assertEquals(expectedResult, collect(sourceCollection, filterPredicate, mapFunction, collectionFactory));
        }
    }


    static Stream<Arguments> collectPropertyNoCollectionFactoryTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,                            propertyExtractor,   expectedResult
                Arguments.of( null,                                        null,                List.of() ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   null,                List.of() ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getName,             List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getCost,             List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyNoCollectionFactoryTestCases")
    @DisplayName("collectProperty: without collection factory test cases")
    public void collectPropertyNoCollectionFactory_testCases(List<PizzaDto> sourceCollection,
                                                             Function<PizzaDto, String> keyExtractor,
                                                             Collection<String> expectedResult) {
        assertEquals(expectedResult, collectProperty(sourceCollection, keyExtractor));
    }


    static Stream<Arguments> collectPropertyAllParametersTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,                            propertyExtractor,   collectionFactory,   expectedResult
                Arguments.of( null,                                        null,                null,                List.of() ),
                Arguments.of( null,                                        null,                setSupplier,         new LinkedHashSet<>() ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getName,             null,                List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getName,             setSupplier,         Set.of(carbonaraCheap.getName()) ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getCost,             null,                List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),   getCost,             setSupplier,         Set.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyAllParametersTestCases")
    @DisplayName("collectProperty: with all parameters test cases")
    public void collectPropertyAllParameters_testCases(List<PizzaDto> sourceCollection,
                                                       Function<PizzaDto, String> keyExtractor,
                                                       Supplier<Collection<String>> collectionFactory,
                                                       Collection<String> expectedResult) {
        assertEquals(expectedResult, collectProperty(sourceCollection, keyExtractor, collectionFactory));
    }


    static Stream<Arguments> collectPropertiesNoCollectionFactoryTestCases() {
        UserDto user1 = new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05");
        UserDto user2 = new UserDto(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25");
        List<UserDto> allUsers = List.of(user1, user2);

        Function<UserDto, Long> getId = UserDto::getId;
        Function<UserDto, String> getName = UserDto::getName;
        Function<UserDto, String> getAddress = UserDto::getAddress;
        Function<UserDto, Integer> getAge = UserDto::getAge;
        Function<UserDto, String> getBirthday = UserDto::getBirthday;
        List<Function<UserDto, ?>> allPropertyExtractors = List.of(getId, getName, getAddress, getAge, getBirthday);
        List<Function<UserDto, ?>> allPropertyExtractorsPlusOne = List.of(getId, getName, getAddress, getAge, getBirthday, getId);

        List<Tuple1<Long>> expectedResultOnePropertyExtractor = List.of(
                Tuple.of(1L),
                Tuple.of(2L)
        );
        List<Tuple2<Long, String>> expectedResultTwoPropertyExtractors = List.of(
                Tuple.of(1L, "user1 name"),
                Tuple.of(2L, "user2 name")
        );
        List<Tuple3<Long, String, String>> expectedResultThreePropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", "user1 address"),
                Tuple.of(2L, "user2 name", "user2 address")
        );
        List<Tuple4<Long, String, String, Integer>> expectedResultFourPropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", "user1 address", 11),
                Tuple.of(2L, "user2 name", "user2 address", 16)
        );
        List<Tuple5<Long, String, String, Integer, String>> expectedResultFivePropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05"),
                Tuple.of(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   propertyExtractors,                            expectedException,                expectedResult
                Arguments.of( null,               null,                                          null,                             List.of() ),
                Arguments.of( allUsers,           null,                                          null,                             List.of() ),
                Arguments.of( allUsers,           allPropertyExtractorsPlusOne,                  IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           List.of(getId),                                null,                             expectedResultOnePropertyExtractor ),
                Arguments.of( allUsers,           List.of(getId, getName),                       null,                             expectedResultTwoPropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress),           null,                             expectedResultThreePropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress, getAge),   null,                             expectedResultFourPropertyExtractors ),
                Arguments.of( allUsers,           allPropertyExtractors,                         null,                             expectedResultFivePropertyExtractors )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertiesNoCollectionFactoryTestCases")
    @DisplayName("collectProperties: without collection factory test cases")
    public <T> void collectPropertiesNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                                   List<Function<? super T, ?>> propertyExtractors,
                                                                   Class<? extends Exception> expectedException,
                                                                   List<Tuple> expectedResult) {
        Function<? super T, ?>[] finalPropertyExtractors =
                null == propertyExtractors
                        ? null
                        : propertyExtractors.toArray(new Function[0]);

        if (null != expectedException) {
            assertThrows(expectedException, () -> collectProperties(sourceCollection, finalPropertyExtractors));
        } else {
            assertEquals(expectedResult, collectProperties(sourceCollection, finalPropertyExtractors));
        }
    }


    static Stream<Arguments> collectPropertiesAllParametersTestCases() {
        UserDto user1 = new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05");
        UserDto user2 = new UserDto(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25");
        List<UserDto> allUsers = List.of(user1, user2);

        Function<UserDto, Long> getId = UserDto::getId;
        Function<UserDto, String> getName = UserDto::getName;
        Function<UserDto, String> getAddress = UserDto::getAddress;
        Function<UserDto, Integer> getAge = UserDto::getAge;
        Function<UserDto, String> getBirthday = UserDto::getBirthday;
        List<Function<UserDto, ?>> allPropertyExtractors = List.of(getId, getName, getAddress, getAge, getBirthday);
        List<Function<UserDto, ?>> allPropertyExtractorsPlusOne = List.of(getId, getName, getAddress, getAge, getBirthday, getId);

        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        Set<Tuple1<Long>> expectedResultOnePropertyExtractor = Set.of(
                Tuple.of(1L),
                Tuple.of(2L)
        );
        Set<Tuple2<Long, String>> expectedResultTwoPropertyExtractors = Set.of(
                Tuple.of(1L, "user1 name"),
                Tuple.of(2L, "user2 name")
        );
        Set<Tuple3<Long, String, String>> expectedResultThreePropertyExtractors = Set.of(
                Tuple.of(1L, "user1 name", "user1 address"),
                Tuple.of(2L, "user2 name", "user2 address")
        );
        Set<Tuple4<Long, String, String, Integer>> expectedResultFourPropertyExtractors = Set.of(
                Tuple.of(1L, "user1 name", "user1 address", 11),
                Tuple.of(2L, "user2 name", "user2 address", 16)
        );
        Set<Tuple5<Long, String, String, Integer, String>> expectedResultFivePropertyExtractors = Set.of(
                Tuple.of(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05"),
                Tuple.of(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   propertyExtractors,                            collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,                                          null,                null,                             List.of() ),
                Arguments.of( null,               null,                                          setSupplier,         null,                             new LinkedHashSet<>() ),
                Arguments.of( allUsers,           null,                                          null,                null,                             List.of() ),
                Arguments.of( allUsers,           null,                                          setSupplier,         null,                             new LinkedHashSet<>() ),
                Arguments.of( allUsers,           allPropertyExtractorsPlusOne,                  null,                IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           allPropertyExtractorsPlusOne,                  setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           List.of(getId),                                setSupplier,         null,                             expectedResultOnePropertyExtractor ),
                Arguments.of( allUsers,           List.of(getId, getName),                       setSupplier,         null,                             expectedResultTwoPropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress),           setSupplier,         null,                             expectedResultThreePropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress, getAge),   setSupplier,         null,                             expectedResultFourPropertyExtractors ),
                Arguments.of( allUsers,           allPropertyExtractors,                         setSupplier,         null,                             expectedResultFivePropertyExtractors )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertiesAllParametersTestCases")
    @DisplayName("collectProperties: with all parameters test cases")
    public <T> void collectPropertiesAllParameters_testCases(Collection<T> sourceCollection,
                                                             List<Function<? super T, ?>> propertyExtractors,
                                                             Supplier<Collection<Tuple>> collectionFactory,
                                                             Class<? extends Exception> expectedException,
                                                             Collection<Tuple> expectedResult) {
        Function<? super T, ?>[] finalPropertyExtractors =
                null == propertyExtractors
                        ? null
                        : propertyExtractors.toArray(new Function[0]);

        if (null != expectedException) {
            assertThrows(expectedException, () -> collectProperties(sourceCollection, collectionFactory, finalPropertyExtractors));
        } else {
            assertEquals(expectedResult, collectProperties(sourceCollection, collectionFactory, finalPropertyExtractors));
        }
    }


    static Stream<Arguments> concatUniqueElementsTestCases() {
        return Stream.of(
                //@formatter:off
                //            collection1ToConcat,   collection2ToConcat,   collection3ToConcat,   expectedResult
                Arguments.of( null,                  null,                  null,                  new LinkedHashSet<>() ),
                Arguments.of( null,                  List.of(),             List.of(),             new LinkedHashSet<>() ),
                Arguments.of( List.of(1, 2),         null,                  List.of(2, 3),         new LinkedHashSet<>(asList(1, 2, 3)) ),
                Arguments.of( List.of(5, 6),         List.of(),             List.of(6, 7),         new LinkedHashSet<>(asList(5, 6, 7)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatUniqueElementsTestCases")
    @DisplayName("concatUniqueElements: test cases")
    public void concatUniqueElements_testCases(List<Integer> collection1ToConcat,
                                               List<Integer> collection2ToConcat,
                                               List<Integer> collection3ToConcat,
                                               LinkedHashSet<Integer> expectedResult) {
        assertEquals(expectedResult, concatUniqueElements(collection1ToConcat, collection2ToConcat, collection3ToConcat));
    }


    static Stream<Arguments> countTestCases() {
        List<Integer> integers = asList(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));

        Predicate<Integer> upperThan10 = i -> 10 < i;
        Predicate<String> lengthGreaterThan3 = i -> 3 < i.length();
        Predicate<Long> upperThan80 = l -> 80 < l;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,      expectedResult
                Arguments.of( null,               null,                 0 ),
                Arguments.of( List.of(),          null,                 0 ),
                Arguments.of( null,               upperThan10,          0 ),
                Arguments.of( List.of(),          upperThan10,          0 ),
                Arguments.of( integers,           upperThan10,          2 ),
                Arguments.of( strings,            lengthGreaterThan3,   1 ),
                Arguments.of( longs,              upperThan80,          0 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("countTestCases")
    @DisplayName("count: test cases")
    public <T> void count_testCases(Collection<T> sourceCollection,
                                    Predicate<? super T> filterPredicate,
                                    int expectedResult) {
        assertEquals(expectedResult, count(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> findTestCases() {
        List<Integer> integers = asList(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));

        Predicate<Integer> upperThan10 = i -> 10 < i;
        Predicate<Integer> upperThan20 = i -> 20 < i;
        Predicate<Long> upperThan50 = l -> 50 < l;
        Predicate<String> moreThan2Characters = s -> 2 < s.length();
        Predicate<String> moreThan5Characters = s -> 5 < s.length();
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,       expectedResult
                Arguments.of( null,               null,                  empty() ),
                Arguments.of( null,               upperThan10,           empty() ),
                Arguments.of( List.of(),          null,                  empty() ),
                Arguments.of( integers,           null,                  empty() ),
                Arguments.of( integers,           upperThan20,           empty() ),
                Arguments.of( strings,            moreThan5Characters,   empty() ),
                Arguments.of( integers,           upperThan10,           of(11) ),
                Arguments.of( strings,            moreThan2Characters,   of("YTGH") ),
                Arguments.of( longs,              upperThan50,           of(54L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    @DisplayName("find: test cases")
    public <T> void find_testCases(Collection<T> sourceCollection,
                                   Predicate<? super T> filterPredicate,
                                   Optional<T> expectedResult) {
        assertEquals(expectedResult, find(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> dropWhileNoCollectionFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   expectedResult
                Arguments.of( null,               null,              List.of() ),
                Arguments.of( List.of(),          null,              List.of() ),
                Arguments.of( null,               isEven,            List.of() ),
                Arguments.of( intsSet,            null,              intsList ),
                Arguments.of( intsSet,            isEven,            List.of(1, 3) )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("dropWhileNoCollectionFactoryTestCases")
    @DisplayName("dropWhile: without collection factory test cases")
    public <T> void dropWhileNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                           Predicate<? super T> filterPredicate,
                                                           List<T> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> dropWhileAllParametersTestCases() {
        List<Integer> ints = new ArrayList<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = asList(1, 3);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            setSupplier,         expectedIsEvenIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileAllParametersTestCases")
    @DisplayName("dropWhile: with all parameters test cases")
    public <T> void dropWhileAllParameters_testCases(Collection<T> sourceCollection,
                                                     Predicate<? super T> filterPredicate,
                                                     Supplier<Collection<T>> collectionFactory,
                                                     Collection<T> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceCollection, filterPredicate, collectionFactory));
    }


    static Stream<Arguments> findLastTestCases() {
        List<Integer> integers = asList(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));

        Predicate<Integer> upperThan10 = i -> 10 < i;
        Predicate<Integer> upperThan20 = i -> 20 < i;
        Predicate<Long> upperThan50 = l -> 50 < l;
        Predicate<String> moreThan2Characters = s -> 2 < s.length();
        Predicate<String> moreThan5Characters = s -> 5 < s.length();
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,       expectedResult
                Arguments.of( null,               null,                  empty() ),
                Arguments.of( null,               upperThan10,           empty() ),
                Arguments.of( List.of(),          null,                  empty() ),
                Arguments.of( integers,           null,                  empty() ),
                Arguments.of( integers,           upperThan20,           empty() ),
                Arguments.of( strings,            moreThan5Characters,   empty() ),
                Arguments.of( integers,           upperThan10,           of(15) ),
                Arguments.of( strings,            moreThan2Characters,   of("IOP") ),
                Arguments.of( longs,              upperThan50,           of(78L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findLastTestCases")
    @DisplayName("findLast: test cases")
    public <T> void findLast_testCases(Collection<T> sourceCollection,
                                       Predicate<? super T> filterPredicate,
                                       Optional<T> expectedResult) {
        assertEquals(expectedResult, findLast(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> foldLeftTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 75L, 12L));

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        BiFunction<String, String, String> concat = (a, b) -> a + b;
        BiFunction<Long, Long, Long> subtract = (a, b) -> a - b;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   initialValue,   accumulator,   expectedException,                expectedResult
                Arguments.of( null,               null,           null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          2,              null,          null,                             2 ),
                Arguments.of( List.of(),          1,              multiply,      null,                             1 ),
                Arguments.of( integers,           0,              null,          null,                             0 ),
                Arguments.of( integers,           1,              multiply,      null,                             15 ),
                Arguments.of( strings,            0,              sumLength,     null,                             6 ),
                Arguments.of( strings,            "-",            concat,        null,                             "-ABEGMT" ),
                Arguments.of( longs,              10L,            subtract,      null,                             -131L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <T, E> void foldLeft_testCases(Collection<T> sourceCollection,
                                          E initialValue,
                                          BiFunction<E, ? super T, E> accumulator,
                                          Class<? extends Exception> expectedException,
                                          E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> foldLeft(sourceCollection, initialValue, accumulator));
        } else {
            assertEquals(expectedResult, foldLeft(sourceCollection, initialValue, accumulator));
        }
    }


    static Stream<Arguments> foldRightTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        List<String> strings = asList("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 75L, 12L));

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        BiFunction<String, String, String> concat = (a, b) -> a + b;
        BiFunction<Long, Long, Long> subtract = (a, b) -> a - b;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   initialValue,   accumulator,   expectedException,                expectedResult
                Arguments.of( null,               null,           null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          2,              null,          null,                             2 ),
                Arguments.of( List.of(),          1,              multiply,      null,                             1 ),
                Arguments.of( integers,           0,              null,          null,                             0 ),
                Arguments.of( integers,           1,              multiply,      null,                             15 ),
                Arguments.of( strings,            0,              sumLength,     null,                             6 ),
                Arguments.of( strings,            "-",            concat,        null,                             "-GMTEAB" ),
                Arguments.of( longs,              10L,            subtract,      null,                             -131L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldRightTestCases")
    @DisplayName("foldRight: test cases")
    public <T, E> void foldRight_testCases(Collection<T> sourceCollection,
                                           E initialValue,
                                           BiFunction<E, ? super T, E> accumulator,
                                           Class<? extends Exception> expectedException,
                                           E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> foldRight(sourceCollection, initialValue, accumulator));
        } else {
            assertEquals(expectedResult, foldRight(sourceCollection, initialValue, accumulator));
        }
    }


    static Stream<Arguments> groupMapNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, 3, 6));
        Function<Integer, Integer> mod3 = i -> i % 3;
        Function<Integer, Integer> square = i -> i * i;
        Map<Integer, List<Integer>> usingMod3AsDiscriminatorKey = new HashMap<>() {{
            put(0, List.of(9, 36));
            put(1, List.of(1));
            put(2, List.of(4));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   valueMapper,   expectedException,                expectedResult
                Arguments.of( null,               null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( null,               mod3,               square,        null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               square,        null,                             Map.of() ),
                Arguments.of( ints,               mod3,               square,        null,                             usingMod3AsDiscriminatorKey )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapNoCollectionFactoryTestCases")
    @DisplayName("groupMap: without collection factory test cases")
    public <T, K, V> void groupMapNoCollectionFactory_testCases(Collection<? extends T> sourceCollection,
                                                                Function<? super T, ? extends K> discriminatorKey,
                                                                Function<? super T, ? extends V> valueMapper,
                                                                Class<? extends Exception> expectedException,
                                                                Map<K, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapAllParametersTestCases() {
        List<String> strings = asList("AA", "BFF", "5TR", "H", "B");
        Function<String, Integer> sLength = String::length;
        Function<String, String> sVersion2 = s -> s + "2";
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<String>> resultWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
        }};
        Map<Integer, Set<String>> resultWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of("H2", "B2")));
            put(2, new LinkedHashSet<>(List.of("AA2")));
            put(3, new LinkedHashSet<>(List.of("BFF2", "5TR2")));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   valueMapper,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sLength,            null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,               sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( List.of(),          sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( strings,            sLength,            sVersion2,     null,                null,                             resultWithDefaultCollectionFactory ),
                Arguments.of( strings,            sLength,            sVersion2,     setSupplier,         null,                             resultWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapAllParametersTestCases")
    @DisplayName("groupMap: with all parameters test cases")
    public <T, K, V> void groupMapAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                          Function<? super T, ? extends K> discriminatorKey,
                                                          Function<? super T, ? extends V> valueMapper,
                                                          Supplier<Collection<V>> collectionFactory,
                                                          Class<? extends Exception> expectedException,
                                                          Map<K, Collection<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, discriminatorKey, valueMapper, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, discriminatorKey, valueMapper, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapReduceTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(2, 4, 5, 7, 9, 12));
        Function<Integer, Integer> mod3 = i -> i % 3;
        Function<Integer, Integer> square = i -> i * i;
        BinaryOperator<Integer> sumAll = Integer::sum;
        Map<Integer, Integer> expectedResult = new HashMap<>() {{
            put(0, 225);
            put(1, 65);
            put(2, 29);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   valueMapper,   reduceValues,   expectedException,                expectedResult
                Arguments.of( null,               null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               square,        null,           IllegalArgumentException.class,   null ),
                Arguments.of( null,               mod3,               square,        sumAll,         null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               square,        sumAll,         null,                             Map.of() ),
                Arguments.of( ints,               mod3,               square,        sumAll,         null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceTestCases")
    @DisplayName("groupMapReduce: test cases")
    public <T, K, V> void groupMapReduce_testCases(Collection<? extends T> sourceCollection,
                                                   Function<? super T, ? extends K> discriminatorKey,
                                                   Function<? super T, V> valueMapper,
                                                   BinaryOperator<V> reduceValues,
                                                   Class<? extends Exception> expectedException,
                                                   Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapReduce(sourceCollection, discriminatorKey, valueMapper, reduceValues));
        } else {
            assertEquals(expectedResult, groupMapReduce(sourceCollection, discriminatorKey, valueMapper, reduceValues));
        }
    }


    static Stream<Arguments> iterateTestCases() {
        UnaryOperator<Integer> divisionBy10 = a -> a / 10;
        Predicate<Integer> untilLowerOrEqualTo50 = a -> 50 >= a;
        Predicate<Integer> untilLowerOrEqualTo0 = a -> 0 >= a;
        return Stream.of(
                //@formatter:off
                //            initialValue,   applyFunction,   untilPredicate,          expectedException,                expectedResult
                Arguments.of( null,           null,            null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 1,              null,            null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 1,              divisionBy10,    null,                    IllegalArgumentException.class,   null ),
                Arguments.of( 42,             divisionBy10,    untilLowerOrEqualTo50,   null,                             List.of() ),
                Arguments.of( 42,             divisionBy10,    untilLowerOrEqualTo0,    null,                             List.of(42, 4) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("iterateTestCases")
    @DisplayName("iterate: test cases")
    public <T> void iterate_testCases(T initialValue,
                                      UnaryOperator<T> applyFunction,
                                      Predicate<T> untilPredicate,
                                      Class<? extends Exception> expectedException,
                                      T expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> iterate(initialValue, applyFunction, untilPredicate));
        } else {
            assertEquals(expectedResult, iterate(initialValue, applyFunction, untilPredicate));
        }
    }


    static Stream<Arguments> reverseListTestCases() {
        List<Integer> integersList = asList(3, 7, 9, 11, 15);
        Set<String> stringsLinkedSet = new LinkedHashSet<>(asList("A", "BT", "YTGH", "IOP"));

        TreeSet<Integer> integersTreeSet = new TreeSet<>(Collections.reverseOrder());
        integersTreeSet.addAll(asList(45, 71, 9, 11, 35));

        PriorityQueue<Long> longsPriorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
        longsPriorityQueue.addAll(asList(54L, 78L, 12L));

        List<Integer> reverseIntegersList = asList(15, 11, 9, 7, 3);
        List<String> reverseStringsLinkedSet = asList("IOP", "YTGH", "BT", "A");
        List<Integer> reverseIntegersTreeSet = asList(9, 11, 35, 45, 71);
        List<Long> reverseLongsPriorityQueue = asList(78L, 54L, 12L);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,     expectedResult
                Arguments.of( null,                 List.of() ),
                Arguments.of( List.of(),            List.of() ),
                Arguments.of( integersList,         reverseIntegersList ),
                Arguments.of( integersTreeSet,      reverseIntegersTreeSet ),
                Arguments.of( stringsLinkedSet,     reverseStringsLinkedSet ),
                Arguments.of( longsPriorityQueue,   reverseLongsPriorityQueue )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("reverseListTestCases")
    @DisplayName("reverseList: test cases")
    public <T> void reverseList_testCases(Collection<T> sourceCollection,
                                          List<T> expectedResult) {
        assertEquals(expectedResult, reverseList(sourceCollection));
    }


    static Stream<Arguments> sliceTestCases() {
        Set<Integer> integers = new LinkedHashSet<>(asList(11, 12, 13, 14));
        List<String> strings = List.of("a", "b", "c", "d", "f");

        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   from,   to,   expectedException,                expectedResult
                Arguments.of( null,               2,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          3,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( integers,           1,      0,    IllegalArgumentException.class,   null ),
                Arguments.of( null,               0,      1,    null,                             List.of() ),
                Arguments.of( List.of(),          0,      1,    null,                             List.of() ),
                Arguments.of( integers,          -1,      0,    null,                             List.of() ),
                Arguments.of( integers,          -1,      3,    null,                             List.of(11, 12, 13) ),
                Arguments.of( integers,           1,      3,    null,                             List.of(12, 13) ),
                Arguments.of( integers,           2,      5,    null,                             List.of(13, 14) ),
                Arguments.of( integers,           6,      8,    null,                             List.of() ),
                Arguments.of( strings,           -1,      1,    null,                             List.of("a") ),
                Arguments.of( strings,            2,      3,    null,                             List.of("c") ),
                Arguments.of( strings,            4,      9,    null,                             List.of("f") ),
                Arguments.of( longs,              0,      1,    null,                             List.of(12L) ),
                Arguments.of( longs,              1,      2,    null,                             List.of(54L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sliceTestCases")
    @DisplayName("slice: test cases")
    public <T> void slice_testCases(Collection<T> sourceCollection,
                                    int from,
                                    int until,
                                    Class<? extends Exception> expectedException,
                                    List<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> slice(sourceCollection, from, until));
        } else {
            assertEquals(expectedResult, slice(sourceCollection, from, until));
        }
    }


    static Stream<Arguments> slidingTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(asList(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,                  expectedException,                expectedResult
                Arguments.of( null,              -1,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,               5,                     null,                             List.of() ),
                Arguments.of( integers,           0,                     null,                             List.of() ),
                Arguments.of( integers,           integers.size(),       null,                             List.of(integers) ),
                Arguments.of( integers,           integers.size() + 1,   null,                             List.of(integers) ),
                Arguments.of( integers,           2,                     null,                             List.of(asList(1, 3), asList(3, 5)) ),
                Arguments.of( strings,            2,                     null,                             List.of(asList("A", "E"), asList("E", "G"), asList("G", "M")) ),
                Arguments.of( strings,            3,                     null,                             List.of(asList("A", "E", "G"), asList("E", "G", "M")) ),
                Arguments.of( longs,              2,                     null,                             List.of(asList(12L, 54L), asList(54L, 78L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T> void sliding_testCases(Collection<T> sourceCollection,
                                      int size,
                                      Class<? extends Exception> expectedException,
                                      List<List<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> sliding(sourceCollection, size));
        } else {
            assertEquals(expectedResult, sliding(sourceCollection, size));
        }
    }


    static Stream<Arguments> splitTestCases() {
        List<Integer> integers = List.of(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   size,                  expectedException,                expectedResult
                Arguments.of( null,              -1,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,               5,                     null,                             List.of() ),
                Arguments.of( integers,           0,                     null,                             List.of() ),
                Arguments.of( integers,           integers.size(),       null,                             List.of(integers) ),
                Arguments.of( integers,           integers.size() + 1,   null,                             List.of(integers) ),
                Arguments.of( strings,            2,                     null,                             List.of(List.of("A", "E"), List.of("G", "M")) ),
                Arguments.of( strings,            3,                     null,                             List.of(List.of("A", "E", "G"), List.of("M")) ),
                Arguments.of( longs,              2,                     null,                             List.of(List.of(12L, 54L), List.of(78L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T> void split_testCases(Collection<T> sourceCollection,
                                    int size,
                                    Class<? extends Exception> expectedException,
                                    List<List<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> split(sourceCollection, size));
        } else {
            assertEquals(expectedResult, split(sourceCollection, size));
        }
    }


    static Stream<Arguments> takeWhileNoCollectionFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   expectedResult
                Arguments.of( null,               null,              List.of() ),
                Arguments.of( List.of(),          null,              List.of() ),
                Arguments.of( null,               isEven,            List.of() ),
                Arguments.of( intsSet,            null,              intsList ),
                Arguments.of( intsSet,            isEven,            List.of(2, 6) )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("takeWhileNoCollectionFactoryTestCases")
    @DisplayName("takeWhile: without collection factory test cases")
    public <T> void takeWhileNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                           Predicate<? super T> filterPredicate,
                                                           List<T> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> takeWhileAllParametersTestCases() {
        List<Integer> ints = new ArrayList<>(asList(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = asList(2, 6);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            setSupplier,         expectedIsEvenIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileAllParametersTestCases")
    @DisplayName("takeWhile: with all parameters test cases")
    public <T> void takeWhileAllParameters_testCases(Collection<T> sourceCollection,
                                                     Predicate<? super T> filterPredicate,
                                                     Supplier<Collection<T>> collectionFactory,
                                                     Collection<T> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceCollection, filterPredicate, collectionFactory));
    }


    static Stream<Arguments> transposeTestCases() {
        List<List<Integer>> emptyLists = List.of(List.of(), List.of());
        List<List<Integer>> integers = List.of(List.of(1, 2, 3), List.of(4, 5, 6));
        Set<Set<String>> strings = new LinkedHashSet<>() {{
            add(new LinkedHashSet<>() {{
                add("a1");
                add("a2");
            }});
            add(new LinkedHashSet<>() {{
                add("b1");
                add("b2");
            }});
            add(new LinkedHashSet<>() {{
                add("c1");
                add("c2");
            }});
        }};
        List<List<Integer>> differentInnerListSizes = List.of(List.of(1, 2), List.of(0), List.of(7, 8, 9));

        List<List<Integer>> integersResult = List.of(
                List.of(1, 4),
                List.of(2, 5),
                List.of(3, 6)
        );
        List<List<String>> stringsResult = List.of(
                List.of("a1", "b1", "c1"),
                List.of("a2", "b2", "c2")
        );
        List<List<Integer>> differentInnerListSizesResult = List.of(
                List.of(1, 0, 7),
                List.of(2, 8),
                List.of(9)
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,          expectedResult
                Arguments.of( null,                      List.of() ),
                Arguments.of( List.of(),                 List.of() ),
                Arguments.of( emptyLists,                List.of() ),
                Arguments.of( integers,                  integersResult ),
                Arguments.of( strings,                   stringsResult ),
                Arguments.of( differentInnerListSizes,   differentInnerListSizesResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("transposeTestCases")
    @DisplayName("transpose: test cases")
    public <T> void transpose_testCases(Collection<Collection<T>> sourceCollection,
                                        List<List<T>> expectedResult) {
        assertEquals(expectedResult, transpose(sourceCollection));
    }


    static Stream<Arguments> unzipTestCases() {
        List<Tuple2<String, Integer>> pairList = List.of(Tuple.of("a", 1), Tuple.of("b", 2), Tuple.of("c", 3));
        Set<Tuple2<String, Boolean>> pairSet = new LinkedHashSet<>() {{
            add(Tuple.of("true", true));
            add(Tuple.of("false", false));
        }};

        Tuple2<List<Object>, List<Object>> emptyPairResult = Tuple.of(List.of(), List.of());
        Tuple2<List<String>, List<Integer>> pairListResult = Tuple.of(
                List.of("a", "b", "c"),
                List.of(1, 2, 3)
        );
        Tuple2<List<String>, List<Boolean>> pairSetResult = Tuple.of(
                List.of("true", "false"),
                List.of(true, false)
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,    expectedResult
                Arguments.of( null,                emptyPairResult ),
                Arguments.of( new ArrayList<>(),   emptyPairResult ),
                Arguments.of( pairList,            pairListResult ),
                Arguments.of( pairSet,             pairSetResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("unzipTestCases")
    @DisplayName("unzip: test cases")
    public <T, E> void unzip_testCases(Collection<Tuple2<T, E>> sourceCollection,
                                       Tuple2<List<T>, List<E>> expectedResult) {
        assertEquals(expectedResult, unzip(sourceCollection));
    }


    static Stream<Arguments> zipTestCases() {
        List<Integer> integers = asList(11, 31, 55);
        List<Boolean> booleans = asList(true, false);
        List<String> strings = asList("h", "o", "p");

        List<Tuple2<Integer, Boolean>> integersBooleansResult = asList(
                Tuple.of(11, true),
                Tuple.of(31, false)
        );
        List<Tuple2<Integer, String>> integersStringsResult = asList(
                Tuple.of(11, "h"),
                Tuple.of(31, "o"),
                Tuple.of(55, "p")
        );
        List<Tuple2<Boolean, String>> booleansStringsResult = asList(
                Tuple.of(true, "h"),
                Tuple.of(false, "o")
        );
        return Stream.of(
                //@formatter:off
                //            sourceLeftCollection,   sourceRightCollection,   expectedResult
                Arguments.of( null,                   null,                    List.of() ),
                Arguments.of( null,                   integers,                List.of() ),
                Arguments.of( integers,               null,                    List.of() ),
                Arguments.of( List.of(),              integers,                List.of() ),
                Arguments.of( integers,               List.of(),               List.of() ),
                Arguments.of( integers,               booleans,                integersBooleansResult ),
                Arguments.of( integers,               strings,                 integersStringsResult ),
                Arguments.of( booleans,               strings,                 booleansStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipTestCases")
    @DisplayName("zip: test cases")
    public <T, E> void zip_testCases(Collection<T> sourceLeftCollection,
                                     Collection<E> sourceRightCollection,
                                     List<Tuple2<T, E>> expectedResult) {
        assertEquals(expectedResult, zip(sourceLeftCollection, sourceRightCollection));
    }


    static Stream<Arguments> zipAllTestCases() {
        List<Integer> integers = asList(11, 31, 55);
        List<Boolean> booleans = asList(true, false);
        List<String> strings = asList("h", "o", "p");

        Integer defaultIntegerValue = 99;
        Boolean defaultBooleanValue = true;
        String defaultStringValue = "x";

        List<Tuple2<Object, Boolean>> booleansWithNullResult = List.of(
                Tuple.of(null, true),
                Tuple.of(null, false)
        );
        List<Tuple2<Integer, Object>> integersWithNullResult = List.of(
                Tuple.of(11, null),
                Tuple.of(31, null),
                Tuple.of(55, null)
        );
        List<Tuple2<Integer, Boolean>> integersBooleansResult = List.of(
                Tuple.of(11, true),
                Tuple.of(31, false),
                Tuple.of(55, defaultBooleanValue)
        );
        List<Tuple2<Integer, String>> integersStringsResult = List.of(
                Tuple.of(11, "h"),
                Tuple.of(31, "o"),
                Tuple.of(55, "p")
        );
        List<Tuple2<Boolean, String>> booleansStringsResult = List.of(
                Tuple.of(true, "h"),
                Tuple.of(false, "o"),
                Tuple.of(defaultBooleanValue, "p")
        );
        return Stream.of(
                //@formatter:off
                //            sourceLeftCollection,   sourceRightCollection,   defaultLeftElement,    defaultRightElement,   expectedResult
                Arguments.of( null,                   null,                    null,                  null,                  List.of() ),
                Arguments.of( null,                   booleans,                null,                  99,                    booleansWithNullResult ),
                Arguments.of( integers,               null,                    91,                    null,                  integersWithNullResult ),
                Arguments.of( List.of(),              booleans,                null,                  75,                    booleansWithNullResult ),
                Arguments.of( integers,               List.of(),               49,                    null,                  integersWithNullResult ),
                Arguments.of( integers,               booleans,                defaultIntegerValue,   defaultBooleanValue,   integersBooleansResult ),
                Arguments.of( integers,               strings,                 defaultIntegerValue,   defaultStringValue,    integersStringsResult ),
                Arguments.of( booleans,               strings,                 defaultBooleanValue,   defaultStringValue,    booleansStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipAllTestCases")
    @DisplayName("zipAll: test cases")
    public <T, E> void zipAll_testCases(Collection<T> sourceLeftCollection,
                                        Collection<E> sourceRightCollection,
                                        T defaultLeftElement,
                                        E defaultRightElement,
                                        List<Tuple2<T, E>> expectedResult) {
        assertEquals(expectedResult,
                zipAll(
                        sourceLeftCollection, sourceRightCollection, defaultLeftElement, defaultRightElement
                )
        );
    }


    static Stream<Arguments> zipWithIndexTestCases() {
        List<Integer> integers = asList(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        List<Tuple2<Integer, Integer>> integersResult = asList(
                Tuple.of(0, 1),
                Tuple.of(1, 3),
                Tuple.of(2, 5)
        );
        List<Tuple2<Integer, String>> stringsResult = asList(
                Tuple.of(0, "A"),
                Tuple.of(1, "E"),
                Tuple.of(2, "G"),
                Tuple.of(3, "M")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               List.of() ),
                Arguments.of( List.of(),          List.of() ),
                Arguments.of( integers,           integersResult ),
                Arguments.of( strings,            stringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("zipWithIndexTestCases")
    @DisplayName("zipWithIndex: test cases")
    public <T> void zipWithIndex_testCases(Collection<T> sourceCollection,
                                           List<Tuple2<Integer, T>> expectedResult) {
        assertEquals(expectedResult, zipWithIndex(sourceCollection));
    }

}
