package com.pizza.annotation;

import com.pizza.configuration.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@PreAuthorize("hasAuthority('" + Constants.ROLE_ADMIN +"')")
public @interface RoleAdmin {
}
