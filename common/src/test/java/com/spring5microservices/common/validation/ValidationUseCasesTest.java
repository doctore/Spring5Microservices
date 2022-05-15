package com.spring5microservices.common.validation;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationUseCasesTest {

    static Stream<Arguments> validateWithCombineTestCases() {
        PizzaDto validPizza = new PizzaDto("Carbonara", 15d);
        PizzaDto invalidPizzaName = new PizzaDto("12#2", 11d);
        PizzaDto invalidPizzaCost = new PizzaDto("Margherita", -5d);
        PizzaDto invalidPizza = new PizzaDto("564", -2d);

        ValidationError validationError1 = ValidationError.of(1, "Name contains invalid characters: '12#2'");
        ValidationError validationError2 = ValidationError.of(2, "Cost must be at least 0");
        ValidationError validationError3 = ValidationError.of(1, "Name contains invalid characters: '564'");
        ValidationError validationError4 = ValidationError.of(2, "Cost must be at least 0");

        Validation<ValidationError, PizzaDto> validValidation = Validation.valid(validPizza);
        Validation<ValidationError, PizzaDto> invalidPizzaNameValidation = Validation.invalid(asList(validationError1));
        Validation<ValidationError, PizzaDto> invalidPizzaCostValidation = Validation.invalid(asList(validationError2));
        Validation<ValidationError, PizzaDto> invalidPizzaValidation = Validation.invalid(asList(validationError3, validationError4));
        return Stream.of(
                //@formatter:off
                //            objectToVerifyInstance,   expectedResult
                Arguments.of( validPizza,               validValidation ),
                Arguments.of( invalidPizzaName,         invalidPizzaNameValidation ),
                Arguments.of( invalidPizzaCost,         invalidPizzaCostValidation ),
                Arguments.of( invalidPizza,             invalidPizzaValidation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("validateWithCombineTestCases")
    @DisplayName("validate: with combine test cases")
    public <E, T> void validateWithCombine_testCases(PizzaDto objectToVerifyInstance,
                                                     Validation<E, T> expectedResult) {
        PizzaDtoValidatorCombine validator = new PizzaDtoValidatorCombine();
        assertEquals(expectedResult, validator.validate(objectToVerifyInstance));
    }


    static Stream<Arguments> validateWithGetFirstInvalidTestCases() {
        PizzaDto validPizza = new PizzaDto("Carbonara", 15d);
        PizzaDto invalidPizzaName = new PizzaDto("12#2", 11d);
        PizzaDto invalidPizzaCost = new PizzaDto("Margherita", -5d);
        PizzaDto invalidPizza = new PizzaDto("564", -2d);

        ValidationError validationError1 = ValidationError.of(1, "Name contains invalid characters: '12#2'");
        ValidationError validationError2 = ValidationError.of(2, "Cost must be at least 0");
        ValidationError validationError3 = ValidationError.of(1, "Name contains invalid characters: '564'");

        Validation<ValidationError, PizzaDto> validValidation = Validation.valid(validPizza);
        Validation<ValidationError, PizzaDto> invalidPizzaNameValidation = Validation.invalid(asList(validationError1));
        Validation<ValidationError, PizzaDto> invalidPizzaCostValidation = Validation.invalid(asList(validationError2));
        Validation<ValidationError, PizzaDto> invalidPizzaValidation = Validation.invalid(asList(validationError3));
        return Stream.of(
                //@formatter:off
                //            objectToVerifyInstance,   expectedResult
                Arguments.of( validPizza,               validValidation ),
                Arguments.of( invalidPizzaName,         invalidPizzaNameValidation ),
                Arguments.of( invalidPizzaCost,         invalidPizzaCostValidation ),
                Arguments.of( invalidPizza,             invalidPizzaValidation )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("validateWithGetFirstInvalidTestCases")
    @DisplayName("validate: with getFirstInvalid test cases")
    public <E, T> void validateWithGetFirstInvalid_testCases(PizzaDto objectToVerifyInstance,
                                                             Validation<E, T> expectedResult) {
        PizzaDtoValidatorGetFirstInvalid validator = new PizzaDtoValidatorGetFirstInvalid();
        assertEquals(expectedResult, validator.validate(objectToVerifyInstance));
    }

}


class PizzaDtoValidatorCombine implements Validate<PizzaDto> {

    private static final String VALID_NAME_CHARS = "[a-zA-Z ]";
    private static final int MIN_COST = 0;

    @Override
    public Validation<ValidationError, PizzaDto> validate(PizzaDto p) {
        return ValidationUtil.combine(validateName(p), validateCost(p));
    }

    private Validation<ValidationError, PizzaDto> validateName(PizzaDto p) {
        final String onlyValidCharacters = p.getName().replaceAll(VALID_NAME_CHARS, "");
        return onlyValidCharacters.isEmpty()
                ? Validation.valid(p)
                : Validation.invalid(
                        asList(
                                ValidationError.of(1, "Name contains invalid characters: '" + p.getName() + "'")
                        )
        );
    }

    private Validation<ValidationError, PizzaDto> validateCost(PizzaDto p) {
        return p.getCost() >= MIN_COST
                ? Validation.valid(p)
                : Validation.invalid(
                        asList(
                                ValidationError.of(2,"Cost must be at least " + MIN_COST)
                        )
        );
    }

}


class PizzaDtoValidatorGetFirstInvalid implements Validate<PizzaDto> {

    private static final String VALID_NAME_CHARS = "[a-zA-Z ]";
    private static final int MIN_COST = 0;


    @Override
    public Validation<ValidationError, PizzaDto> validate(PizzaDto p) {
        return ValidationUtil.getFirstInvalid(() -> validateName(p), () -> validateCost(p));
    }

    private Validation<ValidationError, PizzaDto> validateName(PizzaDto p) {
        final String onlyValidCharacters = p.getName().replaceAll(VALID_NAME_CHARS, "");
        return onlyValidCharacters.isEmpty()
                ? Validation.valid(p)
                : Validation.invalid(
                        asList(
                                ValidationError.of(1, "Name contains invalid characters: '" + p.getName() + "'")
                        )
        );
    }

    private Validation<ValidationError, PizzaDto> validateCost(PizzaDto p) {
        return p.getCost() >= MIN_COST
                ? Validation.valid(p)
                : Validation.invalid(
                        asList(
                                ValidationError.of(2,"Cost must be at least " + MIN_COST)
                        )
        );
    }

}
