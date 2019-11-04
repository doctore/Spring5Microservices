package com.security.jwt.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Used to extend functionality provided by {@link UserDetailsService}
 */
public interface IUserService extends UserDetailsService {

    /**
     * Verify if the given password matches with the one belongs to {@code userDetails}.
     *
     * @param passwordToVerify
     *    Password to veryfy
     * @param userDetails
     *    {@link UserDetails} which password will be compare
     *
     * @return {@code true} if {@code passwordToVerify} matches with {@link UserDetails#getPassword()}, {@code false} otherwise.
     */
    boolean passwordsMatch(String passwordToVerify, UserDetails userDetails);

}
