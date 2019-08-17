package com.pizza.configuration.validator;

import com.pizza.configuration.validator.annotation.HasEnumInternalStringValue;
import com.pizza.enums.IEnumInDatabase;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

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

	private static final String ERROR_MESSAGE_PARAMETER = "values";

	List<String> enumValidValues;
	String constraintTemplate;
	private boolean isNullAccepted;
	
	@Override
    public void initialize(final HasEnumInternalStringValue hasInternalStringValue) {
		enumValidValues = Arrays.stream(hasInternalStringValue.enumClass().getEnumConstants())
				                .map(e -> ((IEnumInDatabase<String>)e).getDatabaseValue())
				                .collect(Collectors.toList());
		constraintTemplate = hasInternalStringValue.message();
		isNullAccepted = hasInternalStringValue.isNullAccepted();
    }
	

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid;
		if (null == value)
			isValid = isNullAccepted;
		else
			isValid = enumValidValues.contains(value);

		if (!isValid) {
			HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
			hibernateContext.disableDefaultConstraintViolation();
			hibernateContext.addMessageParameter(ERROR_MESSAGE_PARAMETER, enumValidValues)
					        .buildConstraintViolationWithTemplate(constraintTemplate)
					        .addConstraintViolation();
		}
		return isValid;
	}

}
