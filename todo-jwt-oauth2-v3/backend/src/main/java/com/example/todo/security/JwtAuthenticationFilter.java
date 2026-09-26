package com.example.todo.security;

import com.example.todo.service.TokenRevocationService;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails; import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component public class JwtAuthenticationFilter extends OncePerRequestFilter{
 private final JwtService jwtService; private final UserDetailsService userDetailsService; private final TokenRevocationService revocationService;
 public JwtAuthenticationFilter(JwtService j,UserDetailsService u,TokenRevocationService r){jwtService=j;userDetailsService=u;revocationService=r;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  if(h==null||!h.startsWith("Bearer ")){chain.doFilter(req,res);return;}
  try{
   String token=h.substring(7);
   if(revocationService.isRevoked(token)||!jwtService.valid(token)||SecurityContextHolder.getContext().getAuthentication()!=null){chain.doFilter(req,res);return;}
   String username=jwtService.username(token); String provider=jwtService.provider(token); UserDetails principal;
   if("KEYCLOAK".equals(provider)) principal=User.withUsername(username).password("").roles("USER").build();
   else principal=userDetailsService.loadUserByUsername(username);
   var auth=new UsernamePasswordAuthenticationToken(principal,null,principal.getAuthorities());
   auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req)); SecurityContextHolder.getContext().setAuthentication(auth);
  }catch(Exception ignored){SecurityContextHolder.clearContext();}
  chain.doFilter(req,res);
 }
}