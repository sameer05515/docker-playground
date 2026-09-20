package com.example.todo.security;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OidcUser user = (OidcUser) authentication.getPrincipal();

        String username = user.getClaimAsString("preferred_username");
        if (username == null) username = user.getClaimAsString("email");
        if (username == null) username = user.getSubject();

        List<String> roles = extractRoles(user);

        String token = jwtService.generateKeycloakToken(
                username, user.getSubject(), roles);

        response.sendRedirect(frontendUrl + "/?token=" +
                URLEncoder.encode(token, StandardCharsets.UTF_8));
    }

    private List<String> extractRoles(OidcUser user) {
        Map<String, Object> realmAccess = user.getClaim("realm_access");
        List<String> roles = new ArrayList<>();

        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> collection) {
            for (Object value : collection) {
                String role = value.toString();
                if ("USER".equals(role) || "ADMIN".equals(role)) {
                    roles.add("ROLE_" + role);
                }
            }
        }

        return roles.isEmpty() ? List.of("ROLE_USER") : roles;
    }
}
