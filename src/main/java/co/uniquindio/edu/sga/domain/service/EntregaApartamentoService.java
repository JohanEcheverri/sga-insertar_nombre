package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;

import java.time.LocalDate;

/**
 * RN-10 / RN-11 - Registro (check-in). La Reserva no puede leer el estado operativo del Apartamento:
 * este servicio decide SI se entrega; cada agregado decide CÓMO cambia de estado.
 */
public class EntregaApartamentoService {

    public void registrarLlegada(Reserva reserva, Apartamento apartamento, LocalDate fechaActual) {
        if (!reserva.getApartamentoId().equals(apartamento.getId())) {
            throw new ReglaDominioException("El apartamento no corresponde al asignado en la reserva.");
        }
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaDominioException("RN-10 - El registro solo se permite sobre una reserva CONFIRMADA.");
        }
        if (fechaActual.isBefore(reserva.getEstancia().fechaEntrada())) {
            throw new ReglaDominioException("RN-10 - No se permite el registro antes de la fecha de entrada.");
        }
        if (!apartamento.puedeRecibirGrupo()) {
            throw new ReglaDominioException("RN-11 - El apartamento " + apartamento.getIdentificadorUnico()
                    + " no está PREPARADO (estado actual: " + apartamento.getEstadoOperativo() + ").");
        }
        reserva.iniciarEstancia();
        apartamento.marcarOcupado();
    }
}
