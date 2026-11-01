package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.infraestructure.rest.mapper.PoliticaCancelacionMapper.PoliticaCancelacionRequest;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.PoliticaCancelacionMapper.PoliticaCancelacionResponse;
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
    public ResponseEntity<PoliticaCancelacionResponse> definir(@RequestBody PoliticaCancelacionRequest solicitud) {
        return ResponseEntity.status(501).build();
    }

    // PUT -> /api/politica-cancelacion
    @PutMapping
    public ResponseEntity<PoliticaCancelacionResponse> actualizar(@RequestBody PoliticaCancelacionRequest solicitud) {
        return ResponseEntity.status(501).build();
    }
}
