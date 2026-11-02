package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.AsignarTarifaRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearTemporadaRequest;
import co.uniquindio.edu.sga.application.dto.request.ModificarTemporadaRequest;
import co.uniquindio.edu.sga.application.dto.response.TemporadaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de temporadas y asignación de tarifas. */
@RestController
@RequestMapping("/api")
public class TemporadaController {

    // POST -> /api/temporadas
    @PostMapping("/temporadas")
    public ResponseEntity<TemporadaResponse> crearTemporada(@Valid @RequestBody CrearTemporadaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/temporadas/{id}
    @PutMapping("/temporadas/{id}")
    public ResponseEntity<TemporadaResponse> modificarTemporada(@PathVariable String id, @Valid @RequestBody ModificarTemporadaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/temporadas/{id}
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Void> eliminarTemporada(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/temporadas
    @GetMapping("/temporadas")
    public ResponseEntity<List<TemporadaResponse>> buscarTemporada(@RequestParam(required = false) String fecha) {
        return RestStub.noImplementado();
    }

    // POST -> /api/tarifas
    @PostMapping("/tarifas") public ResponseEntity<TemporadaResponse> asignarTarifaATemporada(@Valid @RequestBody AsignarTarifaRequest solicitud) {
        return RestStub.noImplementado();
    }
}
