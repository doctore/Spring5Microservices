package com.security.jwt.model;

import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.SignatureAlgorithmEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = {"clientId"})
@NoArgsConstructor
public class JwtClientDetails implements UserDetails {

    private static final long serialVersionUID = -171319389828209358L;
    
    @NotNull
    @Size(min=1,max=64)
    private String clientId;

    @NotNull
    @Size(min=1,max=128)
    private String clientSecret;

    @NotNull
    @Size(min=1,max=256)
    private String signatureSecret;

    @NotNull
    private SignatureAlgorithmEnum signatureAlgorithm;

    @NotNull
    private AuthenticationConfigurationEnum authenticationGenerator;

    @NotNull
    @Size(min=1,max=32)
    private String tokenType;

    @NotNull
    private boolean useJwe;

    @NotNull
    private int accessTokenValidity;

    @NotNull
    private int refreshTokenValidity;

    @Override
    public String getUsername() {
        return clientId;
    }

    @Override
    public String getPassword() {
        return clientSecret;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
