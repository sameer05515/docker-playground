package com.example.todo.security;
import com.example.todo.entity.AppUser; import com.example.todo.repository.UserRepository;
import org.springframework.security.core.userdetails.*; import org.springframework.stereotype.Service;
@Service public class CustomUserDetailsService implements UserDetailsService{
 private final UserRepository repository; public CustomUserDetailsService(UserRepository r){repository=r;}
 @Override public UserDetails loadUserByUsername(String username){AppUser u=repository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));return User.builder().username(u.getUsername()).password(u.getPassword()).roles(u.getRole()).build();}
}