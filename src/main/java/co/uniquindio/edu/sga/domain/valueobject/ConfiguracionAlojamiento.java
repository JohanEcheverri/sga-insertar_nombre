package co.uniquindio.edu.sga.domain.valueobject;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

public record ConfiguracionAlojamiento(
    int umbralEdadFacturable,
    LocalTime entrada,
    LocalTime salida,
    int tiempoPreparacionHoras,
    int plazoConfirmacionHoras,
    LocalTime horaLimiteNoShow,
    double porcentajeAnticipo          // L-11: 0 = no se exige anticipo; 0.25 = 25 % del valor total
) {
    public ConfiguracionAlojamiento {
        if (umbralEdadFacturable < 0) {
            throw new ReglaDominioException("El umbral de edad facturable no puede ser negativo.");
        }
        if (entrada == null || salida == null || horaLimiteNoShow == null) {
            throw new ReglaDominioException("Las horas de check-in, check-out y límite de no-show son obligatorias.");
        }
        if (tiempoPreparacionHoras <= 0 || plazoConfirmacionHoras <= 0) {
            throw new ReglaDominioException("Los tiempos de preparación y confirmación deben ser mayores a cero.");
        }
        if (horaLimiteNoShow.isBefore(entrada)) {
            throw new ReglaDominioException("La hora límite de no-show no puede ser anterior a la hora de check-in.");
        }
        if (!Double.isFinite(porcentajeAnticipo) || porcentajeAnticipo < 0 || porcentajeAnticipo > 1) {
            throw new ReglaDominioException("El porcentaje de anticipo debe estar entre 0 y 1.");
        }
    }

    /** Constructor anterior (sin anticipo): se conserva para no romper código y pruebas existentes. */
    public ConfiguracionAlojamiento(int umbralEdadFacturable, LocalTime entrada, LocalTime salida,
                                    int tiempoPreparacionHoras, int plazoConfirmacionHoras,
                                    LocalTime horaLimiteNoShow) {
        this(umbralEdadFacturable, entrada, salida, tiempoPreparacionHoras,
                plazoConfirmacionHoras, horaLimiteNoShow, 0.0);
    }

    /** RN-26 - Anticipo exigido para confirmar: porcentaje del valor total, redondeado al peso (COP sin decimales). */
    public BigDecimal anticipoPara(Dinero valorTotal) {
        return valorTotal.valor()
                .multiply(BigDecimal.valueOf(porcentajeAnticipo))
                .setScale(0, RoundingMode.HALF_UP);
    }
}
