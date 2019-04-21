package com.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

/**
 * Class used to receive the authorization information related with logged users
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class UsernameAuthoritiesDto {

    private String username;
    private Set<String> authorities;


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
