package co.uniquindio.edu.sga.application.usecase;

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

/** POST /api/cotizaciones — Desglose noche por noche (RN-05, RN-06). No crea ni reserva nada. */
@Service
public class CotizarEstanciaUseCase {

    public record Command(UUID apartamentoId, LocalDate entrada, LocalDate salida, List<LocalDate> fechasNacimiento) { }

    public record Linea(LocalDate noche, String temporada, BigDecimal tarifa, int ocupantesFacturables,
                        BigDecimal subtotal) { }

    public record Result(UUID apartamentoId, int ocupantesFacturables, BigDecimal total, List<Linea> lineas) { }

    private final AlojamientoRepository alojamientoRepository;
    private final CotizadorEstanciaService cotizador;

    public CotizarEstanciaUseCase(AlojamientoRepository alojamientoRepository, CotizadorEstanciaService cotizador) {
        this.alojamientoRepository = alojamientoRepository;
        this.cotizador = cotizador;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Apartamento apartamento = Cargas.apartamento(alojamiento, cmd.apartamentoId());
        if (!apartamento.capacidadPermitida(cmd.fechasNacimiento().size())) {
            throw new AforoExcedidoException(apartamento.getCapacidad().aforoMax(), cmd.fechasNacimiento().size());
        }
        List<FechaNacimiento> fechas = cmd.fechasNacimiento().stream().map(FechaNacimiento::new)
                .collect(Collectors.toCollection(ArrayList::new));
        var cot = cotizador.cotizar(alojamiento, apartamento.getId(), new Estancia(cmd.entrada(), cmd.salida()), fechas);
        return new Result(cmd.apartamentoId(), cot.ocupantesFacturables(), cot.total().valor(),
                cot.lineas().stream().map(l -> new Linea(l.noche(), l.temporada(), l.tarifa().valor(),
                        l.ocupantesFacturables(), l.subtotal())).toList());
    }
}
