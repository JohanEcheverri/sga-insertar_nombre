package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Bloqueo")
@Getter
@Setter
@NoArgsConstructor
public class BloqueoEntity {

    @Id
    private String id;

    @Column(name = "apartamento_id", nullable = false, length = 15)
    private String apartamentoId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "motivo", length = 250)
    private String motivo;
    @Column(nullable = false)
    private boolean activo = true;
}
