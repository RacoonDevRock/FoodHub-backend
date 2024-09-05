package com.project.FoodHub.controller;

import com.project.FoodHub.config.service.IUserDetailService;
import com.project.FoodHub.dto.*;
import com.project.FoodHub.exception.IncorrectCredentials;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
        return ResponseEntity.ok(userDetailService.confirmAccount(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> iniciarSesion(@Valid @RequestBody AuthRequest authRequest) throws IncorrectCredentials {
        return ResponseEntity.ok(userDetailService.iniciarSesion(authRequest));
    }

}
