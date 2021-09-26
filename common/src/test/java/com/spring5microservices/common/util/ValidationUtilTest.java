package com.spring5microservices.common.util;

import com.spring5microservices.common.validation.Valid;
import com.spring5microservices.common.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationUtilTest {

    static Stream<Arguments> combineTestCases() {
        Validation<Integer> validInt1 = Validation.valid(1);
        Validation<Integer> validInt4 = Validation.valid(4);
        Validation<Integer> invalidProb1 = Validation.invalid(asList("problem1"));
        Validation<Integer> invalidProb2 = Validation.invalid(asList("problem2"));
        Validation<Integer> allValidationsArray[] = new Validation[] { validInt1, invalidProb1, validInt4, invalidProb2 };

        List<String> allErrors = new ArrayList<>(invalidProb1.getErrors());
        allErrors.addAll(invalidProb2.getErrors());
        Validation<Integer> invalidAll = Validation.invalid(allErrors);
        return Stream.of(
                //@formatter:off
                //            validations,                                               expectedResult
                Arguments.of( null,                                                      Valid.empty() ),
                Arguments.of( new Validation[] {},                                       Valid.empty() ),
                Arguments.of( new Validation[] { validInt1 },                            validInt1 ),
                Arguments.of( new Validation[] { validInt1, validInt4 },                 validInt4 ),
                Arguments.of( new Validation[] { invalidProb1, validInt1, validInt4 },   invalidProb1 ),
                Arguments.of( new Validation[] { validInt1, validInt4, invalidProb1 },   invalidProb1 ),
                Arguments.of( new Validation[] { invalidProb1, invalidProb2 },           invalidAll ),
                Arguments.of( allValidationsArray,                                       invalidAll )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("combineTestCases")
    @DisplayName("combine: test cases")
    public <T> void combine_testCases(Validation<? super T> validations[],
                                      Validation<T> expectedResult) {
        assertEquals(expectedResult, ValidationUtil.combine(validations));
    }


    static Stream<Arguments> getFirstInvalidTestCases() {
        Validation<Integer> validInt1 = Validation.valid(1);
        Validation<Integer> validInt4 = Validation.valid(4);
        Validation<Integer> invalidProb1 = Validation.invalid(asList("problem1"));
        Validation<Integer> invalidProb2 = Validation.invalid(asList("problem2"));

        Supplier<Validation<Integer>> supValidInt1 = () -> validInt1;
        Supplier<Validation<Integer>> supValidInt4 = () -> validInt4;
        Supplier<Validation<Integer>> supInvalidProb1 = () -> invalidProb1;
        Supplier<Validation<Integer>> supInvalidProb2 = () -> invalidProb2;
        return Stream.of(
                //@formatter:off
                //            supplier1,         supplier2,         supplier3,         expectedResult
                Arguments.of( null,              null,              null,              Valid.empty() ),
                Arguments.of( supValidInt1,      null,              null,              validInt1 ),
                Arguments.of( supValidInt1,      supValidInt4,      null,              validInt4 ),
                Arguments.of( supInvalidProb1,   supInvalidProb2,   null,              invalidProb1 ),
                Arguments.of( supInvalidProb1,   supValidInt1,      supValidInt4,      invalidProb1 ),
                Arguments.of( supValidInt1,      supValidInt4,      supInvalidProb1,   invalidProb1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getFirstInvalidTestCases")
    @DisplayName("getFirstInvalid: test cases")
    public <T> void getFirstInvalid_testCases(Supplier<Validation<? super T>> supplier1,
                                              Supplier<Validation<? super T>> supplier2,
                                              Supplier<Validation<? super T>> supplier3,
                                              Validation<T> expectedResult) {
        Validation<T> result = null;
        if (Objects.isNull(supplier1) && Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
            result = ValidationUtil.getFirstInvalid();
        }
        else if (Objects.isNull(supplier2) && Objects.isNull(supplier3)) {
            result = ValidationUtil.getFirstInvalid(supplier1);
        }
        else if (Objects.isNull(supplier3)) {
            result = ValidationUtil.getFirstInvalid(supplier1, supplier2);
        }
        else {
            result = ValidationUtil.getFirstInvalid(supplier1, supplier2, supplier3);
        }
        assertEquals(expectedResult, result);
    }

}
