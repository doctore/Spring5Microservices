package com.spring5microservices.common.util.validator.enums;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *    Validates if the given {@link String} matches with one of the internal {@link String} property belonging to the
 * provided {@link Class} of {@link Enum}
 */
public class EnumHasInternalStringValueValidator implements ConstraintValidator<EnumHasInternalStringValue, String> {

	private static final String ERROR_MESSAGE_PARAMETER = "values";

	List<String> enumValidValues;
	String constraintTemplate;
	private boolean isNullAccepted;


	@Override
    public void initialize(final EnumHasInternalStringValue hasInternalStringValue) {
		enumValidValues = Arrays.stream(hasInternalStringValue.enumClass().getEnumConstants())
				                .map(e -> ((IEnumInternalPropertyValue<String>)e).getInternalPropertyValue())
				                .collect(Collectors.toList());
		constraintTemplate = hasInternalStringValue.message();
		isNullAccepted = hasInternalStringValue.isNullAccepted();
    }


	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = null == value ? isNullAccepted
				                        : enumValidValues.contains(value);
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
