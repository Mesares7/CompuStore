package com.compustore.usersservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Utility class for creating and validating JWT tokens.  The token contains
 * the username as the subject and a comma‑separated list of authorities as
 * a custom claim.  The expiration and signing key are externalised in
 * configuration.  This approach follows the pattern described in the
 * role‑based access control tutorial, where the token builder sets the
 * subject, includes authorities and sets the expiration before signing【923295981449190†L657-L669】.
 */
@Component
public class JwtTokenUtil {
    /**
     * Secret key used to sign the JWT.  Injected from application properties or
     * environment.  Do not expose this value publicly.
     */
    @Value("${jwt.secret:secret-key}")
    private String jwtSecret;

    /**
     * Token validity in milliseconds.  Defaults to five hours (5 * 60 * 60 * 1000).
     */
    @Value("${jwt.expirationMs:18000000}")
    private long jwtExpirationMs;

    /**
     * Claim key used to store authorities in the token.
     */
    public static final String AUTHORITIES_KEY = "roles";

    /**
     * Generate a JWT from an Authentication object.  Extracts the username and
     * authorities, then builds and signs the token.
     *
     * @param authentication Spring Security authentication
     * @return the JWT string
     */
    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /**
     * Extract the username (subject) from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extract authorities (roles) from the token.
     */
    public String getAuthoritiesFromToken(String token) {
        return (String) getClaims(token).get(AUTHORITIES_KEY);
    }

    /**
     * Validate a token against the provided user details.  The token must not
     * be expired and the username in the token must match the user details.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Check whether a token has expired.
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Parse the token and return all claims.
     */
    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}