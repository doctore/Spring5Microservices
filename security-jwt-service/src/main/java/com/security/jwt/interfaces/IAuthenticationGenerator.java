package com.security.jwt.interfaces;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Functionality related with the authentication process.
 */
public interface IAuthenticationGenerator {

    /**
     * Return the data required for the authentication process.
     *
     * @param username
     *    {@link User} identifier use to get the information to fill the tokens
     *
     * @return {@link RawAuthenticationInformationDto} with information to include
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exists.
     */
    RawAuthenticationInformationDto getRawAuthenticationInformation(String username);

    /**
     * Return the key in the access token used to store the {@code username} information.
     *
     * @return {@link String}
     */
    String getUsernameKey();

    /**
     * Return the key in the access token used to store the {@code roles} information.
     *
     * @return {@link String}
     */
    String getRolesKey();

}
