package com.project.FoodHub.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.project.FoodHub.config.security.service.IUserDetailService;
import com.project.FoodHub.dto.*;
import com.project.FoodHub.exception.CreadorNoEncontradoException;
import com.project.FoodHub.exception.IncorrectCredentials;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AutenticacionController {

    private final IUserDetailService userDetailService;

    @PostMapping("/registrar")
    public ResponseEntity<ConfirmacionResponse> register(@Valid @RequestBody CreadorRequest request){
        return ResponseEntity.ok(userDetailService.registrar(request));
    }

    @GetMapping("/confirmar")
    public ResponseEntity<MessageResponse> confirmarCuenta(@RequestParam("token") String token) {
        try {
            MessageResponse response = userDetailService.confirmAccount(token);
            return ResponseEntity.ok(response);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("El token ha expirado."));
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Token inválido."));
        } catch (CreadorNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) throws IncorrectCredentials {
        AuthResponse authResponse = userDetailService.iniciarSesion(authRequest);

        Cookie jwtCookie = new Cookie("JWT-TOKEN", authResponse.getToken());
        jwtCookie.setHttpOnly(true);
//        jwtCookie.setSecure(true); //en produccion (HTTPS)
        jwtCookie.setMaxAge(1800); // 30 min en seg
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie); // agrega cookie

        String sameSiteCookie = String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure; SameSite=Lax",
                jwtCookie.getName(), jwtCookie.getValue(), jwtCookie.getMaxAge(), jwtCookie.getPath());

        response.setHeader("Set-Cookie", sameSiteCookie);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/verify-auth")
    public ResponseEntity<Boolean> verificarAutenticacion(HttpServletRequest request) {
        try {
            // Buscar la cookie JWT-TOKEN
            String token = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("JWT-TOKEN"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}
