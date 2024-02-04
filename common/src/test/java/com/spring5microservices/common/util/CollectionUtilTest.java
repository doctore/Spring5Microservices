package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.UserDto;
import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple1;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.collection.tuple.Tuple3;
import com.spring5microservices.common.collection.tuple.Tuple4;
import com.spring5microservices.common.collection.tuple.Tuple6;
import com.spring5microservices.common.interfaces.functional.PartialFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionUtilTest {

    static Stream<Arguments> andThenNoCollectionFactoryTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, null, 6));

        Function<Integer, Integer> plus1 =
                i -> null == i
                        ? null
                        : i + 1;
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? null
                        : String.valueOf(i * 2);
        Function<String, Integer> safeLength =
                s -> null == s
                        ? null
                        : s.length();
        Function<Integer, Integer> integerIdentity = Function.identity();

        List<Integer> expectedApplyPlus1AndIdentityResult = asList(2, 3, null, 7);
        List<String> expectedApplyPlus1AndMultiply2StringResult = asList("4", "6", null, "14");
        List<Integer> expectedApplyMultiply2StringAndLengthResult = asList(1, 1, null, 2);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   firstMapper,       secondMapper,      expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,                             List.of() ),
                Arguments.of( null,               plus1,             null,              null,                             List.of() ),
                Arguments.of( null,               null,              multiply2String,   null,                             List.of() ),
                Arguments.of( null,               plus1,             multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,              null,                             List.of() ),
                Arguments.of( List.of(),          plus1,             null,              null,                             List.of() ),
                Arguments.of( List.of(),          null,              multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(),          plus1,             multiply2String,   null,                             List.of() ),
                Arguments.of( ints,               null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1,             null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              multiply2String,   IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1,             integerIdentity,   null,                             expectedApplyPlus1AndIdentityResult ),
                Arguments.of( ints,               plus1,             multiply2String,   null,                             expectedApplyPlus1AndMultiply2StringResult ),
                Arguments.of( ints,               multiply2String,   safeLength,        null,                             expectedApplyMultiply2StringAndLengthResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenNoCollectionFactoryTestCases")
    @DisplayName("andThen: without collection factory test cases")
    public <T, E, R> void andThenNoCollectionFactory_testCases(Collection<? extends T> sourceCollection,
                                                               Function<? super T, ? extends E> firstMapper,
                                                               Function<? super E, ? extends R> secondMapper,
                                                               Class<? extends Exception> expectedException,
                                                               List<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            andThen(
                                    sourceCollection, firstMapper, secondMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    andThen(
                            sourceCollection, firstMapper, secondMapper
                    )
            );
        }
    }


    static Stream<Arguments> andThenAllParametersTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, 2, null, 6));

        Function<Integer, Integer> plus1 =
                i -> null == i
                        ? null
                        : i + 1;
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? null
                        : String.valueOf(i * 2);
        Function<String, Integer> safeLength =
                s -> null == s
                        ? null
                        : s.length();
        Function<Integer, Integer> integerIdentity = Function.identity();

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedApplyPlus1AndIdentityResult = asList(2, 3, null, 7);
        Set<Integer> expectedApplyPlus1AndIdentityResultSet = new LinkedHashSet<>(expectedApplyPlus1AndIdentityResult);
        List<String> expectedApplyPlus1AndMultiply2StringResult = asList("4", "6", null, "14");
        Set<String> expectedApplyPlus1AndMultiply2StringResultSet = new LinkedHashSet<>(expectedApplyPlus1AndMultiply2StringResult);
        List<Integer> expectedApplyMultiply2StringAndLengthResult = asList(1, 1, null, 2);
        Set<Integer> expectedApplyMultiply2StringAndLengthResultSet = new LinkedHashSet<>(expectedApplyMultiply2StringAndLengthResult);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   firstMapper,       secondMapper,      collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,                null,                             List.of() ),
                Arguments.of( null,               null,              null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               plus1,             null,              null,                null,                             List.of() ),
                Arguments.of( null,               plus1,             null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               null,              multiply2String,   null,                null,                             List.of() ),
                Arguments.of( null,               null,              multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               plus1,             multiply2String,   null,                null,                             List.of() ),
                Arguments.of( null,               plus1,             multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          null,              null,              null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          plus1,             null,              null,                null,                             List.of() ),
                Arguments.of( List.of(),          plus1,             null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          null,              multiply2String,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,              multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          plus1,             multiply2String,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          plus1,             multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( ints,               null,              null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1,             null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1,             null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              multiply2String,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              multiply2String,   setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1,             integerIdentity,   null,                null,                             expectedApplyPlus1AndIdentityResult ),
                Arguments.of( ints,               plus1,             integerIdentity,   setSupplier,         null,                             expectedApplyPlus1AndIdentityResultSet ),
                Arguments.of( ints,               plus1,             multiply2String,   null,                null,                             expectedApplyPlus1AndMultiply2StringResult ),
                Arguments.of( ints,               plus1,             multiply2String,   setSupplier,         null,                             expectedApplyPlus1AndMultiply2StringResultSet ),
                Arguments.of( ints,               multiply2String,   safeLength,        null,                null,                             expectedApplyMultiply2StringAndLengthResult ),
                Arguments.of( ints,               multiply2String,   safeLength,        setSupplier,         null,                             expectedApplyMultiply2StringAndLengthResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenAllParametersTestCases")
    @DisplayName("andThen: with all parameters test cases")
    public <T, E, R> void andThenAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                         Function<? super T, ? extends E> firstMapper,
                                                         Function<? super E, ? extends R> secondMapper,
                                                         Supplier<Collection<R>> collectionFactory,
                                                         Class<? extends Exception> expectedException,
                                                         Collection<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            andThen(
                                    sourceCollection, firstMapper, secondMapper, collectionFactory
                            )
            );
        } else {
            assertEquals(expectedResult,
                    andThen(
                            sourceCollection, firstMapper, secondMapper, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPredicateAndFunctionsTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6));
        Predicate<Integer> isEven = i -> i % 2 == 0;

        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);

        List<String> expectedIntsNoFilterResult = List.of("2", "3", "4", "7");
        List<String> expectedIntsResult = List.of("2", "3", "6", "7");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultMapper,     orElseMapper,      expectedException,                expectedResult
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
    @MethodSource("applyOrElseWithPredicateAndFunctionsTestCases")
    @DisplayName("applyOrElse: with Predicate and Functions test cases")
    public <T, E> void applyOrElseWithPredicateAndFunctions_testCases(Collection<? extends T> sourceCollection,
                                                                      Predicate<? super T> filterPredicate,
                                                                      Function<? super T, ? extends E> defaultMapper,
                                                                      Function<? super T, ? extends E> orElseMapper,
                                                                      Class<? extends Exception> expectedException,
                                                                      List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceCollection, filterPredicate, defaultMapper, orElseMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultMapper, orElseMapper
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPredicateFunctionsAndSupplierTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 6);
        Predicate<Integer> isOdd = i -> i % 2 == 1;

        Function<Integer, String> plus1String = i -> String.valueOf(i + 1);
        Function<Integer, String> multiply2String = i -> String.valueOf(i * 2);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedIntsResultNoFilterList = List.of("2", "3", "4", "7");
        List<String> expectedIntsResultList = List.of("2", "4", "4", "12");
        Set<String> expectedIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   defaultMapper,     orElseMapper,      collectionFactory,  expectedException,                expectedResult
                Arguments.of( null,               null,              null,              null,              null,               null,                             List.of() ),
                Arguments.of( null,               null,              null,              null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( null,               isOdd,             null,              null,              null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             null,              null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( null,               null,              plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( null,               null,              plus1String,       null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( null,               isOdd,             plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             plus1String,       null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( null,               isOdd,             plus1String,       multiply2String,   null,               null,                             List.of() ),
                Arguments.of( null,               isOdd,             plus1String,       multiply2String,   setSupplier,        null,                             Set.of() ),
                Arguments.of( List.of(),          null,              null,              null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          null,              null,              null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( List.of(),          isOdd,             null,              null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             null,              null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( List.of(),          null,              plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          null,              plus1String,       null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       null,              null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       null,              setSupplier,        null,                             Set.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       multiply2String,   null,               null,                             List.of() ),
                Arguments.of( List.of(),          isOdd,             plus1String,       multiply2String,   setSupplier,        null,                             Set.of() ),
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
    @MethodSource("applyOrElseWithPredicateFunctionsAndSupplierTestCases")
    @DisplayName("applyOrElse: with Predicate, Functions and Supplier test cases")
    public <T, E> void applyOrElseWithPredicateFunctionsAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                              Predicate<? super T> filterPredicate,
                                                                              Function<? super T, ? extends E> defaultMapper,
                                                                              Function<? super T, ? extends E> orElseMapper,
                                                                              Supplier<Collection<E>> collectionFactory,
                                                                              Class<? extends Exception> expectedException,
                                                                              Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceCollection, filterPredicate, defaultMapper, orElseMapper, collectionFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, filterPredicate, defaultMapper, orElseMapper, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPartialFunctionAndFunctionTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, null, 3, 6));
        PartialFunction<Integer, String> plus1StringIfEven = PartialFunction.of(
            i -> null != i && 0 == i % 2,
            i -> null == i
                    ? null
                    : String.valueOf(i + 1)
        );
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? null
                        : String.valueOf(i * 2);

        List<String> expectedIntsResult = asList("2", null, "6", "7");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,     orElseMapper,      expectedException,                expectedResult
                Arguments.of( null,               null,                null,              null,                             List.of() ),
                Arguments.of( null,               plus1StringIfEven,   null,              null,                             List.of() ),
                Arguments.of( null,               plus1StringIfEven,   multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(),          null,                null,              null,                             List.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   null,              null,                             List.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   multiply2String,   null,                             List.of() ),
                Arguments.of( List.of(1),         null,                null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         plus1StringIfEven,   null,              IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,                multiply2String,   IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1StringIfEven,   multiply2String,   null,                             expectedIntsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithPartialFunctionAndFunctionTestCases")
    @DisplayName("applyOrElse: with PartialFunction and Function test cases")
    public <T, E> void applyOrElseWithPartialFunctionAndFunction_testCases(Collection<? extends T> sourceCollection,
                                                                           PartialFunction<? super T, ? extends E> partialFunction,
                                                                           Function<? super T, ? extends E> orElseMapper,
                                                                           Class<? extends Exception> expectedException,
                                                                           List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceCollection, partialFunction, orElseMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, partialFunction, orElseMapper
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPartialFunctionFunctionAndSupplierTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(asList(1, null, 3, 6));
        PartialFunction<Integer, String> plus1StringIfEven = PartialFunction.of(
                i -> null != i && 0 == i % 2,
                i -> null == i
                        ? null
                        : String.valueOf(i + 1)
        );
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? null
                        : String.valueOf(i * 2);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedIntsResult = asList("2", null, "6", "7");
        Set<String> expectedIntsResultSetSupplier = new LinkedHashSet<>(asList("2", null, "6", "7"));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,     orElseMapper,      collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,                null,              null,                null,                             List.of() ),
                Arguments.of( null,               null,                null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               plus1StringIfEven,   null,              null,                null,                             List.of() ),
                Arguments.of( null,               plus1StringIfEven,   null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               plus1StringIfEven,   multiply2String,   null,                null,                             List.of() ),
                Arguments.of( null,               plus1StringIfEven,   multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          null,                null,              null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,                null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   null,              null,                null,                             List.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   null,              setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   multiply2String,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          plus1StringIfEven,   multiply2String,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(1),         null,                null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,                null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         plus1StringIfEven,   null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         plus1StringIfEven,   null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               plus1StringIfEven,   multiply2String,   null,                null,                             expectedIntsResult ),
                Arguments.of( ints,               plus1StringIfEven,   multiply2String,   setSupplier,         null,                             expectedIntsResultSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithPartialFunctionFunctionAndSupplierTestCases")
    @DisplayName("applyOrElse: with PartialFunction, Function and Supplier test cases")
    public <T, E> void applyOrElseWithPartialFunctionFunctionAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                                   PartialFunction<? super T, ? extends E> partialFunction,
                                                                                   Function<? super T, ? extends E> orElseMapper,
                                                                                   Supplier<Collection<E>> collectionFactory,
                                                                                   Class<? extends Exception> expectedException,
                                                                                   Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceCollection, partialFunction, orElseMapper, collectionFactory
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceCollection, partialFunction, orElseMapper, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> collectWithPredicateAndFunctionTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6));
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
    @MethodSource("collectWithPredicateAndFunctionTestCases")
    @DisplayName("collect: with Predicate and Function test cases")
    public <T, E> void collectWithPredicateAndFunction_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> collectWithPredicateFunctionAndSupplierTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 6);
        Set<String> collectedInts = new LinkedHashSet<>(List.of("1", "3"));

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
    @MethodSource("collectWithPredicateFunctionAndSupplierTestCases")
    @DisplayName("collect: with Predicate, Function and Supplier test cases")
    public <T, E> void collectWithPredicateFunctionAndSupplier_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> collectWithPartialFunctionTestCases() {
        List<Integer> ints = asList(1, null, 12, 33, 45, 6);
        PartialFunction<Integer, String> toStringIfLowerThan20 = PartialFunction.of(
                i -> null != i && 0 > i.compareTo(20),
                i -> null == i
                        ? null
                        : i.toString()
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,         expectedException,                expectedResult
                Arguments.of( null,               null,                    null,                             List.of() ),
                Arguments.of( null,               toStringIfLowerThan20,   null,                             List.of() ),
                Arguments.of( List.of(),          null,                    null,                             List.of() ),
                Arguments.of( List.of(),          toStringIfLowerThan20,   null,                             List.of() ),
                Arguments.of( List.of(1),         null,                    IllegalArgumentException.class,   null ),
                Arguments.of( ints,               toStringIfLowerThan20,   null,                             List.of("1", "12", "6") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPartialFunctionTestCases")
    @DisplayName("collect: with PartialFunction test cases")
    public <T, E> void collectWithPartialFunction_testCases(Collection<? extends T> sourceCollection,
                                                            PartialFunction<? super T, ? extends E> partialFunction,
                                                            Class<? extends Exception> expectedException,
                                                            List<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, partialFunction));
        } else {
            assertEquals(expectedResult, collect(sourceCollection, partialFunction));
        }
    }


    static Stream<Arguments> collectWithPartialFunctionAndSupplierTestCases() {
        List<String> strings = asList("A", "AB", null, "ABC", "T");
        PartialFunction<String, Integer> lengthIfSizeGreaterThan1 = PartialFunction.of(
                s -> null != s && 1 < s.length(),
                s -> null == s
                        ? null
                        : s.length()
        );
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,            collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,                       null,                null,                             List.of() ),
                Arguments.of( null,               null,                       setSupplier,         null,                             Set.of() ),
                Arguments.of( null,               lengthIfSizeGreaterThan1,   null,                null,                             List.of() ),
                Arguments.of( null,               lengthIfSizeGreaterThan1,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          null,                       null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,                       setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),          lengthIfSizeGreaterThan1,   null,                null,                             List.of() ),
                Arguments.of( List.of(),          lengthIfSizeGreaterThan1,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of("1"),       null,                       null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of("1"),       null,                       setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( strings,            lengthIfSizeGreaterThan1,   null,                null,                             List.of(2, 3) ),
                Arguments.of( strings,            lengthIfSizeGreaterThan1,   setSupplier,         null,                             Set.of(2, 3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPartialFunctionAndSupplierTestCases")
    @DisplayName("collect: with PartialFunction and Supplier test cases")
    public <T, E> void collectWithPartialFunctionAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                       PartialFunction<? super T, ? extends E> partialFunction,
                                                                       Supplier<Collection<E>> collectionFactory,
                                                                       Class<? extends Exception> expectedException,
                                                                       Collection<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceCollection, partialFunction, collectionFactory));
        } else {
            assertEquals(expectedResult, collect(sourceCollection, partialFunction, collectionFactory));
        }
    }


    static Stream<Arguments> collectFirstTestCases() {
        List<String> strings = asList("A", "AB", null, "ABC", "T");
        PartialFunction<String, Integer> lengthIfSizeGreaterThan1 = PartialFunction.of(
                s -> null != s && 1 < s.length(),
                s -> null == s
                        ? null
                        : s.length()
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,            expectedException,            expectedResult
                Arguments.of( null,               null,                       null,                         empty() ),
                Arguments.of( null,               lengthIfSizeGreaterThan1,   null,                         empty() ),
                Arguments.of( List.of(),          null,                       null,                         empty() ),
                Arguments.of( List.of(),          lengthIfSizeGreaterThan1,   null,                         empty() ),
                Arguments.of( List.of("1"),       null,                   IllegalArgumentException.class,   null ),
                Arguments.of( strings,            lengthIfSizeGreaterThan1,   null,                             of(2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectFirstTestCases")
    @DisplayName("collectFirst: test cases")
    public <T, E> void collectFirst_testCases(Collection<? extends T> sourceCollection,
                                              PartialFunction<? super T, ? extends E> partialFunction,
                                              Class<? extends Exception> expectedException,
                                              Optional<E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collectFirst(sourceCollection, partialFunction));
        } else {
            assertEquals(expectedResult, collectFirst(sourceCollection, partialFunction));
        }
    }


    static Stream<Arguments> concatNoCollectionFactoryTestCases() {
        List<Integer> ints1 = List.of(1, 2, 3);
        List<Integer> ints2 = List.of(4, 5);
        List<Integer> ints3 = List.of(6);
        List<Integer> intsWithNulls = asList(6, null, 7, null);

        List<Integer> expectedResultInts123 = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> expectedResultInts2AndWithNulls = asList(4, 5, 6, null, 7, null);
        return Stream.of(
                //@formatter:off
                //            collectionToConcat1,   collectionToConcat2,   collectionToConcat3,   expectedResult
                Arguments.of( null,                  null,                  null,                  List.of() ),
                Arguments.of( List.of(),             null,                  null,                  List.of() ),
                Arguments.of( List.of(),             List.of(),             null,                  List.of() ),
                Arguments.of( List.of(),             List.of(),             List.of(),             List.of() ),
                Arguments.of( ints1,                 null,                  List.of(),             ints1 ),
                Arguments.of( ints1,                 ints2,                 ints3,                 expectedResultInts123 ),
                Arguments.of( ints2,                 null,                  intsWithNulls,         expectedResultInts2AndWithNulls )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatNoCollectionFactoryTestCases")
    @DisplayName("concat: without collection factory test cases")
    public <T> void concatNoCollectionFactory_testCases(Collection<? extends T> collectionToConcat1,
                                                        Collection<? extends T> collectionToConcat2,
                                                        Collection<? extends T> collectionToConcat3,
                                                        List<T> expectedResult) {
        assertEquals(expectedResult, concat(collectionToConcat1, collectionToConcat2, collectionToConcat3));
    }


    static Stream<Arguments> concatAllParametersTestCases() {
        List<String> strings1 = List.of("1", "2", "3");
        List<String> strings2 = List.of("4", "5", "4");
        List<String> strings3 = List.of("6", "7", "7");
        List<String> stringsWithNulls = asList("6", null, "7", null);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedResultStrings2DefaultSupplier = List.of("4", "5", "4");
        Set<String> expectedResultStrings2SetSupplier = Set.of("4", "5");
        List<String> expectedResultStrings123DefaultSupplier = List.of("1", "2", "3", "4", "5", "4", "6", "7", "7");
        Set<String> expectedResultStrings123SetSupplier = Set.of("1", "2", "3", "4", "5", "6", "7");
        List<String> expectedResultStrings2AndWithNullsDefaultSupplier = asList("4", "5", "4", "6", null, "7", null);
        Set<String> expectedResultStrings2AndWithNullsSetSupplier = new LinkedHashSet<>(asList("4", "5", "6", null, "7"));
        return Stream.of(
                //@formatter:off
                //            collectionToConcat1,   collectionToConcat2,   collectionToConcat3,   collectionFactory,   expectedResult
                Arguments.of( null,                  null,                  null,                  null,                List.of() ),
                Arguments.of( null,                  null,                  null,                  setSupplier,         Set.of() ),
                Arguments.of( List.of(),             null,                  null,                  null,                List.of() ),
                Arguments.of( List.of(),             null,                  null,                  setSupplier,         Set.of() ),
                Arguments.of( List.of(),             Set.of(),              null,                  null,                List.of() ),
                Arguments.of( List.of(),             Set.of(),              null,                  setSupplier,         Set.of() ),
                Arguments.of( List.of(),             Set.of(),              List.of(),             null,                List.of() ),
                Arguments.of( List.of(),             Set.of(),              List.of(),             setSupplier,         Set.of() ),
                Arguments.of( strings2,              Set.of(),              List.of(),             null,                expectedResultStrings2DefaultSupplier ),
                Arguments.of( strings2,              Set.of(),              null,                  setSupplier,         expectedResultStrings2SetSupplier ),
                Arguments.of( strings1,              strings2,              strings3,              null,                expectedResultStrings123DefaultSupplier ),
                Arguments.of( strings1,              strings2,              strings3,              setSupplier,         expectedResultStrings123SetSupplier ),
                Arguments.of( strings2,              null,                  stringsWithNulls,      null,                expectedResultStrings2AndWithNullsDefaultSupplier ),
                Arguments.of( strings2,              null,                  stringsWithNulls,      setSupplier,         expectedResultStrings2AndWithNullsSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatAllParametersTestCases")
    @DisplayName("concat: with all parameters test cases")
    public <T> void concatAllParameters_testCases(Collection<? extends T> collectionToConcat1,
                                                  Collection<? extends T> collectionToConcat2,
                                                  Collection<? extends T> collectionToConcat3,
                                                  Supplier<Collection<T>> collectionFactory,
                                                  Collection<T> expectedResult) {
        assertEquals(expectedResult, concat(collectionFactory, collectionToConcat1, collectionToConcat2, collectionToConcat3));
    }


    static Stream<Arguments> copyNoCollectionFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6, 2);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);

        List<Integer> expectedIntsListResult = new ArrayList<>(intsList);
        List<Integer> expectedIntsSetResult = new ArrayList<>(intsSet);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               List.of() ),
                Arguments.of( List.of(),          List.of() ),
                Arguments.of( intsList,           expectedIntsListResult ),
                Arguments.of( intsSet,            expectedIntsSetResult )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("copyNoCollectionFactoryTestCases")
    @DisplayName("copy: without collection factory test cases")
    public <T> void copyNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                      List<T> expectedResult) {
        List<T> result = copy(sourceCollection);
        assertEquals(expectedResult, result);
        if (null != expectedResult && !expectedResult.isEmpty()) {
            expectedResult.clear();
            assertTrue(expectedResult.isEmpty());
            assertFalse(result.isEmpty());
        }
    }


    static Stream<Arguments> copyAllParametersTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6, 2);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsListResultList = new ArrayList<>(intsList);
        Set<Integer> expectedIntsListResultSet = new LinkedHashSet<>(intsList);
        List<Integer> expectedIntsSetResultList = new ArrayList<>(intsSet);
        Set<Integer> expectedIntsSetResultSet = new LinkedHashSet<>(intsSet);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   collectionFactory,   expectedResult
                Arguments.of( null,               null,                List.of() ),
                Arguments.of( null,               setSupplier,         Set.of() ),
                Arguments.of( List.of(),          null,                List.of() ),
                Arguments.of( List.of(),          setSupplier,         Set.of() ),
                Arguments.of( intsList,           null,                expectedIntsListResultList ),
                Arguments.of( intsList,           setSupplier,         expectedIntsListResultSet ),
                Arguments.of( intsSet,            null,                expectedIntsSetResultList ),
                Arguments.of( intsSet,            setSupplier,         expectedIntsSetResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("copyAllParametersTestCases")
    @DisplayName("copy: with all parameters test cases")
    public <T> void copyAllParameters_testCases(Collection<T> sourceCollection,
                                                Supplier<Collection<T>> collectionFactory,
                                                Collection<T> expectedResult) {
        Collection<T> result = copy(sourceCollection, collectionFactory);
        assertEquals(expectedResult, result);
        if (null != expectedResult && !expectedResult.isEmpty()) {
            expectedResult.clear();
            assertTrue(expectedResult.isEmpty());
            assertFalse(result.isEmpty());
        }
    }


    static Stream<Arguments> countTestCases() {
        List<Integer> integers = List.of(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(List.of("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 78L, 12L));

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


    static Stream<Arguments> dropWhileNoCollectionFactoryTestCases() {
        List<Integer> intsList = List.of(1, 3, 4, 5, 6);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   expectedResult
                Arguments.of( null,               null,              List.of() ),
                Arguments.of( null,               isOdd,             List.of() ),
                Arguments.of( List.of(),          null,              List.of() ),
                Arguments.of( List.of(),          isOdd,             List.of() ),
                Arguments.of( intsSet,            null,              intsList ),
                Arguments.of( intsSet,            isOdd,             List.of(4, 5, 6) )
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
        List<Integer> ints = List.of(2, 4, 6, 5, 8);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = List.of(5, 8);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              null,                ints ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            null,                expectedIntsResultList ),
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



    static Stream<Arguments> filterNoCollectionFactoryTestCases() {
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
    @MethodSource("filterNoCollectionFactoryTestCases")
    @DisplayName("filter: without collection factory test cases")
    public <T> void filterNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                        Predicate<? super T> filterPredicate,
                                                        List<T> expectedResult) {
        assertEquals(expectedResult, filter(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> filterAllParametersTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 6);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = List.of(2, 6);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              null,                ints ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            setSupplier,         expectedIsEvenIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterAllParametersTestCases")
    @DisplayName("filter: with all parameters test cases")
    public <T> void filterAllParameters_testCases(Collection<T> sourceCollection,
                                                  Predicate<? super T> filterPredicate,
                                                  Supplier<Collection<T>> collectionFactory,
                                                  Collection<T> expectedResult) {
        assertEquals(expectedResult, filter(sourceCollection, filterPredicate, collectionFactory));
    }


    static Stream<Arguments> filterNotNoCollectionFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   expectedResult
                Arguments.of( null,               null,              List.of() ),
                Arguments.of( null,               isEven,            List.of() ),
                Arguments.of( List.of(),          null,              List.of() ),
                Arguments.of( List.of(),          isEven,            List.of() ),
                Arguments.of( intsSet,            null,              intsList ),
                Arguments.of( intsSet,            isEven,            List.of(1, 3) )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("filterNotNoCollectionFactoryTestCases")
    @DisplayName("filterNot: without collection factory test cases")
    public <T> void filterNotNoCollectionFactory_testCases(Collection<T> sourceCollection,
                                                           Predicate<? super T> filterPredicate,
                                                           List<T> expectedResult) {
        assertEquals(expectedResult, filterNot(sourceCollection, filterPredicate));
    }


    static Stream<Arguments> filterNotAllParametersTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 6);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = List.of(1, 3);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              null,                ints ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            setSupplier,         expectedIsEvenIntsResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterNotAllParametersTestCases")
    @DisplayName("filterNot: with all parameters test cases")
    public <T> void filterNotAllParameters_testCases(Collection<T> sourceCollection,
                                                     Predicate<? super T> filterPredicate,
                                                     Supplier<Collection<T>> collectionFactory,
                                                     Collection<T> expectedResult) {
        assertEquals(expectedResult, filterNot(sourceCollection, filterPredicate, collectionFactory));
    }


    static Stream<Arguments> findTestCases() {
        List<Integer> integers = List.of(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(List.of("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 78L, 12L));

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


    static Stream<Arguments> findLastTestCases() {
        List<Integer> integers = List.of(3, 7, 9, 11, 15);
        Set<String> strings = new LinkedHashSet<>(List.of("A", "BT", "YTGH", "IOP"));
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 78L, 12L));

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


    static Stream<Arguments> flattenNoCollectionFactoryTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 1, 21);
        Set<List<String>> strings = new LinkedHashSet<>(asList(null, List.of("a", "b"), null, List.of("5", "6")));
        List<Object> longs = asList(3L, List.of(6L, 8L), List.of(List.of(11L, 21L)), null);

        List<String> setListResult = asList(null, "a", "b", "5", "6");
        List<Long> listObjectResult = asList(3L, 6L, 8L, 11L, 21L, null);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               List.of() ),
                Arguments.of( List.of(),          List.of() ),
                Arguments.of( ints,               ints ),
                Arguments.of( strings,            setListResult ),
                Arguments.of( longs,              listObjectResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenNoCollectionFactoryTestCases")
    @DisplayName("flatten: without collection factory test cases")
    public <T> void flattenNoCollectionFactory_testCases(Collection<Object> sourceCollection,
                                                         List<T> expectedResult) {
        assertEquals(expectedResult, flatten(sourceCollection));
    }


    static Stream<Arguments> flattenAllParametersTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 1, 21);
        Set<List<String>> strings = new LinkedHashSet<>(asList(null, List.of("a", "b"), null, List.of("5", "6")));
        List<Object> longs = asList(3L, List.of(6L, 8L), List.of(List.of(11L, 21L)), null);

        Supplier<Collection<Integer>> setIntegerSupplier = LinkedHashSet::new;
        Supplier<Collection<String>> setStringSupplier = LinkedHashSet::new;
        Supplier<Collection<Long>> setLongSupplier = LinkedHashSet::new;

        List<String> setListResult = asList(null, "a", "b", "5", "6");
        List<Long> listObjectResult = asList(3L, 6L, 8L, 11L, 21L, null);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   collectionFactory,    expectedResult
                Arguments.of( null,               null,                 List.of() ),
                Arguments.of( null,               setIntegerSupplier,   Set.of() ),
                Arguments.of( List.of(),          null,                 List.of() ),
                Arguments.of( List.of(),          setIntegerSupplier,   Set.of() ),
                Arguments.of( ints,               null,                 ints ),
                Arguments.of( ints,               setIntegerSupplier,   new LinkedHashSet<>(ints) ),
                Arguments.of( strings,            null,                 setListResult ),
                Arguments.of( strings,            setStringSupplier,    new LinkedHashSet<>(setListResult) ),
                Arguments.of( longs,              null,                 listObjectResult ),
                Arguments.of( longs,              setLongSupplier,      new LinkedHashSet<>(listObjectResult) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenAllParametersTestCases")
    @DisplayName("flatten: with all parameters test cases")
    public <T> void flattenAllParameters_testCases(Collection<Object> sourceCollection,
                                                   Supplier<Collection<T>> collectionFactory,
                                                   Collection<T> expectedResult) {
        assertEquals(expectedResult, flatten(sourceCollection, collectionFactory));
    }


    static Stream<Arguments> foldLeftWithInitialValueAndAccumulatorTestCases() {
        List<Integer> integers = List.of(1, 3, 5);
        List<String> strings = List.of("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 75L, 12L));

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Long, Long, Long> sum = (a, b) -> ofNullable(a).orElse(0L) + ofNullable(b).orElse(0L);
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        BiFunction<String, String, String> concat = (a, b) -> ofNullable(a).orElse("") + ofNullable(b).orElse("");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   initialValue,   accumulator,   expectedResult
                Arguments.of( null,               null,           null,          null ),
                Arguments.of( List.of(),          2,              null,          2 ),
                Arguments.of( List.of(),          1,              multiply,      1 ),
                Arguments.of( integers,           0,              null,          0 ),
                Arguments.of( integers,           1,              multiply,      15 ),
                Arguments.of( longs,              null,           sum,           141L ),
                Arguments.of( longs,              3L,             sum,           144L ),
                Arguments.of( strings,            0,              sumLength,     6 ),
                Arguments.of( strings,            "-",            concat,        "-ABEGMT" ),
                Arguments.of( strings,            null,           concat,        "ABEGMT" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftWithInitialValueAndAccumulatorTestCases")
    @DisplayName("foldLeft: with initialValue and accumulator test cases")
    public <T, E> void foldLeftWithInitialValueAndAccumulator_testCases(Collection<T> sourceCollection,
                                                                        E initialValue,
                                                                        BiFunction<E, ? super T, E> accumulator,
                                                                        E expectedResult) {
        assertEquals(expectedResult, foldLeft(sourceCollection, initialValue, accumulator));
    }


    static Stream<Arguments> foldLeftAllParametersTestCases() {
        List<Integer> integers = List.of(1, 3, 5, 6);
        List<String> strings = asList("AB", "E", "GMTY", null);

        Predicate<Integer> isOdd = i -> null != i && 1 == i % 2;
        Predicate<String> longerThan2 = s -> null != s && 2 < s.length();

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> ofNullable(a).orElse(1) * ofNullable(b).orElse(1);
        BiFunction<String, String, String> concat = (a, b) -> ofNullable(a).orElse("") + ofNullable(b).orElse("");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   initialValue,   accumulator,   expectedResult
                Arguments.of( null,               null,              null,           null,          null ),
                Arguments.of( null,               isOdd,             null,           null,          null ),
                Arguments.of( List.of(),          null,              null,           null,          null ),
                Arguments.of( List.of(),          isOdd,             null,           null,          null ),
                Arguments.of( List.of(),          null,              2,              null,          2 ),
                Arguments.of( List.of(),          isOdd,             2,              null,          2 ),
                Arguments.of( List.of(),          null,              1,              multiply,      1 ),
                Arguments.of( List.of(),          isOdd,             1,              multiply,      1 ),
                Arguments.of( integers,           null,              null,           null,          null ),
                Arguments.of( integers,           null,              null,           multiply,      90 ),
                Arguments.of( integers,           null,              0,              null,          0 ),
                Arguments.of( integers,           null,              1,              multiply,      90 ),
                Arguments.of( integers,           isOdd,             1,              multiply,      15 ),
                Arguments.of( strings,            null,              null,           null,          null ),
                Arguments.of( strings,            longerThan2,       null,           null,          null ),
                Arguments.of( strings,            null,              "",             null,          "" ),
                Arguments.of( strings,            longerThan2,       "",             null,          "" ),
                Arguments.of( strings,            null,              null,           concat,        "ABEGMTY" ),
                Arguments.of( strings,            longerThan2,       null,           concat,        "GMTY" ),
                Arguments.of( strings,            null,              "-",            concat,        "-ABEGMTY" ),
                Arguments.of( strings,            longerThan2,       "-",            concat,        "-GMTY" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftAllParametersTestCases")
    @DisplayName("foldLeft: with all parameters test cases")
    public <T, E> void foldLeftAllParameters_testCases(Collection<T> sourceCollection,
                                                       Predicate<? super T> filterPredicate,
                                                       E initialValue,
                                                       BiFunction<E, ? super T, E> accumulator,
                                                       E expectedResult) {
        assertEquals(expectedResult, foldLeft(sourceCollection, filterPredicate, initialValue, accumulator));
    }


    static Stream<Arguments> foldRightWithInitialValueAndAccumulatorTestCases() {
        List<Integer> integers = List.of(1, 3, 5);
        List<String> strings = List.of("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 75L, 12L));

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        BiFunction<Long, Long, Long> sum = (a, b) -> ofNullable(a).orElse(0L) + ofNullable(b).orElse(0L);
        BiFunction<Integer, String, Integer> sumLength = (a, b) -> a + b.length();
        BiFunction<String, String, String> concat = (a, b) -> ofNullable(a).orElse("") + ofNullable(b).orElse("");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   initialValue,   accumulator,   expectedResult
                Arguments.of( null,               null,           null,          null ),
                Arguments.of( List.of(),          2,              null,          2 ),
                Arguments.of( List.of(),          1,              multiply,      1 ),
                Arguments.of( integers,           0,              null,          0 ),
                Arguments.of( integers,           1,              multiply,      15 ),
                Arguments.of( longs,              null,           sum,           141L ),
                Arguments.of( longs,              3L,             sum,           144L ),
                Arguments.of( strings,            0,              sumLength,     6 ),
                Arguments.of( strings,            "-",            concat,        "-GMTEAB" ),
                Arguments.of( strings,            null,           concat,        "GMTEAB" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldRightWithInitialValueAndAccumulatorTestCases")
    @DisplayName("foldRight: with initialValue and accumulator test cases")
    public <T, E> void foldRightWithInitialValueAndAccumulator_testCases(Collection<T> sourceCollection,
                                                                         E initialValue,
                                                                         BiFunction<E, ? super T, E> accumulator,
                                                                         E expectedResult) {
        assertEquals(expectedResult, foldRight(sourceCollection, initialValue, accumulator));
    }


    static Stream<Arguments> foldRightAllParametersTestCases() {
        List<Integer> integers = List.of(1, 3, 5, 6);
        List<String> strings = List.of("AB", "E", "GMTY");

        Predicate<Integer> isOdd = i -> null != i && 1 == i % 2;
        Predicate<String> longerThan2 = s -> null != s && 2 < s.length();

        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> ofNullable(a).orElse(1) * ofNullable(b).orElse(1);
        BiFunction<String, String, String> concat = (a, b) -> ofNullable(a).orElse("") + ofNullable(b).orElse("");
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   initialValue,   accumulator,   expectedResult
                Arguments.of( null,               null,              null,           null,          null ),
                Arguments.of( null,               isOdd,             null,           null,          null ),
                Arguments.of( List.of(),          null,              null,           null,          null ),
                Arguments.of( List.of(),          isOdd,             null,           null,          null ),
                Arguments.of( List.of(),          null,              2,              null,          2 ),
                Arguments.of( List.of(),          isOdd,             2,              null,          2 ),
                Arguments.of( List.of(),          null,              1,              multiply,      1 ),
                Arguments.of( List.of(),          isOdd,             1,              multiply,      1 ),
                Arguments.of( integers,           null,              null,           null,          null ),
                Arguments.of( integers,           null,              null,           multiply,      90 ),
                Arguments.of( integers,           null,              0,              null,          0 ),
                Arguments.of( integers,           null,              1,              multiply,      90 ),
                Arguments.of( integers,           isOdd,             1,              multiply,      15 ),
                Arguments.of( strings,            null,              null,           null,          null ),
                Arguments.of( strings,            longerThan2,       null,           null,          null ),
                Arguments.of( strings,            null,              "",             null,          "" ),
                Arguments.of( strings,            longerThan2,       "",             null,          "" ),
                Arguments.of( strings,            null,              null,           concat,        "GMTYEAB" ),
                Arguments.of( strings,            longerThan2,       null,           concat,        "GMTY" ),
                Arguments.of( strings,            null,              "-",            concat,        "-GMTYEAB" ),
                Arguments.of( strings,            longerThan2,       "-",            concat,        "-GMTY" )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldRightAllParametersTestCases")
    @DisplayName("foldRight: with all parameters test cases")
    public <T, E> void foldRightAllParameters_testCases(Collection<T> sourceCollection,
                                                        Predicate<? super T> filterPredicate,
                                                        E initialValue,
                                                        BiFunction<E, ? super T, E> accumulator,
                                                        E expectedResult) {
        assertEquals(expectedResult, foldRight(sourceCollection, filterPredicate, initialValue, accumulator));
    }


    static Stream<Arguments> frequencyWithNoObjectToSearchTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpensive = new PizzaDto("Carbonara", 10D);
        PizzaDto margherita = new PizzaDto("Margherita", 7.5d);

        Set<PizzaDto> allPizzasSet = new HashSet<>(asList(carbonaraCheap, carbonaraExpensive, margherita, null));
        List<PizzaDto> allPizzasListWithNulls = asList(carbonaraCheap, carbonaraExpensive, margherita, null, null);
        List<PizzaDto> allPizzasListRepeated = List.of(carbonaraCheap, carbonaraExpensive, margherita, margherita, carbonaraExpensive, margherita);

        Map<PizzaDto, Integer> resultSet = new HashMap<>() {{
            put(carbonaraCheap, 1);
            put(carbonaraExpensive, 1);
            put(margherita, 1);
            put(null, 1);
        }};
        Map<PizzaDto, Integer> resultWithNulls = new HashMap<>() {{
            put(carbonaraCheap, 1);
            put(carbonaraExpensive, 1);
            put(margherita, 1);
            put(null, 2);
        }};
        Map<PizzaDto, Integer> resultRepeated = new HashMap<>() {{
            put(carbonaraCheap, 1);
            put(carbonaraExpensive, 2);
            put(margherita, 3);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,         expectedResult
                Arguments.of( null,                     Map.of() ),
                Arguments.of( List.of(),                Map.of() ),
                Arguments.of( allPizzasSet,             resultSet ),
                Arguments.of( allPizzasListWithNulls,   resultWithNulls ),
                Arguments.of( allPizzasListRepeated,    resultRepeated )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("frequencyWithNoObjectToSearchTestCases")
    @DisplayName("frequency: with no object to search test cases")
    public <T> void frequencyWithNoObjectToSearch_testCases(Collection<? extends T> sourceCollection,
                                                            Map<T, Integer> expectedResult) {
        assertEquals(expectedResult, frequency(sourceCollection));
    }


    static Stream<Arguments> frequencyWithObjectToSearchTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpensive = new PizzaDto("Carbonara", 10D);
        PizzaDto margherita = new PizzaDto("Margherita", 7.5d);
        PizzaDto notIncluded = new PizzaDto("Not found", 9d);

        Set<PizzaDto> allPizzasSet = new HashSet<>(asList(carbonaraCheap, carbonaraExpensive, margherita, null));
        List<PizzaDto> allPizzasListWithNulls = asList(carbonaraCheap, carbonaraExpensive, margherita, null, null);
        List<PizzaDto> allPizzasListRepeated = List.of(carbonaraCheap, carbonaraExpensive, margherita, margherita, carbonaraExpensive, margherita);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,         objectToSearch,       expectedResult
                Arguments.of( null,                     null,                 0 ),
                Arguments.of( null,                     carbonaraCheap,       0 ),
                Arguments.of( List.of(),                null,                 0 ),
                Arguments.of( List.of(),                carbonaraCheap,       0 ),
                Arguments.of( allPizzasSet,             null,                 1 ),
                Arguments.of( allPizzasSet,             notIncluded,          0 ),
                Arguments.of( allPizzasSet,             carbonaraCheap,       1 ),
                Arguments.of( allPizzasListWithNulls,   null,                 2 ),
                Arguments.of( allPizzasListWithNulls,   notIncluded,          0 ),
                Arguments.of( allPizzasListWithNulls,   carbonaraCheap,       1 ),
                Arguments.of( allPizzasListRepeated,    carbonaraExpensive,   2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("frequencyWithObjectToSearchTestCases")
    @DisplayName("frequency: with object to search test cases")
    public <T> void frequencyWithObjectToSearch_testCases(Collection<? extends T> sourceCollection,
                                                          T objectToSearch,
                                                          int expectedResult) {
        assertEquals(expectedResult, frequency(sourceCollection, objectToSearch));
    }


    static Stream<Arguments> fromIteratorNoCollectionFactoryTestCases() {
        List<Integer> ints = List.of(1, 2, 3, 6, 5, 1, 21);
        Iterator<String> emptyIterator = Collections.emptyIterator();
        return Stream.of(
                //@formatter:off
                //            sourceIterator,    expectedResult
                Arguments.of( null,              List.of() ),
                Arguments.of( emptyIterator,     List.of() ),
                Arguments.of( ints.iterator(),   ints )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromIteratorNoCollectionFactoryTestCases")
    @DisplayName("fromIterator: without collection factory test cases")
    public <T> void fromIteratorNoCollectionFactory_testCases(Iterator<? extends T> sourceIterator,
                                                              List<T> expectedResult) {
        assertEquals(expectedResult, fromIterator(sourceIterator));
    }


    static Stream<Arguments> fromIteratorAllParametersTestCases() {
        List<String> stringList = List.of("A", "RF", "T", "BC", "YH", "CC", "D");
        Iterator<String> emptyIterator = Collections.emptyIterator();
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Set<String> expectedAllStringResultSet = new LinkedHashSet<>(stringList);
        return Stream.of(
                //@formatter:off
                //            sourceIterator,          collectionFactory,   expectedResult
                Arguments.of( null,                    null,                List.of() ),
                Arguments.of( null,                    setSupplier,         Set.of() ),
                Arguments.of( emptyIterator,           null,                List.of() ),
                Arguments.of( emptyIterator,           setSupplier,         Set.of() ),
                Arguments.of( stringList.iterator(),   null,                stringList ),
                Arguments.of( stringList.iterator(),   setSupplier,         expectedAllStringResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromIteratorAllParametersTestCases")
    @DisplayName("fromIterator: with all parameters test cases")
    public <T> void fromIteratorAllParameters_testCases(Iterator<? extends T> sourceIterator,
                                                        Supplier<Collection<T>> collectionFactory,
                                                        Collection<T> expectedResult) {
        assertEquals(expectedResult, fromIterator(sourceIterator, collectionFactory));
    }


    static Stream<Arguments> groupByWithDiscriminatorKeyTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 3, 5, 6));
        Function<Integer, Integer> mod3 = i -> i % 3;
        Map<Integer, List<Integer>> usingMod3AsDiscriminatorKey = new HashMap<>() {{
            put(0, List.of(3, 6));
            put(1, List.of(1));
            put(2, List.of(5));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   expectedException,                expectedResult
                Arguments.of( null,               null,               null,                             Map.of() ),
                Arguments.of( List.of(),          null,               null,                             Map.of() ),
                Arguments.of( List.of(1),         null,               IllegalArgumentException.class,   null ),
                Arguments.of( null,               mod3,               null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               null,                             Map.of() ),
                Arguments.of( ints,               mod3,               null,                             usingMod3AsDiscriminatorKey )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByWithDiscriminatorKeyTestCases")
    @DisplayName("groupBy: with discriminatorKey test cases")
    public <T, K> void groupByWithDiscriminatorKey_testCases(Collection<? extends T> sourceCollection,
                                                             Function<? super T, ? extends K> discriminatorKey,
                                                             Class<? extends Exception> expectedException,
                                                             Map<K, List<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupBy(sourceCollection, discriminatorKey));
        } else {
            assertEquals(expectedResult, groupBy(sourceCollection, discriminatorKey));
        }
    }


    static Stream<Arguments> groupByAllParametersTestCases() {
        List<Integer> ints = new ArrayList<>(List.of(1, 3, 5, 6, 3));
        Function<Integer, Integer> mod3 = i -> i % 3;

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<Integer>> expectedResultDefaultCollectionFactory = new HashMap<>() {{
            put(0, List.of(3, 6, 3));
            put(1, List.of(1));
            put(2, List.of(5));
        }};
        Map<Integer, Set<Integer>> expectedResultWithSetCollectionFactory = new HashMap<>() {{
            put(0, Set.of(3, 6));
            put(1, Set.of(1));
            put(2, Set.of(5));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,               null,                null,                             Map.of() ),
                Arguments.of( null,               null,               setSupplier,         null,                             Map.of() ),
                Arguments.of( null,               mod3,               null,                null,                             Map.of() ),
                Arguments.of( null,               mod3,               setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,               null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,               setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,               null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,               setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          mod3,               null,                null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               setSupplier,         null,                             Map.of() ),
                Arguments.of( ints,               mod3,               null,                null,                             expectedResultDefaultCollectionFactory ),
                Arguments.of( ints,               mod3,               setSupplier,         null,                             expectedResultWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByAllParametersTestCases")
    @DisplayName("groupBy: with all parameters test cases")
    public <T, K> void groupByAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                      Function<? super T, ? extends K> discriminatorKey,
                                                      Supplier<Collection<T>> collectionFactory,
                                                      Class<? extends Exception> expectedException,
                                                      Map<K, Collection<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupBy(sourceCollection, discriminatorKey, collectionFactory));
        } else {
            assertEquals(expectedResult, groupBy(sourceCollection, discriminatorKey, collectionFactory));
        }
    }


    static Stream<Arguments> groupByMultiKeyWithDiscriminatorKeyTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6, 11, 12));
        Function<Integer, List<String>> oddEvenAndCompareWith10Key = i -> {
            List<String> keys = new ArrayList<>();
            if (0 == i % 2) {
                keys.add("even");
            } else {
                keys.add("odd");
            }
            if (10 > i) {
                keys.add("smaller10");
            } else {
                keys.add("greaterEqual10");
            }
            return keys;
        };

        Map<String, List<Integer>> expectedResult = new HashMap<>() {{
            put("even", List.of(2, 6, 12));
            put("odd", List.of(1, 3, 11));
            put("smaller10", List.of(1, 2, 3, 6));
            put("greaterEqual10", List.of(11, 12));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,             expectedException,                expectedResult
                Arguments.of( null,               null,                         null,                             Map.of() ),
                Arguments.of( List.of(),          null,                         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,                         IllegalArgumentException.class,   null ),
                Arguments.of( null,               oddEvenAndCompareWith10Key,   null,                             Map.of() ),
                Arguments.of( List.of(),          oddEvenAndCompareWith10Key,   null,                             Map.of() ),
                Arguments.of( ints,               oddEvenAndCompareWith10Key,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByMultiKeyWithDiscriminatorKeyTestCases")
    @DisplayName("groupByMultiKey: with discriminatorKey test cases")
    public <T, K> void groupByMultiKeyWithDiscriminatorKey_testCases(Collection<? extends T> sourceCollection,
                                                                     Function<? super T, Collection<? extends K>> discriminatorKey,
                                                                     Class<? extends Exception> expectedException,
                                                                     Map<K, List<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupByMultiKey(sourceCollection, discriminatorKey));
        } else {
            assertEquals(expectedResult, groupByMultiKey(sourceCollection, discriminatorKey));
        }
    }


    static Stream<Arguments> groupByMultiKeyAllParametersTestCases() {
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3, 6, 11, 12, 2));
        Function<Integer, List<String>> oddEvenAndCompareWith10Key = i -> {
            List<String> keys = new ArrayList<>();
            if (0 == i % 2) {
                keys.add("even");
            } else {
                keys.add("odd");
            }
            if (10 > i) {
                keys.add("smaller10");
            } else {
                keys.add("greaterEqual10");
            }
            return keys;
        };

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<String, List<Integer>> expectedResultDefaultCollectionFactory = new HashMap<>() {{
            put("even", List.of(2, 6, 12, 2));
            put("odd", List.of(1, 3, 11));
            put("smaller10", List.of(1, 2, 3, 6, 2));
            put("greaterEqual10", List.of(11, 12));
        }};
        Map<String, Set<Integer>> expectedResultWithSetCollectionFactory = new HashMap<>() {{
            put("even", Set.of(2, 6, 12));
            put("odd", Set.of(1, 3, 11));
            put("smaller10", Set.of(1, 2, 3, 6));
            put("greaterEqual10", Set.of(11, 12));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,                         null,                null,                             Map.of() ),
                Arguments.of( null,               null,                         setSupplier,         null,                             Map.of() ),
                Arguments.of( null,               oddEvenAndCompareWith10Key,   null,                null,                             Map.of() ),
                Arguments.of( null,               oddEvenAndCompareWith10Key,   setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,                         null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,                         setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,                         null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,                         setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          oddEvenAndCompareWith10Key,   null,                null,                             Map.of() ),
                Arguments.of( List.of(),          oddEvenAndCompareWith10Key,   setSupplier,         null,                             Map.of() ),
                Arguments.of( ints,               oddEvenAndCompareWith10Key,   null,                null,                             expectedResultDefaultCollectionFactory ),
                Arguments.of( ints,               oddEvenAndCompareWith10Key,   setSupplier,         null,                             expectedResultWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByMultiKeyAllParametersTestCases")
    @DisplayName("groupByMultiKey: with all parameters test cases")
    public <T, K> void groupByMultiKeyAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                              Function<? super T, Collection<? extends K>> discriminatorKey,
                                                              Supplier<Collection<T>> collectionFactory,
                                                              Class<? extends Exception> expectedException,
                                                              Map<K, Collection<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupByMultiKey(sourceCollection, discriminatorKey, collectionFactory));
        } else {
            assertEquals(expectedResult, groupByMultiKey(sourceCollection, discriminatorKey, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapWithFunctionsTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6));
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
                Arguments.of( null,               null,               null,          null,                             Map.of() ),
                Arguments.of( List.of(),          null,               null,          null,                             Map.of() ),
                Arguments.of( List.of(1),         null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,               square,        IllegalArgumentException.class,   null ),
                Arguments.of( null,               mod3,               square,        null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               square,        null,                             Map.of() ),
                Arguments.of( ints,               mod3,               square,        null,                             usingMod3AsDiscriminatorKey )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithFunctionsTestCases")
    @DisplayName("groupMap: with discriminatorKey and valueMapper test cases")
    public <T, K, V> void groupMapWithFunctions_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> groupMapWithPredicateAndFunctionsTestCases() {
        List<String> strings = List.of("AA", "BFF", "5TR", "H", "B", "TYSSGT");
        Predicate<String> sSmallerThan5 = s -> 5 > s.length();
        Function<String, Integer> sLength = String::length;
        Function<String, String> sVersion2 = s -> s + "2";

        Map<Integer, List<String>> resultNoFilter = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
            put(6, List.of("TYSSGT2"));
        }};
        Map<Integer, List<String>> resultFilter = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   discriminatorKey,   valueMapper,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,               null,          null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,               null,          null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     sLength,            null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,               sVersion2,     IllegalArgumentException.class,   null ),
                Arguments.of( null,               null,              sLength,            sVersion2,     null,                             Map.of() ),
                Arguments.of( null,               sSmallerThan5,     sLength,            sVersion2,     null,                             Map.of() ),
                Arguments.of( strings,            null,              sLength,            sVersion2,     null,                             resultNoFilter ),
                Arguments.of( strings,            sSmallerThan5,     sLength,            sVersion2,     null,                             resultFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPredicateAndFunctionsTestCases")
    @DisplayName("groupMap: with filterPredicate, discriminatorKey and valueMapper test cases")
    public <T, K, V> void groupMapWithPredicateAndFunctions_testCases(Collection<? extends T> sourceCollection,
                                                                      Predicate<? super T> filterPredicate,
                                                                      Function<? super T, ? extends K> discriminatorKey,
                                                                      Function<? super T, ? extends V> valueMapper,
                                                                      Class<? extends Exception> expectedException,
                                                                      Map<K, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, filterPredicate, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, filterPredicate, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapWithPredicateFunctionsAndSupplierTestCases() {
        List<String> strings = List.of("AA", "BFF", "5TR", "H", "B", "TYSSGT");
        Predicate<String> sSmallerThan5 = s -> 5 > s.length();
        Function<String, Integer> sLength = String::length;
        Function<String, String> sVersion2 = s -> s + "2";
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<String>> resultNoFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
            put(6, List.of("TYSSGT2"));
        }};
        Map<Integer, List<String>> resultFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
        }};
        Map<Integer, Set<String>> resultNoFilterWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of("H2", "B2")));
            put(2, new LinkedHashSet<>(List.of("AA2")));
            put(3, new LinkedHashSet<>(List.of("BFF2", "5TR2")));
            put(6, new LinkedHashSet<>(List.of("TYSSGT2")));
        }};
        Map<Integer, Set<String>> resultFilterWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of("H2", "B2")));
            put(2, new LinkedHashSet<>(List.of("AA2")));
            put(3, new LinkedHashSet<>(List.of("BFF2", "5TR2")));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   discriminatorKey,   valueMapper,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,               null,          null,                null,                             Map.of() ),
                Arguments.of( null,               null,              null,               null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,               null,          null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,               null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     sLength,            null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,               sVersion2,     null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,               null,              sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( null,               sSmallerThan5,     sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,              sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( List.of(),          sSmallerThan5,     sLength,            sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( strings,            null,              sLength,            sVersion2,     null,                null,                             resultNoFilterWithDefaultCollectionFactory ),
                Arguments.of( strings,            sSmallerThan5,     sLength,            sVersion2,     null,                null,                             resultFilterWithDefaultCollectionFactory ),
                Arguments.of( strings,            null,              sLength,            sVersion2,     setSupplier,         null,                             resultNoFilterWithSetCollectionFactory ),
                Arguments.of( strings,            sSmallerThan5,     sLength,            sVersion2,     setSupplier,         null,                             resultFilterWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPredicateFunctionsAndSupplierTestCases")
    @DisplayName("groupMap: with filterPredicate, discriminatorKey, valueMapper and collectionFactory test cases")
    public <T, K, V> void groupMapWithPredicateFunctionsAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                              Predicate<? super T> filterPredicate,
                                                                              Function<? super T, ? extends K> discriminatorKey,
                                                                              Function<? super T, ? extends V> valueMapper,
                                                                              Supplier<Collection<V>> collectionFactory,
                                                                              Class<? extends Exception> expectedException,
                                                                              Map<K, Collection<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapWithPartialFunctionTestCases() {
        List<String> strings = List.of("AA", "BFF", "5TR", "H", "B", "TYSSGT");
        PartialFunction<String, Map.Entry<Integer, String>> partialFunction = PartialFunction.of(
                s -> null != s && 5 > s.length(),
                s -> new AbstractMap.SimpleEntry<>(
                        s.length(),
                        s + "2"
                )
        );
        Map<Integer, List<String>> expectedResult = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,               partialFunction,   null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   null,                             Map.of() ),
                Arguments.of( strings,            partialFunction,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPartialFunctionTestCases")
    @DisplayName("groupMap: with partialFunction test cases")
    public <T, K, V> void groupMapWithPartialFunction_testCases(Collection<? extends T> sourceCollection,
                                                                PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                                Class<? extends Exception> expectedException,
                                                                Map<K, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, partialFunction));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, partialFunction));
        }
    }


    static Stream<Arguments> groupMapWithPartialFunctionAndSupplierTestCases() {
        List<String> strings = List.of("AA", "BFF", "5TR", "H", "B", "TYSSGT");
        PartialFunction<String, Map.Entry<Integer, String>> partialFunction = PartialFunction.of(
                s -> null != s && 5 > s.length(),
                s -> new AbstractMap.SimpleEntry<>(
                        s.length(),
                        s + "2"
                )
        );
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<String>> expectedResultDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of("H2", "B2"));
            put(2, List.of("AA2"));
            put(3, List.of("BFF2", "5TR2"));
        }};
        Map<Integer, Set<String>> expectedResultWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of("H2", "B2")));
            put(2, new LinkedHashSet<>(List.of("AA2")));
            put(3, new LinkedHashSet<>(List.of("BFF2", "5TR2")));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                null,                             Map.of() ),
                Arguments.of( null,               null,              setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,              setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( null,               partialFunction,   null,                null,                             Map.of() ),
                Arguments.of( null,               partialFunction,   setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   null,                null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   setSupplier,         null,                             Map.of() ),
                Arguments.of( strings,            partialFunction,   null,                null,                             expectedResultDefaultCollectionFactory ),
                Arguments.of( strings,            partialFunction,   setSupplier,         null,                             expectedResultWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPartialFunctionAndSupplierTestCases")
    @DisplayName("groupMap: with partialFunction and collectionFactory test cases")
    public <T, K, V> void groupMapWithPartialFunctionAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                           PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                                           Supplier<Collection<V>> collectionFactory,
                                                                           Class<? extends Exception> expectedException,
                                                                           Map<K, Collection<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceCollection, partialFunction, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceCollection, partialFunction, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapMultiKeyWithFunctionsTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6, 11, 12));
        Function<Integer, List<String>> oddEvenAndCompareWith10 = i -> {
            List<String> keys = new ArrayList<>();
            if (0 == i % 2) {
               keys.add("evenKey");
            } else {
               keys.add("oddKey");
            }
            if (10 > i) {
               keys.add("smaller10Key");
            } else {
               keys.add("greaterEqual10Key");
            }
            return keys;
         };
        Function<Integer, Integer> square = i -> i * i;
        Map<String, List<Integer>> expectedResult = new HashMap<>() {{
            put("evenKey", List.of(4, 36, 144));
            put("oddKey", List.of(1, 9, 121));
            put("smaller10Key", List.of(1, 4, 9, 36));
            put("greaterEqual10Key", List.of(121, 144));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   discriminatorKey,          valueMapper,   expectedException,                expectedResult
                Arguments.of( null,               null,                      null,          null,                             Map.of() ),
                Arguments.of( List.of(),          null,                      null,          null,                             Map.of() ),
                Arguments.of( List.of(1),         null,                      null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         oddEvenAndCompareWith10,   null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,                      square,        IllegalArgumentException.class,   null ),
                Arguments.of( null,               oddEvenAndCompareWith10,   square,        null,                             Map.of() ),
                Arguments.of( List.of(),          oddEvenAndCompareWith10,   square,        null,                             Map.of() ),
                Arguments.of( ints,               oddEvenAndCompareWith10,   square,        null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapMultiKeyWithFunctionsTestCases")
    @DisplayName("groupMapMultiKey: with discriminatorKey and valueMapper test cases")
    public <T, K, V> void groupMapMultiKeyWithFunctions_testCases(Collection<? extends T> sourceCollection,
                                                                  Function<? super T, Collection<? extends K>> discriminatorKey,
                                                                  Function<? super T, ? extends V> valueMapper,
                                                                  Class<? extends Exception> expectedException,
                                                                  Map<K, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapMultiKey(sourceCollection, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMapMultiKey(sourceCollection, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapMultiKeyWithPredicateAndFunctionsTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6, 11, 12));
        Predicate<Integer> smallerThan10 = i -> 10 > i;
        Function<Integer, List<String>> oddEvenAndCompareWith5 = i -> {
            List<String> keys = new ArrayList<>();
            if (0 == i % 2) {
                keys.add("evenKey");
            } else {
                keys.add("oddKey");
            }
            if (5 > i) {
                keys.add("smaller5Key");
            } else {
                keys.add("greaterEqual5Key");
            }
            return keys;
        };
        Function<Integer, Integer> square = i -> i * i;

        Map<String, List<Integer>> resultNoFilter = new HashMap<>() {{
            put("evenKey", List.of(4, 36, 144));
            put("oddKey", List.of(1, 9, 121));
            put("smaller5Key", List.of(1, 4, 9));
            put("greaterEqual5Key", List.of(36, 121, 144));
        }};
        Map<String, List<Integer>> resultFilter = new HashMap<>() {{
            put("evenKey", List.of(4, 36));
            put("oddKey", List.of(1, 9));
            put("smaller5Key", List.of(1, 4, 9));
            put("greaterEqual5Key", List.of(36));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   discriminatorKey,         valueMapper,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                     null,          null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                     null,          null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,                     null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         smallerThan10,     null,                     null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         smallerThan10,     oddEvenAndCompareWith5,   null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         smallerThan10,     null,                     square,        IllegalArgumentException.class,   null ),
                Arguments.of( null,               null,              oddEvenAndCompareWith5,   square,        null,                             Map.of() ),
                Arguments.of( null,               smallerThan10,     oddEvenAndCompareWith5,   square,        null,                             Map.of() ),
                Arguments.of( ints,               null,              oddEvenAndCompareWith5,   square,        null,                             resultNoFilter ),
                Arguments.of( ints,               smallerThan10,     oddEvenAndCompareWith5,   square,        null,                             resultFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapMultiKeyWithPredicateAndFunctionsTestCases")
    @DisplayName("groupMapMultiKey: with filterPredicate, discriminatorKey and valueMapper test cases")
    public <T, K, V> void groupMapMultiKeyWithPredicateAndFunctions_testCases(Collection<? extends T> sourceCollection,
                                                                              Predicate<? super T> filterPredicate,
                                                                              Function<? super T, Collection<? extends K>> discriminatorKey,
                                                                              Function<? super T, ? extends V> valueMapper,
                                                                              Class<? extends Exception> expectedException,
                                                                              Map<K, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapMultiKey(sourceCollection, filterPredicate, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMapMultiKey(sourceCollection, filterPredicate, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapMultiKeyWithPredicateFunctionsAndSupplierTestCases() {
        List<String> strings = List.of("AA", "BFF", "5TRY", "H", "B", "TYSSGT");
        Predicate<String> sSmallerThan5 = s -> 5 > s.length();
        Function<String, List<String>> startWithVowelAndSmallerThan3 = s -> {
            List<String> keys = new ArrayList<>();
            if ("AEIOUaeiou".contains(s.substring(0, 1))) {
                keys.add("startWithVowelKey");
            } else {
                keys.add("notStartWithVowelKey");
            }
            if (3 > s.length()) {
                keys.add("lengthSmaller3Key");
            } else {
                keys.add("lengthGreaterEqual3Key");
            }
            return keys;
        };
        Function<String, String> sVersion2 = s -> s + "2";
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<String, List<String>> resultNoFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put("startWithVowelKey", List.of("AA2"));
            put("notStartWithVowelKey", List.of("BFF2", "5TRY2", "H2", "B2", "TYSSGT2"));
            put("lengthSmaller3Key", List.of("AA2", "H2", "B2"));
            put("lengthGreaterEqual3Key", List.of("BFF2", "5TRY2", "TYSSGT2"));
        }};
        Map<String, List<String>> resultFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put("startWithVowelKey", List.of("AA2"));
            put("notStartWithVowelKey", List.of("BFF2", "5TRY2", "H2", "B2"));
            put("lengthSmaller3Key", List.of("AA2", "H2", "B2"));
            put("lengthGreaterEqual3Key", List.of("BFF2", "5TRY2"));
        }};
        Map<String, Set<String>> resultNoFilterWithSetCollectionFactory = new HashMap<>() {{
            put("startWithVowelKey", new LinkedHashSet<>(List.of("AA2")));
            put("notStartWithVowelKey", new LinkedHashSet<>(List.of("BFF2", "5TRY2", "H2", "B2", "TYSSGT2")));
            put("lengthSmaller3Key", new LinkedHashSet<>(List.of("AA2", "H2", "B2")));
            put("lengthGreaterEqual3Key", new LinkedHashSet<>(List.of("BFF2", "5TRY2", "TYSSGT2")));
        }};
        Map<String, Set<String>> resultFilterWithSetCollectionFactory = new HashMap<>() {{
            put("startWithVowelKey", new LinkedHashSet<>(List.of("AA2")));
            put("notStartWithVowelKey", new LinkedHashSet<>(List.of("BFF2", "5TRY2", "H2", "B2")));
            put("lengthSmaller3Key", new LinkedHashSet<>(List.of("AA2", "H2", "B2")));
            put("lengthGreaterEqual3Key", new LinkedHashSet<>(List.of("BFF2", "5TRY2")));

        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   discriminatorKey,                valueMapper,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                            null,          null,                null,                             Map.of() ),
                Arguments.of( null,               null,              null,                            null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                            null,          null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                            null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,                            null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,                            null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     startWithVowelAndSmallerThan3,   null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         sSmallerThan5,     null,                            sVersion2,     null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,               null,              startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( null,               sSmallerThan5,     startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( List.of(),          null,              startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( List.of(),          sSmallerThan5,     startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             Map.of() ),
                Arguments.of( strings,            null,              startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             resultNoFilterWithDefaultCollectionFactory ),
                Arguments.of( strings,            sSmallerThan5,     startWithVowelAndSmallerThan3,   sVersion2,     null,                null,                             resultFilterWithDefaultCollectionFactory ),
                Arguments.of( strings,            null,              startWithVowelAndSmallerThan3,   sVersion2,     setSupplier,         null,                             resultNoFilterWithSetCollectionFactory ),
                Arguments.of( strings,            sSmallerThan5,     startWithVowelAndSmallerThan3,   sVersion2,     setSupplier,         null,                             resultFilterWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapMultiKeyWithPredicateFunctionsAndSupplierTestCases")
    @DisplayName("groupMapMultiKey: with filterPredicate, discriminatorKey, valueMapper and collectionFactory test cases")
    public <T, K, V> void groupMapMultiKeyWithPredicateFunctionsAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                                      Predicate<? super T> filterPredicate,
                                                                                      Function<? super T, Collection<? extends K>> discriminatorKey,
                                                                                      Function<? super T, ? extends V> valueMapper,
                                                                                      Supplier<Collection<V>> collectionFactory,
                                                                                      Class<? extends Exception> expectedException,
                                                                                      Map<K, Collection<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapMultiKey(sourceCollection, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMapMultiKey(sourceCollection, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapReduceWithFunctionsAndBinaryOperatorTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(2, 4, 5, 7, 9, 12));
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
                Arguments.of( null,               null,               null,          null,           null,                             Map.of() ),
                Arguments.of( null,               mod3,               square,        sumAll,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,               null,          null,           null,                             Map.of() ),
                Arguments.of( List.of(),          mod3,               square,        sumAll,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         mod3,               square,        null,           IllegalArgumentException.class,   null ),
                Arguments.of( ints,               mod3,               square,        sumAll,         null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceWithFunctionsAndBinaryOperatorTestCases")
    @DisplayName("groupMapReduce: with discriminatorKey, valueMapper and reduceValues test cases")
    public <T, K, V> void groupMapReduceWithFunctionsAndBinaryOperator_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> groupMapReduceWithPartialFunctionAndBinaryOperatorTestCases() {
        Set<Integer> ints = new LinkedHashSet<>(List.of(1, 2, 3, 6, 7, 11, 12));
        PartialFunction<Integer, Map.Entry<Integer, Integer>> partialFunction = PartialFunction.of(
                i -> null != i && 10 > i,
                i -> null == i
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                i % 3,
                                i + 1
                )
        );
        BinaryOperator<Integer> sumAll = Integer::sum;
        Map<Integer, Integer> expectedResult = new HashMap<>() {{
            put(0, 11);
            put(1, 10);
            put(2, 3);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,   reduceValues,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,           null,                             Map.of() ),
                Arguments.of( null,               partialFunction,   sumAll,         null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,           null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   sumAll,         null,                             Map.of() ),
                Arguments.of( List.of(1),         null,              null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         partialFunction,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( List.of(1),         null,              sumAll,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               partialFunction,   sumAll,         null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceWithPartialFunctionAndBinaryOperatorTestCases")
    @DisplayName("groupMapReduce: with partialFunction and reduceValues test cases")
    public <T, K, V> void groupMapReduceWithPartialFunctionAndBinaryOperator_testCases(Collection<? extends T> sourceCollection,
                                                                                       PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                                                       BinaryOperator<V> reduceValues,
                                                                                       Class<? extends Exception> expectedException,
                                                                                       Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapReduce(sourceCollection, partialFunction, reduceValues));
        } else {
            assertEquals(expectedResult, groupMapReduce(sourceCollection, partialFunction, reduceValues));
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


    static Stream<Arguments> mapNoCollectionFactoryTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        List<PizzaDto> allPizzas = List.of(carbonaraCheap, carbonaraExpense);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   mapFunction,   expectedException,                expectedResult
                Arguments.of( null,               null,          null,                             List.of() ),
                Arguments.of( List.of(),          null,          null,                             List.of() ),
                Arguments.of( allPizzas,          null,          IllegalArgumentException.class,   null ),
                Arguments.of( allPizzas,          getName,       null,                             List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( allPizzas,          getCost,       null,                             List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapNoCollectionFactoryTestCases")
    @DisplayName("map: without collection factory test cases")
    public <T, E> void mapNoCollectionFactory_testCases(List<? extends T> sourceCollection,
                                                        Function<? super T, ? extends E> mapFunction,
                                                        Class<? extends Exception> expectedException,
                                                        List<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceCollection, mapFunction));
        } else {
            assertEquals(expectedResult, map(sourceCollection, mapFunction));
        }
    }


    static Stream<Arguments> mapAllParametersTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        List<PizzaDto> allPizzas = List.of(carbonaraCheap, carbonaraExpense);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   mapFunction,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,          null,                null,                             List.of() ),
                Arguments.of( null,               null,          setSupplier,         null,                             new LinkedHashSet<>() ),
                Arguments.of( List.of(),          null,          null,                null,                             List.of() ),
                Arguments.of( List.of(),          null,          setSupplier,         null,                             new LinkedHashSet<>() ),
                Arguments.of( allPizzas,          null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( allPizzas,          null,          setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( allPizzas,          getName,       null,                null,                             List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( allPizzas,          getName,       setSupplier,         null,                             Set.of(carbonaraCheap.getName()) ),
                Arguments.of( allPizzas,          getCost,       null,                null,                             List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) ),
                Arguments.of( allPizzas,          getCost,       setSupplier,         null,                             Set.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapAllParametersTestCases")
    @DisplayName("map: with all parameters test cases")
    public <T, E> void mapAllParameters_testCases(List<? extends T> sourceCollection,
                                                  Function<? super T, ? extends E> mapFunction,
                                                  Supplier<Collection<E>> collectionFactory,
                                                  Class<? extends Exception> expectedException,
                                                  Collection<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceCollection, mapFunction, collectionFactory));
        } else {
            assertEquals(expectedResult, map(sourceCollection, mapFunction, collectionFactory));
        }
    }


    static Stream<Arguments> mapMultiNoCollectionFactoryTestCases() {
        UserDto user1 = new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05", "test1@test.es");
        UserDto user2 = new UserDto(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25", "test2@test.es");
        List<UserDto> allUsers = List.of(user1, user2);

        Function<UserDto, Long> getId = UserDto::getId;
        Function<UserDto, String> getName = UserDto::getName;
        Function<UserDto, String> getAddress = UserDto::getAddress;
        Function<UserDto, Integer> getAge = UserDto::getAge;
        Function<UserDto, String> getBirthday = UserDto::getBirthday;
        Function<UserDto, String> getEmail = UserDto::getEmail;
        List<Function<UserDto, ?>> allPropertyExtractors = List.of(getId, getName, getAddress, getAge, getBirthday, getEmail);
        List<Function<UserDto, ?>> allPropertyExtractorsPlusOne = List.of(getId, getName, getAddress, getAge, getBirthday, getId, getName, getId);

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
        List<Tuple6<Long, String, String, Integer, String, String>> expectedResultSixPropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05", "test1@test.es"),
                Tuple.of(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25", "test2@test.es")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   mapFunctions,                                  expectedException,                expectedResult
                Arguments.of( null,               null,                                          null,                             List.of() ),
                Arguments.of( List.of(),          null,                                          null,                             List.of() ),
                Arguments.of( allUsers,           null,                                          IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           allPropertyExtractorsPlusOne,                  IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           List.of(getId),                                null,                             expectedResultOnePropertyExtractor ),
                Arguments.of( allUsers,           List.of(getId, getName),                       null,                             expectedResultTwoPropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress),           null,                             expectedResultThreePropertyExtractors ),
                Arguments.of( allUsers,           List.of(getId, getName, getAddress, getAge),   null,                             expectedResultFourPropertyExtractors ),
                Arguments.of( allUsers,           allPropertyExtractors,                         null,                             expectedResultSixPropertyExtractors )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapMultiNoCollectionFactoryTestCases")
    @DisplayName("mapMulti: without collection factory test cases")
    @SuppressWarnings("unchecked")
    public <T> void mapMultiNoCollectionFactory_testCases(Collection<? extends T> sourceCollection,
                                                          List<Function<? super T, ?>> mapFunctions,
                                                          Class<? extends Exception> expectedException,
                                                          List<Tuple> expectedResult) {
        Function<? super T, ?>[] finalMapFunctions =
                null == mapFunctions
                        ? null
                        : mapFunctions.toArray(new Function[0]);

        if (null != expectedException) {
            assertThrows(expectedException, () -> mapMulti(sourceCollection, finalMapFunctions));
        } else {
            assertEquals(expectedResult, mapMulti(sourceCollection, finalMapFunctions));
        }
    }


    static Stream<Arguments> mapMultiAllParametersTestCases() {
        UserDto user1 = new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05", "test1@test.es");
        UserDto user2 = new UserDto(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25", "test2@test.es");
        List<UserDto> allUsers = List.of(user1, user2);

        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        Function<UserDto, Long> getId = UserDto::getId;
        Function<UserDto, String> getName = UserDto::getName;
        Function<UserDto, String> getAddress = UserDto::getAddress;
        Function<UserDto, Integer> getAge = UserDto::getAge;
        Function<UserDto, String> getBirthday = UserDto::getBirthday;
        Function<UserDto, String> getEmail = UserDto::getEmail;
        List<Function<UserDto, ?>> allPropertyExtractors = List.of(getId, getName, getAddress, getAge, getBirthday, getEmail);
        List<Function<UserDto, ?>> allPropertyExtractorsPlusOne = List.of(getId, getName, getAddress, getAge, getBirthday, getId, getName, getId);

        List<Tuple1<Long>> expectedResultOnePropertyExtractor = List.of(
                Tuple.of(1L),
                Tuple.of(2L)
        );
        List<Tuple3<Long, String, Integer>> expectedResultThreePropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", 11),
                Tuple.of(2L, "user2 name", 16)
        );
        List<Tuple6<Long, String, String, Integer, String, String>> expectedResultSixPropertyExtractors = List.of(
                Tuple.of(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05", "test1@test.es"),
                Tuple.of(2L, "user2 name", "user2 address", 16, "2006-11-15 14:10:25", "test2@test.es")
        );
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   collectionFactory,   mapFunctions,                      expectedException,                expectedResult
                Arguments.of( null,               null,                null,                              null,                             List.of() ),
                Arguments.of( null,               setSupplier,         null,                              null,                             Set.of() ),
                Arguments.of( List.of(),          null,                null,                              null,                             List.of() ),
                Arguments.of( List.of(),          setSupplier,         null,                              null,                             Set.of() ),
                Arguments.of( allUsers,           null,                null,                              IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           setSupplier,         null,                              IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           null,                allPropertyExtractorsPlusOne,      IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           setSupplier,         allPropertyExtractorsPlusOne,      IllegalArgumentException.class,   null ),
                Arguments.of( allUsers,           null,                List.of(getId),                    null,                             expectedResultOnePropertyExtractor ),
                Arguments.of( allUsers,           setSupplier,         List.of(getId),                    null,                             new LinkedHashSet<>(expectedResultOnePropertyExtractor) ),
                Arguments.of( allUsers,           null,                List.of(getId, getName, getAge),   null,                             expectedResultThreePropertyExtractors ),
                Arguments.of( allUsers,           setSupplier,         List.of(getId, getName, getAge),   null,                             new LinkedHashSet<>(expectedResultThreePropertyExtractors) ),
                Arguments.of( allUsers,           null,                allPropertyExtractors,             null,                             expectedResultSixPropertyExtractors ),
                Arguments.of( allUsers,           setSupplier,         allPropertyExtractors,             null,                             new LinkedHashSet<>(expectedResultSixPropertyExtractors) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapMultiAllParametersTestCases")
    @DisplayName("mapMulti: with all parameters test cases")
    @SuppressWarnings("unchecked")
    public <T> void mapMultiAllParameters_testCases(Collection<? extends T> sourceCollection,
                                                    Supplier<Collection<Tuple>> collectionFactory,
                                                    List<Function<? super T, ?>> mapFunctions,
                                                    Class<? extends Exception> expectedException,
                                                    Collection<Tuple> expectedResult) {
        Function<? super T, ?>[] finalMapFunctions =
                null == mapFunctions
                        ? null
                        : mapFunctions.toArray(new Function[0]);

        if (null != expectedException) {
            assertThrows(expectedException, () -> mapMulti(sourceCollection, collectionFactory, finalMapFunctions));
        } else {
            assertEquals(expectedResult, mapMulti(sourceCollection, collectionFactory, finalMapFunctions));
        }
    }


    static Stream<Arguments> maxNoComparatorTestCases() {
        List<Integer> ints = List.of(1, 2, 3);
        List<Integer> intsWithNulls = asList(6, null, 7, null);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               empty() ),
                Arguments.of( List.of(),          empty() ),
                Arguments.of( ints,               of(3) ),
                Arguments.of( intsWithNulls,      of(7) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxNoComparatorTestCases")
    @DisplayName("max: without comparator test cases")
    public <T extends Comparable<? super T>> void maxNoComparator_testCases(Collection<? extends T> sourceCollection,
                                                                            Optional<T> expectedResult) {
        assertEquals(expectedResult, max(sourceCollection));
    }


    static Stream<Arguments> maxAllParametersTestCases() {
        List<String> strings = List.of("1", "2", "3");
        List<String> stringsWithNulls = asList("6", null, "7", null);

        Comparator<String> naturalComparator = Comparator.nullsFirst(Comparator.naturalOrder());
        Comparator<String> reverseComparator = Comparator.nullsLast(Comparator.reverseOrder());
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   comparator,          expectedException,                expectedResult
                Arguments.of( null,               null,                null,                             empty() ),
                Arguments.of( List.of(),          null,                null,                             empty() ),
                Arguments.of( null,               naturalComparator,   null,                             empty() ),
                Arguments.of( List.of(),          naturalComparator,   null,                             empty() ),
                Arguments.of( strings,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( strings,            naturalComparator,   null,                             of("3") ),
                Arguments.of( strings,            reverseComparator,   null,                             of("1") ),
                Arguments.of( stringsWithNulls,   naturalComparator,   null,                             of("7") ),
                Arguments.of( stringsWithNulls,   reverseComparator,   null,                             empty() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxAllParametersTestCases")
    @DisplayName("max: with all parameters test cases")
    public <T> void maxAllParameters_testCases(Collection<? extends T> sourceCollection,
                                               Comparator<? super T> comparator,
                                               Class<? extends Exception> expectedException,
                                               Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> max(sourceCollection, comparator));
        } else {
            assertEquals(expectedResult, max(sourceCollection, comparator));
        }
    }


    static Stream<Arguments> minNoComparatorTestCases() {
        List<Integer> ints = List.of(1, 2, 3);
        List<Integer> intsWithNulls = asList(6, null, 7, null);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   expectedResult
                Arguments.of( null,               empty() ),
                Arguments.of( List.of(),          empty() ),
                Arguments.of( ints,               of(1) ),
                Arguments.of( intsWithNulls,      of(6) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minNoComparatorTestCases")
    @DisplayName("min: without comparator test cases")
    public <T extends Comparable<? super T>> void minNoComparator_testCases(Collection<? extends T> sourceCollection,
                                                                            Optional<T> expectedResult) {
        assertEquals(expectedResult, min(sourceCollection));
    }


    static Stream<Arguments> minAllParametersTestCases() {
        List<String> strings = List.of("1", "2", "3");
        List<String> stringsWithNulls = asList("6", null, "7", null);

        Comparator<String> naturalComparator = Comparator.nullsFirst(Comparator.naturalOrder());
        Comparator<String> reverseComparator = Comparator.nullsLast(Comparator.reverseOrder());
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   comparator,          expectedException,                expectedResult
                Arguments.of( null,               null,                null,                             empty() ),
                Arguments.of( List.of(),          null,                null,                             empty() ),
                Arguments.of( null,               naturalComparator,   null,                             empty() ),
                Arguments.of( List.of(),          naturalComparator,   null,                             empty() ),
                Arguments.of( strings,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( strings,            naturalComparator,   null,                             of("1") ),
                Arguments.of( strings,            reverseComparator,   null,                             of("3") ),
                Arguments.of( stringsWithNulls,   naturalComparator,   null,                             empty() ),
                Arguments.of( stringsWithNulls,   reverseComparator,   null,                             of("7") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minAllParametersTestCases")
    @DisplayName("min: with all parameters test cases")
    public <T> void minAllParameters_testCases(Collection<? extends T> sourceCollection,
                                               Comparator<? super T> comparator,
                                               Class<? extends Exception> expectedException,
                                               Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> min(sourceCollection, comparator));
        } else {
            assertEquals(expectedResult, min(sourceCollection, comparator));
        }
    }


    static Stream<Arguments> reverseListTestCases() {
        List<Integer> integersList = List.of(3, 7, 9, 11, 15);
        Set<String> stringsLinkedSet = new LinkedHashSet<>(List.of("A", "BT", "YTGH", "IOP"));

        TreeSet<Integer> integersTreeSet = new TreeSet<>(Collections.reverseOrder());
        integersTreeSet.addAll(List.of(45, 71, 9, 11, 35));

        PriorityQueue<Long> longsPriorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
        longsPriorityQueue.addAll(List.of(54L, 78L, 12L));

        List<Integer> reverseIntegersList = List.of(15, 11, 9, 7, 3);
        List<String> reverseStringsLinkedSet = List.of("IOP", "YTGH", "BT", "A");
        List<Integer> reverseIntegersTreeSet = List.of(9, 11, 35, 45, 71);
        List<Long> reverseLongsPriorityQueue = List.of(78L, 54L, 12L);
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
        Set<Integer> integers = new LinkedHashSet<>(List.of(11, 12, 13, 14));
        List<String> strings = List.of("a", "b", "c", "d", "f");

        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 78L, 12L));
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   from,   to,   expectedException,                expectedResult
                Arguments.of( null,               2,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( null,              -1,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          3,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),         -1,      1,    IllegalArgumentException.class,   null ),
                Arguments.of( integers,           0,      0,    IllegalArgumentException.class,   null ),
                Arguments.of( integers,           1,      0,    IllegalArgumentException.class,   null ),
                Arguments.of( null,               0,      1,    null,                             List.of() ),
                Arguments.of( List.of(),          0,      1,    null,                             List.of() ),
                Arguments.of( integers,           0,      1,    null,                             List.of(11) ),
                Arguments.of( integers,           1,      2,    null,                             List.of(12) ),
                Arguments.of( integers,           2,      5,    null,                             List.of(13, 14) ),
                Arguments.of( integers,           6,      8,    null,                             List.of() ),
                Arguments.of( strings,            0,      1,    null,                             List.of("a") ),
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


    static Stream<Arguments> sortOnlyCollectionsParameterTestCases() {
        List<Integer> ints1 = List.of(2, 1, 3);
        List<Integer> ints2 = List.of(4, 5, 2);
        List<Integer> ints3 = List.of(7, 6);
        List<Integer> intsWithNulls = asList(6, null, 7, null);

        List<Integer> expectedResultInts1 = List.of(1, 2, 3);
        List<Integer> expectedResultInts123 = List.of(1, 2, 2, 3, 4, 5, 6, 7);
        List<Integer> expectedResultInts2AndWithNulls = asList(null, null, 2, 4, 5, 6, 7);
        return Stream.of(
                //@formatter:off
                //            collectionToSort1,   collectionToSort2,   collectionToSort3,   expectedResult
                Arguments.of( null,                null,                null,                List.of() ),
                Arguments.of( List.of(),           null,                null,                List.of() ),
                Arguments.of( List.of(),           List.of(),           null,                List.of() ),
                Arguments.of( List.of(),           List.of(),           List.of(),           List.of() ),
                Arguments.of( ints1,               null,                Set.of(),            expectedResultInts1 ),
                Arguments.of( ints1,               ints2,               ints3,               expectedResultInts123 ),
                Arguments.of( ints2,               null,                intsWithNulls,       expectedResultInts2AndWithNulls )

        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("sortOnlyCollectionsParameterTestCases")
    @DisplayName("sort: with collections as parameter test cases")
    public <T extends Comparable<? super T>> void sortOnlyCollectionsParameter_testCases(Collection<? extends T> collectionToSort1,
                                                                                         Collection<? extends T> collectionToSort2,
                                                                                         Collection<? extends T> collectionToSort3,
                                                                                         List<Object> expectedResult) {
        assertEquals(expectedResult, sort(collectionToSort1, collectionToSort2, collectionToSort3));
    }


    static Stream<Arguments> sortCollectionsAndCollectionFactoryParametersTestCases() {
        List<String> strings1 = List.of("1", "2", "3");
        List<String> strings2 = List.of("4", "5", "4");
        List<String> strings3 = List.of("6", "7", "7");
        List<String> stringsWithNulls = asList("6", null, "7", null);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedResultStrings2DefaultSupplier = List.of("4", "4", "5");
        Set<String> expectedResultStrings2SetSupplier = Set.of("4", "5");
        List<String> expectedResultStrings123DefaultSupplier = List.of("1", "2", "3", "4", "4", "5", "6", "7", "7");
        Set<String> expectedResultStrings123SetSupplier = Set.of("1", "2", "3", "4", "5", "6", "7");
        List<String> expectedResultStrings2AndWithNullsDefaultSupplier = asList(null, null, "4", "4", "5", "6", "7");
        Set<String> expectedResultStrings2AndWithNullsSetSupplier = new LinkedHashSet<>(asList(null, "4", "5", "6", "7"));
        return Stream.of(
                //@formatter:off
                //            colToConcat1,   colToConcat2,   colToConcat3,       collectionFactory,   expectedResult
                Arguments.of( null,           null,           null,               null,                List.of() ),
                Arguments.of( null,           null,           null,               setSupplier,         Set.of() ),
                Arguments.of( List.of(),      null,           null,               null,                List.of() ),
                Arguments.of( List.of(),      null,           null,               setSupplier,         Set.of() ),
                Arguments.of( List.of(),      Set.of(),       null,               null,                List.of() ),
                Arguments.of( List.of(),      Set.of(),       null,               setSupplier,         Set.of() ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          null,                List.of() ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          setSupplier,         Set.of() ),
                Arguments.of( strings2,       Set.of(),       List.of(),          null,                expectedResultStrings2DefaultSupplier ),
                Arguments.of( strings2,       Set.of(),       List.of(),          setSupplier,         expectedResultStrings2SetSupplier ),
                Arguments.of( strings1,       strings2,       strings3,           null,                expectedResultStrings123DefaultSupplier ),
                Arguments.of( strings1,       strings2,       strings3,           setSupplier,         expectedResultStrings123SetSupplier ),
                Arguments.of( strings2,       null,           stringsWithNulls,   null,                expectedResultStrings2AndWithNullsDefaultSupplier ),
                Arguments.of( strings2,       null,           stringsWithNulls,   setSupplier,         expectedResultStrings2AndWithNullsSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sortCollectionsAndCollectionFactoryParametersTestCases")
    @DisplayName("sort: with collections and collection factory parameters test cases")
    public <T extends Comparable<? super T>> void sortCollectionsAndCollectionFactory_testCases(Collection<? extends T> collectionToSort1,
                                                                                                Collection<? extends T> collectionToSort2,
                                                                                                Collection<? extends T> collectionToSort3,
                                                                                                Supplier<Collection<T>> collectionFactory,
                                                                                                Collection<Object> expectedResult) {
        assertEquals(expectedResult, sort(collectionFactory, collectionToSort1, collectionToSort2, collectionToSort3));
    }


    static Stream<Arguments> sortCollectionsAndComparatorParametersTestCases() {
        List<Integer> ints1 = List.of(2, 1, 3);
        List<Integer> ints2 = List.of(4, 5, 2);
        List<Integer> ints3 = List.of(7, 6);
        List<Integer> intsWithNulls = asList(6, null, 7, null);

        Comparator<Integer> reverseComparator = Comparator.nullsLast(Comparator.reverseOrder());

        List<Integer> expectedResultInts1ReverseComparator = List.of(3, 2, 1);
        List<Integer> expectedResultInts123ReverseComparator = List.of(7, 6, 5, 4, 3, 2, 2, 1);
        List<Integer> expectedResultInts2AndWithNullsReverseComparator = asList(7, 6, 5, 4, 2, null, null);
        return Stream.of(
                //@formatter:off
                //            colToConcat1,   colToConcat2,   colToConcat3,    comparator,          expectedException,                expectedResult
                Arguments.of( null,           null,           null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,           null,           null,            reverseComparator,   null,                             List.of() ),
                Arguments.of( List.of(),      null,           null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      null,           null,            reverseComparator,   null,                             List.of() ),
                Arguments.of( List.of(),      Set.of(),       null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       null,            reverseComparator,   null,                             List.of() ),
                Arguments.of( List.of(),      Set.of(),       List.of(),       null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       List.of(),       reverseComparator,   null,                             List.of() ),
                Arguments.of( ints1,          Set.of(),       List.of(),       null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints1,          Set.of(),       List.of(),       reverseComparator,   null,                             expectedResultInts1ReverseComparator ),
                Arguments.of( ints1,          ints2,          ints3,           null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints1,          ints2,          ints3,           reverseComparator,   null,                             expectedResultInts123ReverseComparator ),
                Arguments.of( ints2,          null,           intsWithNulls,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( ints2,          null,           intsWithNulls,   reverseComparator,   null,                             expectedResultInts2AndWithNullsReverseComparator )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sortCollectionsAndComparatorParametersTestCases")
    @DisplayName("sort: with collections and collection factory parameters test cases")
    public <T> void sortCollectionsAndComparator_testCases(Collection<? extends T> collectionToSort1,
                                                           Collection<? extends T> collectionToSort2,
                                                           Collection<? extends T> collectionToSort3,
                                                           Comparator<? super T> comparator,
                                                           Class<? extends Exception> expectedException,
                                                           List<Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> sort(comparator, collectionToSort1, collectionToSort2, collectionToSort3)
            );
        } else {
            assertEquals(
                    expectedResult,
                    sort(comparator, collectionToSort1, collectionToSort2, collectionToSort3)
            );
        }
    }


    static Stream<Arguments> sortAllParametersTestCases() {
        List<String> strings1 = List.of("1", "2", "3");
        List<String> strings2 = List.of("4", "5", "4");
        List<String> strings3 = List.of("6", "7", "7");
        List<String> stringsWithNulls = asList("6", null, "7", null);

        Comparator<String> reverseComparator = Comparator.nullsLast(Comparator.reverseOrder());
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        List<String> expectedResultStrings2_DefaultSupplierReverseComparator = List.of("5", "4", "4");
        Set<String> expectedResultStrings2_SetSupplierReverseComparator = Set.of("5", "4");
        List<String> expectedResultStrings123_DefaultSupplierReverseComparator = List.of("7", "7", "6", "5", "4", "4", "3", "2", "1");
        Set<String> expectedResultStrings123_SetSupplierReverseComparator = Set.of("7", "6", "5", "4", "3", "2", "1");
        List<String> expectedResultStringsStrings2AndWithNulls_DefaultSupplierReverseComparator = asList("7", "6", "5", "4", "4", null, null);
        Set<String> expectedResultStringsStrings2AndWithNulls_SetSupplierReverseComparator = new LinkedHashSet<>(asList("7", "6", "5", "4", null));
        return Stream.of(
                //@formatter:off
                //            colToConcat1,   colToConcat2,   colToConcat3,       comparator,          collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,           null,           null,               null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,           null,           null,               reverseComparator,   null,                null,                             List.of() ),
                Arguments.of( null,           null,           null,               null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( null,           null,           null,               reverseComparator,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),      null,           null,               null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      null,           null,               reverseComparator,   null,                null,                             List.of() ),
                Arguments.of( List.of(),      null,           null,               null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      null,           null,               reverseComparator,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),      Set.of(),       null,               null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       null,               reverseComparator,   null,                null,                             List.of() ),
                Arguments.of( List.of(),      Set.of(),       null,               null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       null,               reverseComparator,   setSupplier,         null,                             Set.of() ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          reverseComparator,   null,                null,                             List.of() ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),      Set.of(),       List.of(),          reverseComparator,   setSupplier,         null,                             Set.of() ),
                Arguments.of( strings2,       Set.of(),       List.of(),          null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( strings2,       Set.of(),       List.of(),          reverseComparator,   null,                null,                             expectedResultStrings2_DefaultSupplierReverseComparator ),
                Arguments.of( strings2,       Set.of(),       List.of(),          null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( strings2,       Set.of(),       List.of(),          reverseComparator,   setSupplier,         null,                             expectedResultStrings2_SetSupplierReverseComparator ),
                Arguments.of( strings1,       strings2,       strings3,           null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( strings1,       strings2,       strings3,           reverseComparator,   null,                null,                             expectedResultStrings123_DefaultSupplierReverseComparator ),
                Arguments.of( strings1,       strings2,       strings3,           null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( strings1,       strings2,       strings3,           reverseComparator,   setSupplier,         null,                             expectedResultStrings123_SetSupplierReverseComparator ),
                Arguments.of( strings2,       null,       stringsWithNulls,       null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( strings2,       null,       stringsWithNulls,       reverseComparator,   null,                null,                             expectedResultStringsStrings2AndWithNulls_DefaultSupplierReverseComparator ),
                Arguments.of( strings2,       null,       stringsWithNulls,       null,                setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( strings2,       null,       stringsWithNulls,       reverseComparator,   setSupplier,         null,                             expectedResultStringsStrings2AndWithNulls_SetSupplierReverseComparator )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sortAllParametersTestCases")
    @DisplayName("sort: with all parameters test cases")
    public <T> void sortAllParameters_testCases(Collection<? extends T> collectionToSort1,
                                                Collection<? extends T> collectionToSort2,
                                                Collection<? extends T> collectionToSort3,
                                                Comparator<? super T> comparator,
                                                Supplier<Collection<T>> collectionFactory,
                                                Class<? extends Exception> expectedException,
                                                Collection<Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> sort(comparator, collectionFactory, collectionToSort1, collectionToSort2, collectionToSort3)
            );

        } else {
            assertEquals(
                    expectedResult,
                    sort(comparator, collectionFactory, collectionToSort1, collectionToSort2, collectionToSort3)
            );
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
        List<Integer> intsList = List.of(1, 3, 4, 5, 6);
        Set<Integer> intsSet = new LinkedHashSet<>(intsList);
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   expectedResult
                Arguments.of( null,               null,              List.of() ),
                Arguments.of( null,               isOdd,             List.of() ),
                Arguments.of( List.of(),          null,              List.of() ),
                Arguments.of( List.of(),          isOdd,             List.of() ),
                Arguments.of( intsSet,            null,              intsList ),
                Arguments.of( intsSet,            isOdd,             List.of(1, 3) )
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
        List<Integer> ints = List.of(2, 4, 6, 5, 8);
        Predicate<Integer> isEven = i -> i % 2 == 0;
        Supplier<Collection<Tuple>> setSupplier = LinkedHashSet::new;

        List<Integer> expectedIntsResultList = List.of(2, 4, 6);
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(ints);
        Set<Integer> expectedIsEvenIntsResultSet = new LinkedHashSet<>(expectedIntsResultList);
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   filterPredicate,   collectionFactory,   expectedResult
                Arguments.of( null,               null,              null,                List.of() ),
                Arguments.of( List.of(),          null,              null,                List.of() ),
                Arguments.of( List.of(),          isEven,            null,                List.of() ),
                Arguments.of( List.of(),          isEven,            setSupplier,         Set.of() ),
                Arguments.of( ints,               null,              null,                ints ),
                Arguments.of( ints,               null,              setSupplier,         expectedAllIntsResultSet ),
                Arguments.of( ints,               isEven,            null,                expectedIntsResultList ),
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


    static Stream<Arguments> toCollectionWithSupplierTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6, 6, 2);
        List<Integer> intsWithNullList = asList(1, null, 3, 6, null, 3);

        Supplier<Collection<Integer>> setSupplier = LinkedHashSet::new;
        Supplier<Collection<Integer>> listSupplier = ArrayList::new;

        Set<Integer> emptySet = new LinkedHashSet<>();
        List<Integer> emptyList = new ArrayList<>();
        Set<Integer> expectedIntsResultSet = new LinkedHashSet<>(intsList);
        Set<Integer> expectedIntsWithNullResultSet = new LinkedHashSet<>(intsWithNullList);
        List<Integer> expectedIntsResultList = new ArrayList<>(intsList);
        List<Integer> expectedIntsWithNullResultList = new ArrayList<>(intsWithNullList);
        return Stream.of(
                //@formatter:off
                //            collectionFactory,   elements,           expectedResult
                Arguments.of( null,                null,               emptyList ),
                Arguments.of( null,                List.of(),          emptyList ),
                Arguments.of( null,                intsList,           expectedIntsResultList ),
                Arguments.of( null,                intsWithNullList,   expectedIntsWithNullResultList ),
                Arguments.of( setSupplier,         null,               emptySet ),
                Arguments.of( listSupplier,        null,               emptyList ),
                Arguments.of( setSupplier,         List.of(),          emptySet ),
                Arguments.of( listSupplier,        intsList,           expectedIntsResultList ),
                Arguments.of( setSupplier,         intsList,           expectedIntsResultSet ),
                Arguments.of( listSupplier,        intsWithNullList,   expectedIntsWithNullResultList ),
                Arguments.of( setSupplier,         intsWithNullList,   expectedIntsWithNullResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionWithSupplierTestCases")
    @DisplayName("toCollection: with supplier test cases")
    @SuppressWarnings("unchecked")
    public <T> void toCollectionWithSupplier_testCases(Supplier<Collection<T>> collectionFactory,
                                                       List<T> elements,
                                                       Collection<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toCollection(collectionFactory, finalElements));
    }


    static Stream<Arguments> toCollectionAllParametersTestCases() {
        List<String> stringList = List.of("A", "A", "B", "C", "D", "C");
        List<String> stringWithNullsList = asList("A", "A", null, "B", null);

        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        Supplier<Collection<String>> listSupplier = ArrayList::new;

        Predicate<String> notA = s -> !"A".equals(s);

        Set<String> emptySet = new LinkedHashSet<>();
        List<String> emptyList = new ArrayList<>();
        Set<String> expectedStringsResultSet = new LinkedHashSet<>(stringList);
        Set<String> expecteStringsWithFilterResultSet = new LinkedHashSet<>(List.of("B", "C", "D"));
        Set<String> expectedStringsWithNullResultSet = new LinkedHashSet<>(stringWithNullsList);
        Set<String> expectedStringsWithNullAndFilterResultSet = new LinkedHashSet<>(asList(null, "B"));

        List<String> expectedStringsResultList = new ArrayList<>(stringList);
        List<String> expecteStringsWithFilterResultList = new ArrayList<>(List.of("B", "C", "D", "C"));
        List<String> expectedStringsWithNullResultList = new ArrayList<>(stringWithNullsList);
        List<String> expectedStringsWithNullAndFilterResultList = new ArrayList<>(asList(null, "B", null));
        return Stream.of(
                //@formatter:off
                //            collectionFactory,   filterPredicate,   elements,              expectedResult
                Arguments.of( null,                null,              null,                  emptyList ),
                Arguments.of( null,                null,              List.of(),             emptyList ),
                Arguments.of( null,                notA,              null,                  emptyList ),
                Arguments.of( null,                notA,              List.of(),             emptyList ),
                Arguments.of( null,                null,              stringList,            expectedStringsResultList ),
                Arguments.of( null,                notA,              stringList,            expecteStringsWithFilterResultList ),
                Arguments.of( null,                null,              stringWithNullsList,   expectedStringsWithNullResultList ),
                Arguments.of( null,                notA,              stringWithNullsList,   expectedStringsWithNullAndFilterResultList ),
                Arguments.of( setSupplier,         null,              null,                  emptySet ),
                Arguments.of( setSupplier,         notA,              null,                  emptySet ),
                Arguments.of( setSupplier,         notA,              List.of(),             emptySet ),
                Arguments.of( listSupplier,        null,              null,                  emptyList ),
                Arguments.of( listSupplier,        notA,              null,                  emptyList ),
                Arguments.of( listSupplier,        notA,              List.of(),             emptyList ),
                Arguments.of( listSupplier,        null,              stringList,            expectedStringsResultList ),
                Arguments.of( listSupplier,        notA,              stringList,            expecteStringsWithFilterResultList ),
                Arguments.of( setSupplier,         null,              stringList,            expectedStringsResultSet ),
                Arguments.of( setSupplier,         notA,              stringList,            expecteStringsWithFilterResultSet ),
                Arguments.of( listSupplier,        null,              stringWithNullsList,   expectedStringsWithNullResultList ),
                Arguments.of( listSupplier,        notA,              stringWithNullsList,   expectedStringsWithNullAndFilterResultList ),
                Arguments.of( setSupplier,         null,              stringWithNullsList,   expectedStringsWithNullResultSet ),
                Arguments.of( setSupplier,         notA,              stringWithNullsList,   expectedStringsWithNullAndFilterResultSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionAllParametersTestCases")
    @DisplayName("toCollection: with all parameters test cases")
    @SuppressWarnings("unchecked")
    public <T> void toCollectionAllParameters_testCases(Supplier<Collection<T>> collectionFactory,
                                                        Predicate<? super T> filterPredicate,
                                                        List<T> elements,
                                                        Collection<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toCollection(collectionFactory, filterPredicate, finalElements));
    }


    static Stream<Arguments> toListOnlyElementsTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2);

        List<Integer> emptyList = new ArrayList<>();
        List<Integer> expectedResult = new ArrayList<>(elements);
        return Stream.of(
                //@formatter:off
                //            elements,    expectedResult
                Arguments.of( null,        emptyList ),
                Arguments.of( List.of(),   emptyList ),
                Arguments.of( elements,    expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toListOnlyElementsTestCases")
    @DisplayName("toList: only with array of elements test cases")
    @SuppressWarnings("unchecked")
    public <T> void toListOnlyElements_testCases(List<T> elements,
                                                 List<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toList(finalElements));
    }


    static Stream<Arguments> toListWithFilterTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2, 7);

        Predicate<Integer> isEven = i -> 0 == i % 2;

        List<Integer> emptyList = new ArrayList<>();
        List<Integer> expectedResult = new ArrayList<>(elements);
        List<Integer> expectedResultWithFilter = List.of(2, 6, 6, 2);
        return Stream.of(
                //@formatter:off
                //            filterPredicate,   elements,    expectedResult
                Arguments.of( null,              null,        emptyList ),
                Arguments.of( null,              List.of(),   emptyList ),
                Arguments.of( isEven,            null,        emptyList ),
                Arguments.of( isEven,            List.of(),   emptyList ),
                Arguments.of( null,              elements,    expectedResult ),
                Arguments.of( isEven,            elements,    expectedResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toListWithFilterTestCases")
    @DisplayName("toList: with filter test cases")
    @SuppressWarnings("unchecked")
    public <T> void toListWithFilter_testCases(Predicate<? super T> filterPredicate,
                                               List<T> elements,
                                               List<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toList(filterPredicate, finalElements));
    }


    static Stream<Arguments> toListAllParametersTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2, 7);

        Predicate<Integer> isOdd = i -> 1 == i % 2;

        Supplier<List<Integer>> linkedListSupplier = LinkedList::new;

        List<Integer> emptyArrayList = new ArrayList<>();
        List<Integer> emptyLinkedList = new LinkedList<>();

        List<Integer> expectedResultArrayList = new ArrayList<>(elements);
        List<Integer> expectedResultLinkedList = new LinkedList<>(elements);
        List<Integer> expectedResultWithFilterArrayList = new ArrayList<>(List.of(1, 3, 7));
        List<Integer> expectedResultWithFilterLinkedList = new LinkedList<>(List.of(1, 3, 7));
        return Stream.of(
                //@formatter:off
                //            listFactory,          filterPredicate,   elements,    expectedResult
                Arguments.of( null,                 null,              null,        emptyArrayList ),
                Arguments.of( null,                 null,              List.of(),   emptyArrayList ),
                Arguments.of( null,                 null,              elements,    expectedResultArrayList ),
                Arguments.of( null,                 isOdd,             null,        emptyArrayList ),
                Arguments.of( null,                 isOdd,             List.of(),   emptyArrayList ),
                Arguments.of( null,                 isOdd,             elements,    expectedResultWithFilterArrayList ),
                Arguments.of( linkedListSupplier,   null,              null,        emptyLinkedList ),
                Arguments.of( linkedListSupplier,   null,              List.of(),   emptyLinkedList ),
                Arguments.of( linkedListSupplier,   null,              elements,    expectedResultLinkedList ),
                Arguments.of( linkedListSupplier,   isOdd,             null,        emptyLinkedList ),
                Arguments.of( linkedListSupplier,   isOdd,             List.of(),   emptyLinkedList ),
                Arguments.of( linkedListSupplier,   isOdd,             elements,    expectedResultWithFilterLinkedList )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toListAllParametersTestCases")
    @DisplayName("toList: with all parameters test cases")
    @SuppressWarnings("unchecked")
    public <T> void toListAllParameters_testCases(Supplier<List<T>> listFactory,
                                                  Predicate<? super T> filterPredicate,
                                                  List<T> elements,
                                                  List<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toList(listFactory, filterPredicate, finalElements));
    }


    static Stream<Arguments> toMapWithKeyMapperTestCases() {
        List<Integer> ints = asList(1, null, 2, 3);

        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Map<String, Integer> expectedResult = new HashMap<>() {{
            put("2", 1);
            put("", null);
            put("4", 2);
            put("6", 3);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   keyMapper,         expectedException,                expectedResult
                Arguments.of( null,               null,              null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   null,                             Map.of() ),
                Arguments.of( ints,               null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapWithKeyMapperTestCases")
    @DisplayName("toMap: with keyMapper test cases")
    public <T, K> void toMapWithKeyMapper_testCases(Collection<? extends T> sourceCollection,
                                                    Function<? super T, ? extends K> keyMapper,
                                                    Class<? extends Exception> expectedException,
                                                    Map<K, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toMap(
                            sourceCollection, keyMapper
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toMap(
                            sourceCollection, keyMapper
                    )
            );
        }
    }


    static Stream<Arguments> toMapWithKeyAndValueMapperTestCases() {
        List<Integer> ints = asList(1, null, 2, 3);

        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Function<Integer, Integer> plus10 =
                i -> null == i
                        ? 0
                        : i + 10;

        Map<String, Integer> expectedResult = new HashMap<>() {{
            put("2", 11);
            put("", 0);
            put("4", 12);
            put("6", 13);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   keyMapper,         valueMapper,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,          null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   null,          null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        null,                             Map.of() ),
                Arguments.of( List.of(),          null,              null,          null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   null,          null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   plus10,        null,                             Map.of() ),
                Arguments.of( ints,               null,              null,          IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,          IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   plus10,        null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapWithKeyAndValueMapperTestCases")
    @DisplayName("toMap: with keyMapper and valueMapper test cases")
    public <T, K, V> void toMapWithKeyAndValueMapper_testCases(Collection<? extends T> sourceCollection,
                                                               Function<? super T, ? extends K> keyMapper,
                                                               Function<? super T, ? extends V> valueMapper,
                                                               Class<? extends Exception> expectedException,
                                                               Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toMap(
                            sourceCollection, keyMapper, valueMapper
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toMap(
                            sourceCollection, keyMapper, valueMapper
                    )
            );
        }
    }


    static Stream<Arguments> toMapWithKeyAndValueMapperAndPredicateTestCases() {
        List<Integer> ints = asList(1, null, 2, 3);

        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Function<Integer, Integer> plus10 =
                i -> null == i
                        ? 0
                        : i + 10;

        Predicate<Integer> isOdd = i -> null != i && 1 == i % 2;

        Map<String, Integer> expectedResultNoFilter = new HashMap<>() {{
            put("2", 11);
            put("", 0);
            put("4", 12);
            put("6", 13);
        }};
        Map<String, Integer> expectedResultWithFilter = new HashMap<>() {{
            put("2", 11);
            put("6", 13);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   keyMapper,         valueMapper,   filterPredicate,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,          null,              null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   null,          null,              null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        null,              null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        isOdd,             null,                             Map.of() ),
                Arguments.of( ints,               null,              null,          null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,          null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        null,              IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        isOdd,             IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          multiply2String,   plus10,        null,              null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   plus10,        isOdd,             null,                             Map.of() ),
                Arguments.of( ints,               multiply2String,   plus10,        null,              null,                             expectedResultNoFilter ),
                Arguments.of( ints,               multiply2String,   plus10,        isOdd,             null,                             expectedResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapWithKeyAndValueMapperAndPredicateTestCases")
    @DisplayName("toMap: with keyMapper, valueMapper and filterPredicate test cases")
    public <T, K, V> void toMapWithKeyAndValueMapperAndPredicate_testCases(Collection<? extends T> sourceCollection,
                                                                           Function<? super T, ? extends K> keyMapper,
                                                                           Function<? super T, ? extends V> valueMapper,
                                                                           Predicate<? super T> filterPredicate,
                                                                           Class<? extends Exception> expectedException,
                                                                           Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toMap(
                            sourceCollection, keyMapper, valueMapper, filterPredicate
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toMap(
                            sourceCollection, keyMapper, valueMapper, filterPredicate
                    )
            );
        }
    }


    static Stream<Arguments> toMapWithKeyAndValueMapperPredicateBinaryOperatorAndSupplierTestCases() {
        List<Integer> ints = asList(1, null, 2, 1);

        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Function<Integer, Integer> plus10 =
                i -> null == i
                        ? 0
                        : i + 10;

        Predicate<Integer> isOdd = i -> null != i && 1 == i % 2;
        BinaryOperator<Integer> keepsOldValue = (oldValue, newValue) -> oldValue;
        Supplier<Map<String, Integer>> mapFactory = LinkedHashMap::new;

        Map<String, Integer> expectedResultNoFilterDefaultFactory = new HashMap<>() {{
            put("2", 11);
            put("", 0);
            put("4", 12);
        }};
        Map<String, Integer> expectedResultNoFilterMapFactory = new LinkedHashMap<>() {{
            put("2", 11);
            put("", 0);
            put("4", 12);
        }};
        Map<String, Integer> expectedResultWithFilterDefaultFactory = new HashMap<>() {{
            put("2", 11);
        }};
        Map<String, Integer> expectedResultWithFilterMapFactory = new LinkedHashMap<>() {{
            put("2", 11);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   keyMapper,         valueMapper,   filterPredicate,   mergeValueFunction,   mapFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,          null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   null,          null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        isOdd,             null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        isOdd,             keepsOldValue,        null,         null,                             Map.of() ),
                Arguments.of( null,               multiply2String,   plus10,        isOdd,             keepsOldValue,        mapFactory,   null,                             Map.of() ),
                Arguments.of( ints,               null,              null,          null,              null,                 null,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,          null,              null,                 null,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,          isOdd,             keepsOldValue,        mapFactory,   IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        null,              null,                 null,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        isOdd,             keepsOldValue,        mapFactory,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,              null,          null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   plus10,        null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( List.of(),          multiply2String,   plus10,        isOdd,             keepsOldValue,        mapFactory,   null,                             Map.of() ),
                Arguments.of( ints,               multiply2String,   plus10,        null,              null,                 null,         null,                             expectedResultNoFilterDefaultFactory ),
                Arguments.of( ints,               multiply2String,   plus10,        isOdd,             null,                 null,         null,                             expectedResultWithFilterDefaultFactory ),
                Arguments.of( ints,               multiply2String,   plus10,        isOdd,             keepsOldValue,        null,         null,                             expectedResultWithFilterDefaultFactory ),
                Arguments.of( ints,               multiply2String,   plus10,        null,              null,                 mapFactory,   null,                             expectedResultNoFilterMapFactory ),
                Arguments.of( ints,               multiply2String,   plus10,        isOdd,             null,                 mapFactory,   null,                             expectedResultWithFilterMapFactory ),
                Arguments.of( ints,               multiply2String,   plus10,        isOdd,             keepsOldValue,        mapFactory,   null,                             expectedResultWithFilterMapFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapWithKeyAndValueMapperPredicateBinaryOperatorAndSupplierTestCases")
    @DisplayName("toMap: with keyMapper, valueMapper, filterPredicate, mergeValueFunction and mapFactory test cases")
    public <T, K, V> void toMapWithKeyAndValueMapperPredicateBinaryOperatorAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                                                 Function<? super T, ? extends K> keyMapper,
                                                                                                 Function<? super T, ? extends V> valueMapper,
                                                                                                 Predicate<? super T> filterPredicate,
                                                                                                 BinaryOperator<V> mergeValueFunction,
                                                                                                 Supplier<Map<K, V>> mapFactory,
                                                                                                 Class<? extends Exception> expectedException,
                                                                                                 Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toMap(
                            sourceCollection, keyMapper, valueMapper, filterPredicate, mergeValueFunction, mapFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toMap(
                            sourceCollection, keyMapper, valueMapper, filterPredicate, mergeValueFunction, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> toMapWithPartialFunctionBinaryOperatorAndSupplierTestCases() {
        List<Integer> ints = asList(1, null, 2, 1);
        PartialFunction<Integer, Map.Entry<String, Integer>> partialFunction = PartialFunction.of(
                i -> null != i && 1 == i % 2,
                i -> null == i
                        ? new AbstractMap.SimpleEntry<>(
                                "",
                                0
                          )
                        : new AbstractMap.SimpleEntry<>(
                                String.valueOf(i * 2),
                                i + 10
                          )
        );
        BinaryOperator<Integer> keepsOldValue = (oldValue, newValue) -> oldValue;
        Supplier<Map<String, Integer>> mapFactory = LinkedHashMap::new;

        Map<String, Integer> expectedResultDefaultFactory = new HashMap<>() {{
            put("2", 11);
        }};
        Map<String, Integer> expectedResultMapFactory = new LinkedHashMap<>() {{
            put("2", 11);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   partialFunction,   mergeValueFunction,   mapFactory,   expectedException,                expectedResult
                Arguments.of( null,               null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               partialFunction,   null,                 null,         null,                             Map.of() ),
                Arguments.of( null,               partialFunction,   keepsOldValue,        null,         null,                             Map.of() ),
                Arguments.of( null,               partialFunction,   keepsOldValue,        mapFactory,   null,                             Map.of() ),
                Arguments.of( ints,               null,              null,                 null,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              keepsOldValue,        null,         IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              keepsOldValue,        mapFactory,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,              null,                 null,         null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   null,                 null,         null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   keepsOldValue,        null,         null,                             Map.of() ),
                Arguments.of( List.of(),          partialFunction,   keepsOldValue,        mapFactory,   null,                             Map.of() ),
                Arguments.of( ints,               partialFunction,   null,                 null,         null,                             expectedResultDefaultFactory ),
                Arguments.of( ints,               partialFunction,   keepsOldValue,        null,         null,                             expectedResultDefaultFactory ),
                Arguments.of( ints,               partialFunction,   keepsOldValue,        mapFactory,   null,                             expectedResultMapFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapWithPartialFunctionBinaryOperatorAndSupplierTestCases")
    @DisplayName("toMap: with partialFunction, mergeValueFunction and mapFactory test cases")
    public <T, K, V> void toMapWithPartialFunctionBinaryOperatorAndSupplier_testCases(Collection<? extends T> sourceCollection,
                                                                                      PartialFunction<? super T, ? extends Map.Entry<K, V>> partialFunction,
                                                                                      BinaryOperator<V> mergeValueFunction,
                                                                                      Supplier<Map<K, V>> mapFactory,
                                                                                      Class<? extends Exception> expectedException,
                                                                                      Map<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toMap(
                            sourceCollection, partialFunction, mergeValueFunction, mapFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toMap(
                            sourceCollection, partialFunction, mergeValueFunction, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> toSetOnlyElementsTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2);

        Set<Integer> emptySet = new LinkedHashSet<>();
        Set<Integer> expectedResult = new LinkedHashSet<>(elements);
        return Stream.of(
                //@formatter:off
                //            elements,    expectedResult
                Arguments.of( null,        emptySet ),
                Arguments.of( List.of(),   emptySet ),
                Arguments.of( elements,    expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toSetOnlyElementsTestCases")
    @DisplayName("toSet: with only source elements test cases")
    @SuppressWarnings("unchecked")
    public <T> void toSetOnlyElements_testCases(List<T> elements,
                                                Set<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toSet(finalElements));
    }


    static Stream<Arguments> toSetWithFilterTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2, 7);

        Predicate<Integer> isEven = i -> 0 == i % 2;

        Set<Integer> emptySet = new LinkedHashSet<>();
        Set<Integer> expectedResult = new LinkedHashSet<>(elements);
        Set<Integer> expectedResultWithFilter = new LinkedHashSet<>(List.of(2, 6, 6, 2));
        return Stream.of(
                //@formatter:off
                //            filterPredicate,   elements,    expectedResult
                Arguments.of( null,              null,        emptySet ),
                Arguments.of( null,              List.of(),   emptySet ),
                Arguments.of( isEven,            null,        emptySet ),
                Arguments.of( isEven,            List.of(),   emptySet ),
                Arguments.of( null,              elements,    expectedResult ),
                Arguments.of( isEven,            elements,    expectedResultWithFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toSetWithFilterTestCases")
    @DisplayName("toSet: with filter test cases")
    @SuppressWarnings("unchecked")
    public <T> void toSetWithFilter_testCases(Predicate<? super T> filterPredicate,
                                              List<T> elements,
                                              Set<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toSet(filterPredicate, finalElements));
    }


    static Stream<Arguments> toSetAllParametersTestCases() {
        List<Integer> elements = List.of(1, 2, 3, 6, 6, 2, 7);

        Predicate<Integer> isOdd = i -> 1 == i % 2;

        Supplier<Set<Integer>> treeSetSupplier = TreeSet::new;

        Set<Integer> emptyLinkedSet = new LinkedHashSet<>();
        Set<Integer> emptyTreeSet = new TreeSet<>();

        Set<Integer> expectedResultLinkedSet = new LinkedHashSet<>(elements);
        Set<Integer> expectedResultTreeSet = new TreeSet<>(elements);
        Set<Integer> expectedResultWithFilterLinkedSet = new LinkedHashSet<>(List.of(1, 3, 7));
        Set<Integer> expectedResultWithFilterTreeSet = new TreeSet<>(List.of(1, 3, 7));
        return Stream.of(
                //@formatter:off
                //            setFactory,        filterPredicate,   elements,    expectedResult
                Arguments.of( null,              null,              null,        emptyLinkedSet ),
                Arguments.of( null,              null,              List.of(),   emptyLinkedSet ),
                Arguments.of( null,              null,              elements,    expectedResultLinkedSet ),
                Arguments.of( null,              isOdd,             null,        emptyLinkedSet ),
                Arguments.of( null,              isOdd,             List.of(),   emptyLinkedSet ),
                Arguments.of( null,              isOdd,             elements,    expectedResultWithFilterLinkedSet ),
                Arguments.of( treeSetSupplier,   null,              null,        emptyTreeSet ),
                Arguments.of( treeSetSupplier,   null,              List.of(),   emptyTreeSet ),
                Arguments.of( treeSetSupplier,   null,              elements,    expectedResultTreeSet ),
                Arguments.of( treeSetSupplier,   isOdd,             null,        emptyTreeSet ),
                Arguments.of( treeSetSupplier,   isOdd,             List.of(),   emptyTreeSet ),
                Arguments.of( treeSetSupplier,   isOdd,             elements,    expectedResultWithFilterTreeSet )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toSetAllParametersTestCases")
    @DisplayName("toSet: with all parameters test cases")
    @SuppressWarnings("unchecked")
    public <T> void toSetAllParameters_testCases(Supplier<Set<T>> setFactory,
                                                 Predicate<? super T> filterPredicate,
                                                 List<T> elements,
                                                 Set<T> expectedResult) {
        T[] finalElements =
                null == elements
                        ? null
                        : (T[]) elements.toArray(new Object[0]);

        assertEquals(expectedResult, toSet(setFactory, filterPredicate, finalElements));
    }


    static Stream<Arguments> transposeTestCases() {
        List<List<Integer>> emptyLists = List.of(List.of(), List.of());
        List<List<Integer>> listsWithNulls = asList(asList(1, null), null, asList(4, null));
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

        List<List<Integer>> listsWithNullsResult = List.of(
                List.of(1, 4),
                asList(null, null)
        );
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
                Arguments.of( listsWithNulls,            listsWithNullsResult ),
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
        List<Integer> integers = List.of(11, 31, 55);
        List<Boolean> booleans = List.of(true, false);
        List<String> strings = List.of("h", "o", "p");

        List<Tuple2<Integer, Boolean>> integersBooleansResult = List.of(
                Tuple.of(11, true),
                Tuple.of(31, false)
        );
        List<Tuple2<Integer, String>> integersStringsResult = List.of(
                Tuple.of(11, "h"),
                Tuple.of(31, "o"),
                Tuple.of(55, "p")
        );
        List<Tuple2<Boolean, String>> booleansStringsResult = List.of(
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
        List<Integer> integers = List.of(11, 31, 55);
        List<Boolean> booleans = List.of(true, false);
        List<String> strings = List.of("h", "o", "p");

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
        assertEquals(
                expectedResult,
                zipAll(
                        sourceLeftCollection, sourceRightCollection, defaultLeftElement, defaultRightElement
                )
        );
    }


    static Stream<Arguments> zipWithIndexTestCases() {
        List<Integer> integers = List.of(1, 3, 5);
        Set<String> strings = new LinkedHashSet<>() {{
            add("A");
            add("E");
            add("G");
            add("M");
        }};
        List<Tuple2<Integer, Integer>> integersResult = List.of(
                Tuple.of(0, 1),
                Tuple.of(1, 3),
                Tuple.of(2, 5)
        );
        List<Tuple2<Integer, String>> stringsResult = List.of(
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
