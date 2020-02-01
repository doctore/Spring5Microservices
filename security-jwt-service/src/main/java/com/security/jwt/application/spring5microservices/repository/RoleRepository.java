package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.model.Role;
import com.security.jwt.configuration.Constants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = Constants.APPLICATIONS.SPRING5_MICROSERVICES + "RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {}
