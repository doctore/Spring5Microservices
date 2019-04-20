package com.authenticationservice.util;

import com.authenticationservice.configuration.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     *    Using the given {@link UserDetails} information generates a valid JWT token encrypted with the selected
     * {@link SignatureAlgorithm}
     *
     * @param userDetails
     *    {@link UserDetails} with the user's information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param expirationTimeInMilliseconds
     *    How many milliseconds the JWT toke will be valid
     *
     * @return {@link Optional} of {@link String} with the JWT
     *
     * @throws IllegalArgumentException if {@code jwtSecretKey} is null or {@code signatureAlgorithm} is null
     */
    public Optional<String> generateJwtToken(UserDetails userDetails, SignatureAlgorithm signatureAlgorithm,
                                             String jwtSecretKey, long expirationTimeInMilliseconds) {

        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        return Optional.ofNullable(userDetails)
                       .map(ud -> {
                           Date now = new Date();
                           Date expirationDate = new Date(now.getTime() + expirationTimeInMilliseconds);

                           Claims claims = Jwts.claims().setSubject(ud.getUsername());
                           claims.put(Constants.JWT.ROLES_KEY, ud.getAuthorities());

                           return Jwts.builder()
                                      .setClaims(claims)
                                      .setIssuedAt(now)
                                      .setExpiration(expirationDate)
                                      .signWith(signatureAlgorithm, jwtSecretKey)
                                      .compact();
                       });
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@code false} if the given token is expired or is not valid, {@code true} otherwise.
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     */
    public boolean isTokenValid(String token, String jwtSecretKey) {
        try {
            return getExpirationDateFromToken(token, jwtSecretKey)
                    .map(exp -> exp.after(new Date()))
                    .orElse(false);

        } catch (JwtException ex) {
            LOGGER.error(String.format("There was an error checking if the token %s is valid", token), ex);
            return false;
        }
    }


    /**
     * Gets the {@link UserDetails#getUsername()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Optional} with {@link UserDetails#getUsername()}
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     */
    public Optional<String> getUsernameFromToken(String token, String jwtSecretKey) {
        try {
            return Optional.ofNullable(getClaimFromToken(token, jwtSecretKey, Claims::getSubject));
        } catch (JwtException ex) {
            LOGGER.error(String.format("There was an error getting the username of token %s", token), ex);
            return Optional.empty();
        }
    }


    /**
     * Gets the {@link Date} from which the given token is no longer valid.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Optional} with {@link Date}
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     */
    public Optional<Date> getExpirationDateFromToken(String token, String jwtSecretKey) {
        try {
            return Optional.ofNullable(getClaimFromToken(token, jwtSecretKey, Claims::getExpiration));
        } catch (JwtException ex) {
            LOGGER.error(String.format("There was an error getting the expiration date of token %s", token), ex);
            return Optional.empty();
        }
    }


    /**
     * Gets the {@link UserDetails#getAuthorities()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link UserDetails#getAuthorities()}
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     */
    public Collection<? extends GrantedAuthority> getRolesFromToken(String token, String jwtSecretKey) {
        try {
            return getClaimFromToken(token, jwtSecretKey,
                    (claims) -> new HashSet<>((Collection)claims.computeIfAbsent(Constants.JWT.ROLES_KEY, k -> new HashSet<>())));
        } catch (JwtException ex) {
            LOGGER.error(String.format("There was an error getting the roles of token %s", token), ex);
            return new HashSet<>();
        }
    }


    /**
     * Get from the given token the required information from its payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param claimsResolver
     *    {@link Function} used to know how to get the information
     *
     * @return {@link T} with the wanted part of the payload
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     */
    private <T> T getClaimFromToken(String token, String jwtSecretKey, Function<Claims, T> claimsResolver) {
        Assert.hasText(token, "token cannot be null or empty");
        Assert.hasText(jwtSecretKey, "jwtSecretKey cannot be null or empty");

        final Claims claims = getAllClaimsFromToken(token, jwtSecretKey);
        return claimsResolver.apply(claims);
    }


    /**
     * Extracts from the given token all the information included in the payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Claims}
     */
    private Claims getAllClaimsFromToken(String token, String jwtSecretKey) {
        return Jwts.parser().setSigningKey(jwtSecretKey)
                            .parseClaimsJws(token)
                            .getBody();
    }

}
