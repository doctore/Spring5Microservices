package com.security.jwt.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.security.jwt.enums.TokenTypeEnum;
import com.security.jwt.exception.UnAuthorizedException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Component
@Log4j2
public class JwtUtil2 {

    // JWE encryption options
    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
    private EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;


    /**
     *    Using the given {@code informationToInclude} generates a valid JWS token (signed JWT) signed with the selected
     * {@link SignatureAlgorithm} and {@code signatureSecret}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return {@link String} with the JWS
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm} or {@code jwtSignatureSecret} are {@code null}
     */
    public String generateJwsToken(Map<String, Object> informationToInclude, JWSAlgorithm signatureAlgorithm,
                                   String signatureSecret, long expirationTimeInSeconds) {
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        JWTClaimsSet claimsSet = addClaims(informationToInclude, expirationTimeInSeconds);
        SignedJWT signedJWT = getSignedJWT(signatureAlgorithm, signatureSecret, claimsSet);
        return signedJWT.serialize();
    }


    /**
     *    Using the given {@code informationToInclude} generates a valid nested JWS and JWE token (signed + encrypted JWT),
     * signed with the selected {@link SignatureAlgorithm} and {@code signatureSecret}.
     *
     *    For the encryption process, the algorithm is AES using a direct encryption with a shared symmetric key provided
     * in {@code encryptionSecret}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return {@link String} with the JWS
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm}, {@code jwtSignatureSecret} or {@code encryptionSecret}
     *                                  are {@code null}
     */
    public String generateJweToken(Map<String, Object> informationToInclude, JWSAlgorithm signatureAlgorithm,
                                   String signatureSecret, String encryptionSecret, long expirationTimeInSeconds) {
        Assert.hasText(encryptionSecret, "encryptionSecret cannot be null or empty");
        String jwsToken = generateJwsToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds);
        return encryptJwsToken(jwsToken, encryptionSecret);
    }





    /**
     * Generate the information to include in the JWT token
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWT toke will be valid
     *
     * @return {@link JWTClaimsSet}
     */
    private JWTClaimsSet addClaims(Map<String, Object> informationToInclude, long expirationTimeInSeconds) {
        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        if (null != informationToInclude)
            informationToInclude.forEach((k, v) -> claimsSet.claim(k, v));

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (expirationTimeInSeconds * 1000));
        return claimsSet
                .issueTime(now)
                .expirationTime(expirationDate)
                .build();
    }

    /**
     * Generate the signed JWT token (a JWS one).
     *
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param claimsSet
     *    {@link JWTClaimsSet} with the information to include
     *
     * @return {@link SignedJWT}
     *
     * @throws IllegalArgumentException it there was a problem creating the JWS token
     */
    private SignedJWT getSignedJWT(JWSAlgorithm signatureAlgorithm, String signatureSecret, JWTClaimsSet claimsSet) {
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(signatureAlgorithm), claimsSet);
            signedJWT.sign(new MACSigner(signatureSecret));
            return  signedJWT;
        } catch (JOSEException e) {
            throw new IllegalArgumentException("The was a problem trying to create a new JWS token", e);
        }
    }

    /**
     * Encrypt the given JWS token using algorithms and encryption method defined by default.
     *
     * @param jwsToken
     *    {@link String} with the JWS token to encrypt
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link String} with the JWE token
     */
    private String encryptJwsToken(String jwsToken, String encryptionSecret) {
        try {
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                            .contentType("JWT")   // Required to indicate nested JWT
                            .build(),
                    new Payload(jwsToken));

            jweObject.encrypt(new DirectEncrypter(encryptionSecret.getBytes()));
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new IllegalArgumentException(format("The was a problem trying to encrypt the JWS token: %s", jwsToken), e);
        }
    }

    /**
     * Decrypt the given JWE token returning the nested JWS one.
     *
     * @param jweToken
     *    {@link String} with the JWE token to decrypt
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link SignedJWT}
     */
    private SignedJWT decryptJweToken(String jweToken, String encryptionSecret) {
        try {
            JWEObject jweObject = JWEObject.parse(jweToken);
            jweObject.decrypt(new DirectDecrypter(encryptionSecret.getBytes()));
            return jweObject.getPayload().toSignedJWT();
        } catch (Exception e) {
            throw new UnAuthorizedException(format("The was a problem trying to decrypt the JWE token: %s", jweToken), e);
        }
    }

    /**
     * Return if the given {@code token} is a JWE, JWS, unknown one.
     *
     * @param token
     *    {@link String} with the token to check
     *
     * @return {@link TokenTypeEnum}
     */
    private TokenTypeEnum getTokenType(String token) {
        Assert.hasText(token, "token cannot be null or empty");
        try {
            Base64URL[] parts = JOSEObject.split(token);
            JSONObject jsonObject = JSONObjectUtils.parse(parts[0].decodeToString());
            Algorithm alg = Header.parseAlgorithm(jsonObject);
            if (alg instanceof JWSAlgorithm) {
                return TokenTypeEnum.JWS;
            } else if (alg instanceof JWEAlgorithm) {
                return TokenTypeEnum.JWE;
            }
            return TokenTypeEnum.UNKNOWN;
        } catch (Exception e) {
            throw new IllegalArgumentException(format("The was a problem trying to figure out the type of token:", token), e);
        }
    }















    public Map<String, Object> getKeys(String token, String jwtSignatureSecret, Set<String> keysToInclude) throws Exception {
        if(null == keysToInclude)
            return new HashMap<>();

        return getAllClaimsFromToken(token, jwtSignatureSecret)
                .entrySet().stream()
                .filter(e -> keysToInclude.contains(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public Map<String, Object> getExceptGivenKeys(String token, String jwtSignatureSecret, Set<String> keysToExclude) throws Exception {
        if(null == keysToExclude)
            return new HashMap<>();

        return getAllClaimsFromToken(token, jwtSignatureSecret)
                .entrySet().stream()
                .filter(e -> !keysToExclude.contains(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }



    /**
     * Extract from the given token all the information included in the payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWT token
     *
     * @return {@link Map} of {@link String}-{@link Object}
     *
     * @throws IllegalArgumentException if {@code token} or {@code signatureSecret} are null or empty
     * @throws UnAuthorizedException if {@code token} was not signed using {@code signatureSecret}
     */
    private Map<String, Object> getAllClaimsFromToken(String token, String signatureSecret) throws Exception {
        Assert.hasText(token, "token cannot be null or empty");
        Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        SignedJWT signedJWT = SignedJWT.parse(token);
        if (!isTokenValidSigned(signedJWT, signatureSecret))
            throw new UnAuthorizedException(format("The given signatureSecret of the token: %s is not valid", token));
        return signedJWT.getJWTClaimsSet().getClaims();
    }


    private boolean isTokenValidSigned(SignedJWT signedJWT, String jwtSignatureSecret) throws Exception {
        JWSVerifier verifier = new MACVerifier(jwtSignatureSecret);
        return signedJWT.verify(verifier);
    }

}
