package com.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UsernameAuthoritiesDto {

    private String username;
    private Set<String> authorities;

    public UsernameAuthoritiesDto(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = Optional.ofNullable(authorities)
                                   .map(allAuth -> {
                                       Set<String> result = new HashSet<>();
                                       for (GrantedAuthority auth: allAuth)
                                           result.add(auth.getAuthority());

                                       return result;
                                    })
                                   .orElse(new HashSet<>());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernameAuthoritiesDto that = (UsernameAuthoritiesDto) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
