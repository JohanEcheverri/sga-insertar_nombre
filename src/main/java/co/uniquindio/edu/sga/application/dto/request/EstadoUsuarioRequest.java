package co.uniquindio.edu.sga.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record EstadoUsuarioRequest(@NotNull Boolean activo) { }
