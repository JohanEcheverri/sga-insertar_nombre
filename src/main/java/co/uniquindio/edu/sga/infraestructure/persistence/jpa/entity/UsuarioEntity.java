package co.uniquindio.edu.sga.infraestructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    private String id;
    private String nombre;
    private String apellido;
    private String documento;
    private String correo;
    private String telefono;
    private String contrasenia;
    private String rol;
    private boolean activo;
    private java.time.LocalDate fechaNacimiento;
}
