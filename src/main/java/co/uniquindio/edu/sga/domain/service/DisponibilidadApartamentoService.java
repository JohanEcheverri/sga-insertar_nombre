package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.AforoExcedidoException;
import co.uniquindio.edu.sga.domain.exception.ApartamentoNoDisponibleException;
import co.uniquindio.edu.sga.domain.exception.DomainException;
import co.uniquindio.edu.sga.domain.repository.BloqueoRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.time.Duration;

/**
 * 3.3 - Disponibilidad de un apartamento para una estancia: activo, capacidad suficiente (RN-02),
 * sin reservas activas solapadas (RN-01), sin bloqueos vigentes (RN-07) y respetando el tiempo de
 * preparación (RN-20). El estado operativo NO interviene (RN-24): un apartamento FUERA_DE_SERVICIO
 * sin bloqueo sigue siendo vendible a futuro.
 */
public class DisponibilidadApartamentoService {

    private final ReservaRepository reservaRepository;
    private final BloqueoRepository bloqueoRepository;

    public DisponibilidadApartamentoService(ReservaRepository reservaRepository, BloqueoRepository bloqueoRepository) {
        this.reservaRepository = reservaRepository;
        this.bloqueoRepository = bloqueoRepository;
    }

    /**
     * @param reservaAExcluir reserva que se está modificando (no debe chocar consigo misma); puede ser null
     * @throws DomainException con el motivo de negocio si el apartamento no está disponible
     */
    public void verificarDisponibilidad(Apartamento apartamento, Estancia estancia, int totalOcupantes,
                                        ConfiguracionAlojamiento configuracion, ReservaId reservaAExcluir) {
        if (!apartamento.isActivo()) {
            throw new ApartamentoNoDisponibleException("El apartamento " + apartamento.getIdentificadorUnico() + " no está activo.");
        }
        if (!apartamento.capacidadPermitida(totalOcupantes)) {
            throw new AforoExcedidoException(apartamento.getCapacidad().aforoMax(), totalOcupantes);
        }

        RangoFechas rango = estancia.rangoFechas();
        boolean recambioImposible = ventanaRecambioHoras(configuracion) < configuracion.tiempoPreparacionHoras();

        for (Reserva otra : reservaRepository.buscarActivasPorApartamento(apartamento.getId())) {
            if (otra.getId().equals(reservaAExcluir)) continue;
            RangoFechas rangoOtra = otra.getEstancia().rangoFechas();
            if (rango.intersecta(rangoOtra)) {
                throw new ApartamentoNoDisponibleException(
                        "RN-01 - El apartamento ya tiene una reserva activa que solapa con las fechas solicitadas.");
            }
            // RN-20 - si el tiempo de preparación no cabe entre salida y entrada, no hay recambio el mismo día.
            if (recambioImposible && (rangoOtra.fechaFin().equals(rango.fechaInicio())
                    || rango.fechaFin().equals(rangoOtra.fechaInicio()))) {
                throw new ApartamentoNoDisponibleException(
                        "RN-20 - No se respeta el tiempo de preparación entre la salida de un grupo y la entrada del siguiente.");
            }
        }

        for (Bloqueo bloqueo : bloqueoRepository.buscarActivosPorApartamento(apartamento.getId())) {
            if (bloqueo.seCruzaConEstancia(estancia)) {
                throw new ApartamentoNoDisponibleException(
                        "RN-07 - El apartamento tiene un bloqueo vigente en las fechas solicitadas (" + bloqueo.getMotivo() + ").");
            }
        }
    }

    /** Versión booleana para búsquedas: no lanza excepciones de negocio. */
    public boolean estaDisponible(Apartamento apartamento, Estancia estancia, int totalOcupantes,
                                  ConfiguracionAlojamiento configuracion) {
        try {
            verificarDisponibilidad(apartamento, estancia, totalOcupantes, configuracion, null);
            return true;
        } catch (DomainException e) {
            return false;
        }
    }

    /** Horas entre la hora de salida y la hora de entrada del mismo día (3.3). */
    private static long ventanaRecambioHoras(ConfiguracionAlojamiento configuracion) {
        return Math.max(0, Duration.between(configuracion.salida(), configuracion.entrada()).toHours());
    }
}
