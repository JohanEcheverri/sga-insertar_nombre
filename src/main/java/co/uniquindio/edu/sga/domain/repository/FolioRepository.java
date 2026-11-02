package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FolioRepository {

    void guardar(Folio folio);

    Optional<Folio> buscarPorId(FolioId id);

    /** Un folio pertenece a una sola reserva (7.7). */
    Optional<Folio> buscarPorReservaId(ReservaId reservaId);

    /** Folios con al menos un pago registrado entre las fechas (reporte de ingresos). */
    List<Folio> buscarConPagosEntre(LocalDateTime desde, LocalDateTime hasta);

    /** Folios de reservas canceladas / no-show con penalidades (reporte L-19). */
    List<Folio> buscarTodos();
}
