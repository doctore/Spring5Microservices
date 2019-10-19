package com.security.jwt.model;

import com.security.jwt.configuration.Constants;
import com.security.jwt.enums.JwtGeneratorConfigurationEnum;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"clientId"})
@NoArgsConstructor
@Table(schema = Constants.DATABASE_SCHEMA.SECURITY)
public class JwtClientDetails implements Serializable {

    private static final long serialVersionUID = -171319389828209358L;
    
    @Id
    @NotNull
    @Size(min=1,max=64)
    private String clientId;

    @NotNull
    @Size(min=1,max=256)
    private String jwtSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SignatureAlgorithm jwtAlgorithm;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JwtGeneratorConfigurationEnum jwtConfiguration;

    @NotNull
    @Size(min=1,max=32)
    private String tokenType;

    @NotNull
    private int accessTokenValidity;

    @NotNull
    private int refreshTokenValidity;

}
