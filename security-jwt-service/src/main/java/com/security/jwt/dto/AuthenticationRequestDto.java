package com.security.jwt.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ApiModel(description="Required data to authenticate a user")
@Wither
public class AuthenticationRequestDto {

    @ApiModelProperty(required = true)
    @NotNull
    @Size(min=1, max=64)
    private String username;

    @ApiModelProperty(position = 1, required = true)
    @NotNull
    @Size(min=1, max=128)
    private String password;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationRequestDto that = (AuthenticationRequestDto) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
