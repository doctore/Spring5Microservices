package com.security.jwt.model;

import com.security.jwt.configuration.Constants;
import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.SignatureAlgorithmEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
@Table(schema = Constants.DATABASE.SCHEMA.SECURITY

        // TODO: REMOVE
        ,name = "jwt_client_details"
)
public class JwtClientDetails implements Serializable {

    private static final long serialVersionUID = -171319389828209358L;
    
    @Id
    @NotNull
    @Size(min=1,max=64)
    @Column(name = "client_id")  // TODO: REMOVE
    private String clientId;

    @NotNull
    @Size(min=1,max=256)
    @Column(name = "signature_secret")  // TODO: REMOVE
    private String signatureSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "signature_algorithm")  // TODO: REMOVE
    private SignatureAlgorithmEnum signatureAlgorithm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_generator")  // TODO: REMOVE
    private AuthenticationConfigurationEnum authenticationGenerator;

    @NotNull
    @Size(min=1,max=32)
    @Column(name = "token_type")  // TODO: REMOVE
    private String tokenType;

    @NotNull
    @Column(name = "use_jwe")  // TODO: REMOVE
    private boolean useJwe;

    @NotNull
    @Column(name = "access_token_validity")  // TODO: REMOVE
    private int accessTokenValidity;

    @NotNull
    @Column(name = "refresh_token_validity")  // TODO: REMOVE
    private int refreshTokenValidity;

}
