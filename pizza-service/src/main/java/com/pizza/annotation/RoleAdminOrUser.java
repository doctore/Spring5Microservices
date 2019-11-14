package com.pizza.annotation;

import com.pizza.configuration.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
@PreAuthorize("hasAnyAuthority('" + Constants.ROLE_ADMIN +"','" + Constants.ROLE_USER + "')")
public @interface RoleAdminOrUser {
}
