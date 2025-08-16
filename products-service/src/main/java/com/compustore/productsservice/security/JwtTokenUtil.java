package com.compustore.productsservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility for parsing and validating JWT tokens.  This class mirrors the
 * implementation in the users service but omits token generation, since
 * products-service only needs to verify tokens issued by the users-service.
 */
@Component
public class JwtTokenUtil {
    @Value("${jwt.secret:secret-key}")
    private String jwtSecret;

    /**
     * Extract the username (subject) from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extract the authorities claim (roles) from the token.
     */
    public String getAuthoritiesFromToken(String token) {
        return (String) getClaims(token).get("roles");
    }

    /**
     * Check whether the token has expired.
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}