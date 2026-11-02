package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.CrearBloqueoRequest;
import co.uniquindio.edu.sga.application.dto.response.BloqueoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Endpoints para consultar y crear bloqueos del calendario de un apartamento. */
@RestController
@RequestMapping("/api/apartamentos/{id}/bloqueos")
public class BloqueoController {

    // POST -> /api/apartamentos/{id}/bloqueos
    @PostMapping
    public ResponseEntity<BloqueoResponse> crearBloqueo(@PathVariable String id, @Valid @RequestBody CrearBloqueoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // GET -> /api/apartamentos/{id}/bloqueos
    @GetMapping public ResponseEntity<List<BloqueoResponse>> consultarBloqueo(@PathVariable String id) {
        return RestStub.noImplementado();
    }
}
