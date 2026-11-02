package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.CrearApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.request.ModificarApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.request.CambiarEstadoApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.response.ApartamentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


/** Rutas REST de apartamentos y recursos dependientes de estos. */
@RestController
@RequestMapping("/api/apartamentos")
public class ApartamentoController {

    // POST -> /api/apartamentos
    @PostMapping
    public ResponseEntity<ApartamentoResponse> crearApartamento(@Valid @RequestBody CrearApartamentoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/apartamentos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApartamentoResponse> modificarApartamento(@PathVariable String id, @Valid @RequestBody ModificarApartamentoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // DELETE -> /api/apartamentos/{id{
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarApartamento(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    // GET -> /api/apartamentos/preparacion-pendiente
    @GetMapping("/preparacion-pendiente")
    public ResponseEntity<List<ApartamentoResponse>> consultarApartamentosPreparacionPendientes() {
        return RestStub.noImplementado();
    }

    // PATCH -> /api/aparatmentos/{id}/estado-operativo
    @PatchMapping("/{id}/estado-operativo")
    public ResponseEntity<ApartamentoResponse> modificarEstadoOperativoApartamento(@PathVariable String id, @Valid @RequestBody CambiarEstadoApartamentoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> api/apartamentos/disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<ApartamentoResponse>> consultarDisponibilidadApartamentoPorFecha(@RequestParam LocalDate fechaEntrada, @RequestParam LocalDate fechaSalida) {
        return RestStub.noImplementado();
    }

    // GET -> api/apartamentos/buscar-capacidad
    @GetMapping("/buscar-capacidad") public ResponseEntity<List<ApartamentoResponse>> buscarApartamentoPorCapacidad(@RequestParam int capacidad) {
        return RestStub.noImplementado();
    }
}
