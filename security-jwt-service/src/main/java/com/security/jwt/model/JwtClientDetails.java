package com.security.jwt.model;

import com.security.jwt.configuration.Constants;
import com.security.jwt.enums.JwtGenerationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"clientId"})
@NoArgsConstructor
@Table(schema = Constants.DATABASE_SCHEMA.SECURITY)
public class JwtClientDetails {

    @Id
    @NotNull
    @Size(min=1,max=128)
    private String clientId;

    @NotNull
    @Size(min=1,max=128)
    private String jwtSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JwtGenerationEnum jwtConfiguration;

    @NotNull
    @Size(min=1,max=32)
    private String tokenType;

    @NotNull
    private int accessTokenValidity;

    @NotNull
    private int refreshTokenValidity;

}
