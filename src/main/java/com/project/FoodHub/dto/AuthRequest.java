package com.project.FoodHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "Por favor ingresa un correo electrónico.")
    @Size(max = 20, message = "El correo debe tener máximo 20 caracteres")
    private String identificador;

    @NotBlank(message = "Por favor ingresa una contraseña")
    private String contrasenia;
}
