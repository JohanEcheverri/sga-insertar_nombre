package co.uniquindio.edu.sga.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import co.uniquindio.edu.sga.application.command.*;
import co.uniquindio.edu.sga.application.exception.*;
import co.uniquindio.edu.sga.application.port.*;
import co.uniquindio.edu.sga.application.result.*;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.application.support.*;
import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.exception.*;
import co.uniquindio.edu.sga.domain.repository.*;
import co.uniquindio.edu.sga.domain.service.*;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RN-21 — Cancela automáticamente las reservas PENDIENTE que superaron el plazo de confirmación, sin
 * penalidad y sin intervención manual. La infraestructura lo dispara periódicamente
 * (p. ej. un @Scheduled cada minuto); este caso de uso no sabe de planificadores.
 */
@Service
@Transactional
public class VencerReservasPendientesUseCase {

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final CancelacionReservaService cancelacionService;
    private final NotificacionPort notificacion;
    private final Clock clock;

    public VencerReservasPendientesUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                           FolioRepository folioRepository, CancelacionReservaService cancelacionService,
                                           NotificacionPort notificacion, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.cancelacionService = cancelacionService;
        this.notificacion = notificacion;
        this.clock = clock;
    }

    /** @return cantidad de reservas canceladas por vencimiento */
    public int ejecutar() {
        int plazo = Cargas.alojamiento(alojamientoRepository).getConfiguracion().plazoConfirmacionHoras();
        LocalDateTime ahora = LocalDateTime.now(clock);
        int canceladas = 0;
        for (Reserva reserva : reservaRepository.buscarPorEstado(EstadoReserva.PENDIENTE)) {
            if (!reserva.haVencidoPlazoConfirmacion(ahora, plazo)) continue;
            try {
                Folio folio = Cargas.folioDeReserva(folioRepository, reserva.getId().valor());
                cancelacionService.procesarVencimiento(reserva, folio, ahora);
                reservaRepository.guardar(reserva);
                folioRepository.guardar(folio);
                canceladas++;
                Seguro.ejecutar("notificar vencimiento", () -> notificacion.reservaCancelada(reserva));
            } catch (RuntimeException e) {
                // una reserva con problemas no debe impedir procesar las demás
                Seguro.ejecutar("vencer reserva " + reserva.getId().valor(), () -> { throw e; });
            }
        }
        return canceladas;
    }
}
