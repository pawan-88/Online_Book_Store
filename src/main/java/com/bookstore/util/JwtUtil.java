package com.bookstore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Token expiration time (10 hours)
    private final long JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // Updated: Generate a secure key dynamically for HS512
    private final Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Secure and correct key size for HS512

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Set username as the token's subject
                .setIssuedAt(new Date()) // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME)) // Token expiration
                .signWith(signingKey, SignatureAlgorithm.HS512) // Sign token with secure key and algorithm
                .compact(); // Build the token
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey) // Use the secure key for parsing
                .build()
                .parseClaimsJws(token)
                .getBody(); // Extract the claims
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey) // Use the secure key for parsing
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(signingKey, SignatureAlgorithm.HS512) // Use secure key and algorithm
                .compact();
    }
}
