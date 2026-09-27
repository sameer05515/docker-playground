package com.example.todo.service;

import com.example.todo.entity.AppUser;
import com.example.todo.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser(OAuth2User oauthUser) {

        Object githubIdAttribute = oauthUser.getAttribute("id");

        if (githubIdAttribute == null) {
            throw new IllegalStateException("GitHub user id not found");
        }

        String githubId = String.valueOf(githubIdAttribute);

        return userRepository.findByGithubId(githubId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Application user not found for GitHub ID: " + githubId
                        )
                );
    }
}