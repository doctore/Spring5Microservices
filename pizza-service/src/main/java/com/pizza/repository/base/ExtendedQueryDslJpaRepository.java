package com.pizza.repository.base;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Extended {@link ExtendedJpaRepository} to include QueryDSL functionality we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@NoRepositoryBean
public interface ExtendedQueryDslJpaRepository<T, ID extends Serializable> extends ExtendedJpaRepository<T, ID> {

    /**
     * Used to get {@link JPAQuery} instance and create custom JPA queries.
     *
     * @return {@link JPAQuery}
     */
    JPAQuery<T> getJPAQuery();

    /**
     * Used to get {@link JPASQLQuery} instance and create custom JPA native queries.
     *
     * @return {@link JPASQLQuery}
     */
    JPASQLQuery<T> getJPASQLQuery();

    /**
     * Generates a {@link JPAQuery} object with a {@code select} and {@code from} using the provided {@code entityPath}.
     *
     * @param entityPath
     *    {@link EntityPath} used in {@code select} and {@code from} clauses.
     *
     * @return @return {@link JPAQuery}
     */
    JPAQuery<T> selectFrom(EntityPath<T> entityPath);

    /**
     * Generates a {@link JPASQLQuery} object with a {@code select} and {@code from} using the provided {@code entityPath}.
     *
     * @param entityPath
     *    {@link EntityPath} used in {@code select} and {@code from} clauses.
     *
     * @return @return {@link JPASQLQuery}
     */
    JPASQLQuery<T> nativeSelectFrom(EntityPath<T> entityPath);

}