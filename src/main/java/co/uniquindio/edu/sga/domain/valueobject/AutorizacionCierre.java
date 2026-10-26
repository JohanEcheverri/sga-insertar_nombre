package co.uniquindio.edu.sga.domain.valueobject;

import java.time.LocalDateTime;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record AutorizacionCierre(String autor, String motivo, LocalDateTime fecha) {
    public AutorizacionCierre {
        // Regla de autorización: deben quedar identificados el autor, el motivo y la fecha.
        if (autor == null || autor.isBlank()) {
            throw new ReglaDominioException("El autor autorizador no puede estar vacío.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("El motivo de la autorización es obligatorio.");
        }
        if (fecha == null) {
            throw new ReglaDominioException("La fecha de autorización no puede ser nula.");
        }
        autor = autor.trim();
        motivo = motivo.trim();
    }
}
