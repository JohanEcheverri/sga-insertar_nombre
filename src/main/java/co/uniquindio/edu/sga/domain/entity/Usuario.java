package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.valueobject.NombreCompleto;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;
import co.uniquindio.edu.sga.domain.valueobject.DocumentoIdentidad;
import co.uniquindio.edu.sga.domain.valueobject.FechaNacimiento;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.Telefono;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;


public class Usuario {

    // ------ Atributos Entidad Usuario ------ //

    private NombreCompleto nombreCompleto;
    private UsuarioId id;
    private DocumentoIdentidad documento;
    private CorreoElectronico correo;
    private Telefono telefono;
    private String contrasenia;
    private RolUsuario rol;
    private boolean activo;
    private FechaNacimiento fechaNacimiento;
}
