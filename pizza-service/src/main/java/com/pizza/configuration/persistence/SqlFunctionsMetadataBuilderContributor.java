package com.pizza.configuration.persistence;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * Include custom functions we want to use in HQL queries
 */
public class SqlFunctionsMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction("trim_custom", new StandardSQLFunction("trim", StandardBasicTypes.STRING));
    }

}
