package com.security.jwt.application.spring5microservices.model;

import com.security.jwt.application.spring5microservices.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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
@Entity
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@Table(schema = Constants.DATABASE.SCHEMA.EAT)
public class User implements UserDetails {

    private static final long serialVersionUID = -2635894377988063111L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Constants.DATABASE.SCHEMA.EAT + "user_id_seq")
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = Constants.DATABASE.SCHEMA.EAT,
               name = "user_role",
               inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<Role> roles;


    /**
     * Get {@link Role} and add them to a {@link Set} of {@link GrantedAuthority}
     *
     * @return {@link Set} of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ofNullable(roles)
                .map(rList -> rList.stream()
                                   .map(r -> new SimpleGrantedAuthority(r.getName().toString()))
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

