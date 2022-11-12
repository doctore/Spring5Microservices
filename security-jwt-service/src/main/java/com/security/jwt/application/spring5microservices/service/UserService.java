package com.security.jwt.application.spring5microservices.service;

import com.security.jwt.configuration.Constants;
import com.security.jwt.interfaces.IUserService;
import com.security.jwt.application.spring5microservices.model.User;
import com.security.jwt.application.spring5microservices.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service(value = Constants.APPLICATIONS.SPRING5_MICROSERVICES + "UserDetailsService")
public class UserService implements IUserService {

    @Lazy
    private final UserRepository userRepository;

    @Lazy
    private final PasswordEncoder passwordEncoder;


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link UserDetails}
     *
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        return ofNullable(username)
                .flatMap(userRepository::findByUsername)
                .map(u ->  {
                    new AccountStatusUserDetailsChecker()
                            .check(u);
                    return u;
                })
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                format("Username: %s not found in database",
                                        username)
                        )
                );
    }


    @Override
    public boolean passwordsMatch(final String passwordToVerify,
                                  final UserDetails userDetails) {
        if (!StringUtils.hasText(passwordToVerify) ||
                (null == userDetails ||
                        !StringUtils.hasText(userDetails.getPassword()))) {
            return false;
        }
        return passwordEncoder.matches(
                passwordToVerify,
                userDetails.getPassword()
        );
    }

}
