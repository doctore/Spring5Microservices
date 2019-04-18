package com.authenticationservice.model;

import com.authenticationservice.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Entity
@Table(schema = Constants.DATABASE_SCHEMA)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator=Constants.DATABASE_SCHEMA + "user_id_seq")
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
    @JoinTable(schema = Constants.DATABASE_SCHEMA,
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
        return roles.stream()
                    .map(r -> new SimpleGrantedAuthority(r.getName().toString()))
                    .collect(Collectors.toSet());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return null == id ? username.equals(user.username) : id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return null == id ? Objects.hash(username) : Objects.hash(id);
    }

}

