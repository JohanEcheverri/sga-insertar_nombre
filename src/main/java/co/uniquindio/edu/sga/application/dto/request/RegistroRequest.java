package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El documento es obligatorio") String documento,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") String correo,
        @NotBlank(message = "El teléfono es obligatorio") String telefono,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        @NotNull(message = "La fecha de nacimiento es obligatoria") @PastOrPresent(message = "La fecha de nacimiento no puede ser futura") LocalDate fechaNacimiento) { }
