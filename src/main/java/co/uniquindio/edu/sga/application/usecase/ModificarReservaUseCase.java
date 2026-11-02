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
 * PUT /api/reservas/{codigo} — RN-14: revalida las condiciones de creación, recalcula el valor con
 * las tarifas VIGENTES y registra la diferencia como ajuste en el folio. La política congelada no cambia.
 */
@Service
@Transactional
public class ModificarReservaUseCase {

    /** apartamentoId y horaEstimadaLlegada son opcionales: null conserva el valor actual. */
    public record Command(UUID reservaId, UUID apartamentoId, LocalDate entrada, LocalDate salida,
                          List<OcupanteDatos> ocupantes, LocalTime horaEstimadaLlegada, Solicitante solicitante) { }

    public record Result(UUID reservaId, BigDecimal valorAnterior, BigDecimal valorNuevo, BigDecimal diferencia,
                         ReservaDetalle detalle) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;
    private final FolioRepository folioRepository;
    private final DisponibilidadApartamentoService disponibilidad;
    private final CotizadorEstanciaService cotizador;
    private final ValidacionAforoOcupantesService validacionGrupo;
    private final Clock clock;

    public ModificarReservaUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository,
                                   FolioRepository folioRepository, DisponibilidadApartamentoService disponibilidad,
                                   CotizadorEstanciaService cotizador, ValidacionAforoOcupantesService validacionGrupo,
                                   Clock clock) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
        this.folioRepository = folioRepository;
        this.disponibilidad = disponibilidad;
        this.cotizador = cotizador;
        this.validacionGrupo = validacionGrupo;
        this.clock = clock;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        ConfiguracionAlojamiento config = alojamiento.getConfiguracion();
        Reserva reserva = Cargas.reserva(reservaRepository, cmd.reservaId());
        cmd.solicitante().exigirAccesoA(reserva);
        Folio folio = Cargas.folioDeReserva(folioRepository, cmd.reservaId());
        if (folio.getEstado() != EstadoFolio.ABIERTO) {
            throw new OperacionNoPermitidaException("El folio está cerrado: no se puede registrar el ajuste de la modificación.");
        }

        ApartamentoId aptoId = cmd.apartamentoId() == null ? reserva.getApartamentoId() : ApartamentoId.of(cmd.apartamentoId());
        Apartamento apartamento = Cargas.apartamento(alojamiento, aptoId.valor());
        Estancia estancia = new Estancia(cmd.entrada(), cmd.salida());
        List<Ocupante> ocupantes = OcupanteDatos.aDominio(cmd.ocupantes());
        LocalTime hora = cmd.horaEstimadaLlegada() == null ? reserva.getHoraEstimadaLlegada() : cmd.horaEstimadaLlegada();

        validacionGrupo.validarGrupo(reserva.getTitular(), ocupantes, estancia, config.umbralEdadFacturable());
        disponibilidad.verificarDisponibilidad(apartamento, estancia, ocupantes.size(), config, reserva.getId());

        List<FechaNacimiento> fechas = ocupantes.stream().map(Ocupante::fechaNacimiento)
                .collect(Collectors.toCollection(ArrayList::new));
        Dinero nuevoValor = cotizador.cotizar(alojamiento, apartamento.getId(), estancia, fechas).total();

        BigDecimal anterior = reserva.getValorCongelado().valor();
        BigDecimal diferencia = nuevoValor.valor().subtract(anterior);
        LocalDateTime ahora = LocalDateTime.now(clock);

        reserva.modificar(aptoId, ocupantes, estancia, hora, nuevoValor, ahora);     // valida estado y RN-04

        if (diferencia.signum() > 0) {
            folio.registrarCargo(new Cargo(TipoCargo.AJUSTE_MODIFICACION, "Ajuste por modificación de la reserva",
                    new Dinero(diferencia), ahora));
        } else if (diferencia.signum() < 0) {
            folio.registrarCargo(new Cargo(TipoCargo.AJUSTE_A_FAVOR, "Ajuste a favor por modificación de la reserva",
                    new Dinero(diferencia.negate()), ahora));
        }
        reservaRepository.guardar(reserva);
        folioRepository.guardar(folio);
        return new Result(reserva.getId().valor(), anterior, nuevoValor.valor(), diferencia, ReservaDetalle.de(reserva));
    }
}
