package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;

/** Datos de entrada de la API para estado usuario. */
public record EstadoUsuarioRequest(@NotNull Boolean activo) { }
