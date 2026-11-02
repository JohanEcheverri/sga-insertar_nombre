package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.response.IndicadoresConsolidadosResponse;
import co.uniquindio.edu.sga.application.dto.response.ReporteOcupacionResponse;
import co.uniquindio.edu.sga.application.usecase.ConsultarIndicadoresConsolidadosUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarOcupacionApartamentoUseCase;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.ReporteMapper;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
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

    private final ConsultarIndicadoresConsolidadosUseCase consultarIndicadoresConsolidadosUseCase;
    private final ConsultarOcupacionApartamentoUseCase consultarOcupacionApartamentoUseCase;
    private final ReporteMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public ReporteController(ConsultarIndicadoresConsolidadosUseCase consultarIndicadoresConsolidadosUseCase,
            ConsultarOcupacionApartamentoUseCase consultarOcupacionApartamentoUseCase, ReporteMapper mapper,
            SolicitanteActualPort solicitanteActual) {
        this.consultarIndicadoresConsolidadosUseCase = consultarIndicadoresConsolidadosUseCase;
        this.consultarOcupacionApartamentoUseCase = consultarOcupacionApartamentoUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // GET -> api/reportes/
    @GetMapping
    public ResponseEntity<IndicadoresConsolidadosResponse> consultarReporte(
            @RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) {
        exigirPersonalInterno();
        var reporte = consultarIndicadoresConsolidadosUseCase.ejecutar(
                new ConsultarIndicadoresConsolidadosUseCase.Command(fechaInicio, fechaFin));
        return ResponseEntity.ok(mapper.aIndicadoresConsolidados(reporte));
    }

    // GET -> api/reportes
    @GetMapping("/ocupacion")
    public ResponseEntity<ReporteOcupacionResponse> consultarOcupacionApartamento(
            @RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) {
        exigirPersonalInterno();
        var resultado = consultarOcupacionApartamentoUseCase.ejecutar(
                new ConsultarOcupacionApartamentoUseCase.Command(fechaInicio, fechaFin));
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        int apartamentos = dias == 0 ? 0 : (int) (resultado.nochesDisponibles() / dias);
        return ResponseEntity.ok(new ReporteOcupacionResponse(fechaInicio, fechaFin,
                resultado.porcentajeOcupacion(), resultado.nochesVendidas(), resultado.nochesDisponibles(), apartamentos));
    }

    private void exigirPersonalInterno() {
        if (!solicitanteActual.obtener().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo el personal interno puede consultar reportes.");
        }
    }
}
