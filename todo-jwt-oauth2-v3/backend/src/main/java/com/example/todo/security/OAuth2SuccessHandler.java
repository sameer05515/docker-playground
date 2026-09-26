package com.example.todo.security;
import jakarta.servlet.http.*; import org.springframework.beans.factory.annotation.Value; import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User; import org.springframework.security.web.authentication.AuthenticationSuccessHandler; import org.springframework.stereotype.Component;
import java.io.IOException; import java.net.URLEncoder; import java.nio.charset.StandardCharsets;
@Component public class OAuth2SuccessHandler implements AuthenticationSuccessHandler{
 private final JwtService jwtService; @Value("${app.frontend-url}") private String frontendUrl;
 public OAuth2SuccessHandler(JwtService j){jwtService=j;}
 @Override public void onAuthenticationSuccess(HttpServletRequest req,HttpServletResponse res,Authentication auth)throws IOException{
  OAuth2User u=(OAuth2User)auth.getPrincipal(); String username=u.getAttribute("preferred_username"); if(username==null)username=u.getAttribute("email"); if(username==null)username=u.getAttribute("sub");
  String token=jwtService.generateKeycloakToken(username); res.sendRedirect(frontendUrl+"/oauth2/success?token="+URLEncoder.encode(token,StandardCharsets.UTF_8));
 }
}