package com.spring5microservices.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@Schema(description = "Authorization information about an specific user")
public class UsernameAuthoritiesDto {

    @Schema(required = true, description = "Identifier of the logged user")
    private String username;

    @Schema(required = true, description = "Roles and/or permissions of the logged user")
    private Set<String> authorities;

    @Schema(description = "Extra data returned by security service")
    private Map<String, Object> additionalInfo;

    public UsernameAuthoritiesDto(final String username,
                                  final Collection<String> authorities,
                                  final Map<String, Object> additionalInfo) {
        this.username = username;
        this.authorities = isNull(authorities)
                ? new HashSet<>()
                : new HashSet<>(authorities);
        this.additionalInfo = isNull(additionalInfo)
                ? new HashMap<>()
                : new HashMap<>(additionalInfo);
    }

}
