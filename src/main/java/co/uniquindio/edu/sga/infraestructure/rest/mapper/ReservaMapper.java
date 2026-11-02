package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.application.dto.response.ReservaDetalleResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;
import co.uniquindio.edu.sga.application.result.ReservaDetalle;

import java.util.List;

/** Convierte Reserva al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface ReservaMapper {

    default ReservaDetalleResponse aDetalle(ReservaDetalle reserva) {
        return new ReservaDetalleResponse(reserva.id().toString(), reserva.folioId().toString(), reserva.apartamentoId().toString(),
                reserva.nombreTitular(), reserva.apellidoTitular(), reserva.usuarioId() == null ? null : reserva.usuarioId().toString(),
                reserva.correoTitular(), reserva.telefonoTitular(), reserva.documentoTitular(), reserva.detalleOcupantes(),
                reserva.entrada(), reserva.salida(), reserva.identificadorExterno(), reserva.horaEstimadaLlegada(),
                reserva.canal().name(), reserva.estado().name(), reserva.valor(), reserva.versionPolitica(), reserva.fechaCreacion());
    }

    default ReservaResumenResponse aResumen(ReservaDetalle reserva) {
        return new ReservaResumenResponse(reserva.id().toString(), reserva.titular(), reserva.apartamentoId().toString(),
                reserva.entrada(), reserva.salida(), reserva.estado().name(), reserva.valor());
    }

    default ReservaDetalleResponse aDetalle(Reserva reserva) {
        var titular = reserva.getTitular();
        List<String> ocupantes = reserva.getOcupantes().stream()
                .map(o -> o.nombreCompleto().obtenerNombreCompleto() + " (" + o.documentoIdentidad().numero() + ")")
                .toList();
        return new ReservaDetalleResponse(reserva.getId().valor().toString(),
                reserva.getFolioId().valor().toString(), reserva.getApartamentoId().valor().toString(),
                titular.nombreCompleto().nombre(), titular.nombreCompleto().apellido(),
                titular.usuarioId().valor().toString(), titular.correo().direccion(),
                titular.telefono().numeroTelefono(), titular.documentoIdentidad().numero(),
                ocupantes, reserva.getEstancia().fechaEntrada(), reserva.getEstancia().fechaSalida(),
                reserva.getIdentificadorExterno(), reserva.getHoraEstimadaLlegada(),
                reserva.getCanalOrigen().name(), reserva.getEstado().name(),
                reserva.getValorCongelado().valor(),
                reserva.getPoliticaCancelacion().version().valor(), reserva.getFechaCreacion());
    }

    default ReservaResumenResponse aResumen(Reserva reserva) {
        return new ReservaResumenResponse(reserva.getId().valor().toString(),
                reserva.getTitular().nombreCompleto().obtenerNombreCompleto(),
                reserva.getApartamentoId().valor().toString(), reserva.getEstancia().fechaEntrada(),
                reserva.getEstancia().fechaSalida(), reserva.getEstado().name(),
                reserva.getValorCongelado().valor());
    }
}
