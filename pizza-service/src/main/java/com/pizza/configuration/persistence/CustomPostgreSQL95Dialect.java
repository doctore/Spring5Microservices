package com.pizza.configuration.persistence;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * Include custom functions we want to use for HQL queries
 */
public class CustomPostgreSQL95Dialect extends PostgreSQL95Dialect {

    public CustomPostgreSQL95Dialect() {
        super();
        registerFunction("trim_custom", new StandardSQLFunction("trim"));
    }

}
