package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import com.security.jwt.application.spring5microservices.model.User;
import com.security.jwt.application.spring5microservices.repository.mapper.UserMapper;
import com.security.jwt.configuration.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Repository(value = Constants.APPLICATIONS.SPRING5_MICROSERVICES + "UserRepository")
public class UserRepository {

    @Lazy
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Gets the {@link User} (including its {@link RoleEnum}s) which {@link User#getUsername()} matches with the given one.
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link Optional} with the {@link User} which username matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<User> findByUsername(@Nullable String username) {
        return ofNullable(username)
                .map(u -> jdbcTemplate.query(
                        "select u.id, u.name, u.username, u.password, u.active, r.name as role "
                      + "from " + Constants.DATABASE_SCHEMA.EAT + ".user u "
                      + "join " + Constants.DATABASE_SCHEMA.EAT + ".user_role ur on (ur.user_id = u.id) "
                      + "join " + Constants.DATABASE_SCHEMA.EAT + ".role r on (r.id = ur.role_id) "
                      + "where u.username = :username",
                      Map.of("username", username),
                      UserMapper.userWithRolesResultExtractor)
                );
    }

}
