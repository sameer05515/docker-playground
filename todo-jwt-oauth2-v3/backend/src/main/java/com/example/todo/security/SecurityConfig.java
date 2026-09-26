package com.example.todo.security;

import org.springframework.context.annotation.*; import org.springframework.security.authentication.*; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.web.cors.*;
import java.util.List;
@Configuration public class SecurityConfig{
 private final JwtAuthenticationFilter jwtFilter; private final OAuth2SuccessHandler oauthSuccess; private final RestAuthenticationEntryPoint entry;
 public SecurityConfig(JwtAuthenticationFilter j,OAuth2SuccessHandler o,RestAuthenticationEntryPoint e){jwtFilter=j;oauthSuccess=o;entry=e;}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable()).cors(c->c.configurationSource(corsConfigurationSource())).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)).exceptionHandling(e->e.authenticationEntryPoint(entry))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/oauth2/**","/login/**","/logout").permitAll().anyRequest().authenticated())
   .oauth2Login(o->o.successHandler(oauthSuccess))
   .logout(l->l.logoutUrl("/logout").logoutSuccessUrl("http://localhost:5173/login").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID"))
   .addFilterBefore(jwtFilter,org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:5173"));c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));c.setAllowCredentials(true);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}