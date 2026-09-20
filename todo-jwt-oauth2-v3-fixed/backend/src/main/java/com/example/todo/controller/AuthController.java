package com.example.todo.controller;

import com.example.todo.entity.AppUser;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtService;
import com.example.todo.service.TokenRevocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager manager;
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserDetailsService users;
    private final JwtService jwt;
    private final TokenRevocationService revocation;

    @Value("${app.keycloak.logout-url}")
    private String keycloakLogoutUrl;

    @Value("${app.frontend-url}/login")
    private String frontendLoginUrl;

    public AuthController(AuthenticationManager manager, UserRepository repository,
                          PasswordEncoder encoder, UserDetailsService users,
                          JwtService jwt, TokenRevocationService revocation) {
        this.manager = manager;
        this.repository = repository;
        this.encoder = encoder;
        this.users = users;
        this.jwt = jwt;
        this.revocation = revocation;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        if (repository.existsByUsername(r.username())) {
            return ResponseEntity.badRequest().body(Map.of("error", "USERNAME_EXISTS"));
        }
        AppUser u = new AppUser();
        u.setUsername(r.username());
        u.setEmail(r.email());
        u.setPassword(encoder.encode(r.password()));
        u.setRole("USER");
        repository.save(u);
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest r) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
        return Map.of("accessToken", jwt.generateLocalToken(users.loadUserByUsername(r.username())));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String header,
            HttpServletRequest request) {
        String provider = "LOCAL";

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwt.valid(token)) {
                    provider = jwt.provider(token);
                    revocation.revoke(token, jwt.expiration(token).toInstant());
                }
            } catch (Exception ignored) {
            }
        }

        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        if ("KEYCLOAK".equals(provider)) {
            String logoutUrl = keycloakLogoutUrl
                    + "?client_id=todo-client"
                    + "&post_logout_redirect_uri="
                    + java.net.URLEncoder.encode(frontendLoginUrl, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(Map.of("message", "Logged out", "logoutUrl", logoutUrl));
        }

        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String username, String password) {}
}
