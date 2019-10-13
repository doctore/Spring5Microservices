package com.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Log4j2
public class JwtUtil {

    /**
     *    Using the given {@code informationToInclude} generates a valid JWT token encrypted with the selected
     * {@link SignatureAlgorithm}
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWT token
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param expirationTimeInSeconds
     *    How many seconds the JWT toke will be valid
     *
     * @return {@link Optional} of {@link String} with the JWT
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm} or {@code jwtSecretKey} are {@code null}
     */
    public Optional<String> generateJwtToken(Map<String, Object> informationToInclude, SignatureAlgorithm signatureAlgorithm,
                                             String jwtSecretKey, long expirationTimeInSeconds) {

        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.notNull(jwtSecretKey, "jwtSecretKey cannot be null");
        return Optional.ofNullable(informationToInclude)
                .map(ud -> {
                    Date now = new Date();
                    Date expirationDate = new Date(now.getTime() + (expirationTimeInSeconds * 1000));

                    return Jwts.builder()
                            .setClaims(informationToInclude)
                            .setIssuedAt(now)
                            .setExpiration(expirationDate)
                            .signWith(getSigningKey(signatureAlgorithm, jwtSecretKey))
                            .compact();
                });
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@code false} if the given token is expired or is not valid, {@code true} otherwise.
     *
     * @throws IllegalArgumentException if {@code token}, {@code signatureAlgorithm} or {@code jwtSecretKey} are null or empty
     */
    public boolean isTokenValid(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey) {
        try {
            return getExpirationDateFromToken(token, signatureAlgorithm, jwtSecretKey)
                    .map(exp -> exp.after(new Date()))
                    .orElse(false);

        } catch (JwtException ex) {
            log.error(String.format("There was an error checking if the token %s is valid", token), ex);
            return false;
        }
    }


    /**
     * Gets the {@link UserDetails#getUsername()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param usernameKeyInToken
     *    Key in the given token used to store username information
     *
     * @return {@link Optional} with {@link UserDetails#getUsername()}
     *
     * @throws IllegalArgumentException if {@code token}, {@code signatureAlgorithm} or {@code jwtSecretKey} are null or empty
     */
    public Optional<String> getUsernameFromToken(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey,
                                                 String usernameKeyInToken) {
        try {
            return Optional.ofNullable(getClaimFromToken(token, signatureAlgorithm, jwtSecretKey,
                    (claims) -> claims.get(usernameKeyInToken, String.class)));
        } catch (JwtException ex) {
            log.error(String.format("There was an error getting the username of token %s", token), ex);
            return Optional.empty();
        }
    }


    /**
     * Gets the {@link UserDetails#getAuthorities()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param rolesKeyInToken
     *    Key in the given token used to store roles information
     *
     * @return {@link UserDetails#getAuthorities()}
     *
     * @throws IllegalArgumentException if {@code token}, {@code signatureAlgorithm} or {@code jwtSecretKey} are null or empty
     */
    public Collection<? extends GrantedAuthority> getRolesFromToken(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey,
                                                                    String rolesKeyInToken) {
        try {
            return getClaimFromToken(token, signatureAlgorithm, jwtSecretKey,
                    (claims) -> new HashSet<>((Collection)claims.computeIfAbsent(rolesKeyInToken, k -> new HashSet<>())));
        } catch (JwtException ex) {
            log.error(String.format("There was an error getting the roles of token %s", token), ex);
            return new HashSet<>();
        }
    }


    /**
     * Gets the {@link Date} from which the given token is no longer valid.
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Optional} with {@link Date}
     *
     * @throws IllegalArgumentException if {@code token}, {@code signatureAlgorithm} or {@code jwtSecretKey} are null or empty
     */
    private Optional<Date> getExpirationDateFromToken(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey) {
        try {
            return Optional.ofNullable(getClaimFromToken(token, signatureAlgorithm, jwtSecretKey, Claims::getExpiration));
        } catch (JwtException ex) {
            log.error(String.format("There was an error getting the expiration date of token %s", token), ex);
            return Optional.empty();
        }
    }

    /**
     * Get from the given token the required information from its payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param claimsResolver
     *    {@link Function} used to know how to get the information
     *
     * @return {@link T} with the wanted part of the payload
     *
     * @throws IllegalArgumentException if {@code token}, {@code signatureAlgorithm} or {@code jwtSecretKey} are null or empty
     */
    private <T> T getClaimFromToken(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey, Function<Claims, T> claimsResolver) {
        Assert.hasText(token, "token cannot be null or empty");
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.hasText(jwtSecretKey, "jwtSecretKey cannot be null or empty");
        final Claims claims = getAllClaimsFromToken(token, signatureAlgorithm, jwtSecretKey);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts from the given token all the information included in the payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Claims}
     */
    private Claims getAllClaimsFromToken(String token, SignatureAlgorithm signatureAlgorithm, String jwtSecretKey) {
        return Jwts.parser()
                .setSigningKey(getSigningKey(signatureAlgorithm, jwtSecretKey))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates the information required to encrypt/decrypt the Jwt token
     *
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     *
     * @return {@link Key}
     */
    private Key getSigningKey(SignatureAlgorithm signatureAlgorithm, String jwtSecretKey) {
        return new SecretKeySpec(jwtSecretKey.getBytes(), signatureAlgorithm.getJcaName());
    }

}
