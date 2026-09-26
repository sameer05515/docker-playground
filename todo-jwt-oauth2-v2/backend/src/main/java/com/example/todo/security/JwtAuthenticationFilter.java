package com.example.todo.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.userdetails.*; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter; import java.io.IOException;
@Component public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UserDetailsService users;
 public JwtAuthenticationFilter(JwtService jwt,UserDetailsService users){this.jwt=jwt;this.users=users;}
 protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization"); if(h==null||!h.startsWith("Bearer ")){chain.doFilter(req,res);return;}
  try{String token=h.substring(7); if(jwt.valid(token)&&SecurityContextHolder.getContext().getAuthentication()==null){String username=jwt.username(token);UserDetails principal;
   if("KEYCLOAK".equals(jwt.provider(token))) principal=User.withUsername(username).password("").roles("USER").build(); else principal=users.loadUserByUsername(username);
   var auth=new UsernamePasswordAuthenticationToken(principal,null,principal.getAuthorities());auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));SecurityContextHolder.getContext().setAuthentication(auth);
  }}catch(Exception ignored){SecurityContextHolder.clearContext();} chain.doFilter(req,res);
 }
}
