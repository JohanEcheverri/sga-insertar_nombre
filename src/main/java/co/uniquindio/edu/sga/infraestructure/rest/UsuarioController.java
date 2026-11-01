package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Rutas REST de usuarios. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // POST -> /api/usuarios
    @PostMapping
    public ResponseEntity<Void> crearUsuario(@RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> modificarUsuario(@PathVariable String id, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Void> buscarUsuarioPorCodigo(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> api/usuarios/{id})/reservas
    @GetMapping("/{id}/reservas")
    public ResponseEntity<Void> consultarHistorialReservasHuesped(@PathVariable String id) {
        return RestStub.noImplementado();
    }
}
