package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.application.dto.response.AutorizacionCierreResponse;
import co.uniquindio.edu.sga.application.dto.response.CargoResponse;
import co.uniquindio.edu.sga.application.dto.response.FolioResponse;

import java.util.List;

/** Convierte Folio al modelo público de la API. */
public interface FolioMapper {
    static FolioResponse aRespuesta(Folio folio) {
        List<CargoResponse> cargos = folio.getCargos().stream()
                .map(c -> new CargoResponse(c.tipoCargo().name(), c.concepto(),
                        c.monto().valor().doubleValue(), c.fechaRegistro()))
                .toList();
        var autorizacion = folio.getAutorizacionCierre();
        var cierre = autorizacion == null ? null
                : new AutorizacionCierreResponse(autorizacion.autor(), autorizacion.motivo(), autorizacion.fecha());
        return new FolioResponse(folio.getId().valor().toString(), folio.getReservaId().valor().toString(),
                folio.getEstado().name(), cargos, folio.getPagos().size(), folio.totalCargos().doubleValue(),
                folio.totalPagos().doubleValue(), folio.saldoPendiente().doubleValue(), cierre);
    }
}
