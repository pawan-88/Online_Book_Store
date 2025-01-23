package com.bookstore.config;

import com.bookstore.service.AuthService;
import com.bookstore.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger authLogger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwtToken = extractTokenFromHeader(request);
        String username = null;

        if (jwtToken != null) {
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                handleTokenException(e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            validateAndAuthenticateUser(jwtToken, username);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return null;
    }

    /**
     * Handles exceptions related to JWT token parsing and validation.
     */
    private void handleTokenException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            authLogger.info("Illegal Argument while fetching the username!");
        } else if (e instanceof ExpiredJwtException) {
            authLogger.info("The JWT token is expired!");
        } else if (e instanceof MalformedJwtException) {
            authLogger.info("The JWT token is invalid!");
        } else {
            authLogger.error("Unexpected error during JWT processing!", e);
        }
    }

    /**
     * Validates the token and sets up authentication in the SecurityContext.
     */
    private void validateAndAuthenticateUser(String jwtToken, String username) {
        UserDetails userDetails = authService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            authLogger.info("Token validation failed!");
        }
    }
}
