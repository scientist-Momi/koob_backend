package com.koob.Koob_backend.config;

import com.koob.Koob_backend.oAuth2User.CustomOAuth2User;
import com.koob.Koob_backend.oAuth2User.CustomOAuth2UserService;
import com.koob.Koob_backend.oAuth2User.CustomOidcUserService;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOidcUserService customOidcUserService, JwtUtil jwtUtil, UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler() {
        return new JwtAuthenticationSuccessHandler(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            if ("OPTIONS".equals(request.getMethod())) {
                                response.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                            }
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
//                        .requestMatchers("/api/v1/books/search2/**").permitAll()
                        .requestMatchers("/api/v1/auth/status").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
                        // Require authentication for /me so the filter must populate the principal
                        .requestMatchers("/api/v1/auth/me").authenticated()
                        // Everything else also requires auth
                        .anyRequest().authenticated()

                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)          // for OAuth2 user info
                                .oidcUserService(customOidcUserService)       // for OIDC user info
                        )
                        .successHandler(jwtAuthenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .deleteCookies("AUTH-TOKEN")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
