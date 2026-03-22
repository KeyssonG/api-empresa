package keysson.apis.empresa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import keysson.apis.empresa.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class DynamicURLFilter extends OncePerRequestFilter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignorar caminhos públicos (como registro de empresa)
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. ADMIN tem acesso livre
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Busca no banco qual módulo protege esta URL (api_url_pattern)
        Integer requiredModuloId = findRequiredModulo(path);

        // 3. Se a URL não estiver mapeada, negamos (Default Deny)
        if (requiredModuloId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Este recurso da API nao possui um modulo de permissao mapeado.");
            return;
        }

        // 4. Verifica se o usuário possui esse modulo_id no Token
        String token = (String) request.getAttribute("CleanJwt");
        List<Map<String, Object>> userModules = jwtUtil.extractModules(token);

        boolean hasAccess = false;
        if (userModules != null) {
            for (Map<String, Object> module : userModules) {
                Object moduleIdObj = module.get("id"); 
                if (moduleIdObj != null && moduleIdObj.toString().equals(requiredModuloId.toString())) {
                    hasAccess = true;
                    break;
                }
            }
        }

        if (hasAccess) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Voce nao tem permissao para este recurso (Modulo ID: " + requiredModuloId + ").");
        }
    }

    private boolean isPublicPath(String path) {
        // O path /register deve ser livre para novas empresas
        return path.contains("/actuator") || path.contains("/register") || path.contains("/login");
    }

    private Integer findRequiredModulo(String path) {
        String sql = "SELECT id, api_url_pattern FROM modulos WHERE api_url_pattern IS NOT NULL";
        try {
            List<Map<String, Object>> modules = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> m : modules) {
                String pattern = (String) m.get("api_url_pattern");
                if (pathMatcher.match(pattern, path)) {
                    return (Integer) m.get("id");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
