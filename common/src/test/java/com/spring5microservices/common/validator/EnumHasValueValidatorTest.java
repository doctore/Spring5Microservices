package com.spring5microservices.common.validator;

import com.spring5microservices.common.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.spring5microservices.common.IngredientEnum.HAM;
import static com.spring5microservices.common.IngredientEnum.ONION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class EnumHasValueValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("isValid: when given string value is not in enum then validation fails")
    public void whenGivenStringValueIsNotInEnum_thenValidationFails() {
        // Given
        IngredientDto dto = new IngredientDto(1, HAM.name() + ONION.name());

        // When
        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());

        ConstraintViolation<IngredientDto> error = violations.iterator().next();
        assertEquals("name", error.getPropertyPath().toString());
        assertEquals("must be one of the values included in [CHEESE, HAM, ONION]", error.getMessage());
    }


    @Test
    @DisplayName("isValid: when given string value is in enum then validation Succeeds")
    public void whenGivenStringValueIsInEnum_thenValidationSucceeds() {
        // Given
        IngredientDto dto = new IngredientDto(1, HAM.name());

        // When
        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

}
