package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;
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
    private String numero;
    private int capacidad;
    @Column(name = "tarifa_noche", precision = 15, scale = 2)
    private BigDecimal tarifaNoche;
}
