package com.spring5microservices.common.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {

    static Stream<Arguments> validTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedException,            expectedResult
                Arguments.of( null,    NullPointerException.class,   null ),
                Arguments.of( 1,       null,                         Valid.of(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("validTestCases")
    @DisplayName("valid: test cases")
    public <T> void valid_testCases(T value, Class<? extends Exception> expectedException, Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Validation.valid(value));
        }
        else {
            assertEquals(expectedResult, Validation.valid(value));
        }
    }


    static Stream<Arguments> invalidTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,               expectedException,            expectedResult
                Arguments.of( null,                NullPointerException.class,   null ),
                Arguments.of( asList(),            null,                         Invalid.of(asList()) ),
                Arguments.of( asList("problem"),   null,                         Invalid.of(asList("problem")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("invalidTestCases")
    @DisplayName("invalid: test cases")
    public <T> void invalid_testCases(List<String> value, Class<? extends Exception> expectedException, Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Validation.invalid(value));
        }
        else {
            assertEquals(expectedResult, Validation.invalid(value));
        }
    }


    static Stream<Arguments> mapTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Function<Integer, String> fromIntegerToString = i -> i.toString();
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,                expectedException,            expectedResult
                Arguments.of( valid,        null,                  NullPointerException.class,   null ),
                Arguments.of( valid,        fromIntegerToString,   null,                         Validation.valid("1") ),
                Arguments.of( invalid,      null,                  null,                         invalid ),
                Arguments.of( invalid,      fromIntegerToString,   null,                         invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public <T, U> void map_testCases(Validation<T> validation,
                                     Function<? super T, ? extends U> mapper,
                                     Class<? extends Exception> expectedException,
                                     Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.map(mapper));
        }
        else {
            assertEquals(expectedResult, validation.map(mapper));
        }
    }


    static Stream<Arguments> mapErrorTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").collect(toList());
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,       expectedException,            expectedResult
                Arguments.of( invalid,      null,         NullPointerException.class,   null ),
                Arguments.of( invalid,      addALetter,   null,                         Validation.invalid(asList("problem2")) ),
                Arguments.of( valid,        null,         null,                         valid ),
                Arguments.of( valid,        addALetter,   null,                         valid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapErrorTestCases")
    @DisplayName("mapError: test cases")
    public <T, U> void mapError_testCases(Validation<T> validation,
                                          Function<List<String>, List<String>> mapper,
                                          Class<? extends Exception> expectedException,
                                          Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.mapError(mapper));
        }
        else {
            assertEquals(expectedResult, validation.mapError(mapper));
        }
    }


    static Stream<Arguments> bimapTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Function<Integer, String> fromIntegerToString = i -> i.toString();
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").collect(toList());
        return Stream.of(
                //@formatter:off
                //            validation,   mapperValid,           mapperInvalid,   expectedException,            expectedResult
                Arguments.of( valid,        null,                  addALetter,      NullPointerException.class,   null ),
                Arguments.of( valid,        fromIntegerToString,   null,            null,                         Validation.valid("1") ),
                Arguments.of( valid,        fromIntegerToString,   addALetter,      null,                         Validation.valid("1") ),
                Arguments.of( invalid,      fromIntegerToString,   null,            NullPointerException.class,   null ),
                Arguments.of( invalid,      null,                  addALetter,      null,                         Validation.invalid(asList("problem2")) ),
                Arguments.of( invalid,      fromIntegerToString,   addALetter,      null,                         Validation.invalid(asList("problem2")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("bimapTestCases")
    @DisplayName("bimap: test cases")
    public <T, U> void bimap_testCases(Validation<T> validation,
                                       Function<? super T, ? extends U> mapperValid,
                                       Function<List<String>, List<String>> mapperInvalid,
                                       Class<? extends Exception> expectedException,
                                       Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.bimap(mapperValid, mapperInvalid));
        }
        else {
            assertEquals(expectedResult, validation.bimap(mapperValid, mapperInvalid));
        }
    }


    static Stream<Arguments> flatmapTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Function<Integer, Validation<String>> fromIntegerToValidWithString = i -> Validation.valid(i.toString());
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,                         expectedException,            expectedResult
                Arguments.of( valid,        null,                           NullPointerException.class,   null ),
                Arguments.of( valid,        fromIntegerToValidWithString,   null,                         Validation.valid("1") ),
                Arguments.of( invalid,      null,                           null,                         invalid ),
                Arguments.of( invalid,      fromIntegerToValidWithString,   null,                         invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flatmapTestCases")
    @DisplayName("flatmap: test cases")
    public <T, U> void flatmap_testCases(Validation<T> validation,
                                         Function<? super T, ? extends Validation<? extends U>> mapper,
                                         Class<? extends Exception> expectedException,
                                         Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.flatMap(mapper));
        }
        else {
            assertEquals(expectedResult, validation.flatMap(mapper));
        }
    }


    static Stream<Arguments> peekTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Consumer<Integer> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   action,   expectedException,            expectedResult
                Arguments.of( valid,        null,     NullPointerException.class,   null ),
                Arguments.of( valid,        action,   null,                         valid ),
                Arguments.of( invalid,      null,     null,                         invalid ),
                Arguments.of( invalid,      action,   null,                         invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekTestCases")
    @DisplayName("peek: test cases")
    public <T, U> void peek_testCases(Validation<T> validation,
                                      Consumer<? super T> action,
                                      Class<? extends Exception> expectedException,
                                      Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.peek(action));
        }
        else {
            assertEquals(expectedResult, validation.peek(action));
        }
    }


    static Stream<Arguments> peekErrorTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Consumer<List<String>> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   action,   expectedException,            expectedResult
                Arguments.of( invalid,      null,     NullPointerException.class,   null ),
                Arguments.of( invalid,      action,   null,                         invalid ),
                Arguments.of( valid,        null,     null,                         valid ),
                Arguments.of( valid,        action,   null,                         valid )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekErrorTestCases")
    @DisplayName("peekError: test cases")
    public <T> void peekError_testCases(Validation<T> validation,
                                        Consumer<List<String>> action,
                                        Class<? extends Exception> expectedException,
                                        Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.peekError(action));
        }
        else {
            assertEquals(expectedResult, validation.peekError(action));
        }
    }


    static Stream<Arguments> bipeekTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Consumer<Integer> actionValid = System.out::println;
        Consumer<List<String>> actionInvalid = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   actionValid,   actionInvalid,   expectedException,            expectedResult
                Arguments.of( valid,        null,          actionInvalid,   NullPointerException.class,   null ),
                Arguments.of( valid,        actionValid,   null,            null,                         valid ),
                Arguments.of( valid,        actionValid,   actionInvalid,   null,                         valid ),
                Arguments.of( invalid,      actionValid,   null,            NullPointerException.class,   null ),
                Arguments.of( invalid,      null,          actionInvalid,   null,                         invalid ),
                Arguments.of( invalid,      actionValid,   actionInvalid,   null,                         invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("bipeekTestCases")
    @DisplayName("bipeek: test cases")
    public <T> void bimap_testCases(Validation<T> validation,
                                       Consumer<? super T> actionValid,
                                       Consumer<List<String>> actionInvalid,
                                       Class<? extends Exception> expectedException,
                                       Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.bipeek(actionValid, actionInvalid));
        }
        else {
            assertEquals(expectedResult, validation.bipeek(actionValid, actionInvalid));
        }
    }


    static Stream<Arguments> filterTestCases() {
        Validation<Integer> validVerifyFilter = Validation.valid(1);
        Validation<Integer> validDoesNotVerifyFilter = Validation.valid(2);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            validation,                 predicate,   expectedException,            expectedResult
                Arguments.of( validVerifyFilter,          null,        NullPointerException.class,   null ),
                Arguments.of( validVerifyFilter,          isOdd,       null,                         of(validVerifyFilter) ),
                Arguments.of( validDoesNotVerifyFilter,   isOdd,       null,                         empty() ),
                Arguments.of( invalid,                    null,        null,                         of(invalid) ),
                Arguments.of( invalid,                    isOdd,       null,                         of(invalid) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <T> void filter_testCases(Validation<T> validation,
                                     Predicate<? super T> predicate,
                                     Class<? extends Exception> expectedException,
                                     Optional<Validation<T>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.filter(predicate));
        }
        else {
            assertEquals(expectedResult, validation.filter(predicate));
        }
    }


    static Stream<Arguments> apTestCases() {
        Validation<Integer> validInt1 = Validation.valid(1);
        Validation<Integer> validInt4 = Validation.valid(4);
        Validation<Integer> invalidProb1 = Validation.invalid(asList("problem1"));
        Validation<Integer> invalidProb2 = Validation.invalid(asList("problem2"));

        List<String> allErrors = new ArrayList<>(invalidProb1.getErrors());
        allErrors.addAll(invalidProb2.getErrors());
        Validation<Integer> invalidAll = Validation.invalid(allErrors);
        return Stream.of(
                //@formatter:off
                //            validation,     validationParam,   expectedResult
                Arguments.of( validInt1,      null,              validInt1 ),
                Arguments.of( invalidProb1,   null,              invalidProb1 ),
                Arguments.of( validInt1,      validInt4,         validInt4 ),
                Arguments.of( validInt1,      invalidProb1,      invalidProb1 ),
                Arguments.of( invalidProb1,   validInt1,         invalidProb1 ),
                Arguments.of( invalidProb1,   invalidProb2,      invalidAll )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("apTestCases")
    @DisplayName("ap: test cases")
    public <T> void ap_testCases(Validation<T> validation,
                                 Validation<? extends T> validationParam,
                                 Validation<T> expectedResult) {
        assertEquals(expectedResult, validation.ap(validationParam));
    }


    static Stream<Arguments> toOptionalTestCases() {
        Validation<Integer> validEmpty = Valid.empty();
        Validation<Integer> validNotEmpty = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,      expectedResult
                Arguments.of( validEmpty,      empty() ),
                Arguments.of( validNotEmpty,   of(validNotEmpty.get()) ),
                Arguments.of( invalid,         empty() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toOptionalTestCases")
    @DisplayName("toOptional: test cases")
    public <T> void toOptional_testCases(Validation<T> validation,
                                         Optional<Validation<T>> expectedResult) {
        assertEquals(expectedResult, validation.toOptional());
    }


    static Stream<Arguments> orElseWithValidationTestCases() {
        Validation<Integer> validInt1 = Validation.valid(1);
        Validation<Integer> validInt4 = Validation.valid(4);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,   other,       expectedResult
                Arguments.of( validInt1,    null,        validInt1 ),
                Arguments.of( validInt1,    validInt4,   validInt1 ),
                Arguments.of( invalid,      null,        null ),
                Arguments.of( invalid,      validInt1,   validInt1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithValidationTestCases")
    @DisplayName("orElse: with validation as parameter test cases")
    public <T> void orElseWithValidation_testCases(Validation<T> validation,
                                                   Validation<? extends T> other,
                                                   Validation<T> expectedResult) {
        assertEquals(expectedResult, validation.orElse(other));
    }


    static Stream<Arguments> orElseWithSupplierTestCases() {
        Validation<Integer> valid = Validation.valid(1);
        Supplier<Validation<Integer>> supplierValid = () -> Validation.valid(4);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,   supplier,        expectedException,            expectedResult
                Arguments.of( valid,        null,            null,                         valid ),
                Arguments.of( valid,        supplierValid,   null,                         valid ),
                Arguments.of( invalid,      null,            NullPointerException.class,   null ),
                Arguments.of( invalid,      supplierValid,   null,                         Validation.valid(4) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithSupplierTestCases")
    @DisplayName("orElse: with supplier as parameter test cases")
    public <T> void orElseWithSupplier_testCases(Validation<T> validation,
                                                 Supplier<Validation<? extends T>> supplier,
                                                 Class<? extends Exception> expectedException,
                                                 Validation<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.orElse(supplier));
        }
        else {
            assertEquals(expectedResult, validation.orElse(supplier));
        }
    }


    static Stream<Arguments> getOrElseThrowTestCases() {
        Validation<Integer> validEmpty = Valid.empty();
        Validation<Integer> validNotEmpty = Validation.valid(1);
        Supplier<Exception> exceptionSupplier = () -> new IllegalArgumentException("Something was wrong");
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,      exceptionSupplier,   expectedException,                expectedResult
                Arguments.of( validEmpty,      null,                null,                             null ),
                Arguments.of( validEmpty,      exceptionSupplier,   null,                             null ),
                Arguments.of( validNotEmpty,   null,                null,                             validNotEmpty.get() ),
                Arguments.of( validNotEmpty,   exceptionSupplier,   null,                             validNotEmpty.get() ),
                Arguments.of( invalid,         null,                NullPointerException.class,       null ),
                Arguments.of( invalid,         exceptionSupplier,   IllegalArgumentException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseThrowTestCases")
    @DisplayName("getOrElseThrow: test cases")
    public <T, X extends Throwable> void getOrElseThrow_testCases(Validation<T> validation,
                                                                  Supplier<X> exceptionSupplier,
                                                                  Class<? extends Exception> expectedException,
                                                                  T expectedResult) throws Throwable {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.getOrElseThrow(exceptionSupplier));
        }
        else {
            assertEquals(expectedResult, validation.getOrElseThrow(exceptionSupplier));
        }
    }


    static Stream<Arguments> isEmptyTestCases() {
        Validation<Integer> validEmpty = Valid.empty();
        Validation<Integer> validNotEmpty = Validation.valid(1);
        Validation<Integer> invalid = Validation.invalid(asList("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,      expectedResult
                Arguments.of( validEmpty,      true ),
                Arguments.of( validNotEmpty,   false ),
                Arguments.of( invalid,         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public <T> void isEmpty_testCases(Validation<T> validation, boolean expectedResult) {
        assertEquals(expectedResult, validation.isEmpty());
    }

}
