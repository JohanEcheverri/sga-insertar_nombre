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

/** GET /api/apartamentos/pendientes-preparacion — Unidades en PENDIENTE_PREPARACION o EN_PREPARACION. */
@Service
public class ConsultarApartamentoPreparacionPendienteUseCase {

    public record Command(int pagina) { }

    private final AlojamientoRepository alojamientoRepository;

    public ConsultarApartamentoPreparacionPendienteUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public Pagina<ApartamentoDetalle> ejecutar(Command cmd) {
        List<ApartamentoDetalle> lista = Cargas.alojamiento(alojamientoRepository).apartamentosActivos().stream()
                .filter(a -> a.getEstadoOperativo() == EstadoOperativo.PENDIENTE_PREPARACION
                        || a.getEstadoOperativo() == EstadoOperativo.EN_PREPARACION)
                .map(ApartamentoDetalle::de).toList();
        return Pagina.de(lista, cmd.pagina());
    }
}
