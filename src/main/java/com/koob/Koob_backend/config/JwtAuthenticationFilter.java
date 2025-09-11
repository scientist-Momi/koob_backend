package com.koob.Koob_backend.config;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only skip preflight; process all other requests including /api/v1/auth/me
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        log.warn("JWT filter: {} {}", request.getMethod(), request.getRequestURI());
        String token = extractTokenFromCookies(request);
        log.warn("JWT filter: AUTH-TOKEN present? {}", token != null);

        if (token != null) {
            try {
                if (jwtUtil.isTokenValid(token)) {
                    String userId = jwtUtil.getUserId(token);
                    String email = jwtUtil.getEmail(token);
                    log.warn("JWT filter: token valid. userId={}, email={}", userId, email);

                    User user = userService.findById(Long.valueOf(userId))
                            .orElseThrow(() -> new RuntimeException("User not found for id " + userId));

                    Authentication current = SecurityContextHolder.getContext().getAuthentication();
                    boolean needsSet =
                            current == null
                                    || !current.isAuthenticated()
                                    || !(current.getPrincipal() instanceof User);

                    if (needsSet) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.warn("JWT filter: Authentication set in context for userId={}", userId);
                    } else {
                        log.warn("JWT filter: Existing Authentication has desired principal, keeping it");
                    }

                } else {
                    log.warn("JWT filter: token invalid/expired");
                }
            } catch (Exception e) {
                log.warn("JWT authentication failed", e);
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "AUTH-TOKEN".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
