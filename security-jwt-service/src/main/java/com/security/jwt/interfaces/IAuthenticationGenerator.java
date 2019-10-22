package com.security.jwt.interfaces;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Functionality related with the authentication process.
 */
public interface IAuthenticationGenerator {

    /**
     * Return the data required for the authentication process.
     *
     * @param userDetails
     *    {@link UserDetails} identifier use to get the information to fill the tokens
     *
     * @return {@link Optional} of {@link RawAuthenticationInformationDto} with information to include
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exists.
     */
    Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(UserDetails userDetails);

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
