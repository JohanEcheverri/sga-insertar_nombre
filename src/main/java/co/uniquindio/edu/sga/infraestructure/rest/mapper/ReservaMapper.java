package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.application.dto.response.ReservaDetalleResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;

import java.util.List;

/** Convierte Reserva al modelo público de la API. */
public interface ReservaMapper {

    static ReservaDetalleResponse aDetalle(Reserva reserva) {
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
                reserva.getValorCongelado().valor().doubleValue(),
                reserva.getPoliticaCancelacion().version().valor(), reserva.getFechaCreacion());
    }

    static ReservaResumenResponse aResumen(Reserva reserva) {
        return new ReservaResumenResponse(reserva.getId().valor().toString(),
                reserva.getTitular().nombreCompleto().obtenerNombreCompleto(),
                reserva.getApartamentoId().valor().toString(), reserva.getEstancia().fechaEntrada(),
                reserva.getEstancia().fechaSalida(), reserva.getEstado().name(),
                reserva.getValorCongelado().valor().doubleValue());
    }
}
