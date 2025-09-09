package com.koob.Koob_backend.config;

import com.koob.Koob_backend.oAuth2User.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(principal.getUser().getId().toString(), principal.getUser().getEmail());

        Cookie cookie = new Cookie("AUTH-TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // change to true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));

        response.addCookie(cookie);
        response.sendRedirect("/"); // redirect to frontend/home
    }
}
