package com.spring5microservices.common.util.validator.enums;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 *    Validates if the given {@link String} matches with one of the internal {@link String} property belonging to the
 * provided {@link Class} of {@link Enum}
 */
public class EnumHasInternalStringValueValidator implements ConstraintValidator<EnumHasInternalStringValue, String> {

	private static final String ERROR_MESSAGE_PARAMETER = "values";

	private List<String> enumValidValues;
	private String constraintTemplate;
	private boolean isNullAccepted;


	@Override
	@SuppressWarnings("unchecked")
    public void initialize(final EnumHasInternalStringValue hasInternalStringValue) {
		enumValidValues = Arrays.stream(hasInternalStringValue.enumClass().getEnumConstants())
				                .map(e -> ((IEnumInternalPropertyValue<String>)e).getInternalPropertyValue())
				                .collect(Collectors.toList());
		constraintTemplate = hasInternalStringValue.message();
		isNullAccepted = hasInternalStringValue.isNullAccepted();
    }


	@Override
	public boolean isValid(final String value,
						   final ConstraintValidatorContext context) {
		boolean isValid =
				isNull(value)
						? isNullAccepted
						: enumValidValues.contains(value);

		if (!isValid) {
			HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
			hibernateContext.disableDefaultConstraintViolation();
			hibernateContext.addMessageParameter(
					        ERROR_MESSAGE_PARAMETER,
							enumValidValues
					)
					.buildConstraintViolationWithTemplate(constraintTemplate)
					.addConstraintViolation();
		}
		return isValid;
	}

}
