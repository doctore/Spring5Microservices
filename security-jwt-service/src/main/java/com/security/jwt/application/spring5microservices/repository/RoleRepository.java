package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {}
