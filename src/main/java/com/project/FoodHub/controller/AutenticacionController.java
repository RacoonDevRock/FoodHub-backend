package com.project.FoodHub.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.project.FoodHub.config.service.IUserDetailService;
import com.project.FoodHub.dto.*;
import com.project.FoodHub.exception.CreadorNoEncontradoException;
import com.project.FoodHub.exception.IncorrectCredentials;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody AuthRequest authRequest) throws IncorrectCredentials {
        return ResponseEntity.ok(userDetailService.iniciarSesion(authRequest));
    }

}
