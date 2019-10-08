package com.security.jwt.repository;

import com.security.jwt.model.JwtClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtClientDetailsRepository extends JpaRepository<JwtClientDetails, String> {

    Optional<JwtClientDetails> findByClientId(@Nullable String clientId);

}
