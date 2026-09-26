package com.example.todo.controller;
import com.example.todo.entity.AppUser; import com.example.todo.repository.UserRepository; import com.example.todo.security.JwtService; import org.springframework.http.ResponseEntity; import org.springframework.security.authentication.*; import org.springframework.security.core.userdetails.UserDetailsService; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final AuthenticationManager manager;private final UserRepository repo;private final PasswordEncoder encoder;private final UserDetailsService users;private final JwtService jwt;
 public AuthController(AuthenticationManager m,UserRepository r,PasswordEncoder e,UserDetailsService u,JwtService j){manager=m;repo=r;encoder=e;users=u;jwt=j;}
 @PostMapping("/register") public ResponseEntity<?> register(@RequestBody RegisterRequest r){if(repo.existsByUsername(r.username()))return ResponseEntity.badRequest().body(Map.of("error","USERNAME_EXISTS"));AppUser u=new AppUser();u.setUsername(r.username());u.setEmail(r.email());u.setPassword(encoder.encode(r.password()));u.setRole("USER");repo.save(u);return ResponseEntity.ok(Map.of("message","Registered successfully"));}
 @PostMapping("/login") public Map<String,String> login(@RequestBody LoginRequest r){manager.authenticate(new UsernamePasswordAuthenticationToken(r.username(),r.password()));return Map.of("accessToken",jwt.generateLocalToken(users.loadUserByUsername(r.username())));}
 public record RegisterRequest(String username,String email,String password){} public record LoginRequest(String username,String password){}
}
