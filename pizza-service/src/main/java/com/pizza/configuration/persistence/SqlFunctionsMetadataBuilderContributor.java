package com.pizza.configuration.persistence;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

/**
 * Include custom functions we want to use in HQL queries
 */
public class SqlFunctionsMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {

        /**
         * To work with QueryDSL datetime operations
         */
        metadataBuilder.applySqlFunction("add_years", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 year' * ?2"));
        metadataBuilder.applySqlFunction("add_months", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 month' * ?2"));
        metadataBuilder.applySqlFunction("add_days", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 day' * ?2"));
        metadataBuilder.applySqlFunction("add_hours", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 hour' * ?2"));
        metadataBuilder.applySqlFunction("add_minutes", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 minute' * ?2"));
        metadataBuilder.applySqlFunction("add_seconds", new SQLFunctionTemplate(StandardBasicTypes.DATE, "?1 + interval '1 second' * ?2"));
    }

}
