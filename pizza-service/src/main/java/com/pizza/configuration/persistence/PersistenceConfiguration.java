package com.pizza.configuration.persistence;

import com.pizza.configuration.Constants;
import com.pizza.repository.impl.ExtendedJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Custom {@link Configuration} class to add specific configuration options related with the persistence.
 */
@Configuration
@EnableJpaRepositories(basePackages = Constants.REPOSITORY_PATH, repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
public class PersistenceConfiguration { }
