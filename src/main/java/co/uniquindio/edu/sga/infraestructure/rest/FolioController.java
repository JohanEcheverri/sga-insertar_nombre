package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Endpoints para consultar y operar el folio asociado a una reserva. */
@RestController
@RequestMapping("/api/reservas/{codigo}/folio")
public class FolioController {

    // GET -> /api/reservas/{codigo}/folio
    @GetMapping
    public ResponseEntity<Void> consultarFolio(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // POST -> /api/reservas/{codigo}/folio/pagos
    @PostMapping("/pagos")
    public ResponseEntity<Void> registrarPago(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // POST -> /api/reservas/{codigo}/folio/cargos
    @PostMapping("/cargos")
    public ResponseEntity<Void> registrarCargo(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/folio/cerrar
    @PutMapping("/cerrar")
    public ResponseEntity<Void> cerrarFolio(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }
}
