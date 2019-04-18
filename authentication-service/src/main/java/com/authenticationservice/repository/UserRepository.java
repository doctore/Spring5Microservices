package com.authenticationservice.repository;

import com.authenticationservice.model.Role;
import com.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Gets the {@link User} (including its {@link Role}s) which {@link User#username} matches with the given one.
     *
     * @param username
     *    Username to search a coincidence in {@link User#username}
     *
     * @return {@link Optional} with the {@link User} which username matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    Optional<User> findByUsername(@Nullable String username);

}
