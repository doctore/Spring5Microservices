package com.spring5microservices.common.util.validation;

import com.spring5microservices.common.util.Try.Try;
import com.spring5microservices.common.util.either.Either;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {

    static Stream<Arguments> validTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,   expectedResult
                Arguments.of( null,    Validation.valid(null) ),
                Arguments.of( 1,       Validation.valid(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("validTestCases")
    @DisplayName("valid: test cases")
    public <E, T> void valid_testCases(T value,
                                       Validation<E, T> expectedResult) {
        assertEquals(expectedResult, Validation.valid(value));
    }


    static Stream<Arguments> invalidTestCases() {
        return Stream.of(
                //@formatter:off
                //            value,                expectedResult
                Arguments.of( null,                 Validation.invalid(null) ),
                Arguments.of( List.of(),            Validation.invalid(List.of()) ),
                Arguments.of( List.of("problem"),   Validation.invalid(List.of("problem")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("invalidTestCases")
    @DisplayName("invalid: test cases")
    public <E, T> void invalid_testCases(List<E> value,
                                         Validation<E, T> expectedResult) {
        assertEquals(expectedResult, Validation.invalid(value));
    }


    static Stream<Arguments> fromEitherTestCases() {
        Either<String, Integer> emptyRightEither = Either.right(null);
        Either<String, Integer> rightEither = Either.right(11);
        Either<String, Integer> emptyLeftEither = Either.left(null);
        Either<String, Integer> leftEither = Either.left("There was a problem");

        Validation<String, Integer> validFromEmptyEither = Validation.valid(null);
        Validation<String, Integer> validFromEither = Validation.valid(11);
        Validation<String, Integer> invalidFromEmptyEither = Validation.invalid(List.of());
        Validation<String, Integer> invalidFromEither = Validation.invalid(List.of("There was a problem"));
        return Stream.of(
                //@formatter:off
                //            either,            expectedResult
                Arguments.of( null,              invalidFromEmptyEither ),
                Arguments.of( emptyRightEither,  validFromEmptyEither ),
                Arguments.of( rightEither,       validFromEither ),
                Arguments.of( emptyLeftEither,   invalidFromEmptyEither ),
                Arguments.of( leftEither,        invalidFromEither )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromEitherTestCases")
    @DisplayName("fromEither: test cases")
    public <E, T> void fromEither_testCases(Either<? extends E, ? extends T> either,
                                            Validation<E, T>  expectedResult) {
        assertEquals(expectedResult, Validation.fromEither(either));
    }


    static Stream<Arguments> fromTryTestCases() {
        Try<Integer> emptySuccessTry = Try.success(null);
        Try<Integer> successTry = Try.success(12);

        IOException exception = new IOException();
        Try<Integer> failureTry = Try.failure(exception);

        Validation<Throwable, Integer> validFromEmptyTry = Validation.valid(null);
        Validation<Throwable, Integer> validFromTry = Validation.valid(12);
        Validation<String, Integer> invalidFromEmptyTry = Validation.invalid(List.of());
        Validation<Throwable, Integer> invalidFromTry = Validation.invalid(List.of(exception));
        return Stream.of(
                //@formatter:off
                //            t,                 expectedResult
                Arguments.of( null,              invalidFromEmptyTry ),
                Arguments.of( emptySuccessTry,   validFromEmptyTry ),
                Arguments.of( successTry,        validFromTry ),
                Arguments.of( failureTry,        invalidFromTry )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromTryTestCases")
    @DisplayName("fromTry: test cases")
    public <T> void fromTry_testCases(Try<? extends T> t,
                                      Validation<Throwable, T>  expectedResult) {
        assertEquals(expectedResult, Validation.fromTry(t));
    }


    static Stream<Arguments> combineTestCases() {
        Validation<String, Integer> valid1 = Validation.valid(1);
        Validation<String, Integer> valid2 = Validation.valid(4);
        Validation<String, Integer> invalid1 = Validation.invalid(List.of("problem1"));
        Validation<String, Integer> invalid2 = Validation.invalid(List.of("problem2"));
        Validation<String, Integer>[] allValidationsArray = new Validation[] { valid1, invalid1, valid2, invalid2 };

        List<String> allErrors = new ArrayList<>(invalid1.getErrors());
        allErrors.addAll(invalid2.getErrors());
        Validation<String, Integer> invalidAll = Validation.invalid(allErrors);
        return Stream.of(
                //@formatter:off
                //            validations,                                     expectedResult
                Arguments.of( null,                                            Valid.empty() ),
                Arguments.of( new Validation[] {},                             Valid.empty() ),
                Arguments.of( new Validation[] { valid1 },                     valid1 ),
                Arguments.of( new Validation[] { valid1, valid2 },             valid2 ),
                Arguments.of( new Validation[] { invalid1, valid1, valid2 },   invalid1 ),
                Arguments.of( new Validation[] { valid1, valid2, invalid1 },   invalid1 ),
                Arguments.of( new Validation[] { invalid1, invalid2 },         invalidAll ),
                Arguments.of( allValidationsArray,                             invalidAll )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("combineTestCases")
    @DisplayName("combine: test cases")
    public <E, T> void combine_testCases(Validation<E, T>[] validations,
                                         Validation<E, T> expectedResult) {
        assertEquals(expectedResult, Validation.combine(validations));
    }


    static Stream<Arguments> combineGetFirstInvalidTestCases() {
        Validation<String, Integer> valid1 = Validation.valid(1);
        Validation<String, Integer> valid2 = Validation.valid(4);
        Validation<String, Integer> invalid1 = Validation.invalid(List.of("problem1"));
        Validation<String, Integer> invalid2 = Validation.invalid(List.of("problem2"));

        Supplier<Validation<String, Integer>> supValid1 = () -> valid1;
        Supplier<Validation<String, Integer>> supValid2 = () -> valid2;
        Supplier<Validation<String, Integer>> supInvalid1 = () -> invalid1;
        Supplier<Validation<String, Integer>> supInvalid2 = () -> invalid2;
        return Stream.of(
                //@formatter:off
                //            supplier1,     supplier2,     supplier3,     expectedResult
                Arguments.of( null,          null,          null,          Valid.empty() ),
                Arguments.of( supValid1,     null,          null,          valid1 ),
                Arguments.of( supValid1,     supValid2,     null,          valid2 ),
                Arguments.of( supInvalid1,   supInvalid2,   null,          invalid1 ),
                Arguments.of( supInvalid1,   supValid1,     supValid2,     invalid1 ),
                Arguments.of( supValid1,     supValid2,     supInvalid1,   invalid1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("combineGetFirstInvalidTestCases")
    @DisplayName("combineGetFirstInvalid: test cases")
    public <E, T> void combineGetFirstInvalid_testCases(Supplier<Validation<E, T>> supplier1,
                                                        Supplier<Validation<E, T>> supplier2,
                                                        Supplier<Validation<E, T>> supplier3,
                                                        Validation<E, T> expectedResult) {
        Validation<E, T> result;
        if (Objects.isNull(supplier1) && Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
            result = Validation.combineGetFirstInvalid();
        } else if (Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
            result = Validation.combineGetFirstInvalid(supplier1);
        } else if (Objects.isNull(supplier3)) {
            result = Validation.combineGetFirstInvalid(supplier1, supplier2);
        } else {
            result = Validation.combineGetFirstInvalid(supplier1, supplier2, supplier3);
        }
        assertEquals(expectedResult, result);
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


    static Stream<Arguments> filterOrElseTestCases() {
        Validation<String, Integer> validVerifyFilter = Validation.valid(1);
        Validation<String, Integer> validDoesNotVerifyFilter = Validation.valid(2);
        Validation<String, Integer> invalid = Validation.invalid(List.of("warning"));
        Validation<String, Integer> invalidResult = Validation.invalid(List.of("error"));

        Predicate<Integer> isOdd = i -> i % 2 == 1;
        Function<Integer, String> errorString = i -> "error";
        return Stream.of(
                //@formatter:off
                //            validation,                 predicate,   mapper,        expectedException,                expectedResult
                Arguments.of( validVerifyFilter,          null,        null,          null,                             validVerifyFilter ),
                Arguments.of( validDoesNotVerifyFilter,   null,        null,          null,                             validDoesNotVerifyFilter ),
                Arguments.of( invalid,                    null,        null,          null,                             invalid ),
                Arguments.of( validVerifyFilter,          isOdd,       null,          null,                             validVerifyFilter ),
                Arguments.of( validVerifyFilter,          isOdd,       errorString,   null,                             validVerifyFilter ),
                Arguments.of( validDoesNotVerifyFilter,   isOdd,       null,          IllegalArgumentException.class,   null ),
                Arguments.of( validDoesNotVerifyFilter,   isOdd,       errorString,   null,                             invalidResult ),
                Arguments.of( invalid,                    isOdd,       errorString,   null,                             invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterOrElseTestCases")
    @DisplayName("filterOrElse: test cases")
    public <E, T> void filterOrElse_testCases(Validation<E, T> validation,
                                              Predicate<? super T> predicate,
                                              Function<? super T, ? extends E> mapper,
                                              Class<? extends Exception> expectedException,
                                              Validation<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.filterOrElse(predicate, mapper));
        } else {
            assertEquals(expectedResult, validation.filterOrElse(predicate, mapper));
        }
    }


    static Stream<Arguments> mapWithValidMapperTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, String> fromIntegerToString = Object::toString;
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,                expectedException,                expectedResult
                Arguments.of( valid,        null,                  IllegalArgumentException.class,   null ),
                Arguments.of( valid,        fromIntegerToString,   null,                             Validation.valid("1") ),
                Arguments.of( invalid,      null,                  null,                             invalid ),
                Arguments.of( invalid,      fromIntegerToString,   null,                             invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithValidMapperTestCases")
    @DisplayName("map: with valid mapper test cases")
    public <E, T, U> void mapWithValidMapper_testCases(Validation<E, T> validation,
                                                       Function<? super T, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Validation<U, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.map(mapper));
        } else {
            assertEquals(expectedResult, validation.map(mapper));
        }
    }


    static Stream<Arguments> mapWithBothMappersTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, String> fromIntegerToString = Object::toString;
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").collect(toList());
        return Stream.of(
                //@formatter:off
                //            validation,   mapperInvalid,   mapperValid,           expectedException,                expectedResult
                Arguments.of( valid,        addALetter,      null,                  IllegalArgumentException.class,   null ),
                Arguments.of( valid,        null,            fromIntegerToString,   null,                             Validation.valid("1") ),
                Arguments.of( valid,        addALetter,      fromIntegerToString,   null,                             Validation.valid("1") ),
                Arguments.of( invalid,      null,            fromIntegerToString,   IllegalArgumentException.class,   null ),
                Arguments.of( invalid,      addALetter,      null,                  null,                             Validation.invalid(List.of("problem2")) ),
                Arguments.of( invalid,      addALetter,      fromIntegerToString,   null,                             Validation.invalid(List.of("problem2")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithBothMappersTestCases")
    @DisplayName("map: with both mappers test cases")
    public <E, T, E2, T2> void mapWithBothMappers_testCases(Validation<E, T> validation,
                                                            Function<Collection<? super E>, Collection<E2>> mapperInvalid,
                                                            Function<? super T, ? extends T2> mapperValid,
                                                            Class<? extends Exception> expectedException,
                                                            Validation<E2, T2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.map(mapperInvalid, mapperValid));
        } else {
            assertEquals(expectedResult, validation.map(mapperInvalid, mapperValid));
        }
    }


    static Stream<Arguments> mapInvalidTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<List<String>, List<String>> addALetter = i -> i.stream().map(elto -> elto + "2").toList();
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,       expectedException,                expectedResult
                Arguments.of( invalid,      null,         IllegalArgumentException.class,   null ),
                Arguments.of( invalid,      addALetter,   null,                             Validation.invalid(List.of("problem2")) ),
                Arguments.of( valid,        null,         null,                             valid ),
                Arguments.of( valid,        addALetter,   null,                             valid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapInvalidTestCases")
    @DisplayName("mapInvalid: test cases")
    public <E, T, U> void mapInvalid_testCases(Validation<E, T> validation,
                                               Function<Collection<? super E>, Collection<U>> mapper,
                                               Class<? extends Exception> expectedException,
                                               Validation<U, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.mapInvalid(mapper));
        } else {
            assertEquals(expectedResult, validation.mapInvalid(mapper));
        }
    }


    static Stream<Arguments> flatmapTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Function<Integer, Validation<String, String>> fromIntegerToValidWithString = i -> Validation.valid(i.toString());
        return Stream.of(
                //@formatter:off
                //            validation,   mapper,                         expectedException,                expectedResult
                Arguments.of( valid,        null,                           IllegalArgumentException.class,   null ),
                Arguments.of( valid,        fromIntegerToValidWithString,   null,                             Validation.valid("1") ),
                Arguments.of( invalid,      null,                           null,                             invalid ),
                Arguments.of( invalid,      fromIntegerToValidWithString,   null,                             invalid )
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
        } else {
            assertEquals(expectedResult, validation.flatMap(mapper));
        }
    }


    static Stream<Arguments> apTestCases() {
        Validation<String, Integer> emptyValid = Validation.valid(null);
        Validation<String, Integer> validInt1 = Validation.valid(1);
        Validation<String, Integer> validInt4 = Validation.valid(4);
        Validation<String, Integer> emptyInvalid = Validation.invalid(null);
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
                Arguments.of( validInt1,      emptyValid,        emptyValid ),
                Arguments.of( emptyValid,     validInt1,         validInt1 ),
                Arguments.of( invalidProb1,   emptyInvalid,      invalidProb1 ),
                Arguments.of( emptyInvalid,   invalidProb1,      invalidProb1 ),
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


    static Stream<Arguments> foldOneMapperTestCases() {
        Integer defaultValue = 11;
        Validation<String, Integer> validWithNoValue = Validation.valid(null);
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem", "problem2"));
        Function<Validation<String, Integer>, Integer> mapper = v ->
                v.isValid()
                        ? ofNullable(v.get()).orElse(defaultValue)
                        : v.getErrors().size();
        return Stream.of(
                //@formatter:off
                //            validation,         mapper,   expectedException,                expectedResult
                Arguments.of( valid,              null,     IllegalArgumentException.class,   null ),
                Arguments.of( invalid,            null,     IllegalArgumentException.class,   null ),
                Arguments.of( validWithNoValue,   mapper,   null,                             defaultValue ),
                Arguments.of( valid,              mapper,   null,                             valid.get() ),
                Arguments.of( invalid,            mapper,   null,                             invalid.getErrors().size() )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldOneMapperTestCases")
    @DisplayName("fold: with one mapper test cases")
    public <E, T, U> void foldOneMapper_testCases(Validation<E, T> validation,
                                                  Function<? super Validation<E, T>, ? extends U> mapper,
                                                  Class<? extends Exception> expectedException,
                                                  U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.fold(mapper));
        } else {
            assertEquals(expectedResult, validation.fold(mapper));
        }
    }


    static Stream<Arguments> foldTwoMappersTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem", "problem2"));
        Function<Integer, String> integerToString = Object::toString;
        Function<Collection<String>, Integer> collectionSize = Collection::size;
        return Stream.of(
                //@formatter:off
                //            validation,   mapperInvalid,     mapperValid,       expectedException,                expectedResult
                Arguments.of( valid,        null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( valid,        collectionSize,    null,              IllegalArgumentException.class,   null ),
                Arguments.of( invalid,      null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( invalid,      null,              integerToString,   IllegalArgumentException.class,   null ),
                Arguments.of( valid,        null,              integerToString,   null,                             "1" ),
                Arguments.of( valid,        collectionSize,    integerToString,   null,                             "1" ),
                Arguments.of( invalid,      collectionSize,    null,              null,                             2 ),
                Arguments.of( invalid,      collectionSize,    integerToString,   null,                             2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldTwoMappersTestCases")
    @DisplayName("fold: with two mappers test cases")
    public <E, T, U> void foldTwoMappers_testCases(Validation<E, T> validation,
                                                   Function<Collection<? super E>, U> mapperInvalid,
                                                   Function<? super T, ? extends U> mapperValid,
                                                   Class<? extends Exception> expectedException,
                                                   U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.fold(mapperInvalid, mapperValid));
        } else {
            assertEquals(expectedResult, validation.fold(mapperInvalid, mapperValid));
        }
    }


    static Stream<Arguments> peekWithValidActionTestCases() {
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
    @MethodSource("peekWithValidActionTestCases")
    @DisplayName("peek: with valid action test cases")
    public <E, T> void peekWithValidAction_testCases(Validation<E, T> validation,
                                                     Consumer<? super T> action,
                                                     Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.peek(action));
    }


    static Stream<Arguments> peekWithBothConsumersTestCases() {
        Validation<String, Integer> valid = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Consumer<Integer> actionValid = System.out::println;
        Consumer<List<String>> actionInvalid = System.out::println;
        return Stream.of(
                //@formatter:off
                //            validation,   actionInvalid,   actionValid,   expectedResult
                Arguments.of( valid,        actionInvalid,   null,          valid ),
                Arguments.of( valid,        null,            actionValid,   valid ),
                Arguments.of( valid,        actionInvalid,   actionValid,   valid ),
                Arguments.of( invalid,      null,            actionValid,   invalid ),
                Arguments.of( invalid,      actionInvalid,   null,          invalid ),
                Arguments.of( invalid,      actionInvalid,   actionValid,   invalid )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekWithBothConsumersTestCases")
    @DisplayName("peek: with both consumers test cases")
    public <E, T> void peekWithBothConsumers_testCases(Validation<E, T> validation,
                                                       Consumer<Collection<? super E>> actionInvalid,
                                                       Consumer<? super T> actionValid,
                                                       Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.peek(actionInvalid, actionValid));
    }


    static Stream<Arguments> peekInvalidTestCases() {
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
    @MethodSource("peekInvalidTestCases")
    @DisplayName("peekInvalid: test cases")
    public <E, T> void peekInvalid_testCases(Validation<E, T> validation,
                                             Consumer<Collection<? super E>> action,
                                             Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.peekInvalid(action));
    }


    static Stream<Arguments> getOrElseTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Integer other = 33;
        return Stream.of(
                //@formatter:off
                //            validation,      other,   expectedResult
                Arguments.of( validEmpty,      null,    validEmpty.get() ),
                Arguments.of( validEmpty,      other,   validEmpty.get() ),
                Arguments.of( validNotEmpty,   null,    validNotEmpty.get() ),
                Arguments.of( validNotEmpty,   other,   validNotEmpty.get() ),
                Arguments.of( invalid,         null,    null ),
                Arguments.of( invalid,         other,   other )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseTestCases")
    @DisplayName("getOrElse: test cases")
    public <E, T> void getOrElse_testCases(Validation<E, T> validation,
                                           T other,
                                           T expectedResult) {
        assertEquals(expectedResult, validation.getOrElse(other));
    }


    static Stream<Arguments> getOrElseThrowWithSupplierTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Supplier<Exception> exceptionSupplier = () -> new IllegalArgumentException("Something was wrong");
        return Stream.of(
                //@formatter:off
                //            validation,      exceptionSupplier,   expectedException,                expectedResult
                Arguments.of( validEmpty,      null,                null,                             null ),
                Arguments.of( validEmpty,      exceptionSupplier,   null,                             null ),
                Arguments.of( validNotEmpty,   null,                null,                             validNotEmpty.get() ),
                Arguments.of( validNotEmpty,   exceptionSupplier,   null,                             validNotEmpty.get() ),
                Arguments.of( invalid,         null,                IllegalArgumentException.class,   null ),
                Arguments.of( invalid,         exceptionSupplier,   IllegalArgumentException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseThrowWithSupplierTestCases")
    @DisplayName("getOrElseThrow: with Supplier as parameter test cases")
    public <E, T, X extends Throwable> void getOrElseThrowWithSupplier_testCases(Validation<E, T> validation,
                                                                                 Supplier<X> exceptionSupplier,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 T expectedResult) throws Throwable {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.getOrElseThrow(exceptionSupplier));
        } else {
            assertEquals(expectedResult, validation.getOrElseThrow(exceptionSupplier));
        }
    }


    static Stream<Arguments> getOrElseThrowWithFunctionTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));

        Function<Collection<Integer>, Exception> exceptionFunction =
                (ints) ->
                        new IllegalArgumentException(
                                format("Something was wrong. Number of errors: %s",
                                        null == ints
                                                ? 0
                                                : ints.size()
                                )
                        );
        return Stream.of(
                //@formatter:off
                //            validation,      exceptionFunction,   expectedException,                expectedResult
                Arguments.of( validEmpty,      null,                null,                             null ),
                Arguments.of( validEmpty,      exceptionFunction,   null,                             null ),
                Arguments.of( validNotEmpty,   null,                null,                             validNotEmpty.get() ),
                Arguments.of( validNotEmpty,   exceptionFunction,   null,                             validNotEmpty.get() ),
                Arguments.of( invalid,         null,                IllegalArgumentException.class,   null ),
                Arguments.of( invalid,         exceptionFunction,   IllegalArgumentException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseThrowWithFunctionTestCases")
    @DisplayName("getOrElseThrow: with Function as parameter test cases")
    public <E, T, X extends Throwable> void getOrElseThrowWithFunction_testCases(Validation<E, T> validation,
                                                                                 Function<Collection<? super E>, X> exceptionFunction,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 T expectedResult) throws Throwable {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.getOrElseThrow(exceptionFunction));
        } else {
            assertEquals(expectedResult, validation.getOrElseThrow(exceptionFunction));
        }
    }


    static Stream<Arguments> orElseWithValidationTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty1 = Validation.valid(1);
        Validation<String, Integer> validNotEmpty2 = Validation.valid(4);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        return Stream.of(
                //@formatter:off
                //            validation,       other,            expectedResult
                Arguments.of( validEmpty,       null,             validEmpty ),
                Arguments.of( validEmpty,       validNotEmpty1,   validEmpty ),
                Arguments.of( validNotEmpty1,   null,             validNotEmpty1 ),
                Arguments.of( validNotEmpty1,   validNotEmpty2,   validNotEmpty1 ),
                Arguments.of( invalid,          null,             null ),
                Arguments.of( invalid,          validNotEmpty1,   validNotEmpty1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithValidationTestCases")
    @DisplayName("orElse: with Validation as parameter test cases")
    public <E, T> void orElseWithValidation_testCases(Validation<E, T> validation,
                                                      Validation<? extends E, ? extends T> other,
                                                      Validation<E, T> expectedResult) {
        assertEquals(expectedResult, validation.orElse(other));
    }


    static Stream<Arguments> orElseWithSupplierTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalid = Validation.invalid(List.of("problem"));
        Supplier<Validation<String, Integer>> supplierValid = () -> Validation.valid(4);
        return Stream.of(
                //@formatter:off
                //            validation,      supplier,        expectedException,                expectedResult
                Arguments.of( validEmpty,      null,            null,                             validEmpty ),
                Arguments.of( validEmpty,      supplierValid,   null,                             validEmpty ),
                Arguments.of( validNotEmpty,   null,            null,                             validNotEmpty ),
                Arguments.of( validNotEmpty,   supplierValid,   null,                             validNotEmpty ),
                Arguments.of( invalid,         null,            IllegalArgumentException.class,   null ),
                Arguments.of( invalid,         supplierValid,   null,                             Validation.valid(4) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithSupplierTestCases")
    @DisplayName("orElse: with Supplier as parameter test cases")
    public <E, T> void orElseWithSupplier_testCases(Validation<E, T> validation,
                                                    Supplier<Validation<? extends E, ? extends T>> supplier,
                                                    Class<? extends Exception> expectedException,
                                                    Validation<E, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.orElse(supplier));
        } else {
            assertEquals(expectedResult, validation.orElse(supplier));
        }
    }


    static Stream<Arguments> isEmptyTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
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


    static Stream<Arguments> toOptionalTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
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


    static Stream<Arguments> toEitherTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalidEmpty = Invalid.empty();
        Validation<String, Integer> invalidNotEmpty = Validation.invalid(List.of("problem", "problem2"));
        return Stream.of(
                //@formatter:off
                //            validation,        expectedResult
                Arguments.of( validEmpty,        Either.right(null) ),
                Arguments.of( validNotEmpty,     Either.<String, Integer>right(validNotEmpty.get()) ),
                Arguments.of( invalidEmpty,      Either.<List<String>, Integer>left(new ArrayList<>()) ),
                Arguments.of( invalidNotEmpty,   Either.<List<String>, Integer>left(List.of("problem", "problem2")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toEitherTestCases")
    @DisplayName("toEither: test cases")
    public <E, T> void toEither_testCases(Validation<E, T> validation,
                                          Either<Collection<E>, T> expectedResult) {
        assertEquals(expectedResult, validation.toEither());
    }


    static Stream<Arguments> toTryTestCases() {
        Validation<String, Integer> validEmpty = Validation.valid(null);
        Validation<String, Integer> validNotEmpty = Validation.valid(1);
        Validation<String, Integer> invalidEmpty = Invalid.empty();
        Validation<String, Integer> invalidNotEmpty = Validation.invalid(List.of("problem", "problem2"));

        NullPointerException nullPointerException = new NullPointerException("Something is missing");
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Something is not valid");

        Try<Integer> tryFromEmptyValid = Try.success(null);
        Try<Integer> tryFromValid = Try.success(1);
        Try<Integer> tryFromEmptyInvalid = Try.failure(nullPointerException);
        Try<Integer> tryFromInvalid = Try.failure(illegalArgumentException);

        Function<Collection<String>, ? extends Throwable> mapperInvalid =
                (c) ->
                        null == c || c.isEmpty()
                                ? nullPointerException
                                : illegalArgumentException;
        return Stream.of(
                //@formatter:off
                //            validation,        mapperInvalid,   expectedException,                expectedResult
                Arguments.of( validEmpty,        null,            null,                             tryFromEmptyValid ),
                Arguments.of( validEmpty,        mapperInvalid,   null,                             tryFromEmptyValid ),
                Arguments.of( validNotEmpty,     null,            null,                             tryFromValid ),
                Arguments.of( validNotEmpty,     mapperInvalid,   null,                             tryFromValid ),
                Arguments.of( invalidEmpty,      null,            IllegalArgumentException.class,   null ),
                Arguments.of( invalidEmpty,      mapperInvalid,   null,                             tryFromEmptyInvalid ),
                Arguments.of( invalidNotEmpty,   null,            IllegalArgumentException.class,   null ),
                Arguments.of( invalidNotEmpty,   mapperInvalid,   null,                            tryFromInvalid )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toTryTestCases")
    @DisplayName("toTry: test cases")
    public <E, T> void toTry_testCases(Validation<E, T> validation,
                                       Function<Collection<? super E>, ? extends Throwable> mapperInvalid,
                                       Class<? extends Exception> expectedException,
                                       Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> validation.toTry(mapperInvalid));
        } else {
            assertEquals(expectedResult, validation.toTry(mapperInvalid));
        }
    }

}
