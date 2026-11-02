package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.response.IndicadoresConsolidadosResponse;
import co.uniquindio.edu.sga.application.dto.response.ReporteOcupacionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

/** Rutas REST de consultas agregadas y reportes. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    // GET -> api/reportes/
    @GetMapping
    public ResponseEntity<IndicadoresConsolidadosResponse> consultarReporte(
            @RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) {
        return ResponseEntity.status(501).build();
    }

    // GET -> api/reportes
    @GetMapping("/ocupacion")
    public ResponseEntity<ReporteOcupacionResponse> consultarOcupacionApartamento(
            @RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) {
        return ResponseEntity.status(501).build();
    }
}
