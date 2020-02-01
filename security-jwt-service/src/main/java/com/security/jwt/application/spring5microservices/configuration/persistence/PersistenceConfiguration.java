package com.security.jwt.application.spring5microservices.configuration.persistence;

import com.security.jwt.application.spring5microservices.configuration.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(value = Constants.APPLICATION_NAME + "PersistenceConfiguration")
@EnableJpaRepositories(
        basePackages = Constants.PATH.REPOSITORY,
        entityManagerFactoryRef = PersistenceConfiguration.ENTITY_MANAGER_FACTORY,
        transactionManagerRef= PersistenceConfiguration.TRANSACTION_MANAGER)
public class PersistenceConfiguration {

    public static final String DATASOURCE = Constants.APPLICATION_NAME + "DataSource";
    public static final String DATASOURCE_PROPERTIES = Constants.APPLICATION_NAME + "DataSourceProperties";
    public static final String ENTITY_MANAGER_FACTORY = Constants.APPLICATION_NAME + "EntityManagerFactory";
    public static final String TRANSACTION_MANAGER = Constants.APPLICATION_NAME + "TransactionManager";

    @Bean(name = DATASOURCE_PROPERTIES)
    @ConfigurationProperties(Constants.DATABASE.DATASOURCE_CONFIGURATION)
    public DataSourceProperties spring5MicroserviceDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = DATASOURCE)
    public DataSource spring5MicroserviceDataSource() {
        return spring5MicroserviceDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }


    @Bean(name = ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean spring5MicroserviceEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(spring5MicroserviceDataSource())
                .packages(Constants.PATH.MODEL)
                .persistenceUnit(Constants.APPLICATION_NAME)
                .build();
    }


    @Bean(name = TRANSACTION_MANAGER)
    public PlatformTransactionManager spring5MicroserviceTransactionManager(
            final @Qualifier(ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

}
