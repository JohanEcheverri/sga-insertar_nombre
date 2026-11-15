package co.uniquindio.edu.sga.infraestructure.rest.exception;

import java.time.Instant;
import java.util.Map;

/** Cuerpo estándar para errores REST. */
public record ErrorResponse(Instant timestamp, int status, String error, String mensaje,
                            String path, Map<String, String> errores) { }
