package com.security.jwt.service;

import com.security.jwt.interfaces.IUserService;
import com.security.jwt.model.User;
import com.security.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.util.Optional.ofNullable;

@Service(value = "userDetailsService")
public class UserService implements IUserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Lazy UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link UserDetails}
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exists in database.
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return ofNullable(username)
                .flatMap(un -> userRepository.findByUsername(un))
                .map(u ->  {
                    new AccountStatusUserDetailsChecker().check(u);
                    return u;
                })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username: %s not found in database", username)));
    }


    @Override
    public boolean passwordsMatch(String rawPassword, String encodedPassword) {
        if (StringUtils.isEmpty(rawPassword) || StringUtils.isEmpty(encodedPassword))
            return false;

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
