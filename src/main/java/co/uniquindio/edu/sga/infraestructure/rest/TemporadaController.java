package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.AsignarTarifaRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearTemporadaRequest;
import co.uniquindio.edu.sga.application.dto.request.ModificarTemporadaRequest;
import co.uniquindio.edu.sga.application.dto.response.TemporadaResponse;
import co.uniquindio.edu.sga.application.usecase.AsignarTarifaUseCase;
import co.uniquindio.edu.sga.application.usecase.BuscarTemporadaUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearTemporadaUseCase;
import co.uniquindio.edu.sga.application.usecase.EliminarTemporadaUseCase;
import co.uniquindio.edu.sga.application.usecase.ModificarTemporadaUseCase;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.TemporadaMapper;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de temporadas y asignación de tarifas. */
@RestController
@RequestMapping("/api")
public class TemporadaController {

    private final CrearTemporadaUseCase crearTemporadaUseCase;
    private final ModificarTemporadaUseCase modificarTemporadaUseCase;
    private final EliminarTemporadaUseCase eliminarTemporadaUseCase;
    private final BuscarTemporadaUseCase buscarTemporadaUseCase;
    private final AsignarTarifaUseCase asignarTarifaUseCase;
    private final TemporadaMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public TemporadaController(CrearTemporadaUseCase crearTemporadaUseCase,
            ModificarTemporadaUseCase modificarTemporadaUseCase,
            EliminarTemporadaUseCase eliminarTemporadaUseCase,
            BuscarTemporadaUseCase buscarTemporadaUseCase,
            AsignarTarifaUseCase asignarTarifaUseCase, TemporadaMapper mapper,
            SolicitanteActualPort solicitanteActual) {
        this.crearTemporadaUseCase = crearTemporadaUseCase;
        this.modificarTemporadaUseCase = modificarTemporadaUseCase;
        this.eliminarTemporadaUseCase = eliminarTemporadaUseCase;
        this.buscarTemporadaUseCase = buscarTemporadaUseCase;
        this.asignarTarifaUseCase = asignarTarifaUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/temporadas
    @PostMapping("/temporadas")
    public ResponseEntity<TemporadaResponse> crearTemporada(@Valid @RequestBody CrearTemporadaRequest solicitud) {
        var resultado = crearTemporadaUseCase.ejecutar(new CrearTemporadaUseCase.Command(
                solicitud.nombre(), solicitud.fechaInicio(), solicitud.fechaFin(), java.util.Map.of(), solicitanteActual.obtener()));
        return ResponseEntity.status(201).body(mapper.aRespuesta(resultado));
    }

    // PUT -> /api/temporadas/{id}
    @PutMapping("/temporadas/{id}")
    public ResponseEntity<TemporadaResponse> modificarTemporada(@PathVariable String id, @Valid @RequestBody ModificarTemporadaRequest solicitud) {
        var resultado = modificarTemporadaUseCase.ejecutar(new ModificarTemporadaUseCase.Command(
                java.util.UUID.fromString(id), solicitud.nombre(), solicitud.fechaInicio(), solicitud.fechaFin(), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aRespuesta(resultado));
    }

    // DELETE -> /api/temporadas/{id}
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Void> eliminarTemporada(@PathVariable String id) {
        eliminarTemporadaUseCase.ejecutar(new EliminarTemporadaUseCase.Command(java.util.UUID.fromString(id), solicitanteActual.obtener()));
        return ResponseEntity.noContent().build();
    }

    // GET -> /api/temporadas
    @GetMapping("/temporadas")
    public ResponseEntity<List<TemporadaResponse>> buscarTemporada(@RequestParam(required = false) String fecha) {
        var dia = fecha == null || fecha.isBlank() ? null : java.time.LocalDate.parse(fecha);
        var pagina = buscarTemporadaUseCase.ejecutar(new BuscarTemporadaUseCase.Command(0, dia));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aRespuesta).toList());
    }

    // POST -> /api/tarifas
    @PostMapping("/tarifas") public ResponseEntity<TemporadaResponse> asignarTarifaATemporada(@Valid @RequestBody AsignarTarifaRequest solicitud) {
        var resultado = asignarTarifaUseCase.ejecutar(new AsignarTarifaUseCase.Command(
                java.util.UUID.fromString(solicitud.temporadaId()), java.util.UUID.fromString(solicitud.apartamentoId()),
                solicitud.precioPorNochePorOcupante(), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aRespuesta(resultado));
    }
}
