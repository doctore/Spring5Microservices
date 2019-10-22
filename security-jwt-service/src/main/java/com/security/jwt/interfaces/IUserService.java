package com.security.jwt.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Used to extend functionality provided by {@link UserDetailsService}
 */
public interface IUserService extends UserDetailsService {

    /**
     * Verify if the given passwords are equals.
     *
     * @param rawPassword
     *    Not encoded password
     * @param encodedPassword
     *    Encoded password
     *
     * @return {@code true} if both passwords are equals, {@code false} otherwise.
     */
    boolean passwordsMatch(String rawPassword, String encodedPassword);

}
