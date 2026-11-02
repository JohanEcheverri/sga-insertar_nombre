package co.uniquindio.edu.sga.application.usecase;

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
 * POST /api/reservas — Crea una reserva PENDIENTE y abre su folio con el cargo de alojamiento.
 * Orden: grupo (RN-23) → disponibilidad (activo, RN-02, RN-01, RN-07, RN-20) → cotización
 * (RN-05/06) → Reserva.crear (RN-04, RN-22). RN-19: el mismo canal + identificador externo no
 * crea dos reservas: devuelve la ya existente.
 */
public class CrearReservaUseCase {

    public record Command(TitularDatos titular, List<OcupanteDatos> ocupantes, UUID apartamentoId,
                          LocalDate entrada, LocalDate salida, LocalTime horaEstimadaLlegada,
                          CanalOrigen canal, String identificadorExterno) { }

    public record Result(UUID reservaId, UUID folioId, EstadoReserva estado, double valorTotal,
                         double anticipoRequerido, boolean yaExistia) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final DisponibilidadApartamentoService disponibilidad;
    private final CotizadorEstanciaService cotizador;
    private final ValidacionAforoOcupantesService validacionGrupo;
    private final NotificacionPort notificacion;
    private final Clock clock;

    public CrearReservaUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                               FolioRepository folioRepository, DisponibilidadApartamentoService disponibilidad,
                               CotizadorEstanciaService cotizador, ValidacionAforoOcupantesService validacionGrupo,
                               NotificacionPort notificacion, Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.disponibilidad = disponibilidad;
        this.cotizador = cotizador;
        this.validacionGrupo = validacionGrupo;
        this.notificacion = notificacion;
        this.clock = clock;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        ConfiguracionAlojamiento config = alojamiento.getConfiguracion();
        boolean tieneIdExterno = cmd.identificadorExterno() != null && !cmd.identificadorExterno().isBlank();
        if (cmd.canal() == CanalOrigen.EXTERNO && !tieneIdExterno) {
            throw new ReglaDominioException("Una reserva del canal EXTERNO debe traer su identificador externo.");
        }

        // RN-19 - idempotencia
        if (tieneIdExterno) {
            Optional<Reserva> existente = reservaRepository
                    .buscarPorCanalEIdentificadorExterno(cmd.canal(), cmd.identificadorExterno().trim());
            if (existente.isPresent()) {
                Reserva r = existente.get();
                return new Result(r.getId().valor(), r.getFolioId().valor(), r.getEstado(),
                        r.getValorCongelado().valor(), config.anticipoPara(r.getValorCongelado()).doubleValue(), true);
            }
        }

        Apartamento apartamento = Cargas.apartamento(alojamiento, cmd.apartamentoId());
        Estancia estancia = new Estancia(cmd.entrada(), cmd.salida());                     // RN-03
        Titular titular = cmd.titular().aDominio();
        List<Ocupante> ocupantes = OcupanteDatos.aDominio(cmd.ocupantes());

        validacionGrupo.validarGrupo(titular, ocupantes, estancia, config.umbralEdadFacturable());   // RN-23
        disponibilidad.verificarDisponibilidad(apartamento, estancia, ocupantes.size(), config, null);

        List<FechaNacimiento> fechas = ocupantes.stream().map(Ocupante::fechaNacimiento)
                .collect(Collectors.toCollection(ArrayList::new));
        Dinero valor = cotizador.cotizar(alojamiento, apartamento.getId(), estancia, fechas).total();

        LocalDateTime ahora = LocalDateTime.now(clock);
        Reserva reserva = Reserva.crear(ReservaId.generar(), FolioId.generar(), apartamento.getId(), titular,
                ocupantes, estancia, cmd.identificadorExterno(), cmd.horaEstimadaLlegada(), cmd.canal(),
                valor, alojamiento.getPoliticaVigente(), ahora);                          // RN-04, RN-22

        Folio folio = Folio.crear(reserva.getFolioId(), reserva.getId());
        folio.registrarCargo(new Cargo(TipoCargo.ALOJAMIENTO, "Alojamiento " + apartamento.getIdentificadorUnico()
                + " del " + estancia.fechaEntrada() + " al " + estancia.fechaSalida(), valor, ahora));

        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        Seguro.ejecutar("notificar reserva creada", () -> notificacion.reservaCreada(reserva));

        return new Result(reserva.getId().valor(), folio.getId().valor(), reserva.getEstado(), valor.valor(),
                config.anticipoPara(valor).doubleValue(), false);
    }
}
