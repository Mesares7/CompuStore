package com.compustore.productsservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Filter for extracting and validating JWTs in the products service.  It parses
 * the token, extracts the username and roles and then places an authentication
 * object into the SecurityContext.  No database lookup is required.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.token.prefix:Bearer }")
    private String tokenPrefix;

    @Value("${jwt.header:Authorization}")
    private String headerString;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(headerString);
        String token = null;
        String username = null;
        if (header != null && header.startsWith(tokenPrefix)) {
            token = header.substring(tokenPrefix.length());
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException | MalformedJwtException | SignatureException e) {
                logger.error("Failed to parse JWT: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.warn("JWT expired: {}", e.getMessage());
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtTokenUtil.isTokenExpired(token)) {
                // Extract authorities from claim
                String authoritiesClaim = jwtTokenUtil.getAuthoritiesFromToken(token);
                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(authoritiesClaim.split(","))
                        .map(role -> {
                            // Spring Security expects "ROLE_" prefix
                            if (!role.startsWith("ROLE_")) {
                                return new SimpleGrantedAuthority(role);
                            }
                            return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}