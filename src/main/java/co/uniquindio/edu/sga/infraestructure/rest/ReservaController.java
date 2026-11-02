package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.ModificarReservaRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearReservaRequest;
import co.uniquindio.edu.sga.application.dto.request.RegistrarLlegadaRequest;
import co.uniquindio.edu.sga.application.dto.request.RegistrarSalidaRequest;
import co.uniquindio.edu.sga.application.dto.response.ReservaDetalleResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de reservas */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    // POST -> /api/reservas/
    @PostMapping
    public ResponseEntity<ReservaDetalleResponse> crearReserva(@Valid @RequestBody CrearReservaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> /api/reservas
    @GetMapping
    public ResponseEntity<List<ReservaResumenResponse>> consultarReservasPorEstado(@RequestParam String estado) {
        return RestStub.noImplementado();
    }

    // GET -> /api/reservas/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> consultarReservasPorCodigo(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/confirmar
    @PutMapping("/{codigo}/confirmar")
    public ResponseEntity<ReservaDetalleResponse> confirmarReserva(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/cancelar
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<ReservaDetalleResponse> cancelarReserva(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/registrar-llegada
    @PutMapping("/{codigo}/registrar-llegada")
    public ResponseEntity<ReservaDetalleResponse> registrarLlegada(@PathVariable String codigo, @Valid @RequestBody RegistrarLlegadaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/registrar-salida
    @PutMapping("/{codigo}/registrar-salida")
    public ResponseEntity<ReservaDetalleResponse> registrarSalida(@PathVariable String codigo, @Valid @RequestBody RegistrarSalidaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}/no-show
    @PutMapping("/{codigo}/no-show")
    public ResponseEntity<ReservaDetalleResponse> declararNoShow(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // PUT -> api/reservas/{codigo}
    @PutMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> modificarReserva(@PathVariable String codigo, @Valid @RequestBody ModificarReservaRequest solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> api/reservas/llegadas
    @GetMapping("/llegadas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarLlegadasPorDia(@RequestParam String fecha) {
        return RestStub.noImplementado();
    }

    // GET -> api/reservas/salidas
    @GetMapping("/salidas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarSalidasPorDia(@RequestParam String fecha) {
        return RestStub.noImplementado();
    }
    // GET -> api/reservas/canales
    @GetMapping("/canales")
    public ResponseEntity<List<ReservaResumenResponse>> consultarReservasPorCanal(@RequestParam String canal) {
        return RestStub.noImplementado();
    }
}
