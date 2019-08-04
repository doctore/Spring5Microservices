package com.pizza.configuration.validator;

import com.pizza.configuration.validator.annotation.HasEnumInternalStringValue;
import com.pizza.enums.IEnumInDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *    Validates if the given {@link String} matches with one of the internal {@link String} property belonging to the
 * provided {@link Class} of {@link Enum}
 */
public class HasEnumInternalStringValueValidator implements ConstraintValidator<HasEnumInternalStringValue, String> {
	
	List<String> enumNames;
	private boolean isNullAccepted;
	
	@Override
    public void initialize(final HasEnumInternalStringValue hasInternalStringValue) {
		enumNames = Arrays.stream(hasInternalStringValue.enumClass().getEnumConstants())
				          .map(e -> ((IEnumInDatabase<String>)e).getDatabaseValue())
				          .collect(Collectors.toList());
		
		isNullAccepted = hasInternalStringValue.isNullAccepted();
    }
	

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (null == value)
			return isNullAccepted;
		
		return enumNames.contains(value);
	}

}
