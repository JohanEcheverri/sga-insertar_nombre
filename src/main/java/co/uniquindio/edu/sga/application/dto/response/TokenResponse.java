package co.uniquindio.edu.sga.application.dto.response;

import java.time.Instant;
import java.util.Collection;

/** Datos que la API devuelve para token. */
public record TokenResponse(String token, String type, Instant expireAt, Collection<String> roles) { }
