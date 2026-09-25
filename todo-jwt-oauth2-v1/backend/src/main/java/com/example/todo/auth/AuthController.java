package com.example.todo.auth;

import com.example.todo.security.JwtService;
import com.example.todo.user.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager auth;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final UserDetailsService userDetails;
    private final JwtService jwt;

    public AuthController(AuthenticationManager auth, UserRepository users,
                          PasswordEncoder encoder, UserDetailsService userDetails,
                          JwtService jwt) {
        this.auth = auth; this.users = users; this.encoder = encoder;
        this.userDetails = userDetails; this.jwt = jwt;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        if (users.existsByUsername(r.username())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        AppUser u = new AppUser();
        u.setUsername(r.username());
        u.setEmail(r.email());
        u.setPassword(encoder.encode(r.password()));
        users.save(u);
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest r) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
        var user = userDetails.loadUserByUsername(r.username());
        return ResponseEntity.ok(Map.of("accessToken", jwt.generateToken(user)));
    }

    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String username, String password) {}
}
