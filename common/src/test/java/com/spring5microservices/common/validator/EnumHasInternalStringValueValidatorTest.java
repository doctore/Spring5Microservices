package com.spring5microservices.common.validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.spring5microservices.common.interfaces.IEnumInternalPropertyValue;
import com.spring5microservices.common.validator.annotation.EnumHasInternalStringValue;
import lombok.Builder;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumHasInternalStringValueValidatorTest {

    private Validator validator;

    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
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
