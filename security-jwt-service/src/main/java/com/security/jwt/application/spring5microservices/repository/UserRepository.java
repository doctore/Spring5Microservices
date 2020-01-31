package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.configuration.Constants;
import com.security.jwt.application.spring5microservices.model.Role;
import com.security.jwt.application.spring5microservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository(value = Constants.APPLICATION_NAME + "UserRepository")
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Gets the {@link User} (including its {@link Role}s) which {@link User#getUsername()} matches with the given one.
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link Optional} with the {@link User} which username matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    Optional<User> findByUsername(@Nullable String username);

}
