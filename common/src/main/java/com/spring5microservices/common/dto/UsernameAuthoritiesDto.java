package com.spring5microservices.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@ApiModel(description="Authorization information about an specific user")
public class UsernameAuthoritiesDto {

    @ApiModelProperty(required = true)
    private String username;

    @ApiModelProperty(position = 1, required = true, value = "roles of the logged user")
    private Set<String> authorities;

    @ApiModelProperty(position = 2, value = "data extra returned by security service")
    private Map<String, Object> additionalInfo;

    public UsernameAuthoritiesDto(String username, Collection<String> authorities, Map<String, Object> additionalInfo) {
        this.username = username;
        this.authorities = null == authorities ? new HashSet<>() : new HashSet<>(authorities);
        this.additionalInfo = additionalInfo;
    }

}
