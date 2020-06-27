package com.security.jwt.repository.mapper;

import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.SignatureAlgorithmEnum;
import com.security.jwt.model.JwtClientDetails;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.RowMapper;

@UtilityClass
public class JwtClientDetailsMapper {

    public static final RowMapper<JwtClientDetails> jwtClientDetailsRowMapper = (resultSet, rowNum) ->
        new JwtClientDetails(
                resultSet.getString("client_id"),
                resultSet.getString("client_secret"),
                resultSet.getString("signature_secret"),
                SignatureAlgorithmEnum.valueOf(resultSet.getString("signature_algorithm")),
                AuthenticationConfigurationEnum.valueOf(resultSet.getString("authentication_generator")),
                resultSet.getString("token_type"),
                resultSet.getBoolean("use_jwe"),
                resultSet.getInt("access_token_validity"),
                resultSet.getInt("refresh_token_validity")
        );

}
