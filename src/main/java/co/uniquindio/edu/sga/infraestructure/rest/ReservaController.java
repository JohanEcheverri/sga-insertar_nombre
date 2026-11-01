package co.uniquindio.edu.sga.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Rutas REST de reservas */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    // POST -> /api/reservas/
    @PostMapping
    public ResponseEntity<Void> crearReserva(@RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> /api/reservas
    @GetMapping
    public ResponseEntity<Void> consultarReservasPorEstado(@RequestParam String estado) {
        return RestStub.noImplementado();
    }

    // GET -> /api/reservas/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<Void> consultarReservasPorCodigo(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/confirmar
    @PutMapping("/{codigo}/confirmar")
    public ResponseEntity<Void> confirmarReserva(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/cancelar
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/registrar-llegada
    @PutMapping("/{codigo}/registrar-llegada")
    public ResponseEntity<Void> registrarLlegada(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/registrar-salida
    @PutMapping("/{codigo}/registrar-salida")
    public ResponseEntity<Void> registrarSalida(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/no-show
    @PutMapping("/{codigo}/no-show")
    public ResponseEntity<Void> declararNoShow(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}
    @PutMapping("/{codigo}")
    public ResponseEntity<Void> modificarReserva(@PathVariable String codigo, @RequestBody Map<String, Object> solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> api/reservas/llegadas
    @GetMapping("/llegadas")
    public ResponseEntity<Void> consultarLlegadasPorDia(@RequestParam String fecha) {
        return RestStub.noImplementado();
    }

    // GET -> api/reservas/salidas
    @GetMapping("/salidas")
    public ResponseEntity<Void> consultarSalidasPorDia(@RequestParam String fecha) {
        return RestStub.noImplementado();
    }
    // GET -> api/reservas/canales
    @GetMapping("/canales")
    public ResponseEntity<Void> consultarReservasPorCanal(@RequestParam String canal) {
        return RestStub.noImplementado();
    }
}
