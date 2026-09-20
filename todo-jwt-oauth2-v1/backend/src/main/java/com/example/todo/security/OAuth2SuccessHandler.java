package com.example.todo.security;

import com.example.todo.user.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository users;
    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2SuccessHandler(UserRepository users, JwtService jwtService,
                                @Value("${app.frontend-url}") String frontendUrl) {
        this.users = users;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        OAuth2User oauth = (OAuth2User) authentication.getPrincipal();
        String email = oauth.getAttribute("email");
        String username = email != null ? email : oauth.getAttribute("preferred_username");

        AppUser user = users.findByUsername(username).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword("");
            return users.save(u);
        });

        var principal = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        String token = jwtService.generateToken(principal);

        response.sendRedirect(frontendUrl + "/oauth2/success?token=" +
                java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8));
    }
}
