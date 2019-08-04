package com.pizza.configuration.validator.annotation;

import com.pizza.configuration.validator.HasEnumInternalStringValueValidator;
import com.pizza.enums.IEnumInDatabase;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *    The annotated element must be included in an internal {@link String} property of the given accepted
 * {@link Class} of {@link Enum}.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = HasEnumInternalStringValueValidator.class)
public @interface HasEnumInternalStringValue {
	
	String message() default "must be one of the values included in the enum";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	
	/**
	 * @return {@link Class} of {@link Enum} used to check the value
	 */
	Class<? extends Enum<? extends IEnumInDatabase>> enumClass();
	
	/**
	 * @return {@code true} if {@code null} is accepted as a valid value, {@code false} otherwise. 
	 */
	boolean isNullAccepted() default false;

}
