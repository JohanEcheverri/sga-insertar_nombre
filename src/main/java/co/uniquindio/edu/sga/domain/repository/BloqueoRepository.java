package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.valueobject.IdentificacionApartamento;
import co.uniquindio.edu.sga.domain.valueobject.Periodo;

import java.util.List;

public interface BloqueoRepository {

    List<Bloqueo> buscarVigentesPorApartamento(IdentificacionApartamento apartamento, Periodo periodo);
}
