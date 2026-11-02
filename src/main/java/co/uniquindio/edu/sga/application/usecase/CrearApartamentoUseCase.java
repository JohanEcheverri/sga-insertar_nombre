package co.uniquindio.edu.sga.application.usecase;

import org.springframework.transaction.annotation.Transactional;

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
 * POST /api/apartamentos — Nueva unidad vendible. Se crea junto con su tarifa en CADA temporada
 * (7.4: todo apartamento activo debe tener tarifas completas); si falta alguna, no se crea.
 */
@Service
@Transactional
public class CrearApartamentoUseCase {

    /** tarifasPorTemporada: id de temporada → precio por ocupante facturable por noche. */
    public record Command(String identificadorUnico, String descripcion, int capacidad,
                          List<DormitorioDatos> dormitorios, Map<UUID, BigDecimal> tarifasPorTemporada,
                          Solicitante solicitante) { }

    private final AlojamientoRepository alojamientoRepository;

    public CrearApartamentoUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public ApartamentoDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null) throw new AccesoDenegadoException("Debe autenticarse para crear un apartamento.");
        cmd.solicitante().exigirAdministrador();
        Alojamiento alojamiento = Cargas.alojamiento(alojamientoRepository);
        Apartamento apartamento = Apartamento.crear(capacidad(cmd.capacidad()), cmd.descripcion(),
                DormitorioDatos.aDominio(cmd.dormitorios()), EstadoOperativo.PREPARADO,
                ApartamentoId.generar(), cmd.identificadorUnico(), true);

        List<Tarifa> tarifas = new ArrayList<>();
        Map<UUID, BigDecimal> precios = cmd.tarifasPorTemporada() == null ? Map.of() : cmd.tarifasPorTemporada();
        precios.forEach((temporadaId, precio) ->
                tarifas.add(new Tarifa(TemporadaId.of(temporadaId), apartamento.getId(), Dinero.de(precio))));

        alojamiento.agregarApartamento(apartamento, tarifas);
        alojamientoRepository.guardar(alojamiento);
        return ApartamentoDetalle.de(apartamento);
    }

    static Capacidad capacidad(int valor) {
        if (valor <= 0 || valor > Byte.MAX_VALUE) {
            throw new ReglaDominioException("La capacidad debe estar entre 1 y " + Byte.MAX_VALUE + " personas.");
        }
        return new Capacidad((byte) valor);
    }
}
