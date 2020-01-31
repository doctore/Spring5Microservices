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
        entityManagerFactoryRef = Constants.APPLICATION_NAME + "EntityManagerFactory",
        transactionManagerRef= Constants.APPLICATION_NAME + "TransactionManager")
public class PersistenceConfiguration {

    @Bean(name = Constants.APPLICATION_NAME + "DataSourceProperties")
    @ConfigurationProperties(Constants.DATABASE.DATASOURCE_CONFIGURATION)
    public DataSourceProperties spring5MicroserviceDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name = Constants.APPLICATION_NAME + "DataSource")
    public DataSource spring5MicroserviceDataSource() {
        return spring5MicroserviceDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }


    @Bean(name = Constants.APPLICATION_NAME + "EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean spring5MicroserviceEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(spring5MicroserviceDataSource())
                .packages(Constants.PATH.MODEL)
                .persistenceUnit(Constants.APPLICATION_NAME)
                .build();
    }


    @Bean(name = Constants.APPLICATION_NAME + "TransactionManager")
    public PlatformTransactionManager spring5MicroserviceTransactionManager(
            final @Qualifier(Constants.APPLICATION_NAME + "EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }



    /*
    @Bean
    @ConfigurationProperties("app.datasource.card")
    public DataSourceProperties cardDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.card.configuration")
    public DataSource cardDataSource() {
        return cardDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }
     */

}
