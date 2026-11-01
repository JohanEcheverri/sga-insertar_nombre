package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Endpoints para consultar y crear bloqueos del calendario de un apartamento. */
@RestController
@RequestMapping("/api/apartamentos/{id}/bloqueos")
public class BloqueoController {

    // POST -> /api/apartamentos/{id}/bloqueos
    @PostMapping
    public ResponseEntity<Void> crearBloqueo(@PathVariable String id, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> /api/apartamentos/{id}/bloqueos
    @GetMapping public ResponseEntity<Void> consultarBloqueo(@PathVariable String id) {
        return RestStub.noImplementado();
    }
}
