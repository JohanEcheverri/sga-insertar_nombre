package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio de dominio (Guía 05)
 * RN-13: la retención por cancelación y por no-show se determina con la versión de política
 * congelada en la reserva (RN-22), no con la vigente al momento del hecho.
 */
public class RetencionService {

    private final ConfiguracionAlojamiento configuracion;

    public RetencionService(ConfiguracionAlojamiento configuracion) {
        if (configuracion == null) {
            throw new ReglaDominioException("La configuración del alojamiento es obligatoria.");
        }
        this.configuracion = configuracion;
    }

    public Dinero porCancelacion(Reserva reserva, PoliticaCancelacion politica, LocalDateTime momento) {
        verificarPoliticaCongelada(reserva, politica);
        LocalDateTime inicioEstancia = reserva.getEstancia().fechaEntrada().atTime(configuracion.entrada());
        long horasAntelacion = Math.max(0, ChronoUnit.HOURS.between(momento, inicioEstancia));

        // La política calcula y retorna directamente el Dinero a retener pasándole el valor congelado
        return politica.calcularRetencionCancelacion(horasAntelacion, reserva.getValorCongelado());
    }

    public Dinero porNoShow(Reserva reserva, PoliticaCancelacion politica) {
        verificarPoliticaCongelada(reserva, politica);

        // La política retorna directamente el Dinero de retención por no-show
        return politica.calcularRetencionNoShow(reserva.getValorCongelado());
    }

    private void verificarPoliticaCongelada(Reserva reserva, PoliticaCancelacion politica) {
        if (reserva.getPoliticaCancelacion() == null ||
                !politica.version().equals(reserva.getPoliticaCancelacion().version())) {
            throw new ReglaDominioException(
                    "La retención debe calcularse con la versión de política congelada en la reserva.");
        }
    }
}