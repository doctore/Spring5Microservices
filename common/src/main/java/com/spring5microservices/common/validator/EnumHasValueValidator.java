package com.spring5microservices.common.validator;

import com.spring5microservices.common.validator.annotation.EnumHasValue;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *    Validates if the given {@link String} matches with one of the values belonging to the
 * provided {@link Class} of {@link Enum}
 */
public class EnumHasValueValidator implements ConstraintValidator<EnumHasValue, String> {

	private static final String ERROR_MESSAGE_PARAMETER = "values";

	List<String> enumValidValues;
	String constraintTemplate;
	private boolean isNullAccepted;

	@Override
    public void initialize(final EnumHasValue hasValue) {
		enumValidValues = Arrays.stream(hasValue.enumClass().getEnumConstants())
				                .map(Enum::name)
				                .collect(Collectors.toList());
		constraintTemplate = hasValue.message();
		isNullAccepted = hasValue.isNullAccepted();
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
