package com.essjr.DinMonex.security.filter;

import com.essjr.DinMonex.auth.JpaUserDetailsService;
import com.essjr.DinMonex.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JpaUserDetailsService jpaUserDetailsService;

    @Autowired
    public JwtAuthFilter(JwtService jwtService, JpaUserDetailsService jpaUserDetailsService) {
        this.jwtService = jwtService;
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.jpaUserDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            // --- TRATAMENTO DO TOKEN EXPIRADO ---
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Retorna 403
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expirado\", \"message\": \"" + e.getMessage() + "\"}");
            return; // IMPORTANTE: Interrompe a requisição aqui, não chama o filterChain

        } catch (Exception e) {
            // Tratamento para token malformado ou inválido
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Retorna 403
            response.getWriter().write("{\"error\": \"Token inválido\"}");
            return; // Interrompe a requisição
        }

        filterChain.doFilter(request, response);
    }
}
