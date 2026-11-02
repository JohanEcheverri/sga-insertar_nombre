package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

import java.time.LocalDate;
import java.util.Set;

/**
 * Filtros de listado de reservas (7.5). Todos los campos son opcionales (null / vacío = sin filtro).
 * Los resultados se devuelven de la más reciente a la más antigua.
 *
 * @param estados             estados admitidos (vacío = cualquiera)
 * @param entrada             reservas cuya fecha de entrada es exactamente esta (llegadas del día)
 * @param salida              reservas cuya fecha de salida es exactamente esta (salidas del día)
 * @param desde/hasta         reservas cuya estancia intersecta [desde, hasta)
 * @param documentoTitular    número de documento del titular
 * @param titularUsuarioId    usuario dueño de la reserva (historial del huésped)
 */
public record FiltroReservas(Set<EstadoReserva> estados, ApartamentoId apartamentoId, CanalOrigen canal,
                             String documentoTitular, UsuarioId titularUsuarioId,
                             LocalDate entrada, LocalDate salida, LocalDate desde, LocalDate hasta) {

    public FiltroReservas {
        estados = estados == null ? Set.of() : Set.copyOf(estados);
    }

    public static FiltroReservas sinFiltros() {
        return new FiltroReservas(null, null, null, null, null, null, null, null, null);
    }
}
