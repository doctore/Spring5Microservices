package com.security.jwt.application.spring5microservices.model;

import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
public class User implements UserDetails {

    private static final long serialVersionUID = -2635894377988063111L;

    private Long id;

    @NotNull
    @Size(min=1,max=128)
    private String name;

    @NotNull
    @Size(min=1,max=64)
    private String username;

    @NotNull
    @Size(min=1,max=128)
    private String password;

    @NotNull
    private boolean active;

    private Set<RoleEnum> roles;


    /**
     * Get {@link RoleEnum} and add them to a {@link Set} of {@link GrantedAuthority}
     *
     * @return {@link Set} of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ofNullable(roles)
                .map(rList -> rList.stream()
                                   .map(r -> new SimpleGrantedAuthority(r.name()))
                                   .collect(toSet()))
                .orElseGet(HashSet::new);
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}

