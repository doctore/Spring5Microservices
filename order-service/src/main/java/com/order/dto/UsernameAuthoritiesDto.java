package com.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Class used to receive the authorization information related with logged users
 */
@AllArgsConstructor
@EqualsAndHashCode(of = {"username"})
@Data
@NoArgsConstructor
public class UsernameAuthoritiesDto {

    @JsonProperty("user_name")
    private String username;
    private Set<String> authorities;

}
