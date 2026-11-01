package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.infraestructure.rest.mapper.ReporteMapper.ReporteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rutas REST de consultas agregadas y reportes. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    // GET -> api/reportes/
    @GetMapping
    public ResponseEntity<ReporteResponse> consultarReporte(@RequestParam String desde, @RequestParam String hasta) {
        return ResponseEntity.status(501).build();
    }

    // GET -> api/reportes
    @GetMapping("/ocupacion")
    public ResponseEntity<ReporteResponse> consultarOcupacionApartamento(@RequestParam String desde, @RequestParam String hasta) {
        return ResponseEntity.status(501).build();
    }
}
