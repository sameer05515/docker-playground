package com.example.todo.security;

import com.example.todo.entity.AppUser;
import com.example.todo.entity.Role;
import com.example.todo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);

        Map<String, Object> attributes = oauthUser.getAttributes();

        String githubId = String.valueOf(attributes.get("id"));
        String username = value(attributes.get("login"));
        String name = value(attributes.get("name"));
        String email = value(attributes.get("email"));

        AppUser user = userRepository.findByGithubId(githubId)
                .map(existing -> updateUser(existing, username, name, email))
                .orElseGet(() -> createUser(githubId, username, name, email));

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                "login"
        );
    }

    private AppUser createUser(
            String githubId,
            String username,
            String name,
            String email) {

        AppUser user = new AppUser();
        user.setGithubId(githubId);
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }

    private AppUser updateUser(
            AppUser user,
            String username,
            String name,
            String email) {

        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    private String value(Object value) {
        return value == null ? null : value.toString();
    }
}
