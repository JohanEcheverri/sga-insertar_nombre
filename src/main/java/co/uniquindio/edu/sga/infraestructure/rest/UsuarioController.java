package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.ModificarUsuarioRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearUsuarioRequest;
import co.uniquindio.edu.sga.application.dto.response.UsuarioResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.usecase.BuscarUsuarioPorCodigoUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarHistorialDeReservasHuespedUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearUsuarioUseCase;
import co.uniquindio.edu.sga.application.usecase.EliminarUsuarioUseCase;
import co.uniquindio.edu.sga.application.usecase.ModificarUsuarioUseCase;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.ReservaMapper;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de usuarios. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ModificarUsuarioUseCase modificarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final BuscarUsuarioPorCodigoUseCase buscarUsuarioPorCodigoUseCase;
    private final ConsultarHistorialDeReservasHuespedUseCase consultarHistorialUseCase;
    private final UsuarioMapper usuarioMapper;
    private final ReservaMapper reservaMapper;
    private final SolicitanteActualPort solicitanteActual;

    public UsuarioController(CrearUsuarioUseCase crearUsuarioUseCase,
            ModificarUsuarioUseCase modificarUsuarioUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase,
            BuscarUsuarioPorCodigoUseCase buscarUsuarioPorCodigoUseCase,
            ConsultarHistorialDeReservasHuespedUseCase consultarHistorialUseCase,
            UsuarioMapper usuarioMapper, ReservaMapper reservaMapper, SolicitanteActualPort solicitanteActual) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.modificarUsuarioUseCase = modificarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.buscarUsuarioPorCodigoUseCase = buscarUsuarioPorCodigoUseCase;
        this.consultarHistorialUseCase = consultarHistorialUseCase;
        this.usuarioMapper = usuarioMapper;
        this.reservaMapper = reservaMapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/usuarios
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest solicitud) {
        RolUsuario rol = RolUsuario.valueOf(solicitud.rol());
        var solicitante = rol == RolUsuario.HUESPED ? solicitanteActual.autenticadoOpcional().orElse(null) : solicitanteActual.obtener();
        var comando = new CrearUsuarioUseCase.Command(solicitud.nombre(), solicitud.apellido(), solicitud.documento(),
                solicitud.correo(), solicitud.telefono(), solicitud.contrasena(), rol, solicitud.fechaNacimiento(), solicitante);
        return ResponseEntity.status(201).body(usuarioMapper.aRespuesta(crearUsuarioUseCase.ejecutar(comando)));
    }

    // PUT -> /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> modificarUsuario(@PathVariable String id, @Valid @RequestBody ModificarUsuarioRequest solicitud) {
        var comando = new ModificarUsuarioUseCase.Command(java.util.UUID.fromString(id), solicitud.nombre(),
                solicitud.apellido(), solicitud.telefono(), solicitanteActual.obtener());
        return ResponseEntity.ok(usuarioMapper.aRespuesta(modificarUsuarioUseCase.ejecutar(comando)));
    }

    // DELETE -> /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        eliminarUsuarioUseCase.ejecutar(new EliminarUsuarioUseCase.Command(java.util.UUID.fromString(id), solicitanteActual.obtener()));
        return ResponseEntity.noContent().build();
    }

    // GET -> /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorCodigo(@PathVariable String id) {
        var resultado = buscarUsuarioPorCodigoUseCase.ejecutar(new BuscarUsuarioPorCodigoUseCase.Command(
                java.util.UUID.fromString(id), solicitanteActual.obtener()));
        return ResponseEntity.ok(usuarioMapper.aRespuesta(resultado));
    }

    // GET -> api/usuarios/{id})/reservas
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarHistorialReservasHuesped(@PathVariable String id) {
        var pagina = consultarHistorialUseCase.ejecutar(new ConsultarHistorialDeReservasHuespedUseCase.Command(
                java.util.UUID.fromString(id), solicitanteActual.obtener(), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(reservaMapper::aResumen).toList());
    }
}
