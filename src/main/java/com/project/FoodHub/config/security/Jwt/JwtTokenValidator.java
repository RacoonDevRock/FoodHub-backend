package com.project.FoodHub.config.security.Jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.FoodHub.config.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        Cookie jwtCookie = getJwtCookie(request.getCookies());

        if (jwtCookie != null) {
            String jwtToken = jwtCookie.getValue();
            processJwtToken(jwtToken, response);
        }

        filterChain.doFilter(request, response);
    }

    private Cookie getJwtCookie(Cookie[] cookies) {
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("JWT-TOKEN".equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    private void processJwtToken(String jwtToken, HttpServletResponse response) throws IOException {
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            if (jwtUtils.isTokenExpired(decodedJWT)) {
                handleExpiredToken(response);
                return;
            }

            setSecurityContext(decodedJWT);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }

    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        Cookie expiredCookie = new Cookie("JWT-TOKEN", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
    }

    private void setSecurityContext(DecodedJWT decodedJWT) {
        String username = jwtUtils.extractUsername(decodedJWT);
        String role = decodedJWT.getClaim("role").asString();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, AuthorityUtils.createAuthorityList(role));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}