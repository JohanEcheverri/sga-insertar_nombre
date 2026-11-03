package co.uniquindio.edu.sga.infraestructure.persistence.jpa.mapper;

import co.uniquindio.edu.sga.domain.valueobject.Ocupante;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.OcupanteEmbeddable;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.EstanciaEmbeddable;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.PoliticaEmbeddable;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.TramoCancelacionEmbeddable;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.ReservaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ReservaPersistenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", source = "id.valor")
    @Mapping(target = "apartamentoId", source = "apartamentoId.valor")
    @Mapping(target = "folioId", source = "folioId.valor")
    @Mapping(target = "identificadorExterno", source = "identificadorExterno")
    @Mapping(target = "documentoTitular", source = "titular.documentoIdentidad.numero")
    @Mapping(target = "estancia", expression = "java(toEstanciaEmbeddable(reserva.getEstancia()))")
    @Mapping(target = "politicaCongelada", expression = "java(toPoliticaEmbeddable(reserva.getPoliticaCancelacion()))")
    @Mapping(target = "tramosCancelacion", expression = "java(toTramosEmbeddable(reserva.getPoliticaCancelacion()))")
    @Mapping(target = "titularUsuarioId", source = "titular.usuarioId.valor")
    @Mapping(target = "titularCorreo", source = "titular.correo.direccion")
    @Mapping(target = "titularTelefono", source = "titular.telefono.numeroTelefono")
    @Mapping(target = "porcentajeRetencionNoShow", source = "politicaCancelacion.porcentajeRetencionNoShow")
    @Mapping(target = "motivoCancelacion", ignore = true)
    @Mapping(target = "valorCongelado", source = "valorCongelado.valor")
    ReservaEntity toReservaEntity(Reserva reserva);

    @Mapping(target = "documento", source = "documentoIdentidad.numero")
    @Mapping(target = "nombre", source = "nombreCompleto.nombre")
    @Mapping(target = "apellido", source = "nombreCompleto.apellido")
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento.valor")
    OcupanteEmbeddable toOcupanteEmbeddable(Ocupante ocupante);

    default EstanciaEmbeddable toEstanciaEmbeddable(Estancia estancia) {
        return new EstanciaEmbeddable(estancia.fechaEntrada(), estancia.fechaSalida());
    }

    default PoliticaEmbeddable toPoliticaEmbeddable(PoliticaCancelacion politica) {
        return new PoliticaEmbeddable(politica.version().valor(), politica.version().vigenteDesde());
    }

    default List<TramoCancelacionEmbeddable> toTramosEmbeddable(PoliticaCancelacion politica) {
        return politica.tramos().stream()
                .map(t -> new TramoCancelacionEmbeddable(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                .toList();
    }

    default Ocupante toOcupanteDomain(OcupanteEmbeddable ocupanteEmbeddable) {
        return new Ocupante(
                new NombreCompleto(ocupanteEmbeddable.nombre(), ocupanteEmbeddable.apellido()),
                new DocumentoIdentidad(ocupanteEmbeddable.documento()),
                new FechaNacimiento(ocupanteEmbeddable.fechaNacimiento())
        );
    }

    default Reserva toReservaDomain(ReservaEntity reservaEntity) {

        List<Ocupante> ocupantes = reservaEntity.getOcupantes().stream()
                .map(this::toOcupanteDomain)
                .toList();

        Ocupante titular = ocupantes.stream()
                .filter(ocupante ->
                        ocupante.documentoIdentidad().numero().equals(reservaEntity.getDocumentoTitular()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("El titular no esta dentro de los ocupantes de la reserva"));

        return Reserva.recontruir(
                new ReservaId(reservaEntity.getCodigo()),
                reservaEntity.getFolioId() == null ? null : new FolioId(reservaEntity.getFolioId()),
                new ApartamentoId(reservaEntity.getApartamentoId()),
                new Titular(titular.nombreCompleto(),
                        new UsuarioId(reservaEntity.getTitularUsuarioId()),
                        new CorreoElectronico(reservaEntity.getTitularCorreo()),
                        new Telefono(reservaEntity.getTitularTelefono()),
                        titular.documentoIdentidad()),
                ocupantes,
                new Estancia(reservaEntity.getEstancia().fechaEntrada(), reservaEntity.getEstancia().fechaSalida()),
                reservaEntity.getIdentificadorExterno(),
                reservaEntity.getHoraEstimadaLlegada(),
                reservaEntity.getCanalOrigen(),
                reservaEntity.getEstado(),
                new Dinero(reservaEntity.getValorCongelado()),
                new PoliticaCancelacion(
                        new VersionPolitica(reservaEntity.getPoliticaCongelada().version(), reservaEntity.getPoliticaCongelada().vigenteDesde()),
                        reservaEntity.getTramosCancelacion().stream()
                                .map(t -> new TramoCancelacion(t.diasAntelacionMinimos(), t.porcentajeRetencion()))
                                .toList(),
                        reservaEntity.getPorcentajeRetencionNoShow()),
                reservaEntity.getFechaCreacion(),
                reservaEntity.getMotivoCancelacion()
        );
    }
}
