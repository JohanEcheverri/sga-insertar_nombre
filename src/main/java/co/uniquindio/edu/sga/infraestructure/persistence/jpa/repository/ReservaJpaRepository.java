package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import co.uniquindio.edu.sga.domain.repository.FiltroReservas;
import co.uniquindio.edu.sga.domain.repository.Pagina;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.ReservaEntity;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper.ReservaPersistenceMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@Primary
@Transactional
@RequiredArgsConstructor
public class ReservaJpaRepository implements ReservaRepository {

    private final ReservaJpaDataRepository dataRepository;
    private final ReservaPersistenceMapper mapper;

    @Override
    public void guardar(Reserva reserva) {
        ReservaEntity entity = mapper.toReservaEntity(reserva);

        dataRepository.findByCodigo(entity.getCodigo())
                .ifPresent(existente -> entity.setId(existente.getId()));

        dataRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(ReservaId codigo) {
        return dataRepository.findByCodigo(codigo.valor())
                .map(mapper::toReservaDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return dataRepository.findByEstado(estado)
                .stream()
                .map(mapper::toReservaDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarActivasPorApartamento(ApartamentoId apartamento) {
        List<EstadoReserva> activas = Arrays.stream(EstadoReserva.values())
                .filter(EstadoReserva::esActiva)
                .toList();

        return dataRepository.findByApartamentoIdAndEstadoIn(apartamento.valor(), activas)
                .stream()
                .map(mapper::toReservaDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorCanalEIdentificadorExterno(CanalOrigen canal, String identificadorExterno) {
        return dataRepository.findAll().stream().map(mapper::toReservaDomain)
                .filter(r -> r.getCanalOrigen() == canal
                        && java.util.Objects.equals(r.getIdentificadorExterno(), identificadorExterno))
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Reserva> buscar(FiltroReservas filtro, int pagina) {
        List<Reserva> filtradas = dataRepository.findAll().stream().map(mapper::toReservaDomain)
                .filter(r -> filtro.estados().isEmpty() || filtro.estados().contains(r.getEstado()))
                .filter(r -> filtro.apartamentoId() == null || filtro.apartamentoId().equals(r.getApartamentoId()))
                .filter(r -> filtro.canal() == null || filtro.canal() == r.getCanalOrigen())
                .filter(r -> filtro.documentoTitular() == null
                        || filtro.documentoTitular().equals(r.getTitular().documentoIdentidad().numero()))
                .filter(r -> filtro.titularUsuarioId() == null
                        || filtro.titularUsuarioId().equals(r.getTitular().usuarioId()))
                .filter(r -> filtro.entrada() == null || filtro.entrada().equals(r.getEstancia().fechaEntrada()))
                .filter(r -> filtro.salida() == null || filtro.salida().equals(r.getEstancia().fechaSalida()))
                .filter(r -> filtro.desde() == null || r.getEstancia().fechaSalida().isAfter(filtro.desde()))
                .filter(r -> filtro.hasta() == null || r.getEstancia().fechaEntrada().isBefore(filtro.hasta()))
                .sorted(java.util.Comparator.comparing(Reserva::getFechaCreacion).reversed()).toList();
        return Pagina.de(filtradas, pagina);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarQueSolapanCon(RangoFechas rango) {
        return dataRepository.findAll().stream().map(mapper::toReservaDomain)
                .filter(r -> r.getEstancia().rangoFechas().intersecta(rango)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeActivaOFuturaDeApartamento(ApartamentoId apartamentoId, LocalDate desde) {
        return buscarActivasPorApartamento(apartamentoId).stream()
                .anyMatch(r -> r.getEstancia().fechaSalida().isAfter(desde));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeActivaDeUsuario(UsuarioId usuarioId) {
        return dataRepository.findAll().stream().map(mapper::toReservaDomain)
                .anyMatch(r -> r.estaActiva() && usuarioId.equals(r.getTitular().usuarioId()));
    }

    public void eliminar(ReservaId codigo) {
        dataRepository.findByCodigo(codigo.valor()).ifPresent(dataRepository::delete);
    }
}
