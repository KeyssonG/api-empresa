package keysson.apis.empresa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import keysson.apis.empresa.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtUtil.isTokenValid(token)) {

            request.setAttribute("CleanJwt", token);

            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();

            // Adiciona a Role
            String role = jwtUtil.extractRole(token);
            if (role != null) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            }

            // Adiciona as Permissões dos Módulos (Chaves)
            java.util.List<java.util.Map<String, Object>> modules = jwtUtil.extractModules(token);
            if (modules != null) {
                for (java.util.Map<String, Object> module : modules) {
                    String chave = (String) module.get("chave");
                    if (chave != null) {
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("MODULO_" + chave.toUpperCase()));
                    }
                }
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    jwtUtil.extractUserId(token),
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}

