package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.RangoFechas;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.ReservaEntity;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper.ReservaPersistenceMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class ReservaJpaRepository implements ReservaRepository {

    private final ReservaJpaDataRepository dataRepository;
    private final ReservaPersistenceMapper mapper;

    @Override
    public Reserva guardar(Reserva reserva) {
        ReservaEntity entity = mapper.toReservaEntity(reserva);

        dataRepository.findByCodigo(entity.getCodigo())
                .ifPresent(existente -> entity.setId(existente.getId()));

        return mapper.toReservaDomain(dataRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> obtenerPorCodigo(ReservaId codigo) {
        return dataRepository.findByCodigo(codigo.valor())
                .map(mapper::toReservaDomain);
    }

    @Transactional(readOnly = true)
    public List<Reserva> buscarPorEstado(EstadoReserva estado) {
        return dataRepository.findByEstado(estado)
                .stream()
                .map(mapper::toReservaDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarActivasPorApartamento(ApartamentoId apartamento, RangoFechas periodo) {
        List<EstadoReserva> activas = Arrays.stream(EstadoReserva.values())
                .filter(EstadoReserva::esActiva)
                .toList();

        return dataRepository.findByApartamentoIdAndEstadoIn(apartamento.valor(), activas)
                .stream()
                .map(mapper::toReservaDomain)
                .filter(reserva -> reserva.getEstancia().rangoFechas().intersecta(periodo))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return dataRepository.findAll().stream().map(mapper::toReservaDomain).toList();
    }

    @Override
    public void eliminar(ReservaId codigo) {
        dataRepository.findByCodigo(codigo.valor()).ifPresent(dataRepository::delete);
    }
}
