package com.security.oauth.model;

import com.security.oauth.configuration.Constants;
import com.security.oauth.enums.RoleEnum;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@Table(schema = Constants.DATABASE_SCHEMA.EAT)
public class Role implements Serializable {

    private static final long serialVersionUID = 4833183317359581882L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Constants.DATABASE_SCHEMA.EAT + ".role_generator")
    @SequenceGenerator(name = Constants.DATABASE_SCHEMA.EAT + ".role_generator", sequenceName = Constants.DATABASE_SCHEMA.EAT + ".role_id_seq", allocationSize = 1)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

}