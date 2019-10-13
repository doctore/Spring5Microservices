package com.security.jwt.interfaces;

import com.security.jwt.dto.RawTokenInformationDto;
import com.security.jwt.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Functionality related with tokens used mainly for authorization purpose.
 */
public interface ITokenInformation {

    /**
     * Return the data required for the token generation.
     *
     * @param username
     *    {@link User} identifier use to get the information to fill the tokens
     *
     * @return {@link RawTokenInformationDto} with information to include
     *
     * @throws UsernameNotFoundException if the given username does not exists.
     */
    RawTokenInformationDto getTokenInformation(String username);

    /**
     * Return the key in the access token used to store the username information.
     *
     * @return {@link String}
     */
    String getUsernameKey();

    /**
     * Return the key in the access token used to store the roles information.
     *
     * @return {@link String}
     */
    String getRolesKey();

}
