package co.uniquindio.edu.sga.infraestructure.rest.exception;

import java.time.Instant;

/** Estructura JSON uniforme para los errores enviados por la API REST. */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
