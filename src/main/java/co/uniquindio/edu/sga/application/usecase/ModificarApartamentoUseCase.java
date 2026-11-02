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

/**
 * PUT /api/apartamentos/{id} — 7.3: cambiar la capacidad NO afecta las reservas ya creadas, aunque
 * queden por encima de la nueva capacidad; el sistema advierte y las lista.
 */
public class ModificarApartamentoUseCase {

    public record Command(UUID apartamentoId, int capacidad, String descripcion, List<DormitorioDatos> dormitorios) { }

    public record Result(ApartamentoDetalle apartamento, List<UUID> reservasQueExcedenLaNuevaCapacidad) { }

    private final AlojamientoRepository alojamientoRepository;
    private final ReservaRepository reservaRepository;

    public ModificarApartamentoUseCase(AlojamientoRepository alojamientoRepository, ReservaRepository reservaRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.reservaRepository = reservaRepository;
    }

    public Result ejecutar(Command cmd) {
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Apartamento apartamento = Cargas.apartamento(alojamiento, cmd.apartamentoId());
        Capacidad nueva = CrearApartamentoUseCase.capacidad(cmd.capacidad());

        apartamento.actualizarDatos(nueva, cmd.descripcion(), DormitorioDatos.aDominio(cmd.dormitorios()));
        alojamientoRepository.guardar(alojamiento);

        List<UUID> afectadas = reservaRepository.buscarActivasPorApartamento(apartamento.getId()).stream()
                .filter(r -> r.getOcupantes().size() > nueva.aforoMax())
                .map(r -> r.getId().valor()).toList();
        return new Result(ApartamentoDetalle.de(apartamento), afectadas);
    }
}
