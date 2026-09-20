package com.example.todo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expiration;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateLocalToken(UserDetails user) {
        String username = user.getUsername();
        String ownerId = "LOCAL:" + username;
        List<String> roles = user.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
        return buildToken(username, ownerId, "LOCAL", roles);
    }

    public String generateKeycloakToken(String username,
                                        String keycloakSubject,
                                        List<String> roles) {
        String ownerId = "KEYCLOAK:" + keycloakSubject;
        return buildToken(username, ownerId, "KEYCLOAK", roles);
    }

    private String buildToken(String username, String ownerId,
                              String provider, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(ownerId)
                .claim("username", username)
                .claim("provider", provider)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .signWith(key)
                .compact();
    }

    public Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean valid(String token) {
        try {
            return claims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String ownerId(String token) {
        return claims(token).getSubject();
    }

    public String username(String token) {
        Object value = claims(token).get("username");
        return value == null ? claims(token).getSubject() : value.toString();
    }

    public String provider(String token) {
        Object value = claims(token).get("provider");
        return value == null ? "LOCAL" : value.toString();
    }

    public List<String> roles(String token) {
        Object value = claims(token).get("roles");
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of("ROLE_USER");
    }

    public Date expiration(String token) {
        return claims(token).getExpiration();
    }
}
