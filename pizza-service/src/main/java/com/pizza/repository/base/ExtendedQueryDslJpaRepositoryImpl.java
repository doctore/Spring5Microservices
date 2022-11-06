package com.pizza.repository.base;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLTemplates;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Extended {@link ExtendedJpaRepositoryImpl} to include QueryDSL functionality we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
public class ExtendedQueryDslJpaRepositoryImpl<T, ID extends Serializable> extends ExtendedJpaRepositoryImpl<T, ID>
        implements ExtendedQueryDslJpaRepository<T, ID> {

    public ExtendedQueryDslJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public JPAQuery<T> getJPAQuery() {
        return new JPAQuery<>(entityManager);
    }

    @Override
    public JPASQLQuery<T> getJPASQLQuery() {
        return new JPASQLQuery<>(
                entityManager,
                getSQLTemplates()
        );
    }

    @Override
    public JPAQuery<T> selectFrom(final EntityPath<T> entityPath) {
        return getJPAQuery()
                .select(entityPath)
                .from(entityPath);
    }

    @Override
    public JPASQLQuery<T> nativeSelectFrom(final EntityPath<T> entityPath) {
        return getJPASQLQuery()
                .select(entityPath)
                .from(entityPath);
    }

    private SQLTemplates getSQLTemplates() {
        return PostgreSQLTemplates
                .builder()
                .printSchema()
                .build();
    }

}
