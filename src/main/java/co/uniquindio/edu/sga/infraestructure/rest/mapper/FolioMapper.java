package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Folio;

/** Convierte Folio al modelo público de la API. */
public interface FolioMapper {
    static FolioResponse aRespuesta(Folio folio) {
        return new FolioResponse(folio.getId().valor().toString(),
                folio.getReservaId().valor().toString(), folio.getEstado(), folio.getCargos(),
                folio.getPagos(), folio.totalCargos(), folio.totalPagos(),
                folio.saldoPendiente(), folio.getAutorizacionCierre());
    }

    public record FolioResponse(String id, String reservaId, Object estado, Object cargos,
                                Object pagos, Object totalCargos, Object totalPagos,
                                Object saldoPendiente, Object autorizacionCierre) { }
}
