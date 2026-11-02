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

/**
 * GET /api/apartamentos/disponibilidad (y búsqueda por capacidad) — Apartamentos vendibles para un rango y
 * un grupo (3.3), con valor estimado y filtro opcional de precio. Los retirados no aparecen (7.3).
 */
@Service
public class ConsultarDisponibilidadApartamentoPorFechaUseCase {

    public record Command(LocalDate entrada, LocalDate salida, List<LocalDate> fechasNacimiento,
                          BigDecimal precioMinimo, BigDecimal precioMaximo, int pagina) { }

    public record ApartamentoDisponible(ApartamentoDetalle apartamento, BigDecimal valorEstimado) { }

    private final AlojamientoRepository alojamientoRepository;
    private final DisponibilidadApartamentoService disponibilidad;
    private final CotizadorEstanciaService cotizador;

    public ConsultarDisponibilidadApartamentoPorFechaUseCase(AlojamientoRepository alojamientoRepository,
                                                              DisponibilidadApartamentoService disponibilidad,
                                                              CotizadorEstanciaService cotizador) {
        this.alojamientoRepository = alojamientoRepository;
        this.disponibilidad = disponibilidad;
        this.cotizador = cotizador;
    }

    public Pagina<ApartamentoDisponible> ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Estancia estancia = new Estancia(cmd.entrada(), cmd.salida());
        List<FechaNacimiento> fechas = cmd.fechasNacimiento().stream().map(FechaNacimiento::new)
                .collect(Collectors.toCollection(ArrayList::new));

        List<ApartamentoDisponible> resultado = new ArrayList<>();
        for (Apartamento a : alojamiento.apartamentosActivos()) {
            if (!disponibilidad.estaDisponible(a, estancia, fechas.size(), alojamiento.getConfiguracion())) continue;
            BigDecimal valor = cotizador.cotizar(alojamiento, a.getId(), estancia, fechas).total().valor();
            if (cmd.precioMinimo() != null && valor.compareTo(cmd.precioMinimo()) < 0) continue;
            if (cmd.precioMaximo() != null && valor.compareTo(cmd.precioMaximo()) > 0) continue;
            resultado.add(new ApartamentoDisponible(ApartamentoDetalle.de(a), valor));
        }
        resultado.sort(Comparator.comparing(ApartamentoDisponible::valorEstimado));
        return Pagina.de(resultado, cmd.pagina());
    }
}
