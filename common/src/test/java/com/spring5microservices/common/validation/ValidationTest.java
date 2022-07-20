package com.spring5microservices.common.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
    public <E, T> void valid_testCases(T value,
                                       Class<? extends Exception> expectedException,
                                       Validation<E, T> expectedResult) {
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
                //            value,                expectedException,            expectedResult
                Arguments.of( null,                 NullPointerException.class,   null ),
                Arguments.of( List.of(),            null,                         Invalid.of(List.of()) ),
                Arguments.of( List.of("problem"),   null,                         Invalid.of(List.of("problem")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("invalidTestCases")
    @DisplayName("invalid: test cases")
    public <E, T> void invalid_testCases(List<String> value,
                                         Class<? extends Exception> expectedException,
                                         Validation<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Validation.invalid(value));
        }
        else {
            assertEquals(expectedResult, Validation.invalid(value));
        }
    }


    static Stream<Arguments> mapTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, String> fromIntegerToString = Object::toString;
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
    public <E, T, U> void map_testCases(Validation<E, T> validation,
                                        Function<? super T, ? extends U> mapper,
                                        Class<? extends Exception> expectedException,
                                        Validation<U, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.map(mapper));
        }
        else {
            assertEquals(expectedResult, validation.map(mapper));
        }
    }


    static Stream<Arguments> mapErrorTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").collect(toList());
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,       expectedException,            expectedResult
                Arguments.of( invalid,      null,         NullPointerException.class,   null ),
                Arguments.of( invalid,      addALetter,   null,                         Validation.invalid(List.of("problem2")) ),
                Arguments.of( valid,        null,         null,                         valid ),
                Arguments.of( valid,        addALetter,   null,                         valid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapErrorTestCases")
    @DisplayName("mapError: test cases")
    public <E, T, U> void mapError_testCases(Validation<E, T> validation,
                                             Function<Collection<? super E>, Collection<U>> mapper,
                                             Class<? extends Exception> expectedException,
                                             Validation<U, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.mapError(mapper));
        }
        else {
            assertEquals(expectedResult, validation.mapError(mapper));
        }
    }


    static Stream<Arguments> bimapTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, String> fromIntegerToString = Object::toString;
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").collect(toList());
        return Stream.of(
                //@formatter:off
                //            validation,   mapperValid,           mapperInvalid,   expectedException,            expectedResult
                Arguments.of( valid,        null,                  addALetter,      NullPointerException.class,   null ),
                Arguments.of( valid,        fromIntegerToString,   null,            null,                         Validation.valid("1") ),
                Arguments.of( valid,        fromIntegerToString,   addALetter,      null,                         Validation.valid("1") ),
                Arguments.of( invalid,      fromIntegerToString,   null,            NullPointerException.class,   null ),
                Arguments.of( invalid,      null,                  addALetter,      null,                         Validation.invalid(List.of("problem2")) ),
                Arguments.of( invalid,      fromIntegerToString,   addALetter,      null,                         Validation.invalid(List.of("problem2")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("bimapTestCases")
    @DisplayName("bimap: test cases")
    public <E, R, T, U> void bimap_testCases(Validation<E, T> validation,
                                             Function<? super T, ? extends U> mapperValid,
                                             Function<Collection<? super E>, Collection<R>> mapperInvalid,
                                             Class<? extends Exception> expectedException,
                                             Validation<R, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.bimap(mapperValid, mapperInvalid));
        }
        else {
            assertEquals(expectedResult, validation.bimap(mapperValid, mapperInvalid));
        }
    }


    static Stream<Arguments> flatmapTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, Validation<String, String>> fromIntegerToValidWithString = i -> Validation.valid(i.toString());
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
    public <E, T, U> void flatmap_testCases(Validation<E, T> validation,
                                            Function<? super T, ? extends Validation<E, ? extends U>> mapper,
                                            Class<? extends Exception> expectedException,
                                            Validation<E, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.flatMap(mapper));
        }
        else {
            assertEquals(expectedResult, validation.flatMap(mapper));
        }
    }


    static Stream<Arguments> peekTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Consumer<Integer> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   action,   expectedResult
                Arguments.of( valid,        null,     valid ),
                Arguments.of( valid,        action,   valid ),
                Arguments.of( invalid,      null,     invalid ),
                Arguments.of( invalid,      action,   invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekTestCases")
    @DisplayName("peek: test cases")
    public <E, T> void peek_testCases(Validation<E, T> validation,
                                      Consumer<? super T> action,
                                      Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.peek(action));
    }


    static Stream<Arguments> peekErrorTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Consumer<List<String>> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   action,   expectedResult
                Arguments.of( invalid,      null,     invalid ),
                Arguments.of( invalid,      action,   invalid ),
                Arguments.of( valid,        null,     valid ),
                Arguments.of( valid,        action,   valid )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekErrorTestCases")
    @DisplayName("peekError: test cases")
    public <E, T> void peekError_testCases(Validation<E, T> validation,
                                           Consumer<Collection<? super E>> action,
                                           Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.peekError(action));
    }


    static Stream<Arguments> bipeekTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Consumer<Integer> actionValid = System.out::println;
        Consumer<List<String>> actionInvalid = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   actionValid,   actionInvalid,   expectedResult
                Arguments.of( valid,        null,          actionInvalid,   valid ),
                Arguments.of( valid,        actionValid,   null,            valid ),
                Arguments.of( valid,        actionValid,   actionInvalid,   valid ),
                Arguments.of( invalid,      actionValid,   null,            invalid ),
                Arguments.of( invalid,      null,          actionInvalid,   invalid ),
                Arguments.of( invalid,      actionValid,   actionInvalid,   invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("bipeekTestCases")
    @DisplayName("bipeek: test cases")
    public <E, T> void bimap_testCases(Validation<E, T> validation,
                                       Consumer<? super T> actionValid,
                                       Consumer<Collection<? super E>> actionInvalid,
                                       Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.bipeek(actionValid, actionInvalid));
    }


    static Stream<Arguments> filterTestCases() {
        Validation<String, Integer> validVerifyFilter = Validation.valid(1);
        Validation<String, Integer> validDoesNotVerifyFilter = Validation.valid(2);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Predicate<Integer> isOdd = i -> i % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            validation,                 predicate,   expectedResult
                Arguments.of( validVerifyFilter,          null,        of(validVerifyFilter) ),
                Arguments.of( validVerifyFilter,          isOdd,       of(validVerifyFilter) ),
                Arguments.of( validDoesNotVerifyFilter,   isOdd,       empty() ),
                Arguments.of( invalid,                    null,        of(invalid) ),
                Arguments.of( invalid,                    isOdd,       of(invalid) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <E, T> void filter_testCases(Validation<E, T> validation,
                                        Predicate<? super T> predicate,
                                        Optional<Validation<E, T>> expectedResult) {
        assertEquals(expectedResult, validation.filter(predicate));
    }


    static Stream<Arguments> apTestCases() {
        Validation<String, Integer> validInt1 = Validation.valid(1);
        Validation<String, Integer> validInt4 = Validation.valid(4);
        Validation<String, Integer> invalidProb1 = Validation.invalid(List.of("problem1"));
        Validation<String, Integer> invalidProb2 = Validation.invalid(List.of("problem2"));

        List<String> allErrors = new ArrayList<>(invalidProb1.getErrors());
        allErrors.addAll(invalidProb2.getErrors());
        Validation<String, Integer> invalidAll = Validation.invalid(allErrors);
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
    public <E, T> void ap_testCases(Validation<E, T> validation,
                                    Validation<E, T> validationParam,
                                    Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.ap(validationParam));
    }


    static Stream<Arguments> toOptionalTestCases() {
        Validation<String, Integer> validEmpty = Valid.empty();
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
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
    public <E, T> void toOptional_testCases(Validation<E, T> validation,
                                            Optional<Validation<E, T>> expectedResult) {
        assertEquals(expectedResult, validation.toOptional());
    }


    static Stream<Arguments> orElseWithValidationTestCases() {
        Validation<String, Integer> validInt1 = Validation.valid(1);
        Validation<String, Integer> validInt4 = Validation.valid(4);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
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
    public <E, T> void orElseWithValidation_testCases(Validation<E, T> validation,
                                                      Validation<? extends E, ? extends T> other,
                                                      Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.orElse(other));
    }


    static Stream<Arguments> orElseWithSupplierTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Supplier<Validation<String, Integer>> supplierValid = () -> Validation.valid(4);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
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
    public <E, T> void orElseWithSupplier_testCases(Validation<E, T> validation,
                                                    Supplier<Validation<? extends E, ? extends T>> supplier,
                                                    Class<? extends Exception> expectedException,
                                                    Validation<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.orElse(supplier));
        }
        else {
            assertEquals(expectedResult, validation.orElse(supplier));
        }
    }


    static Stream<Arguments> getOrElseThrowTestCases() {
        Validation<String, Integer> validEmpty = Valid.empty();
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Supplier<Exception> exceptionSupplier = () -> new IllegalArgumentException("Something was wrong");
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
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
    public <E, T, X extends Throwable> void getOrElseThrow_testCases(Validation<E, T> validation,
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
        Validation<String, Integer> validEmpty = Valid.empty();
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
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
    public <E, T> void isEmpty_testCases(Validation<E, T> validation,
                                         boolean expectedResult) {
        assertEquals(expectedResult, validation.isEmpty());
    }

}
