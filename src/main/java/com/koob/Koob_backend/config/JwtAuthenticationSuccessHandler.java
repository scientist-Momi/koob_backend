package com.koob.Koob_backend.config;

import com.koob.Koob_backend.oAuth2User.CustomOAuth2User;
import com.koob.Koob_backend.oAuth2User.CustomOidcUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Object principalObj = authentication.getPrincipal();
        String userId;
        String email;

        if (principalObj instanceof CustomOAuth2User oauth2Principal) {
            userId = oauth2Principal.getUser().getId().toString();
            email = oauth2Principal.getUser().getEmail();
        } else if (principalObj instanceof CustomOidcUser oidcPrincipal) {
            userId = oidcPrincipal.getUser().getId().toString();
            email = oidcPrincipal.getUser().getEmail();
        } else {
            throw new IllegalStateException("Unsupported principal type: " + principalObj.getClass().getName());
        }

        String jwt = jwtUtil.generateToken(userId, email);

        boolean isSecure = false;
        String sameSite = "Lax";


        ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", jwt)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(jwtUtil.getExpirationMs() / 1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.sendRedirect("http://localhost:5173/dashboard");

    }
}
