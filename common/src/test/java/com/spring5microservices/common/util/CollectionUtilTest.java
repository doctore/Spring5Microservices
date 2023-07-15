package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.UserDto;
import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple1;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.collection.tuple.Tuple3;
import com.spring5microservices.common.collection.tuple.Tuple4;
import com.spring5microservices.common.collection.tuple.Tuple5;
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

        PartialFunction<Integer, String> plus1StringIfEven = new PartialFunction<>() {

            @Override
            public String apply(final Integer i) {
                return null == i
                        ? null
                        : String.valueOf(i + 1);
            }

            @Override
            public boolean isDefinedAt(final Integer i) {
                return null != i &&
                        0 == i % 2;
            }
        };
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

        PartialFunction<Integer, String> plus1StringIfEven = new PartialFunction<>() {

            @Override
            public String apply(final Integer i) {
                return null == i
                        ? null
                        : String.valueOf(i + 1);
            }

            @Override
            public boolean isDefinedAt(final Integer i) {
                return null != i &&
                        0 == i % 2;
            }
        };
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


    static Stream<Arguments> asSetNoSetFactoryTestCases() {
        List<Integer> intsList = List.of(1, 2, 3, 6, 6, 2);
        List<Integer> intsWithNullList = asList(1, null, 3, 6, null, 3);

        Set<Integer> emptySet = new LinkedHashSet<>();
        Set<Integer> expectedAllIntsResultSet = new LinkedHashSet<>(List.of(1, 2, 3, 6));
        Set<Integer> expectedAllIntsWithNullResultSet = new LinkedHashSet<>(asList(1, null, 3, 6));
        return Stream.of(
                //@formatter:off
                //            elements,           expectedResult
                Arguments.of( null,               emptySet ),
                Arguments.of( List.of(),          emptySet ),
                Arguments.of( intsList,           expectedAllIntsResultSet ),
                Arguments.of( intsWithNullList,   expectedAllIntsWithNullResultSet )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("asSetNoSetFactoryTestCases")
    @DisplayName("asSet: without set factory test cases")
    public void asSetNoSetFactory_testCases(List<Object> elements,
                                            Set<Object> expectedResult) {
        Object[] finalElements =
                null == elements
                        ? null
                        : elements.toArray(new Object[0]);

        assertEquals(expectedResult, asSet(finalElements));
    }


    static Stream<Arguments> asSetAllParametersTestCases() {
        List<String> stringList = List.of("A", "A", "B", "C", "D", "C");
        List<String> stringWithNullsList = asList("A", "A", null, "B", null);
        Supplier<Set<String>> setSupplier = HashSet::new;

        Set<String> emptySet = new HashSet<>();
        Set<String> expectedAllStringResultSet = new LinkedHashSet<>(List.of("A", "B", "C", "D"));
        Set<String> expectedAllStringWithNullResultSet = new LinkedHashSet<>(asList("A", null, "B"));
        return Stream.of(
                //@formatter:off
                //            setFactory,    elements,              expectedResult
                Arguments.of( null,          null,                  emptySet ),
                Arguments.of( setSupplier,   null,                  emptySet ),
                Arguments.of( null,         List.of(),              emptySet ),
                Arguments.of( setSupplier,   stringList,            expectedAllStringResultSet ),
                Arguments.of( setSupplier,   stringWithNullsList,   expectedAllStringWithNullResultSet )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("asSetAllParametersTestCases")
    @DisplayName("asSet: with all parameters test cases")
    public void asSetAllParameters_testCases(Supplier<Set<Object>> setFactory,
                                             List<Object> elements,
                                             Set<Object> expectedResult) {
        Object[] finalElements =
                null == elements
                        ? null
                        : elements.toArray(new Object[0]);

        assertEquals(expectedResult, asSet(setFactory, finalElements));
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
        PartialFunction<Integer, String> toStringIfLowerThan20 = new PartialFunction<>() {

            @Override
            public String apply(final Integer i) {
                return null == i
                        ? null
                        : i.toString();
            }

            @Override
            public boolean isDefinedAt(final Integer i) {
                return null != i &&
                        0 > i.compareTo(20);
            }
        };
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
        PartialFunction<String, Integer> lengthIfSizeGreaterThan1 = new PartialFunction<>() {

            @Override
            public Integer apply(final String s) {
                return null == s
                        ? null
                        : s.length();
            }

            @Override
            public boolean isDefinedAt(final String s) {
                return null != s &&
                        1 < s.length();
            }
        };
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
        PartialFunction<String, Integer> lengthIfSizeGreaterThan1 = new PartialFunction<>() {

            @Override
            public Integer apply(final String s) {
                return null == s
                        ? null
                        : s.length();
            }

            @Override
            public boolean isDefinedAt(final String s) {
                return null != s &&
                        1 < s.length();
            }
        };
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


    static Stream<Arguments> collectPropertyNoCollectionFactoryTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        List<PizzaDto> allPizzas = List.of(carbonaraCheap, carbonaraExpense);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   propertyExtractor,   expectedResult
                Arguments.of( null,               null,                List.of() ),
                Arguments.of( allPizzas,          null,                List.of() ),
                Arguments.of( allPizzas,          getName,             List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( allPizzas,          getCost,             List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectPropertyNoCollectionFactoryTestCases")
    @DisplayName("collectProperty: without collection factory test cases")
    public void collectPropertyNoCollectionFactory_testCases(List<PizzaDto> sourceCollection,
                                                             Function<PizzaDto, String> keyExtractor,
                                                             List<String> expectedResult) {
        assertEquals(expectedResult, collectProperty(sourceCollection, keyExtractor));
    }


    static Stream<Arguments> collectPropertyAllParametersTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto("Carbonara", 5D);
        PizzaDto carbonaraExpense = new PizzaDto("Carbonara", 10D);
        List<PizzaDto> allPizzas = List.of(carbonaraCheap, carbonaraExpense);

        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;
        return Stream.of(
                //@formatter:off
                //            sourceCollection,   propertyExtractor,   collectionFactory,   expectedResult
                Arguments.of( null,               null,                null,                List.of() ),
                Arguments.of( null,               null,                setSupplier,         new LinkedHashSet<>() ),
                Arguments.of( allPizzas,          getName,             null,                List.of(carbonaraCheap.getName(), carbonaraExpense.getName()) ),
                Arguments.of( allPizzas,          getName,             setSupplier,         Set.of(carbonaraCheap.getName()) ),
                Arguments.of( allPizzas,          getCost,             null,                List.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) ),
                Arguments.of( allPizzas,          getCost,             setSupplier,         Set.of(carbonaraCheap.getCost(), carbonaraExpense.getCost()) )
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
    public <T> void collectPropertiesNoCollectionFactory_testCases(Collection<? extends T> sourceCollection,
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
    public <T> void collectPropertiesAllParameters_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> foldLeftTestCases() {
        List<Integer> integers = List.of(1, 3, 5);
        List<String> strings = List.of("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 75L, 12L));

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
        List<Integer> integers = List.of(1, 3, 5);
        List<String> strings = List.of("AB", "E", "GMT");
        PriorityQueue<Long> longs = new PriorityQueue<>(Comparator.naturalOrder());
        longs.addAll(List.of(54L, 75L, 12L));

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
                Arguments.of( null,               null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,               null,          IllegalArgumentException.class,   null ),
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
                Arguments.of( null,               null,              null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,              null,               null,          IllegalArgumentException.class,   null ),
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


    static Stream<Arguments> groupMapAllParametersTestCases() {
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
                Arguments.of( null,               null,              null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          null,              null,               null,          null,                IllegalArgumentException.class,   null ),
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
    @MethodSource("groupMapAllParametersTestCases")
    @DisplayName("groupMap: with all parameters test cases")
    public <T, K, V> void groupMapAllParameters_testCases(Collection<? extends T> sourceCollection,
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


    static Stream<Arguments> groupMapReduceTestCases() {
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
                Arguments.of( ints,               null,              null,          IllegalArgumentException.class,   null ),
                Arguments.of( ints,               multiply2String,   null,          IllegalArgumentException.class,   null ),
                Arguments.of( ints,               null,              plus10,        IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),          multiply2String,   plus10,        null,                             Map.of() ),
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

        PartialFunction<Integer, Map.Entry<String, Integer>> partialFunction = new PartialFunction<>() {

            @Override
            public Map.Entry<String, Integer> apply(final Integer integer) {
                return null == integer
                        ? new AbstractMap.SimpleEntry<>(
                                "",
                                0
                          )
                        : new AbstractMap.SimpleEntry<>(
                                String.valueOf(integer * 2),
                                integer + 10
                          );
            }

            @Override
            public boolean isDefinedAt(final Integer integer) {
                return null != integer &&
                        1 == integer % 2;
            }
        };

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
