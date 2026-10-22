package co.uniquindio.edu.sga.domain.valueobject;

import java.time.LocalTime;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public record ConfiguracionAlojamiento(
    int umbralEdadFacturable,
    LocalTime entrada,
    LocalTime salida,
    int tiempoPreparacionHoras,
    int plazoConfirmacionHoras,
    LocalTime horaLimiteNoShow
) {
    public ConfiguracionAlojamiento{
        if (umbralEdadFacturable < 0) {
            throw new ReglaDominioException("El umbral de edad facturable no puede ser negativo.");
        }
        if (entrada == null || salida == null || horaLimiteNoShow == null) {
            throw new ReglaDominioException("Las horas de check-in, check-out y límite de no-show son obligatorias.");
        }
        if (tiempoPreparacionHoras <= 0 || plazoConfirmacionHoras <= 0) {
            throw new ReglaDominioException("Los tiempos de preparación y confirmación deben ser mayores a cero.");
        }
        // Validar que el No-Show no sea antes del Check-in
        if (horaLimiteNoShow.isBefore(entrada)) {
            throw new ReglaDominioException("La hora límite de no-show no puede ser anterior a la hora de check-in.");
        }
    }
}
