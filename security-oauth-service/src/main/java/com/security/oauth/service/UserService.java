package com.security.oauth.service;

import com.security.oauth.model.User;
import com.security.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service(value = "userDetailsService")
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserService(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link UserDetails}
     *
     * @throws UsernameNotFoundException if the given username does not exists in database.
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ofNullable(username)
                .flatMap(un -> userRepository.findByUsername(un))
                .map(u ->  {
                    new AccountStatusUserDetailsChecker().check(u);
                    return u;
                })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username: %s not found in database", username)));
    }

}
