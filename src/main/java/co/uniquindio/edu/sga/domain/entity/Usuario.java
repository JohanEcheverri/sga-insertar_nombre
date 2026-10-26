package co.uniquindio.edu.sga.domain.entity;

import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.CorreoElectronico;
import co.uniquindio.edu.sga.domain.valueobject.DocumentoIdentidad;
import co.uniquindio.edu.sga.domain.valueobject.FechaNacimiento;
import co.uniquindio.edu.sga.domain.valueobject.NombreCompleto;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import co.uniquindio.edu.sga.domain.valueobject.Telefono;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

/** Entidad que representa la identidad y estado de una persona registrada en el sistema. */
public class Usuario {
    // Se conservan exactamente los atributos definidos para Usuario.
    private NombreCompleto nombreCompleto;
    private UsuarioId id;
    private DocumentoIdentidad documento;
    private CorreoElectronico correo;
    private Telefono telefono;
    private String contrasenia;
    private RolUsuario rol;
    private boolean activo;
    private FechaNacimiento fechaNacimiento;

    private Usuario(NombreCompleto nombreCompleto, UsuarioId id, DocumentoIdentidad documento,
                    CorreoElectronico correo, Telefono telefono, String contrasenia,
                    RolUsuario rol, boolean activo, FechaNacimiento fechaNacimiento) {
        this.nombreCompleto = nombreCompleto;
        this.id = id;
        this.documento = documento;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.activo = activo;
        this.fechaNacimiento = fechaNacimiento;
    }

    /** Regla de creación: todo usuario debe tener identidad, contacto, credencial, rol y fecha de nacimiento válidos. */
    public static Usuario crear(NombreCompleto nombreCompleto, UsuarioId id,
                                DocumentoIdentidad documento, CorreoElectronico correo,
                                Telefono telefono, String contrasenia, RolUsuario rol,
                                boolean activo, FechaNacimiento fechaNacimiento) {
        exigir(nombreCompleto != null, "El usuario debe tener nombre completo.");
        exigir(id != null, "El usuario debe tener identificador.");
        exigir(documento != null, "El usuario debe tener documento de identidad.");
        exigir(correo != null, "El usuario debe tener correo electrónico.");
        exigir(telefono != null, "El usuario debe tener teléfono.");
        validarContrasenia(contrasenia);
        exigir(rol != null, "El usuario debe tener un rol.");
        exigir(fechaNacimiento != null, "El usuario debe tener fecha de nacimiento.");
        return new Usuario(nombreCompleto, id, documento, correo, telefono,
                contrasenia, rol, activo, fechaNacimiento);
    }

    /** Genera un identificador UUID y registra el usuario inicialmente activo. */
    public static Usuario registrar(NombreCompleto nombreCompleto, DocumentoIdentidad documento,
                                    CorreoElectronico correo, Telefono telefono,
                                    String contrasenia, RolUsuario rol,
                                    FechaNacimiento fechaNacimiento) {
        return crear(nombreCompleto, UsuarioId.generar(), documento, correo, telefono,
                contrasenia, rol, true, fechaNacimiento);
    }

    /** Cambia el rol explícitamente; la autorización de acciones según el rol pertenece a los casos de uso. */
    public void cambiarRol(RolUsuario nuevoRol) {
        exigir(nuevoRol != null, "El nuevo rol del usuario no puede ser nulo.");
        this.rol = nuevoRol;
    }

    /** Rol consultable por los casos de uso; este método no concede por sí mismo permisos de aplicación. */
    public boolean tieneRol(RolUsuario rolConsultado) {
        exigir(rolConsultado != null, "El rol consultado no puede ser nulo.");
        return this.rol == rolConsultado;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void activar() {
        exigir(!activo, "El usuario ya está activo.");
        activo = true;
    }

    public void desactivar() {
        exigir(activo, "El usuario ya está inactivo.");
        activo = false;
    }

    public void actualizarNombreCompleto(NombreCompleto nombreCompleto) {
        exigir(nombreCompleto != null, "El nombre completo no puede ser nulo.");
        this.nombreCompleto = nombreCompleto;
    }

    public void actualizarDocumento(DocumentoIdentidad documento) {
        exigir(documento != null, "El documento no puede ser nulo.");
        this.documento = documento;
    }

    public void actualizarCorreo(CorreoElectronico correo) {
        exigir(correo != null, "El correo no puede ser nulo.");
        this.correo = correo;
    }

    public void actualizarTelefono(Telefono telefono) {
        exigir(telefono != null, "El teléfono no puede ser nulo.");
        this.telefono = telefono;
    }

    public void actualizarFechaNacimiento(FechaNacimiento fechaNacimiento) {
        exigir(fechaNacimiento != null, "La fecha de nacimiento no puede ser nula.");
        this.fechaNacimiento = fechaNacimiento;
    }

    /** Recibe la credencial ya protegida por la capa de aplicación; no realiza hashing ni autenticación. */
    public void cambiarContrasenia(String nuevaContrasenia) {
        validarContrasenia(nuevaContrasenia);
        this.contrasenia = nuevaContrasenia;
    }

    private static void validarContrasenia(String contrasenia) {
        exigir(contrasenia != null && !contrasenia.isBlank(), "La contraseña es obligatoria.");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new ReglaDominioException(mensaje);
    }

    public NombreCompleto getNombreCompleto() { return nombreCompleto; }
    public UsuarioId getId() { return id; }
    public DocumentoIdentidad getDocumento() { return documento; }
    public CorreoElectronico getCorreo() { return correo; }
    public Telefono getTelefono() { return telefono; }
    /** Devuelve la credencial almacenada, que debe ser un hash y nunca una contraseña en texto claro. */
    public String getContrasenia() { return contrasenia; }
    public RolUsuario getRol() { return rol; }
    public FechaNacimiento getFechaNacimiento() { return fechaNacimiento; }
}
