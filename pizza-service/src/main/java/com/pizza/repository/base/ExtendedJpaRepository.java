package com.pizza.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;

/**
 * Extended {@link JpaRepository} to include custom methods we want to share among all repositories.
 *
 * @param <T>
 *    Entity owner of the current repository
 * @param <ID>
 *    Primary key of the Entity
 */
@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    /**
     * Return the internal {@link EntityManager} to provide more functionality to the repositories.
     *
     * @return {@link EntityManager}
     */
    EntityManager getEntityManager();


    /**
     * Return the HQL representation of the internal query of the given {@link TypedQuery}.
     *
     * @param query
     *    {@link TypedQuery} to get its SQL query
     *
     * @return {@link String} with the HQL representation of the internal query,
     *         empty {@link String} is there is any error getting it.
     */
    String getHQLQuery(final TypedQuery query);

}
