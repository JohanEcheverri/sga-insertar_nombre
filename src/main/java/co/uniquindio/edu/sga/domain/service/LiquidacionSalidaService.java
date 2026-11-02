package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.EstadoFolio;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import org.springframework.stereotype.Component;

/**
 * RN-29 - La salida no se concreta sin cerrar el folio. Reserva, Folio y Apartamento son agregados
 * separados: el servicio los coordina sin fusionar sus fronteras de consistencia.
 * (El cierre con saldo pendiente lo autoriza el administrador al cerrar el folio, RN-17.)
 */
@Component
public class LiquidacionSalidaService {

    public void registrarSalida(Reserva reserva, Folio folio, Apartamento apartamento) {
        if (!folio.getReservaId().equals(reserva.getId())) {
            throw new ReglaDominioException("El folio no pertenece a la reserva indicada.");
        }
        if (!reserva.getApartamentoId().equals(apartamento.getId())) {
            throw new ReglaDominioException("El apartamento no corresponde al asignado en la reserva.");
        }
        if (reserva.getEstado() != EstadoReserva.EN_CURSO) {
            throw new ReglaDominioException("Solo una reserva EN_CURSO puede registrar su salida.");
        }
        if (folio.getEstado() != EstadoFolio.CERRADO) {
            throw new ReglaDominioException("RN-29 - La salida no se puede concretar sin que se cierre el folio.");
        }
        if (apartamento.getEstadoOperativo() != EstadoOperativo.OCUPADO) {
            throw new ReglaDominioException("El apartamento debe estar OCUPADO para registrar la salida.");
        }
        reserva.finalizar();
        apartamento.marcarPendientePreparacion();
    }
}
