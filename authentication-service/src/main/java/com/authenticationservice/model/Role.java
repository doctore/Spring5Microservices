package com.authenticationservice.model;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(schema = Constants.DATABASE_SCHEMA)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator=Constants.DATABASE_SCHEMA + "role_id_seq")
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return null == id ? name.equals(role.name) : id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(name) : Objects.hash(id);
    }

}