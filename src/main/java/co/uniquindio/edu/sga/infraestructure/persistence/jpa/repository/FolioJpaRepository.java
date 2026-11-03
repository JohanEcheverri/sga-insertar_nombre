package co.uniquindio.edu.sga.infraestructure.persistence.jpa.repository;

import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

/** Adaptador JPA del agregado contable Folio. */
@Repository @Primary @Transactional @RequiredArgsConstructor
public class FolioJpaRepository implements FolioRepository {
    private final FolioJpaDataRepository data;
    /** Guarda cargos, pagos y autorización de cierre del folio. */
    public void guardar(Folio f) { data.save(aEntidad(f)); }
    /** Busca un folio por su identificador. */
    @Transactional(readOnly=true) public Optional<Folio> buscarPorId(FolioId id) { return data.findById(id.valor().toString()).map(this::aDominio); }
    /** Busca el folio asociado a una reserva. */
    @Transactional(readOnly=true) public Optional<Folio> buscarPorReservaId(ReservaId id) { return data.findByReservaId(id.valor().toString()).map(this::aDominio); }
    /** Busca folios con pagos dentro del intervalo semiabierto indicado. */
    @Transactional(readOnly=true) public List<Folio> buscarConPagosEntre(LocalDateTime desde, LocalDateTime hasta) { return data.findAll().stream().filter(e->e.getPagos().stream().anyMatch(p->!p.getFechaRegistro().isBefore(desde)&&p.getFechaRegistro().isBefore(hasta))).map(this::aDominio).toList(); }
    /** Devuelve todos los folios. */
    @Transactional(readOnly=true) public List<Folio> buscarTodos() { return data.findAll().stream().map(this::aDominio).toList(); }
    private FolioEntity aEntidad(Folio f) { var e=new FolioEntity();e.setId(f.getId().valor().toString());e.setReservaId(f.getReservaId().valor().toString());e.setEstado(f.getEstado().name());e.setFechaCreacion(LocalDateTime.now()); f.getCargos().forEach(c->{var x=new CargoEmbeddable();x.setTipo(c.tipoCargo().name());x.setConcepto(c.concepto());x.setMonto(c.monto().valor());x.setFechaRegistro(c.fechaRegistro());e.getCargos().add(x);}); f.getPagos().forEach(p->{var x=new PagoEmbeddable();x.setMonto(p.monto().valor());x.setMedio(p.medio().name());x.setReferencia(p.referencia());x.setFechaRegistro(p.fechaRegistro());e.getPagos().add(x);}); var a=f.getAutorizacionCierre();if(a!=null){e.setAutorizacionAutor(a.autor());e.setAutorizacionMotivo(a.motivo());e.setAutorizacionFecha(a.fecha());}return e; }
    private Folio aDominio(FolioEntity e) { var f=Folio.crear(FolioId.of(UUID.fromString(e.getId())),ReservaId.of(UUID.fromString(e.getReservaId()))); e.getCargos().forEach(c->f.registrarCargo(new Cargo(TipoCargo.valueOf(c.getTipo()),c.getConcepto(),Dinero.de(c.getMonto()),c.getFechaRegistro())));e.getPagos().forEach(p->f.registrarPago(new Pago(Dinero.de(p.getMonto()),MedioPago.valueOf(p.getMedio()),p.getReferencia(),p.getFechaRegistro())));if("CERRADO".equals(e.getEstado())){var a=new AutorizacionCierre(e.getAutorizacionAutor(),e.getAutorizacionMotivo(),e.getAutorizacionFecha());if(f.tieneSaldoPendiente())f.cerrarConSaldoAutorizado(a);else f.cerrar(a);}return f; }
}
