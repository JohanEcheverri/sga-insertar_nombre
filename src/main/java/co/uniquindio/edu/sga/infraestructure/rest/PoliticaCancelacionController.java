package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.DefinirPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.request.ActualizarPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.response.PoliticaCancelacionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints para definir y actualizar la política vigente del alojamiento. */
@RestController
@RequestMapping("/api/politicas-cancelacion")
public class PoliticaCancelacionController {

    // POST -> /api/politica-cancelacion
    @PostMapping
    public ResponseEntity<PoliticaCancelacionResponse> definir(@Valid @RequestBody DefinirPoliticaCancelacionRequest solicitud) {
        return ResponseEntity.status(501).build();
    }

    // PUT -> /api/politica-cancelacion
    @PutMapping
    public ResponseEntity<PoliticaCancelacionResponse> actualizar(@Valid @RequestBody ActualizarPoliticaCancelacionRequest solicitud) {
        return ResponseEntity.status(501).build();
    }
}
