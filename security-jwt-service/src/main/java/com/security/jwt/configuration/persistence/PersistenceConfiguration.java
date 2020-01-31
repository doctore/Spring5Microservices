package com.security.jwt.configuration.persistence;

import com.security.jwt.configuration.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(value = Constants.APPLICATION_NAME + "PersistenceConfiguration")
@EnableJpaRepositories(
        basePackages = Constants.PATH.REPOSITORY,
        entityManagerFactoryRef = Constants.APPLICATION_NAME + "EntityManagerFactory",
        transactionManagerRef= Constants.APPLICATION_NAME + "TransactionManager")
public class PersistenceConfiguration {

    @Primary
    @Bean(name = Constants.APPLICATION_NAME + "DataSourceProperties")
    @ConfigurationProperties(Constants.DATABASE.DATASOURCE_CONFIGURATION)
    public DataSourceProperties securityJwtDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean(name = Constants.APPLICATION_NAME + "DataSource")
    public DataSource securityJwtDataSource() {
        return securityJwtDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }


    @Primary
    @Bean(name = Constants.APPLICATION_NAME + "EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean securityJwtEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(securityJwtDataSource())
                .packages(Constants.PATH.MODEL)
                .persistenceUnit(Constants.APPLICATION_NAME)
                .build();
    }


    @Primary
    @Bean(name = Constants.APPLICATION_NAME + "TransactionManager")
    public PlatformTransactionManager securityJwtTransactionManager(
            final @Qualifier(Constants.APPLICATION_NAME + "EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

}
