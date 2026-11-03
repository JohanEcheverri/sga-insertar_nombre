package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alojamientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoEntity {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String ciudad;
    private String direccion;
}