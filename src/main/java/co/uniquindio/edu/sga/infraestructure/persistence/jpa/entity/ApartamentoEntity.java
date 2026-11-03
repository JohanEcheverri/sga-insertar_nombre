package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apartamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartamentoEntity {

    @Id
    private String id;
    private String alojamientoId;
    private String identificadorUnico;
    private String descripcion;
    private int capacidad;
    private String estadoOperativo;
    private boolean disponible;
    private boolean activo = true;
    @ElementCollection
    @CollectionTable(name = "apartamento_dormitorios", joinColumns = @JoinColumn(name = "apartamento_id"))
    private List<DormitorioEmbeddable> dormitorios = new ArrayList<>();
}
