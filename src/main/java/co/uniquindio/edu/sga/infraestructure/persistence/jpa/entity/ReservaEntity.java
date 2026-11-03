package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;


import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Reserva")
@Getter
@Setter
@NoArgsConstructor
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false)
    private UUID codigo;

    @Column(name = "apartamento_id", nullable = false)
    private UUID apartamentoId;

    @Column(name = "folio_id")
    private UUID folioId;

    @Column(name = "identificador_externo", length = 100)
    private String identificadorExterno;

    @Embedded // Este elemento se vuelve columnas de la tabla reserva
    private EstanciaEmbeddable estancia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoReserva estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origen", nullable = false, length = 12)
    private CanalOrigen canalOrigen;

    @Column(name = "documento_titular", nullable = false, length = 15)
    private String documentoTitular;

    @ElementCollection
    @CollectionTable(name = "reserva_ocupantes", // Creación de tabla
            joinColumns = @JoinColumn(name = "reserva_id")) // Relación llave foránea
    private List<OcupanteEmbeddable> ocupantes = new ArrayList<>();

    @Column(name = "hora_estimada_llegada")
    private LocalTime horaEstimadaLlegada;

    @Column(name = "valor_congelado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorCongelado;

    @Embedded
    private PoliticaEmbeddable politicaCongelada;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "titular_usuario_id", nullable = false)
    private UUID titularUsuarioId;

    @Column(name = "titular_correo", nullable = false, length = 150)
    private String titularCorreo;

    @Column(name = "titular_telefono", nullable = false, length = 30)
    private String titularTelefono;

    @Column(name = "porcentaje_retencion_no_show", nullable = false, precision = 5, scale = 4)
    private BigDecimal porcentajeRetencionNoShow;

    @ElementCollection
    @CollectionTable(name = "reserva_tramos_cancelacion", joinColumns = @JoinColumn(name = "reserva_id"))
    private List<TramoCancelacionEmbeddable> tramosCancelacion = new ArrayList<>();

    @Column(name = "motivo_cancelacion", length = 300)
    private String motivoCancelacion;
}
