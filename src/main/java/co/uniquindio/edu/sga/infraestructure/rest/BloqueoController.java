package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.CrearBloqueoRequest;
import co.uniquindio.edu.sga.application.dto.response.BloqueoResponse;
import co.uniquindio.edu.sga.application.usecase.ConsultarBloqueoUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearBloqueoUseCase;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.BloqueoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


/** Endpoints para consultar y crear bloqueos del calendario de un apartamento. */
@RestController
@RequestMapping("/api/apartamentos/{id}/bloqueos")
public class BloqueoController {

    private final CrearBloqueoUseCase crearBloqueoUseCase;
    private final ConsultarBloqueoUseCase consultarBloqueoUseCase;
    private final BloqueoMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public BloqueoController(CrearBloqueoUseCase crearBloqueoUseCase,
                             ConsultarBloqueoUseCase consultarBloqueoUseCase,
                             BloqueoMapper mapper, SolicitanteActualPort solicitanteActual) {
        this.crearBloqueoUseCase = crearBloqueoUseCase;
        this.consultarBloqueoUseCase = consultarBloqueoUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/apartamentos/{id}/bloqueos
    @PostMapping
    public ResponseEntity<BloqueoResponse> crearBloqueo(@PathVariable String id, @Valid @RequestBody CrearBloqueoRequest solicitud) {
        var comando = mapper.aComando(id, solicitud, solicitanteActual.obtener());
        var resultado = crearBloqueoUseCase.ejecutar(comando);
        return ResponseEntity.status(201).body(mapper.aRespuesta(resultado));
    }

    // GET -> /api/apartamentos/{id}/bloqueos
    @GetMapping public ResponseEntity<List<BloqueoResponse>> consultarBloqueo(@PathVariable String id) {
        if (!solicitanteActual.obtener().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo el personal interno puede consultar bloqueos.");
        }
        var pagina = consultarBloqueoUseCase.ejecutar(new ConsultarBloqueoUseCase.Command(UUID.fromString(id), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aRespuesta).toList());
    }
}
