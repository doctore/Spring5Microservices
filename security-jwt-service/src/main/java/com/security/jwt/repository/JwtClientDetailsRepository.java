package com.security.jwt.repository;

import com.security.jwt.configuration.Constants;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.repository.mapper.JwtClientDetailsMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Repository
public class JwtClientDetailsRepository {

    @Lazy
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<JwtClientDetails> findByClientId(@Nullable String clientId) {
        try {
            return ofNullable(clientId)
                    .map(cId -> jdbcTemplate.queryForObject(
                            "select client_id, client_secret, signature_secret, signature_algorithm, authentication_generator "
                          + "     ,token_type, use_jwe, access_token_validity, refresh_token_validity "
                          + "from " + Constants.DATABASE_SCHEMA.SECURITY + ".jwt_client_details "
                          + "where client_id = :clientId",
                          Map.of("clientId", clientId),
                          JwtClientDetailsMapper.jwtClientDetailsRowMapper)
                    );
        }
        catch (EmptyResultDataAccessException exception) {
            return empty();
        }
    }

}
