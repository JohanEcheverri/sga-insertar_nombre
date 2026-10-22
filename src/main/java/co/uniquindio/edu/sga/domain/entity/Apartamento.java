package co.uniquindio.edu.sga.domain.entity;

import java.util.List;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.Capacidad;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;
import co.uniquindio.edu.sga.domain.valueobject.Dormitorio;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;

public class Apartamento {

    // ------ Atributos Entidad Apartamento ------ //

    private ApartamentoId id;
    private String identificadorUnico, descripcion;
    private Capacidad capacidad;
    private EstadoOperativo estadoOperativo;
    private List<Dormitorio> dormitorios;
    private Boolean disponible;

    public Apartamento(Capacidad capacidad, String descripcion, List<Dormitorio> dormitorios, EstadoOperativo estadoOperativo, ApartamentoId id, String identificadorUnico, boolean disponible) {
        if(capacidad == null || descripcion == null || dormitorios == null || estadoOperativo == null || id == null || identificadorUnico == null){
            throw new ReglaDominioException("Ningún atributo puede ser nulo");
        }
        if(capacidad.valor() <= 0){
            throw new ReglaDominioException("La capacidad del apartamento debe ser mayor a cero");
        }
        if(dormitorios.isEmpty()){
            throw new ReglaDominioException("El apartamento debe tener al menos un dormitorio");
        }
        if(identificadorUnico.isBlank()  || descripcion.isBlank()){
            throw new ReglaDominioException("El identificador único, nombre y descripción no pueden estar vacíos");
        }
        this.capacidad = capacidad;
        this.descripcion = descripcion;
        this.dormitorios = dormitorios;
        this.estadoOperativo = estadoOperativo.PREPARADO;
        this.disponible = disponible;
        this.id = id;
        this.identificadorUnico = identificadorUnico;
    }
    
    public static Apartamento crear(Capacidad capacidad, String descripcion, List<Dormitorio> dormitorios, EstadoOperativo estadoOperativo, ApartamentoId id, String identificadorUnico, boolean disponible) {
        return new Apartamento(capacidad, descripcion, dormitorios, estadoOperativo, id, identificadorUnico, disponible);
    }

    // ** RN-02 El número total de ocupantes no puede exceder la capacidad del apartamento. Sin excepciones **
    public boolean capacidadPermitida(int totalOcupantes){
        return totalOcupantes <= capacidad.valor();
    }

    // ** RN-11 Un apartamento solo puede recibir un grupo si su estado operativo es PREPARADO.
    public boolean puedeRecibirGrupo(){
        return disponible && estadoOperativo == EstadoOperativo.PREPARADO;
    }

    












}
