package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.configuration.Constants;
import com.security.jwt.application.spring5microservices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = Constants.APPLICATION_NAME + "RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {}
