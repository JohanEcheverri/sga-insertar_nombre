package co.uniquindio.edu.sga.infraestructure.rest.mapper;

import org.mapstruct.Mapper;

import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.application.dto.response.ApartamentoResponse;
import co.uniquindio.edu.sga.application.dto.request.CrearApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.request.DormitorioRequest;
import co.uniquindio.edu.sga.application.command.DormitorioDatos;
import co.uniquindio.edu.sga.application.result.ApartamentoDetalle;
import co.uniquindio.edu.sga.application.usecase.CrearApartamentoUseCase;
import co.uniquindio.edu.sga.application.security.Solicitante;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Convierte Apartamento al modelo público de la API. */
@Mapper(componentModel = "spring")
public interface ApartamentoMapper {
    default CrearApartamentoUseCase.Command aComando(CrearApartamentoRequest solicitud, Solicitante solicitante) {
        List<DormitorioDatos> dormitorios = solicitud.dormitorios().stream()
                .map(d -> new DormitorioDatos(d.nombre(), d.cantidadCamas(), d.tipoCamas())).toList();
        Map<UUID, java.math.BigDecimal> tarifas = solicitud.tarifas().stream().collect(Collectors.toMap(
                t -> UUID.fromString(t.temporadaId()), co.uniquindio.edu.sga.application.dto.request.TarifaRequest::precioPorNochePorOcupante));
        return new CrearApartamentoUseCase.Command(solicitud.identificadorUnico(), solicitud.descripcion(),
                solicitud.aforoMaximo(), dormitorios, tarifas, solicitante);
    }

    default ApartamentoResponse aRespuesta(ApartamentoDetalle apartamento, List<DormitorioRequest> dormitorios) {
        return new ApartamentoResponse(apartamento.id().toString(), apartamento.identificador(), apartamento.descripcion(),
                apartamento.capacidad(), apartamento.estadoOperativo().name(), dormitorios.stream()
                .map(d -> d.nombre() + ": " + d.cantidadCamas() + " cama(s) " + d.tipoCamas()).toList(), apartamento.disponible());
    }

    default ApartamentoResponse aRespuesta(ApartamentoDetalle apartamento) {
        return new ApartamentoResponse(apartamento.id().toString(), apartamento.identificador(), apartamento.descripcion(),
                apartamento.capacidad(), apartamento.estadoOperativo().name(), apartamento.dormitorios(), apartamento.disponible());
    }

    default ApartamentoResponse aRespuesta(Apartamento a) {
        List<String> dormitorios = a.getDormitorios().stream()
                .map(d -> d.nombre() + ": " + d.cantidadCamas() + " cama(s) " + d.tipoCamas())
                .toList();
        return new ApartamentoResponse(a.getId().valor().toString(), a.getIdentificadorUnico(),
                a.getDescripcion(), (int) a.getCapacidad().aforoMax(),
                a.getEstadoOperativo().name(), dormitorios, a.isDisponible());
    }
}
