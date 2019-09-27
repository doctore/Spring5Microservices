package com.spring5microservices.common.validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.spring5microservices.common.interfaces.IEnumInternalPropertyValue;
import com.spring5microservices.common.validator.annotation.EnumHasInternalStringValue;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class EnumHasInternalStringValueValidatorTest {

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
        PizzaDto dto = PizzaDto.builder().name(PizzaEnum.CARBONARA.getInternalPropertyValue() + PizzaEnum.MARGUERITA.getInternalPropertyValue()).build();

        // When
        Set<ConstraintViolation<PizzaDto>> violations = validator.validate(dto);

        // Then
        assertEquals(1, violations.size());

        ConstraintViolation<PizzaDto> error = violations.iterator().next();
        assertEquals("name", error.getPropertyPath().toString());
        assertEquals("must be one of the values included in [Margherita, Carbonara]", error.getMessage());
    }


    @Test
    @DisplayName("isValid: when given string value is in enum then validation Succeeds")
    public void whenGivenStringValueIsInEnum_thenValidationSucceeds() {
        // Given
        PizzaDto dto = PizzaDto.builder().name(PizzaEnum.CARBONARA.getInternalPropertyValue()).build();

        // When
        Set<ConstraintViolation<PizzaDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

}


enum PizzaEnum implements IEnumInternalPropertyValue<String> {
    MARGUERITA("Margherita"),
    CARBONARA("Carbonara");

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    @Override
    public String getInternalPropertyValue() {
        return this.databaseValue;
    }
}

@Builder
@Data
class PizzaDto {
    @EnumHasInternalStringValue(enumClass=PizzaEnum.class)
    private String name;

    private Double cost;
}
