package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.valueobject.PoliticaCancelacion;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.FolioId;
import co.uniquindio.edu.sga.domain.valueobject.Dinero;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.Titular;
import co.uniquindio.edu.sga.domain.valueobject.Ocupante;
import co.uniquindio.edu.sga.domain.valueobject.Estancia;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class Reserva {
    //---- Atributos Entidad Reserva------
    private ReservaId id;
    private FolioId folioId;
    private ApartamentoId apartamentoId;
    private Titular titular;
    private List<Ocupante> ocupantes;
    private Estancia estancia;
    private String identificadorExterno;
    private LocalTime horaEstimadaLlegada;
    private CanalOrigen canalOrigen;
    private EstadoReserva estado;
    private Dinero valorCongelado;
    private PoliticaCancelacion politicaCancelacion;
    private LocalDateTime fechaCreacion;

    public Reserva(ReservaId id, FolioId folioId, ApartamentoId apartamentoId, Titular titular,
            List<Ocupante> ocupantes, Estancia estancia, String identificadorExterno, LocalTime horaEstimadaLlegada,
            CanalOrigen canalOrigen, EstadoReserva estado, Dinero valorCongelado,
            PoliticaCancelacion politicaCancelacion, LocalDateTime fechaCreacion) {
        this.id = id;
        this.folioId = folioId;
        this.apartamentoId = apartamentoId;
        this.titular = titular;
        this.ocupantes = ocupantes;
        this.estancia = estancia;
        this.identificadorExterno = identificadorExterno;
        this.horaEstimadaLlegada = horaEstimadaLlegada;
        this.canalOrigen = canalOrigen;
        this.estado = EstadoReserva.PENDIENTE;
        this.valorCongelado = valorCongelado;
        this.politicaCancelacion = politicaCancelacion;
        this.fechaCreacion = fechaCreacion;
    }

    public static Reserva crear(ReservaId id, FolioId folioId, ApartamentoId apartamentoId, Titular titular,
            List<Ocupante> ocupantes, Estancia estancia, String identificadorExterno, LocalTime horaEstimadaLlegada,
            CanalOrigen canalOrigen, EstadoReserva estado, Dinero valorCongelado,
            PoliticaCancelacion politicaCancelacion, LocalDateTime fechaCreacion) {

        // RN-04 No se pueden crear reservas cuya fecha de entrada sea anterior a la fecha actual.
        
        return new Reserva(id, folioId, apartamentoId, titular, ocupantes, estancia, identificadorExterno,
                horaEstimadaLlegada, canalOrigen, estado, valorCongelado, politicaCancelacion, fechaCreacion);
    }


}
