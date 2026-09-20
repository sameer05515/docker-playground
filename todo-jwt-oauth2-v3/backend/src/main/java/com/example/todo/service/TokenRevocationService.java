package com.example.todo.service;

import com.example.todo.entity.RevokedToken;
import com.example.todo.repository.RevokedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class TokenRevocationService {

    private final RevokedTokenRepository repository;

    public TokenRevocationService(RevokedTokenRepository repository) {
        this.repository = repository;
    }

    public void revoke(String token, Instant expiresAt) {
        String hash = hash(token);

        if (!repository.existsByTokenHash(hash)) {
            RevokedToken revoked = new RevokedToken();
            revoked.setTokenHash(hash);
            revoked.setExpiresAt(expiresAt);
            repository.save(revoked);
        }
    }

    public boolean isRevoked(String token) {
        return repository.existsByTokenHash(hash(token));
    }

    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void cleanup() {
        int deleted = repository.deleteExpired(Instant.now());
        System.out.println("Expired revoked tokens deleted: " + deleted);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash JWT", e);
        }
    }
}