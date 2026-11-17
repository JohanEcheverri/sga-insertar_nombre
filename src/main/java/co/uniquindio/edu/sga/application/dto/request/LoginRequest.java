package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Datos de entrada de la API para login. */
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password) { }
