package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.application.result.FolioDetalle;
import co.uniquindio.edu.sga.application.dto.response.AutorizacionCierreResponse;
import co.uniquindio.edu.sga.application.dto.response.CargoResponse;
import co.uniquindio.edu.sga.application.dto.response.FolioResponse;
import co.uniquindio.edu.sga.application.dto.response.PagoResponse;

import java.util.List;

/** Convierte Folio al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface FolioMapper {
    default FolioResponse aRespuesta(FolioDetalle folio) {
        List<CargoResponse> cargos = folio.cargos().stream()
                .map(c -> new CargoResponse(c.tipo().name(), c.concepto(), c.monto(), c.fecha(), c.esCredito()))
                .toList();
        List<PagoResponse> pagos = folio.pagos().stream()
                .map(p -> new PagoResponse(p.medio().name(), p.monto(), p.referencia(), p.fecha())).toList();
        return new FolioResponse(folio.id().toString(), folio.reservaId().toString(),
                folio.estado().name(), cargos, pagos, folio.pagos().size(), folio.totalCargos(),
                folio.totalPagos(), folio.saldo(), folio.saldoAFavor(), null);
    }

    default FolioResponse aRespuesta(Folio folio) {
        List<CargoResponse> cargos = folio.getCargos().stream()
                .map(c -> new CargoResponse(c.tipoCargo().name(), c.concepto(),
                        c.monto().valor(), c.fechaRegistro(), c.tipoCargo().esCredito()))
                .toList();
        var autorizacion = folio.getAutorizacionCierre();
        List<PagoResponse> pagos = folio.getPagos().stream()
                .map(p -> new PagoResponse(p.medio().name(), p.monto().valor(), p.referencia(), p.fechaRegistro())).toList();
        var cierre = autorizacion == null ? null
                : new AutorizacionCierreResponse(autorizacion.autor(), autorizacion.motivo(), autorizacion.fecha());
        return new FolioResponse(folio.getId().valor().toString(), folio.getReservaId().valor().toString(),
                folio.getEstado().name(), cargos, pagos, folio.getPagos().size(), folio.totalCargos(),
                folio.totalPagos(), folio.saldo(), folio.saldoAFavor(), cierre);
    }
}
