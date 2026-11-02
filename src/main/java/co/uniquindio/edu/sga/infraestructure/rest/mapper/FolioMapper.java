package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.application.result.FolioDetalle;
import co.uniquindio.edu.sga.application.dto.response.AutorizacionCierreResponse;
import co.uniquindio.edu.sga.application.dto.response.CargoResponse;
import co.uniquindio.edu.sga.application.dto.response.FolioResponse;

import java.util.List;

/** Convierte Folio al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface FolioMapper {
    default FolioResponse aRespuesta(FolioDetalle folio) {
        List<CargoResponse> cargos = folio.cargos().stream()
                .map(c -> new CargoResponse(c.tipo().name(), c.concepto(), c.monto(), c.fecha()))
                .toList();
        return new FolioResponse(folio.id().toString(), folio.reservaId().toString(),
                folio.estado().name(), cargos, folio.pagos().size(), folio.totalCargos(),
                folio.totalPagos(), folio.saldo(), null);
    }

    default FolioResponse aRespuesta(Folio folio) {
        List<CargoResponse> cargos = folio.getCargos().stream()
                .map(c -> new CargoResponse(c.tipoCargo().name(), c.concepto(),
                        c.monto().valor(), c.fechaRegistro()))
                .toList();
        var autorizacion = folio.getAutorizacionCierre();
        var cierre = autorizacion == null ? null
                : new AutorizacionCierreResponse(autorizacion.autor(), autorizacion.motivo(), autorizacion.fecha());
        return new FolioResponse(folio.getId().valor().toString(), folio.getReservaId().valor().toString(),
                folio.getEstado().name(), cargos, folio.getPagos().size(), folio.totalCargos(),
                folio.totalPagos(), folio.saldoPendiente(), cierre);
    }
}
