package com.example.todo.service;

import com.example.todo.entity.RevokedToken;
import com.example.todo.repository.RevokedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class TokenRevocationService {
    private final RevokedTokenRepository repository;
    public TokenRevocationService(RevokedTokenRepository repository){this.repository=repository;}
    public void revoke(String token, Instant expiresAt){repository.save(new RevokedToken(hash(token),expiresAt));}
    public boolean isRevoked(String token){return repository.existsByTokenHash(hash(token));}
    @Scheduled(fixedDelay=3600000)
    public void cleanup(){repository.deleteByExpiresAtBefore(Instant.now());}
    private String hash(String token){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
}