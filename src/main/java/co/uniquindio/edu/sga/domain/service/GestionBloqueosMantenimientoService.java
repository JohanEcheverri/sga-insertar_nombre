package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import org.springframework.stereotype.Component;

/**
 * RN-28 - No se puede aplicar un bloqueo a un apartamento con reservas activas para esa estancia.
 * Requiere consultar las reservas de otros agregados, por eso vive en un servicio de dominio.
 * RN-24 - Crear un bloqueo NO modifica el estado operativo del apartamento.
 */
@Component
public class GestionBloqueosMantenimientoService {

    private final ReservaRepository reservaRepository;

    public GestionBloqueosMantenimientoService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public Bloqueo crearBloqueo(Apartamento apartamento, RangoFechas rango, String motivo) {
        if (!apartamento.isActivo()) {
            throw new ReglaDominioException("No se puede bloquear un apartamento retirado.");
        }
        for (Reserva r : reservaRepository.buscarActivasPorApartamento(apartamento.getId())) {
            if (rango.intersecta(r.getEstancia().rangoFechas())) {
                throw new ReglaDominioException(
                        "RN-28 - No se puede bloquear: el apartamento tiene una reserva activa en esas fechas.");
            }
        }
        return Bloqueo.crear(apartamento.getId(), rango, motivo);
    }
}
