package com.mediconnect.filters;

import com.mediconnect.utilities.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader);
    if(authHeader!=null && authHeader.startsWith("Bearer ")){
        String token = authHeader.substring(7);
        try{
            Claims claims = jwtUtil.extractAllClaims(token);
            String email = claims.getSubject();
            String role = claims.get("role",String.class);

            System.out.println("Decoded JWT Email: " + email);
            System.out.println("Decoded JWT Role: " + role);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            System.out.println("Authorities set: " + authToken.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }catch (Exception e) {
            System.out.println("JWT Error: " + e.getMessage());
        }
    }
    filterChain.doFilter(request,response);
    }
}
