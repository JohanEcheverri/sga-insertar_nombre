package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import java.util.List;
import java.util.Optional;

/** Define las operaciones de consulta y persistencia de apartamento. */
public interface ApartamentoRepository {

    Apartamento guardar(Apartamento apartamento);

    Optional<Apartamento> obtenerPorIdentificacion(ApartamentoId identificacion);

    List<Apartamento> listarTodos();

    void eliminar(ApartamentoId identificacion);
}