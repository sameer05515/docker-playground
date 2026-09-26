package com.example.todo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key; private final long expiration;
    public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expiration){key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;}
    public String generateLocalToken(UserDetails u){return build(u.getUsername(),"LOCAL");}
    public String generateKeycloakToken(String username){return build(username,"KEYCLOAK");}
    private String build(String username,String provider){return Jwts.builder().subject(username).claim("provider",provider).claim("roles",java.util.List.of("ROLE_USER")).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration)).signWith(key).compact();}
    public Claims claims(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
    public boolean valid(String token){try{return claims(token).getExpiration().after(new Date());}catch(JwtException|IllegalArgumentException e){return false;}}
    public String username(String token){return claims(token).getSubject();}
    public String provider(String token){Object p=claims(token).get("provider");return p==null?"LOCAL":p.toString();}
    public Date expiration(String token){return claims(token).getExpiration();}
}