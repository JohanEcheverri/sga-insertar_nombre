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
 * PATCH /api/apartamentos/{id}/estado-operativo — Personal de servicio: PENDIENTE_PREPARACION →
 * EN_PREPARACION → PREPARADO. Solo el ADMINISTRADOR puede declarar FUERA_DE_SERVICIO (7.1).
 * OCUPADO y PENDIENTE_PREPARACION se alcanzan únicamente por registro y salida.
 */
public class ModificarEstadoOperativoApartamentoUseCase {

    public record Command(UUID apartamentoId, EstadoOperativo nuevoEstado, Solicitante solicitante) { }

    private final AlojamientoRepository alojamientoRepository;

    public ModificarEstadoOperativoApartamentoUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public ApartamentoDetalle ejecutar(Command cmd) {
        RolUsuario rol = cmd.solicitante().rol();
        if (rol != RolUsuario.ADMINISTRADOR && rol != RolUsuario.PERSONAL_SERVICIO) {
            throw new AccesoDenegadoException("Solo administración o personal de servicio cambian el estado operativo.");
        }
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Apartamento apartamento = Cargas.apartamento(alojamiento, cmd.apartamentoId());

        switch (cmd.nuevoEstado()) {
            case EN_PREPARACION -> apartamento.iniciarPreparacion();
            case PREPARADO -> apartamento.marcarPreparado();
            case FUERA_DE_SERVICIO -> {
                cmd.solicitante().exigirAdministrador();
                apartamento.marcarFueraDeServicio();
            }
            default -> throw new OperacionNoPermitidaException("El estado " + cmd.nuevoEstado()
                    + " no se asigna manualmente: lo determinan el registro y la salida del grupo.");
        }
        alojamientoRepository.guardar(alojamiento);
        return ApartamentoDetalle.de(apartamento);
    }
}
