package com.security.jwt.repository;

import com.security.jwt.model.JwtClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtClientDetailsRepository extends JpaRepository<JwtClientDetails, String> {
}
