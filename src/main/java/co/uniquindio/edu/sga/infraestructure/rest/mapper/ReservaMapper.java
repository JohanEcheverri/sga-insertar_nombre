package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import co.uniquindio.edu.sga.domain.entity.Reserva;

/** Convierte Reserva al modelo público de la API. */
public interface ReservaMapper {

    public static ReservaResponse aRespuesta(Reserva reserva) {
        return new ReservaResponse(reserva.getId().valor().toString(),
                reserva.getFolioId().valor().toString(), reserva.getApartamentoId().valor().toString(),
                reserva.getTitular(), reserva.getOcupantes(), reserva.getEstancia(),
                reserva.getIdentificadorExterno(), reserva.getHoraEstimadaLlegada(),
                reserva.getCanalOrigen(), reserva.getEstado(), reserva.getValorCongelado(),
                reserva.getPoliticaCancelacion(), reserva.getFechaCreacion());
    }

    public record ReservaResponse(String codigo, String folioId, String apartamentoId,
                                  Object titular, Object ocupantes, Object estancia,
                                  String identificadorExterno, Object horaEstimadaLlegada,
                                  Object canalOrigen, Object estado, Object valorCongelado,
                                  Object politicaCancelacion, Object fechaCreacion) { }
}
