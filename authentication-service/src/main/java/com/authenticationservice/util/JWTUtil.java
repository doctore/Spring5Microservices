package com.authenticationservice.util;

import com.authenticationservice.configuration.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component
public class JWTUtil {

    private String secretKey;
    private long validityInMilliseconds;


    @Autowired
    public JWTUtil(@Value("${security.jwt.token.secretKey}") String secretKey,
                   @Value("${security.jwt.token.expireLength}") long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
    }


    /**
     *    Using the given {@link UserDetails} information generates a valid JWT token encrypted with the selected
     * {@link SignatureAlgorithm}
     *
     * @param userDetails
     *    {@link UserDetails} with the user's information
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     *
     * @return {@link Optional} of {@link String} with the JWT
     */
    public Optional<String> generateJWTToken(UserDetails userDetails, SignatureAlgorithm signatureAlgorithm) {
        return Optional.ofNullable(userDetails)
                       .map(ud -> {
                           Date now = new Date();
                           Date expirationDate = new Date(now.getTime() + this.validityInMilliseconds);

                           Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
                           claims.put(Constants.JWT.ROLES_KEY, userDetails.getAuthorities());

                           return Jwts.builder()
                                      .setClaims(claims)
                                      .setIssuedAt(now)
                                      .setExpiration(expirationDate)
                                      .signWith(signatureAlgorithm, this.secretKey)
                                      .compact();
                       });
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate
     *
     * @return {@code false} if the given token is {@code null} or is expired, {@code true} otherwise.
     */
    public boolean isTokenValid(String token) {
        if (null == token)
            return false;

        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    /**
     * Gets the {@link UserDetails#getUsername()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     *
     * @return {@link UserDetails#getUsername()}
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    /**
     * Gets the {@link Date} from which the given token is no longer valid-
     *
     * @param token
     *    JWT token to extract the required information
     *
     * @return {@link Date}
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    /**
     * Gets the {@link UserDetails#getAuthorities()} included in the given JWT token.
     *
     * @param token
     *    JWT token to extract the required information
     *
     * @return {@link UserDetails#getAuthorities()}
     */
    public Collection<? extends GrantedAuthority> getRolesFromToken(String token) {
        return getClaimFromToken(token, (claims) -> (Set)claims.computeIfAbsent(Constants.JWT.ROLES_KEY, k -> new HashSet<>()));
    }


    /**
     * Get from the given token the required information from its payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param claimsResolver
     *    {@link Function} used to know how to get the information
     *
     * @return {@link T} with the wanted part of the payload
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Extracts from the given token all the information included in the payload
     *
     * @param token
     *    JWT token to extract the required information
     *
     * @return {@link Claims}
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(this.secretKey)
                            .parseClaimsJws(token)
                            .getBody();
    }

}
