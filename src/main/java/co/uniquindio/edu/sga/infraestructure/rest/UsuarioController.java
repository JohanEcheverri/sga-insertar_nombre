package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.ModificarUsuarioRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearUsuarioRequest;
import co.uniquindio.edu.sga.application.dto.response.UsuarioResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de usuarios. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // POST -> /api/usuarios
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> modificarUsuario(@PathVariable String id, @Valid @RequestBody ModificarUsuarioRequest solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorCodigo(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> api/usuarios/{id})/reservas
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarHistorialReservasHuesped(@PathVariable String id) {
        return RestStub.noImplementado();
    }
}
