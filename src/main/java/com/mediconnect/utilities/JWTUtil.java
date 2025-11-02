package com.mediconnect.utilities;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil {
    private final SecretKey SECRET_KEY;
    private final long EXPIRATION_TIME;
    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationTime) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.EXPIRATION_TIME = expirationTime;
        System.out.println("JWTUtil initialized with expiration(ms): " + EXPIRATION_TIME);

    }


    public String generateToken(String email, String id, String name, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("name", name);
        claims.put("role", role);

        String token = createToken(claims, email);
        System.out.println("Generated JWT for " + email);
        System.out.println("Token: " + token);

        Claims decodedClaims = extractAllClaims(token);
        System.out.println("Token issued at: " + decodedClaims.getIssuedAt());
        System.out.println("Token expires at: " + decodedClaims.getExpiration());

        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        System.out.println("Creating token at: " + now);
        System.out.println("Token will expire at: " + expiryDate);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Extracted claims from token: " + claims);
        return claims;
    }
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    public boolean isTokenExpired(String token) {

        Date expiration = extractAllClaims(token).getExpiration();
        boolean expired = expiration.before(new Date());
        System.out.println("Checking token expiration: " + expiration + " | Expired? " + expired);
        return expired;
    }
}
