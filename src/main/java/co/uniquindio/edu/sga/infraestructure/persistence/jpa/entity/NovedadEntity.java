package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "novedades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovedadEntity {

    @Id
    private String id;
    private String apartamentoId;
    private String autor;
    private String descripcion;
    private String gravedad;
    private LocalDateTime fechaReporte;
    private boolean anulada;
}
