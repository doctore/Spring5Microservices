package com.pizza.repository.base;

import org.hibernate.query.internal.QueryImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;

import static java.util.Optional.ofNullable;

/**
 * Extended {@link SimpleJpaRepository} to include custom methods we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
public class ExtendedJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements ExtendedJpaRepository<T, ID> {

    protected EntityManager entityManager;

    public ExtendedJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation,
                                     final EntityManager entityManager) {
        super(
                entityInformation,
                entityManager
        );
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }


    @Override
    public String getHQLQuery(final TypedQuery query) {
        return ofNullable(query)
                .map(q -> {
                    try {
                        return query.unwrap(QueryImpl.class)
                                .getQueryString();

                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse("");
    }

}
