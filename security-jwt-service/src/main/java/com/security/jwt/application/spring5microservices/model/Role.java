package com.security.jwt.application.spring5microservices.model;

import com.security.jwt.application.spring5microservices.configuration.Constants;
import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@Table(schema = Constants.DATABASE.SCHEMA.EAT)
public class Role implements Serializable {

    private static final long serialVersionUID = -3655820157062921094L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Constants.DATABASE.SCHEMA.EAT + "role_id_seq")
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

}