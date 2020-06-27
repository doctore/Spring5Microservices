package com.security.jwt.application.spring5microservices.repository.mapper;

import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import com.security.jwt.application.spring5microservices.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.HashSet;

@UtilityClass
public class UserMapper {

    public static final ResultSetExtractor<User> userWithRolesResultExtractor = (resultSet) -> {
        User user = null;
        while (resultSet.next()) {
            if (resultSet.isFirst()) {
                user = new User(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("active"),
                        new HashSet<>());
            }
            user.getRoles().add(RoleEnum.valueOf(resultSet.getString("role")));
        }
        return user;
    };

}
