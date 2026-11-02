package co.uniquindio.edu.sga.domain.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Puerto de persistencia del agregado Reserva (era una clase; debe ser interfaz). */
public interface ReservaRepository {

    /** Inserta o actualiza. */
    void guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(ReservaId id);

    /** RN-19 - Combinación canal + identificador externo única (idempotencia). */
    Optional<Reserva> buscarPorCanalEIdentificadorExterno(CanalOrigen canal, String identificadorExterno);

    /** Reservas PENDIENTE, CONFIRMADA o EN_CURSO del apartamento (RN-01, RN-20, RN-28). */
    List<Reserva> buscarActivasPorApartamento(ApartamentoId apartamentoId);

    List<Reserva> buscarPorEstado(EstadoReserva estado);

    /** Listado paginado (10 por página), de la más reciente a la más antigua. */
    Pagina<Reserva> buscar(FiltroReservas filtro, int pagina);

    /** Reservas de cualquier estado cuya estancia intersecta el rango (reportes). */
    List<Reserva> buscarQueSolapanCon(RangoFechas rango);

    /** 7.3 - ¿Tiene reservas activas cuya salida sea posterior a la fecha dada? (retirar apartamento). */
    boolean existeActivaOFuturaDeApartamento(ApartamentoId apartamentoId, LocalDate desde);

    /** 7.2 - ¿Es titular de reservas PENDIENTE/CONFIRMADA/EN_CURSO? (eliminar usuario). */
    boolean existeActivaDeUsuario(UsuarioId usuarioId);
}
