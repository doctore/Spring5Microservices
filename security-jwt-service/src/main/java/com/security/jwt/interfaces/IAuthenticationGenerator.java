package com.security.jwt.interfaces;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.exception.UnauthorizedException;
import com.security.jwt.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Functionality related with the authentication process.
 */
public interface IAuthenticationGenerator {

    /**
     * Refresh the data required for the authentication process when {@code username} and {@password} are given.
     *
     * @param username
     *    {@link User} identifier use to get the information to fill the tokens
     *
     * @return {@link Optional} of {@link RawAuthenticationInformationDto} with information to include
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exists.
     */
    Optional<RawAuthenticationInformationDto> refreshRawAuthenticationInformation(String username);

    /**
     * Retrurn the data required for the authentication process when {@code username} is given.
     *
     * @param username
     *    {@link User} identifier use to get the information to fill the tokens
     * @param password
     *    {@link User} raw password used to verify the existence of the user
     *
     * @return {@link Optional} of {@link RawAuthenticationInformationDto} with information to include
     *
     * @throws UnauthorizedException if the given {@code password} does not match with existing in database
     * @throws UsernameNotFoundException if the given {@code username} does not exists.
     */
    Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(String username, String password);

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
