package com.spring5microservices.common.validator.enums;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *    The annotated element must be included in an internal {@link String} property of the given accepted
 * {@link Class} of {@link Enum}.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = EnumHasInternalStringValueValidator.class)
public @interface EnumHasInternalStringValue {

	String message() default "must be one of the values included in {values}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return {@link Class} of {@link Enum} used to check the value
	 */
	Class<? extends Enum<? extends IEnumInternalPropertyValue<String>>> enumClass();

	/**
	 * @return {@code true} if {@code null} is accepted as a valid value, {@code false} otherwise.
	 */
	boolean isNullAccepted() default false;

}
