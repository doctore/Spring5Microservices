package com.spring5microservices.common.util.Try;

import com.spring5microservices.common.util.either.Either;
import com.spring5microservices.common.util.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TryTest {

    static Stream<Arguments> ofTestCases() {
        Supplier<Long> supplierSuccess = () -> 23L;
        Supplier<Long> supplierDivisionByZero = () -> 12L / 0;

        Try<Long> expectedFailureNullSupplier = Try.failure(new NullPointerException());
        Try<Long> expectedFailureDivisionByZero = Try.failure(new ArithmeticException("/ by zero"));
        return Stream.of(
                //@formatter:off
                //            supplier,                 expectedResult
                Arguments.of( null,                     expectedFailureNullSupplier ),
                Arguments.of( supplierSuccess,          Try.success(23L) ),
                Arguments.of( supplierDivisionByZero,   expectedFailureDivisionByZero )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T> void of_testCases(Supplier<T> supplier,
                                 Try<T> expectedResult) {

        Try<T> result = Try.of(supplier);
        compareTry(expectedResult, result);
    }


    static Stream<Arguments> successTestCases() {
       return Stream.of(
                //@formatter:off
                //            value,            expectedResult
                Arguments.of( null,             Try.success(null) ),
                Arguments.of( 1,                Try.success(1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("successTestCases")
    @DisplayName("success: test cases")
    public <T> void success_testCases(T value,
                                      Try<T> expectedResult) {
        assertEquals(expectedResult, Try.success(value));
    }


    static Stream<Arguments> failureTestCases() {
        IOException exception = new IOException();
        InterruptedException fatalException = new InterruptedException();
        return Stream.of(
                //@formatter:off
                //            exception,        expectedException,                expectedResult
                Arguments.of( null,             IllegalArgumentException.class,   null ),
                Arguments.of( fatalException,   InterruptedException.class,       null ),
                Arguments.of( exception,        null,                             Failure.of(exception) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("failureTestCases")
    @DisplayName("failure: test cases")
    public <T> void failure_testCases(Throwable exception,
                                      Class<? extends Exception> expectedException,
                                      Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Try.failure(exception));
        }
        else {
            assertEquals(expectedResult, Try.failure(exception));
        }
    }


    static Stream<Arguments> combineTestCases() {
        Try<Long> success1 = Try.success(10L);
        Try<Long> success2 = Try.success(20L);
        Try<Long> failure1 = Try.failure(new IndexOutOfBoundsException("Index out of bound error"));
        Try<Long> failure2 = Try.failure(new UnsupportedOperationException("Unsupported operation error"));
        Try<Long>[] allTriesArray = new Try[] { success1, failure1, success2, failure2 };

        BiFunction<Long, Long, Long> sumAll = Long::sum;
        BiFunction<Long, Long, Long> functionSuccessThrowsAnException = (l1, l2) -> {
            throw new NumberFormatException("No way to convert Long");
        };

        BiFunction<Throwable, Throwable, Throwable> alwaysReturnLastThrowable = (t1, t2) -> t2;
        BiFunction<Throwable, Throwable, Throwable> functionFailureThrowsAnException = (t1, t2) -> {
            throw new NoSuchElementException("There was a problem");
        };

        Try<Long> sumAllSuccess = Try.success(30L);
        Try<Long> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new NumberFormatException("No way to convert Long")
        );

        Try<Integer> expectedAllFailures = Try.failure(failure2.getException());
        Try<Integer> expectedFromMapperFailureThrowsAnException = Try.failure(
                new NoSuchElementException("There was a problem")
        );
        return Stream.of(
                //@formatter:off
                //            mapperFailure,                      mapperSuccess,                      tries,                                        expectedException,                expectedResult
                Arguments.of( null,                               null,                               null,                                         IllegalArgumentException.class,   null ),
                Arguments.of( alwaysReturnLastThrowable,          null,                               null,                                         IllegalArgumentException.class,   null ),
                Arguments.of( null,                               sumAll,                             null,                                         IllegalArgumentException.class,   null ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             null,                                         null,                             Success.empty() ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] {},                                 null,                             Success.empty() ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] { success1 },                       null,                             success1 ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] { success1, success2 },             null,                             sumAllSuccess ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] { failure1, success1, success2 },   null,                             failure1 ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] { success1, success2, failure1 },   null,                             failure1 ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             new Try[] { failure1, failure2 },             null,                             expectedAllFailures ),
                Arguments.of( alwaysReturnLastThrowable,          sumAll,                             allTriesArray,                                null,                             expectedAllFailures ),
                Arguments.of( alwaysReturnLastThrowable,          functionSuccessThrowsAnException,   new Try[] { success1, success2 },             null,                             expectedFromMapperSuccessThrowsAnException ),
                Arguments.of( functionFailureThrowsAnException,   sumAll,                             new Try[] { failure1, failure2 },             null,                             expectedFromMapperFailureThrowsAnException )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("combineTestCases")
    @DisplayName("combine: test cases")
    public <T> void combine_testCases(BiFunction<? super Throwable, ? super Throwable, ? extends Throwable> mapperFailure,
                                      BiFunction<? super T, ? super T, ? extends T> mapperSuccess,
                                      Try<T>[] tries,
                                      Class<? extends Exception> expectedException,
                                      Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Try.combine(mapperFailure, mapperSuccess, tries));
        }
        else {
            Try<T> result = Try.combine(mapperFailure, mapperSuccess, tries);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> combineGetFirstLeftTestCases() {
        Try<Long> success1 = Try.success(10L);
        Try<Long> success2 = Try.success(20L);
        Try<Long> failure1 = Try.failure(new IndexOutOfBoundsException("Index out of bound error"));
        Try<Long> failure2 = Try.failure(new UnsupportedOperationException("Unsupported operation error"));

        Supplier<Try<Long>> supSuccess1 = () -> success1;
        Supplier<Try<Long>> supSuccess2 = () -> success2;
        Supplier<Try<Long>> supFailure1 = () -> failure1;
        Supplier<Try<Long>> supFailure2 = () -> failure2;

        BiFunction<Long, Long, Long> sumAll = Long::sum;
        BiFunction<Long, Long, Long> functionSuccessThrowsAnException = (l1, l2) -> {
            throw new NumberFormatException("No way to convert Long");
        };

        Try<Long> sumAllSuccess = Try.success(30L);
        Try<Long> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new NumberFormatException("No way to convert Long")
        );
        return Stream.of(
                //@formatter:off
                //            mapperSuccess,                      supplier1,     supplier2,     supplier3,     expectedException,                expectedResult
                Arguments.of( null,                               null,          null,          null,          IllegalArgumentException.class,   null ),
                Arguments.of( null,                               supSuccess1,   supSuccess2,   supFailure1,   IllegalArgumentException.class,   null ),
                Arguments.of( sumAll,                             null,          null,          null,          null,                             Success.empty() ),
                Arguments.of( sumAll,                             supSuccess1,   null,          null,          null,                             success1 ),
                Arguments.of( sumAll,                             supSuccess1,   supSuccess2,   null,          null,                             sumAllSuccess ),
                Arguments.of( sumAll,                             supFailure1,   supFailure2,   null,          null,                             failure1 ),
                Arguments.of( sumAll,                             supFailure1,   supSuccess1,   supSuccess2,   null,                             failure1 ),
                Arguments.of( sumAll,                             supSuccess1,   supSuccess2,   supFailure1,   null,                             failure1 ),
                Arguments.of( functionSuccessThrowsAnException,   supSuccess1,   supSuccess2,   supFailure1,   null,                             expectedFromMapperSuccessThrowsAnException )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("combineGetFirstLeftTestCases")
    @DisplayName("combineGetFirstLeft: test cases")
    public <T> void combineGetFirstLeft_testCases(BiFunction<? super T, ? super T, ? extends T> mapperSuccess,
                                                  Supplier<Try<T>> supplier1,
                                                  Supplier<Try<T>> supplier2,
                                                  Supplier<Try<T>> supplier3,
                                                  Class<? extends Exception> expectedException,
                                                  Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> Try.combineGetFirstFailure(mapperSuccess, supplier1));
        }
        else {
            Try<T> result;
            if (Objects.isNull(supplier1) && Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
                result = Try.combineGetFirstFailure(mapperSuccess);
            }
            else if (Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
                result = Try.combineGetFirstFailure(mapperSuccess, supplier1);
            }
            else if (Objects.isNull(supplier3)) {
                result = Try.combineGetFirstFailure(mapperSuccess, supplier1, supplier2);
            }
            else {
                result = Try.combineGetFirstFailure(mapperSuccess, supplier1, supplier2, supplier3);
            }
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> failedTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("All work as expected");
        Try<String> failure = Try.failure(new ArrayIndexOutOfBoundsException("Array index out of bound error"));

        Try<Throwable> expectedFromSuccess = Try.failure(new UnsupportedOperationException("failed() cannot be invoked from a 'success' Try"));
        Try<Throwable> expectedFromFailure = Try.success(failure.getException());
        return Stream.of(
                //@formatter:off
                //            try,               expectedResult
                Arguments.of( successEmpty,      expectedFromSuccess ),
                Arguments.of( successNotEmpty,   expectedFromSuccess ),
                Arguments.of( failure,           expectedFromFailure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("failedTestCases")
    @DisplayName("failed: test cases")
    public <T> void failed_testCases(Try<T> t,
                                     Try<Throwable> expectedResult) {
        Try<Throwable> result = t.failed();
        if (t.isSuccess()) {
            compareFailureTry(expectedResult, result);
            assertEquals(UnsupportedOperationException.class, result.getException().getClass());
        }
        else {
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> filterTestCases() {
        Try<Long> successVerifyFilter = Try.success(12L);
        Try<Long> successDoesNotVerifyFilter = Try.success(9L);
        Try<Long> failure = Try.failure(new ClassNotFoundException("Class not found error"));

        Predicate<Long> isEven = l -> l % 2 == 0;
        Predicate<Long> predicateThrowsAnException = l -> {
            throw new RuntimeException("There was a problem");
        };

        Try<Long> expectedFromSuccessDoesNotVerify = Try.failure(
                new NoSuchElementException("Predicate does not hold for " + successDoesNotVerifyFilter.get())
        );
        Try<Long> expectedFromPredicateThrowsAnException = Try.failure(
                new RuntimeException("There was a problem")
        );
        return Stream.of(
                //@formatter:off
                //            try,                          predicate,                    expectedResult
                Arguments.of( successVerifyFilter,          null,                         successVerifyFilter ),
                Arguments.of( successVerifyFilter,          isEven,                       successVerifyFilter ),
                Arguments.of( successDoesNotVerifyFilter,   isEven,                       expectedFromSuccessDoesNotVerify ),
                Arguments.of( successDoesNotVerifyFilter,   predicateThrowsAnException,   expectedFromPredicateThrowsAnException ),
                Arguments.of( failure,                      null,                         failure ),
                Arguments.of( failure,                      isEven,                       failure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterTestCases")
    @DisplayName("filter: test cases")
    public <T> void filter_testCases(Try<T> t,
                                     Predicate<? super T> predicate,
                                     Try<T> expectedResult) {
        Try<T> result = t.filter(predicate);
        compareTry(expectedResult, result);
    }


    static Stream<Arguments> mapWithSuccessMapperTestCases() {
        Try<String> success = Try.success("There was no problem");
        Try<String> failure = Try.failure(new IOException("IO error"));

        Function<String, Integer> fromStringToInteger = Integer::parseInt;
        Function<String, String> fromStringToString = s -> s + "v2";

        Try<String> expectedFromSuccess = Try.success(
                success.get() + "v2"
        );
        Try<Long> expectedFromMapperThrowsAnException = Try.failure(
                new NumberFormatException("For input string: \"There was no problem\"")
        );
        return Stream.of(
                //@formatter:off
                //            try,       mapper,                expectedException,                expectedResult
                Arguments.of( success,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( success,   fromStringToString,    null,                             expectedFromSuccess ),
                Arguments.of( success,   fromStringToInteger,   null,                             expectedFromMapperThrowsAnException ),
                Arguments.of( failure,   null,                  null,                             failure ),
                Arguments.of( failure,   fromStringToString,    null,                             failure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithSuccessMapperTestCases")
    @DisplayName("map: with success mapper test cases")
    public <T, U> void mapWithSuccessMapper_testCases(Try<T> t,
                                                      Function<? super T, ? extends U> mapper,
                                                      Class<? extends Exception> expectedException,
                                                      Try<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.map(mapper));
        }
        else {
            Try<U> result = t.map(mapper);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> mapFailureTestCases() {
        Try<String> success = Try.success("There was no problem");
        Try<String> failure = Try.failure(new IOException("IO error"));

        Function<Throwable, Throwable> fromIoToOutOfBoundsException = e -> new ArrayIndexOutOfBoundsException(e.getMessage());
        Function<Throwable, Throwable> functionThrowsAnException = e -> {
            throw new RuntimeException("There was a problem");
        };

        Try<String> expectedFromFailure = Try.failure(
                new ArrayIndexOutOfBoundsException(
                        failure.getException().getMessage()
                )
        );
        Try<String> expectedFromMapperThrowsAnException = Try.failure(
                new RuntimeException("There was a problem")
        );
        return Stream.of(
                //@formatter:off
                //            try,       mapper,                         expectedException,                expectedResult
                Arguments.of( failure,   null,                           IllegalArgumentException.class,   null ),
                Arguments.of( failure,   fromIoToOutOfBoundsException,   null,                             expectedFromFailure ),
                Arguments.of( failure,   functionThrowsAnException,      null,                             expectedFromMapperThrowsAnException ),
                Arguments.of( success,   null,                           null,                             success ),
                Arguments.of( success,   fromIoToOutOfBoundsException,   null,                             success )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFailureTestCases")
    @DisplayName("mapFailure: test cases")
    public <T> void mapFailure_testCases(Try<T> t,
                                         Function<? super Throwable, ? extends Throwable> mapper,
                                         Class<? extends Exception> expectedException,
                                         Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.mapFailure((mapper)));
        }
        else {
            Try<T> result = t.mapFailure(mapper);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> mapWithBothMappersTestCases() {
        Try<Integer> success = Try.success(21);
        Try<Integer> failure = Try.failure(new IllegalAccessException("Illegal access error"));

        Function<Throwable, Throwable> fromIllegalAccessToNoSuchMethodException = e -> new NoSuchMethodException(e.getMessage());
        Function<Throwable, Throwable> functionFailureThrowsAnException = e -> {
            throw new NoSuchElementException("There was a problem");
        };
        Function<Integer, String> fromIntegerToString = Object::toString;
        Function<Integer, String> functionSuccessThrowsAnException = i -> {
            throw new NumberFormatException("No way to convert Integer");
        };

        Try<Integer> expectedFromFailure = Try.failure(
                new NoSuchMethodException(
                        failure.getException().getMessage()
                )
        );
        Try<Integer> expectedFromMapperFailureThrowsAnException = Try.failure(
                new NoSuchElementException("There was a problem")
        );

        Try<String> expectedFromSuccess = Try.success(
                success.get().toString()
        );
        Try<String> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new NumberFormatException("No way to convert Integer")
        );
        return Stream.of(
                //@formatter:off
                //            try,       mapperFailure,                              mapperSuccess,                      expectedException,                expectedResult
                Arguments.of( success,   fromIllegalAccessToNoSuchMethodException,   null,                               IllegalArgumentException.class,   null ),
                Arguments.of( success,   null,                                       fromIntegerToString,                null,                             expectedFromSuccess ),
                Arguments.of( success,   null,                                       functionSuccessThrowsAnException,   null,                             expectedFromMapperSuccessThrowsAnException ),
                Arguments.of( success,   fromIllegalAccessToNoSuchMethodException,   fromIntegerToString,                null,                             expectedFromSuccess ),
                Arguments.of( failure,   null,                                       fromIntegerToString,                IllegalArgumentException.class,   null ),
                Arguments.of( failure,   fromIllegalAccessToNoSuchMethodException,   null,                               null,                             expectedFromFailure ),
                Arguments.of( failure,   functionFailureThrowsAnException,           null,                               null,                             expectedFromMapperFailureThrowsAnException ),
                Arguments.of( failure,   fromIllegalAccessToNoSuchMethodException,   fromIntegerToString,                null,                             expectedFromFailure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapWithBothMappersTestCases")
    @DisplayName("map: with both mappers test cases")
    public <T, U> void mapWithBothMappers_testCases(Try<T> t,
                                                    Function<? super Throwable, ? extends Throwable> mapperFailure,
                                                    Function<? super T, ? extends U> mapperSuccess,
                                                    Class<? extends Exception> expectedException,
                                                    Try<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.map(mapperFailure, mapperSuccess));
        }
        else {
            Try<U> result = t.map(mapperFailure, mapperSuccess);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> flatmapTestCases() {
        Try<Integer> success = Try.success(21);
        Try<Integer> failure = Try.failure(new IllegalAccessException());

        Function<Integer, Try<String>> fromIntegerToSuccessWithString = i -> Try.success(i.toString());
        Function<Integer, Try<String>> functionThrowsAnException = i -> {
            throw new NumberFormatException("No way to convert Integer");
        };

        Try<String> expectedFromSuccess = Try.success(
                success.get().toString()
        );
        Try<String> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new NumberFormatException("No way to convert Integer")
        );
        return Stream.of(
                //@formatter:off
                //            try,       mapper,                           expectedException,                expectedResult
                Arguments.of( success,   null,                             IllegalArgumentException.class,   null ),
                Arguments.of( success,   fromIntegerToSuccessWithString,   null,                             expectedFromSuccess ),
                Arguments.of( success,   functionThrowsAnException,        null,                             expectedFromMapperSuccessThrowsAnException ),
                Arguments.of( failure,   null,                             null,                             failure ),
                Arguments.of( failure,   fromIntegerToSuccessWithString,   null,                             failure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flatmapTestCases")
    @DisplayName("flatmap: test cases")
    public <T, U> void flatmap_testCases(Try<T> t,
                                         Function<? super T, ? extends Try<? extends U>> mapper,
                                         Class<? extends Exception> expectedException,
                                         Try<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.flatMap(mapper));
        }
        else {
            Try<U> result = t.flatMap(mapper);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> apTestCases() {
        Try<Integer> successEmpty = Try.success(null);
        Try<Integer> successNotEmpty1 = Try.success(11);
        Try<Integer> successNotEmpty2 = Try.success(13);
        Try<Integer> failure1 = Try.failure(new ArrayIndexOutOfBoundsException("Array index out of bound error"));
        Try<Integer> failure2 = Try.failure(new IOException("IO error"));

        BiFunction<Integer, Integer, Integer> sumAll = Integer::sum;
        BiFunction<Integer, Integer, Integer> functionSuccessThrowsAnException = (i1, i2) -> {
            throw new NumberFormatException("No way to convert Integer");
        };

        BiFunction<Throwable, Throwable, Throwable> alwaysReturnLastThrowable = (t1, t2) -> t2;
        BiFunction<Throwable, Throwable, Throwable> functionFailureThrowsAnException = (t1, t2) -> {
            throw new NoSuchElementException("There was a problem");
        };

        Try<Integer> sumAllSuccess = Try.success(24);
        Try<Integer> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new NumberFormatException("No way to convert Integer")
        );

        Try<Integer> expectedAllFailures = Try.failure(failure2.getException());
        Try<Integer> expectedFromMapperFailureThrowsAnException = Try.failure(
                new NoSuchElementException("There was a problem")
        );
        return Stream.of(
                //@formatter:off
                //            try,                tryParam,           mapperFailure,                      mapperSuccess,                      expectedException,                expectedResult
                Arguments.of( successEmpty,       null,               null,                               null,                               null,                             successEmpty ),
                Arguments.of( successNotEmpty1,   null,               null,                               null,                               null,                             successNotEmpty1 ),
                Arguments.of( failure1,           null,               null,                               null,                               null,                             failure1 ),
                Arguments.of( successEmpty,       successNotEmpty1,   alwaysReturnLastThrowable,          null,                               IllegalArgumentException.class,   null ),
                Arguments.of( failure1,           failure2,           null,                               sumAll,                             IllegalArgumentException.class,   null ),
                Arguments.of( successNotEmpty1,   failure1,           null,                               null,                               null,                             failure1 ),
                Arguments.of( failure2,           successNotEmpty2,   null,                               null,                               null,                             failure2 ),
                Arguments.of( successNotEmpty1,   successNotEmpty2,   null,                               sumAll,                             null,                             sumAllSuccess ),
                Arguments.of( successNotEmpty1,   successNotEmpty2,   null,                               functionSuccessThrowsAnException,   null,                             expectedFromMapperSuccessThrowsAnException ),
                Arguments.of( failure1,           failure2,           alwaysReturnLastThrowable,          null,                               null,                             expectedAllFailures ),
                Arguments.of( failure1,           failure2,           functionFailureThrowsAnException,   null,                               null,                             expectedFromMapperFailureThrowsAnException )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("apTestCases")
    @DisplayName("ap: test cases")
    public <T> void ap_testCases(Try<T> t,
                                 Try<? extends T> tryParam,
                                 BiFunction<? super Throwable, ? super Throwable, ? extends Throwable> mapperFailure,
                                 BiFunction<? super T, ? super T, ? extends T> mapperSuccess,
                                 Class<? extends Exception> expectedException,
                                 Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.ap(tryParam, mapperFailure, mapperSuccess));
        }
        else {
            Try<T> result = t.ap(tryParam, mapperFailure, mapperSuccess);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> foldTestCases() {
        Try<Integer> success = Try.success(98);
        Try<Integer> failure = Try.failure(new ArrayIndexOutOfBoundsException("Array index out of bound error"));

        Function<Integer, String> integerToString = Object::toString;
        Function<Integer, Integer> functionSuccessThrowsAnException = i -> {
            throw new NumberFormatException("No way to convert Integer");
        };
        Function<Throwable, String> getErrorMessage = Throwable::getMessage;

        String expectedSuccessResult = "98";
        String expectedSuccessResultThrowingAnException = "No way to convert Integer";
        String expectedFailureResult = failure.getException().getMessage();
        return Stream.of(
                //@formatter:off
                //            try,       mapperFailure,     mapperSuccess,                      expectedException,                expectedResult
                Arguments.of( success,   null,              null,                               IllegalArgumentException.class,   null ),
                Arguments.of( success,   getErrorMessage,   null,                               IllegalArgumentException.class,   null ),
                Arguments.of( failure,   null,              null,                               IllegalArgumentException.class,   null ),
                Arguments.of( failure,   null,              integerToString,                    IllegalArgumentException.class,   null ),
                Arguments.of( success,   null,              integerToString,                    null,                             expectedSuccessResult ),
                Arguments.of( success,   getErrorMessage,   integerToString,                    null,                             expectedSuccessResult ),
                Arguments.of( success,   getErrorMessage,   functionSuccessThrowsAnException,   null,                             expectedSuccessResultThrowingAnException ),
                Arguments.of( failure,   getErrorMessage,   null,                               null,                             expectedFailureResult),
                Arguments.of( failure,   getErrorMessage,   integerToString,                    null,                             expectedFailureResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldTestCases")
    @DisplayName("fold: test cases")
    public <T, U> void fold_testCases(Try<T> t,
                                      Function<? super Throwable, ? extends U> mapperFailure,
                                      Function<? super T, ? extends U> mapperSuccess,
                                      Class<? extends Exception> expectedException,
                                      U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.fold(mapperFailure, mapperSuccess));
        }
        else {
            assertEquals(expectedResult, t.fold(mapperFailure, mapperSuccess));
        }
    }


    static Stream<Arguments> peekWithSuccessActionTestCases() {
        Try<Integer> success = Try.success(98);
        Try<Integer> failure = Try.failure(new ArrayIndexOutOfBoundsException("Array index out of bound error"));
        Consumer<Integer> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            try,       action,   expectedResult
                Arguments.of( success,   null,     success ),
                Arguments.of( success,   action,   success ),
                Arguments.of( failure,   null,     failure ),
                Arguments.of( failure,   action,   failure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekWithSuccessActionTestCases")
    @DisplayName("peek: with success action test cases")
    public <T> void peekWithValidAction_testCases(Try<T> t,
                                                  Consumer<? super T> action,
                                                  Try<T> expectedResult) {
        Try<T> result = t.peek(action);
        compareTry(expectedResult, result);
    }


    static Stream<Arguments> peekFailureTestCases() {
        Try<Integer> success = Try.success(23);
        Try<Integer> failure = Try.failure(new IndexOutOfBoundsException("Index out of bound error"));
        Consumer<Throwable> action = System.out::println;
        return Stream.of(
                //@formatter:off
                //            try,       action,   expectedResult
                Arguments.of( failure,   null,     failure ),
                Arguments.of( failure,   action,   failure ),
                Arguments.of( success,   null,     success ),
                Arguments.of( success,   action,   success )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekFailureTestCases")
    @DisplayName("peekFailure: test cases")
    public <T> void peekFailure_testCases(Try<T> t,
                                          Consumer<? super Throwable> action,
                                          Try<T> expectedResult) {
        Try<T> result = t.peekFailure(action);
        compareTry(expectedResult, result);
    }


    static Stream<Arguments> peekWithBothConsumersTestCases() {
        Try<Integer> success = Try.success(33);
        Try<Integer> failure = Try.failure(new IOException("IO error"));
        Consumer<Integer> actionSuccess = System.out::println;
        Consumer<Throwable> actionFailure = System.out::println;
        return Stream.of(
                //@formatter:off
                //            try,       actionFailure,   actionSuccess,   expectedResult
                Arguments.of( success,   actionFailure,   null,            success ),
                Arguments.of( success,   null,            actionSuccess,   success ),
                Arguments.of( success,   actionFailure,   actionSuccess,   success ),
                Arguments.of( failure,   null,            actionSuccess,   failure ),
                Arguments.of( failure,   actionFailure,   null,            failure ),
                Arguments.of( failure,   actionFailure,   actionSuccess,   failure )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("peekWithBothConsumersTestCases")
    @DisplayName("peek: with both consumers test cases")
    public <T> void peekWithBothConsumers_testCases(Try<T> t,
                                                    Consumer<? super Throwable> actionFailure,
                                                    Consumer<? super T> actionSuccess,
                                                    Try<T> expectedResult) {
        Try<T> result = t.peek(actionFailure, actionSuccess);
        compareTry(expectedResult, result);
    }


    static Stream<Arguments> getOrElseTestCases() {
        Try<Long> successEmpty = Try.success(null);
        Try<Long> successNotEmpty = Try.success(15L);
        Try<Long> failure = Try.failure(new IOException("IO error"));
        Long other = 22L;
        return Stream.of(
                //@formatter:off
                //            try,               other,   expectedResult
                Arguments.of( successEmpty,      null,    successEmpty.get() ),
                Arguments.of( successEmpty,      other,   successEmpty.get() ),
                Arguments.of( successNotEmpty,   null,    successNotEmpty.get() ),
                Arguments.of( successNotEmpty,   other,   successNotEmpty.get() ),
                Arguments.of( failure,           null,    null ),
                Arguments.of( failure,           other,   other )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseTestCases")
    @DisplayName("getOrElse: test cases")
    public <T> void getOrElse_testCases(Try<T> t,
                                        T other,
                                        T expectedResult) {
        assertEquals(expectedResult, t.getOrElse(other));
    }


    static Stream<Arguments> orElseWithTryTestCases() {
        Try<Integer> successEmpty = Try.success(null);
        Try<Integer> successNotEmpty1 = Try.success(14);
        Try<Integer> successNotEmpty2 = Try.success(27);
        Try<Integer> failure = Try.failure(new IOException("IO error"));
        return Stream.of(
                //@formatter:off
                //            try,                other,              expectedResult
                Arguments.of( successEmpty,       null,               successEmpty ),
                Arguments.of( successEmpty,       successNotEmpty1,   successEmpty ),
                Arguments.of( successNotEmpty1,   null,               successNotEmpty1 ),
                Arguments.of( successNotEmpty1,   successNotEmpty2,   successNotEmpty1 ),
                Arguments.of( failure,            null,               null ),
                Arguments.of( failure,            successNotEmpty1,   successNotEmpty1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithTryTestCases")
    @DisplayName("orElse: with Try as parameter test cases")
    public <T> void orElseWithTry_testCases(Try<T> t,
                                            Try<? extends T> other,
                                            Try<T> expectedResult) {
        Try<T> result = t.orElse(other);
        if (null != expectedResult) {
            compareTry(expectedResult, result);
        }
        else {
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> orElseWithSupplierTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("Expected result");
        Try<String> failure = Try.failure(new FileNotFoundException("File not found error"));

        Supplier<Try<String>> supplierSuccess = () -> Try.success("No error found");
        Supplier<Try<String>> supplierThrowsException = () -> {
            throw new IllegalArgumentException("Illegal argument error");
        };

        Try<String> expectedSuccessSupplierResult = Try.success("No error found");
        Try<String> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new IllegalArgumentException("Illegal argument error")
        );
        return Stream.of(
                //@formatter:off
                //            try,               supplier,                  expectedException,                expectedResult
                Arguments.of( successEmpty,      null,                      null,                             successEmpty ),
                Arguments.of( successEmpty,      supplierSuccess,           null,                             successEmpty ),
                Arguments.of( successNotEmpty,   null,                      null,                             successNotEmpty ),
                Arguments.of( successNotEmpty,   supplierSuccess,           null,                             successNotEmpty ),
                Arguments.of( failure,           null,                      IllegalArgumentException.class,   null ),
                Arguments.of( failure,           supplierSuccess,           null,                             expectedSuccessSupplierResult ),
                Arguments.of( failure,           supplierThrowsException,   null,                             expectedFromMapperSuccessThrowsAnException )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orElseWithSupplierTestCases")
    @DisplayName("orElse: with Supplier as parameter test cases")
    public <T> void orElseWithSupplier_testCases(Try<T> t,
                                                 Supplier<Try<? extends T>> supplier,
                                                 Class<? extends Exception> expectedException,
                                                 Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.orElse(supplier));
        }
        else {
            Try<T> result = t.orElse(supplier);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> recoverTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("Expected result");
        Try<String> failure = Try.failure(new FileNotFoundException("File not found error"));

        Function<Throwable, String> mapperFailure = Throwable::getMessage;
        Function<Throwable, String> mapperThrowsException = t -> {
            throw new IllegalArgumentException("Illegal argument error");
        };

        Try<String> expectedSuccessSupplierResult = Try.success("File not found error");
        Try<String> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new IllegalArgumentException("Illegal argument error")
        );
        return Stream.of(
                //@formatter:off
                //            try,               mapperFailure,           expectedException,                expectedResult
                Arguments.of( successEmpty,      null,                    null,                             successEmpty ),
                Arguments.of( successEmpty,      mapperFailure,           null,                             successEmpty ),
                Arguments.of( successNotEmpty,   null,                    null,                             successNotEmpty ),
                Arguments.of( successNotEmpty,   mapperFailure,           null,                             successNotEmpty ),
                Arguments.of( failure,           null,                    IllegalArgumentException.class,   null ),
                Arguments.of( failure,           mapperFailure,           null,                             expectedSuccessSupplierResult ),
                Arguments.of( failure,           mapperThrowsException,   null,                             expectedFromMapperSuccessThrowsAnException )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("recoverTestCases")
    @DisplayName("recover: test cases")
    public <T> void recover_testCases(Try<T> t,
                                      Function<? super Throwable, ? extends T> mapperFailure,
                                      Class<? extends Exception> expectedException,
                                      Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.recover(mapperFailure));
        }
        else {
            Try<T> result = t.recover(mapperFailure);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> recoverWithTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("Expected result");
        Try<String> failure = Try.failure(new FileNotFoundException("File not found error"));

        Function<Throwable, Try<String>> mapperFailure = thr -> Try.success(thr.getMessage());
        Function<Throwable, Try<String>> mapperThrowsException = t -> {
            throw new IllegalArgumentException("Illegal argument error");
        };

        Try<String> expectedSuccessSupplierResult = Try.success("File not found error");
        Try<String> expectedFromMapperSuccessThrowsAnException = Try.failure(
                new IllegalArgumentException("Illegal argument error")
        );
        return Stream.of(
                //@formatter:off
                //            try,               mapperFailure,           expectedException,                expectedResult
                Arguments.of( successEmpty,      null,                    null,                             successEmpty ),
                Arguments.of( successEmpty,      mapperFailure,           null,                             successEmpty ),
                Arguments.of( successNotEmpty,   null,                    null,                             successNotEmpty ),
                Arguments.of( successNotEmpty,   mapperFailure,           null,                             successNotEmpty ),
                Arguments.of( failure,           null,                    IllegalArgumentException.class,   null ),
                Arguments.of( failure,           mapperFailure,           null,                             expectedSuccessSupplierResult ),
                Arguments.of( failure,           mapperThrowsException,   null,                             expectedFromMapperSuccessThrowsAnException )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("recoverWithTestCases")
    @DisplayName("recoverWith: test cases")
    public <T> void recoverWith_testCases(Try<T> t,
                                          Function<? super Throwable, ? extends Try<? extends T>> mapperFailure,
                                          Class<? extends Exception> expectedException,
                                          Try<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.recoverWith(mapperFailure));
        }
        else {
            Try<T> result = t.recoverWith(mapperFailure);
            compareTry(expectedResult, result);
        }
    }


    static Stream<Arguments> isEmptyTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("All work as expected");
        Try<String> failure = Try.failure(new ArrayIndexOutOfBoundsException());
        return Stream.of(
                //@formatter:off
                //            try,               expectedResult
                Arguments.of( successEmpty,      true ),
                Arguments.of( successNotEmpty,   false ),
                Arguments.of( failure,           true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isEmptyTestCases")
    @DisplayName("isEmpty: test cases")
    public <T> void isEmpty_testCases(Try<T> t,
                                      boolean expectedResult) {
        assertEquals(expectedResult, t.isEmpty());
    }


    static Stream<Arguments> toOptionalTestCases() {
        Try<String> successEmpty = Try.success(null);
        Try<String> successNotEmpty = Try.success("All work as expected");
        Try<String> failure = Try.failure(new ArrayIndexOutOfBoundsException());
        return Stream.of(
                //@formatter:off
                //            try,               expectedResult
                Arguments.of( successEmpty,      empty() ),
                Arguments.of( successNotEmpty,   of(successNotEmpty.get()) ),
                Arguments.of( failure,           empty() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toOptionalTestCases")
    @DisplayName("toOptional: test cases")
    public <T> void toOptional_testCases(Try<T> t,
                                         Optional<T> expectedResult) {
        assertEquals(expectedResult, t.toOptional());
    }


    static Stream<Arguments> toEitherTestCases() {
        Try<Long> successEmpty = Try.success(null);
        Try<Long> successNotEmpty = Try.success(9L);

        IOException exception = new IOException();
        Try<Long> failure = Try.failure(exception);
        return Stream.of(
                //@formatter:off
                //            try,               expectedResult
                Arguments.of( successEmpty,      Either.right(null) ),
                Arguments.of( successNotEmpty,   Either.<Throwable, Long>right(successNotEmpty.get()) ),
                Arguments.of( failure,           Either.<Throwable, Long>left(failure.getException()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toEitherTestCases")
    @DisplayName("toEither: test cases")
    public <T> void toEither_testCases(Try<T> t,
                                       Either<Throwable, T> expectedResult) {
        assertEquals(expectedResult, t.toEither());
    }


    static Stream<Arguments> toValidationTestCases() {
        Try<Integer> successEmpty = Try.success(null);
        Try<Integer> successNotEmpty = Try.success(31);

        IllegalArgumentException exception = new IllegalArgumentException();
        Try<Integer> failure = Try.failure(exception);

        Validation<Throwable, Integer> validFromEmptyTry = Validation.valid(null);
        Validation<Throwable, Integer> validFromTry = Validation.valid(31);
        Validation<Throwable, Integer> invalidFromTry = Validation.invalid(List.of(exception));
        return Stream.of(
                //@formatter:off
                //            try,               expectedResult
                Arguments.of( successEmpty,      validFromEmptyTry ),
                Arguments.of( successNotEmpty,   validFromTry ),
                Arguments.of( failure,           invalidFromTry )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toValidationTestCases")
    @DisplayName("toValidation: test cases")
    public <T> void toValidation_testCases(Try<T> t,
                                           Validation<Throwable, T> expectedResult) {
        assertEquals(expectedResult, t.toValidation());
    }


    private <T> void compareTry(Try<T> expectedResult,
                                Try<T> result) {
        if (!expectedResult.isSuccess()) {
            compareFailureTry(result, expectedResult);
        }
        else {
            assertEquals(expectedResult, result);
        }
    }


    private <T> void compareFailureTry(Try<T> expectedResult,
                                       Try<T> result) {
        assertFalse(result.isSuccess());
        assertEquals(expectedResult.isSuccess(), result.isSuccess());
        assertEquals(expectedResult.getException().getClass(), result.getException().getClass());
        assertEquals(expectedResult.getException().getMessage(), result.getException().getMessage());
    }

}
