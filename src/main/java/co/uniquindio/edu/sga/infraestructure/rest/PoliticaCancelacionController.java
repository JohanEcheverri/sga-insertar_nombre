package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.DefinirPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.request.ActualizarPoliticaCancelacionRequest;
import co.uniquindio.edu.sga.application.dto.response.PoliticaCancelacionResponse;
import co.uniquindio.edu.sga.application.usecase.ActualizarPoliticaCancelacionUseCase;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.PoliticaCancelacionMapper;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
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

    private final ActualizarPoliticaCancelacionUseCase actualizarPoliticaCancelacionUseCase;
    private final PoliticaCancelacionMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public PoliticaCancelacionController(ActualizarPoliticaCancelacionUseCase actualizarPoliticaCancelacionUseCase,
                                         PoliticaCancelacionMapper mapper, SolicitanteActualPort solicitanteActual) {
        this.actualizarPoliticaCancelacionUseCase = actualizarPoliticaCancelacionUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/politica-cancelacion
    @PostMapping
    public ResponseEntity<PoliticaCancelacionResponse> definir(@Valid @RequestBody DefinirPoliticaCancelacionRequest solicitud) {
        var resultado = actualizarPoliticaCancelacionUseCase.ejecutar(mapper.aComando(solicitud, solicitanteActual.obtener()));
        return ResponseEntity.status(201).body(mapper.aRespuesta(solicitud.tramos(), solicitud.porcentajeRetencionNoShow(), resultado.versionNueva()));
    }

    // PUT -> /api/politica-cancelacion
    @PutMapping
    public ResponseEntity<PoliticaCancelacionResponse> actualizar(@Valid @RequestBody ActualizarPoliticaCancelacionRequest solicitud) {
        var resultado = actualizarPoliticaCancelacionUseCase.ejecutar(mapper.aComando(solicitud, solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aRespuesta(solicitud.tramos(), solicitud.porcentajeRetencionNoShow(), resultado.versionNueva()));
    }
}
