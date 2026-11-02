package co.uniquindio.edu.sga.infraestructure.persistence.inmemory;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.repository.FiltroReservas;
import co.uniquindio.edu.sga.domain.repository.Pagina;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;

import java.time.LocalDate;
import java.util.*;

public class ReservaRepositoryInMemory implements ReservaRepository {

    private final Map<ReservaId, Reserva> reservas = new LinkedHashMap<>();

    @Override
    public void guardar(Reserva reserva) {
        reservas.put(reserva.getId(), reserva);
    }

    @Override
    public Optional<Reserva> buscarPorId(ReservaId id) {
        return Optional.ofNullable(reservas.get(id));
    }

    @Override
    public Optional<Reserva> buscarPorCanalEIdentificadorExterno(CanalOrigen canal, String identificadorExterno) {
        return reservas.values().stream()
                .filter(r -> r.getCanalOrigen() == canal && identificadorExterno.equals(r.getIdentificadorExterno()))
                .findFirst();
    }

    @Override
    public List<Reserva> buscarActivasPorApartamento(ApartamentoId apartamentoId) {
        return reservas.values().stream()
                .filter(r -> r.getApartamentoId().equals(apartamentoId) && r.estaActiva()).toList();
    }

    @Override
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return reservas.values().stream().filter(r -> r.getEstado() == estado).toList();
    }

    @Override
    public Pagina<Reserva> buscar(FiltroReservas f, int pagina) {
        List<Reserva> filtradas = reservas.values().stream().filter(r -> coincide(f, r))
                .sorted(Comparator.comparing(Reserva::getFechaCreacion).reversed()).toList();
        return Pagina.de(filtradas, pagina);
    }

    @Override
    public List<Reserva> buscarQueSolapanCon(RangoFechas rango) {
        return reservas.values().stream().filter(r -> r.getEstancia().rangoFechas().intersecta(rango)).toList();
    }

    @Override
    public boolean existeActivaOFuturaDeApartamento(ApartamentoId apartamentoId, LocalDate desde) {
        return reservas.values().stream().anyMatch(r -> r.getApartamentoId().equals(apartamentoId)
                && r.estaActiva() && !r.getEstancia().fechaSalida().isBefore(desde));
    }

    @Override
    public boolean existeActivaDeUsuario(UsuarioId usuarioId) {
        return reservas.values().stream().anyMatch(r -> r.estaActiva()
                && usuarioId.equals(r.getTitular().usuarioId()));
    }

    private static boolean coincide(FiltroReservas f, Reserva r) {
        if (!f.estados().isEmpty() && !f.estados().contains(r.getEstado())) return false;
        if (f.apartamentoId() != null && !f.apartamentoId().equals(r.getApartamentoId())) return false;
        if (f.canal() != null && f.canal() != r.getCanalOrigen()) return false;
        if (f.documentoTitular() != null
                && !f.documentoTitular().equals(r.getTitular().documentoIdentidad().numero())) return false;
        if (f.titularUsuarioId() != null && !f.titularUsuarioId().equals(r.getTitular().usuarioId())) return false;
        if (f.entrada() != null && !f.entrada().equals(r.getEstancia().fechaEntrada())) return false;
        if (f.salida() != null && !f.salida().equals(r.getEstancia().fechaSalida())) return false;
        if (f.desde() != null && f.hasta() != null
                && !r.getEstancia().rangoFechas().intersecta(new RangoFechas(f.desde(), f.hasta()))) return false;
        return true;
    }
}
