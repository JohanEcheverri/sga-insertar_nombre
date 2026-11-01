package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;

import java.util.List;
import java.util.Optional;

public interface FolioRepository {

    Folio guardar(Folio folio);

    Optional<Folio> obtenerPorId(ReservaId id);

    Optional<Folio> obtenerPorReservaCodigo(ReservaId codigoReserva);

    List<Folio> listarTodos();
}