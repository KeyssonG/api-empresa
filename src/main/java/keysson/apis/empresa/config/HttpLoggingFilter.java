package keysson.apis.empresa.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {

            long duration = System.currentTimeMillis() - start;

            log.info(
                    new StringMapMessage()
                            .with("event", "http_request")
                            .with("http.method", request.getMethod())
                            .with("http.status_code", response.getStatus())
                            .with("url.path", request.getRequestURI())
                            .with("duration_ms", duration)
            );
        }
    }
}
