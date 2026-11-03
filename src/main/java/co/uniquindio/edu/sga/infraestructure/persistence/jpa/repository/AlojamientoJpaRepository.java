package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.*;
import co.uniquindio.edu.sga.domain.repository.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.AlojamientoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/** Adaptador JPA del agregado único de alojamiento y sus componentes persistentes. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class AlojamientoJpaRepository implements AlojamientoRepository {
    private final AlojamientoJpaDataRepository data;
    private final ApartamentoRepository apartamentos;
    private final TemporadaRepository temporadas;
    private final PoliticaCancelacionRepository politicas;
    /** Recupera el alojamiento y reconstruye sus componentes desde los puertos JPA. */
    @Transactional(readOnly=true) public Optional<Alojamiento> obtener() { return data.findAll().stream().findFirst().map(this::aDominio); }
    /** Persiste los datos del alojamiento y guarda sus apartamentos y temporadas. */
    public void guardar(Alojamiento a) { var e=new AlojamientoEntity();e.setId(a.getId().valor().toString());e.setNombre(a.getNombre());e.setDepartamento(a.getUbicacion().departamento());e.setCiudad(a.getUbicacion().ciudad());e.setDireccion(a.getUbicacion().direccion());var c=a.getConfiguracion();e.setUmbralEdadFacturable(c.umbralEdadFacturable());e.setHoraEntrada(c.entrada());e.setHoraSalida(c.salida());e.setTiempoPreparacionHoras(c.tiempoPreparacionHoras());e.setPlazoConfirmacionHoras(c.plazoConfirmacionHoras());e.setHoraLimiteNoShow(c.horaLimiteNoShow());e.setPorcentajeAnticipo(c.porcentajeAnticipo());e.setPoliticaVersion(a.getPoliticaVigente().version().valor());e.setPoliticaVigenteDesde(a.getPoliticaVigente().version().vigenteDesde());data.save(e);a.getApartamentos().forEach(apartamentos::guardar);a.getTemporadas().forEach(temporadas::guardar); }
    private Alojamiento aDominio(AlojamientoEntity e) { var policyVersion=new VersionPolitica(e.getPoliticaVersion(),e.getPoliticaVigenteDesde());var policy=politicas.obtenerPorVersion(policyVersion).orElseThrow(()->new IllegalStateException("No existe la política de cancelación vinculada al alojamiento."));var c=new ConfiguracionAlojamiento(e.getUmbralEdadFacturable(),e.getHoraEntrada(),e.getHoraSalida(),e.getTiempoPreparacionHoras(),e.getPlazoConfirmacionHoras(),e.getHoraLimiteNoShow(),Objects.requireNonNullElse(e.getPorcentajeAnticipo(),BigDecimal.ZERO));return Alojamiento.crear(AlojamientoId.of(UUID.fromString(e.getId())),e.getNombre(),new Ubicacion(e.getDepartamento(),e.getCiudad(),e.getDireccion()),c,apartamentos.listarTodos(),temporadas.listarTodas(),policy); }
}
