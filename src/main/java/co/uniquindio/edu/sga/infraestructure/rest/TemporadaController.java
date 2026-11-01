package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Rutas REST de temporadas y asignación de tarifas. */
@RestController
@RequestMapping("/api")
public class TemporadaController {

    // POST -> /api/temporadas
    @PostMapping("/temporadas")
    public ResponseEntity<Void> crearTemporada(@RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/temporadas/{id}
    @PutMapping("/temporadas/{id}")
    public ResponseEntity<Void> modificarTemporada(@PathVariable String id, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/temporadas/{id}
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Void> eliminarTemporada(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/temporadas
    @GetMapping("/temporadas")
    public ResponseEntity<Void> buscarTemporada(@RequestParam(required = false) String fecha) {
        return RestStub.noImplementado();
    }

    // POST -> /api/tarifas
    @PostMapping("/tarifas") public ResponseEntity<Void> asignarTarifaATemporada(@RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }
}
