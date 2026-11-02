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

/** GET /api/reportes (ingresos) — 7.9: suma de PAGOS registrados en el periodo, por medio de pago. Los cargos no pagados no son ingreso. */
public class ConsultarIngresosUseCase {

    public record Command(LocalDate desde, LocalDate hasta) { }

    public record Result(BigDecimal total, Map<MedioPago, BigDecimal> porMedioPago) { }

    private final FolioRepository folioRepository;

    public ConsultarIngresosUseCase(FolioRepository folioRepository) {
        this.folioRepository = folioRepository;
    }

    public Result ejecutar(Command cmd) {
        new RangoFechas(cmd.desde(), cmd.hasta());                       // valida el periodo
        LocalDateTime ini = cmd.desde().atStartOfDay();
        LocalDateTime fin = cmd.hasta().atStartOfDay();
        Map<MedioPago, BigDecimal> porMedio = new EnumMap<>(MedioPago.class);
        BigDecimal total = BigDecimal.ZERO;
        for (Folio folio : folioRepository.buscarConPagosEntre(ini, fin)) {
            for (Pago p : folio.getPagos()) {
                if (p.fechaRegistro().isBefore(ini) || !p.fechaRegistro().isBefore(fin)) continue;
                BigDecimal monto = p.monto().valor();
                porMedio.merge(p.medio(), monto, BigDecimal::add);
                total = total.add(monto);
            }
        }
        return new Result(total, porMedio);
    }
}
