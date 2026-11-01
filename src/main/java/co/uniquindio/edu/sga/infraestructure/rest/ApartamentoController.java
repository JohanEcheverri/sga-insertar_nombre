package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Rutas REST de apartamentos y recursos dependientes de estos. */
@RestController
@RequestMapping("/api/apartamentos")
public class ApartamentoController {

    // POST -> /api/apartamentos
    @PostMapping
    public ResponseEntity<Void> crearApartamento(@RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/apartamentos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> modificarApartamento(@PathVariable String id, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/apartamentos/{id{
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarApartamento(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/apartamentos/preparacion-pendiente
    @GetMapping("/preparacion-pendiente")
    public ResponseEntity<Void> consultarApartamentosPreparacionPendientes() {
        return RestStub.noImplementado();
    }

    // PATCH -> /api/aparatmentos/{id}/estado-operativo
    @PatchMapping("/{id}/estado-operativo")
    public ResponseEntity<Void> modificarEstadoOperativoApartamento(@PathVariable String id, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> api/apartamentos/disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<Void> consultarDisponibilidadApartamentoPorFecha(@RequestParam String fechaEntrada, @RequestParam String fechaSalida) {
        return RestStub.noImplementado();
    }

    // GET -> api/apartamentos/buscar-capacidad
    @GetMapping("/buscar-capacidad") public ResponseEntity<Void> buscarApartamentoPorCapacidad(@RequestParam int capacidad) {
        return RestStub.noImplementado();
    }
}
