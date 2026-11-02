package co.uniquindio.edu.sga.domain.service;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.ConfiguracionAlojamiento;
import co.uniquindio.edu.sga.domain.valueobject.TipoCargo;

import java.time.LocalDateTime;

/** 7.5 - No-show: a partir de la hora límite del día de entrada; aplica la consecuencia de la política congelada. */
public class NoShowProcesamientoServicio {

    public CancelacionReservaService.Resultado ejecutarNoShow(Reserva reserva, Folio folio,
                                                              ConfiguracionAlojamiento configuracion,
                                                              LocalDateTime ahora) {
        CancelacionReservaService.exigirFolioDeLaReserva(reserva, folio);
        LocalDateTime limite = reserva.getEstancia().fechaEntrada().atTime(configuracion.horaLimiteNoShow());
        if (ahora.isBefore(limite)) {
            throw new ReglaDominioException("El no-show solo puede declararse a partir de la hora límite ("
                    + configuracion.horaLimiteNoShow() + ") del día de entrada.");
        }
        reserva.marcarNoShow();                                // RN-08: solo desde CONFIRMADA
        return CancelacionReservaService.liquidar(reserva, folio,
                reserva.getPoliticaCancelacion().retencionPorNoShow(), TipoCargo.PENALIDAD_NO_SHOW,
                "Penalidad por no-show (política " + reserva.getPoliticaCancelacion().version().valor() + ")", ahora);
    }
}
