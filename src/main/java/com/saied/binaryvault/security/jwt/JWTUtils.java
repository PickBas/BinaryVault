package com.saied.binaryvault.security.jwt;


import static java.time.temporal.ChronoUnit.HOURS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JWTUtils {

    private static final Integer ACCESS_TOKEN_EXPIRATION_TIME_HOURS = 2;
    private static final Integer REFRESH_TOKEN_EXPIRATION_TIME_HOURS = 168;
    private static final String SECRET = "secretsecretsecretsecretsecret0u09710928391827301927309secretsecretsecretsecretsecret0989080";
    private static final String ISSUER = "https://binaryvault.com";

    /**
     * Issuing access token
     *
     * @param subject Subject of JWT. In this case - user's username
     * @param claims Information about the token
     * @return Newly generated access token
     */
    public String issueAccessToken(
        String subject,
        Map<String, Object> claims
    ) {
        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer(ISSUER)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(
                Date.from(
                    Instant.now().plus(ACCESS_TOKEN_EXPIRATION_TIME_HOURS, HOURS)
                )
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Issuing refresh token
     *
     * @param subject Subject of JWT. In this case - user's username
     * @param claims Information about the token
     * @return Newly generated refresh token
     */
    public String issueRefreshToken(
        String subject,
        Map<String, Object> claims
    ) {
        return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuer(ISSUER)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(
                Date.from(
                    Instant.now().plus(REFRESH_TOKEN_EXPIRATION_TIME_HOURS, HOURS)
                )
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isTokenValid(String token, String username) {
        String subject = this.getSubject(token);
        return username.equals(subject) && !this.isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return this.getClaims(token)
            .getExpiration()
            .before(
                Date.from(Instant.now())
            );
    }
}
