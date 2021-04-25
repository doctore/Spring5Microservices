package com.pizza.configuration.persistence;

import com.pizza.configuration.Constants;
import com.pizza.repository.base.ExtendedQueryDslJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Custom {@link Configuration} class to add specific configuration options related with the persistence.
 */
@Configuration
@EnableJpaRepositories(basePackages = Constants.PATH.REPOSITORY, repositoryBaseClass = ExtendedQueryDslJpaRepositoryImpl.class)
public class PersistenceConfiguration { }
