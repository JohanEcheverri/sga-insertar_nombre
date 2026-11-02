package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.RegistrarNovedadRequest;
import co.uniquindio.edu.sga.application.dto.response.NovedadResponse;
import co.uniquindio.edu.sga.application.usecase.ConsultarNovedadUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearNovedadUseCase;
import co.uniquindio.edu.sga.application.usecase.EliminarNovedadUseCase;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.NovedadMapper;
import co.uniquindio.edu.sga.domain.valueobject.GravedadNovedad;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


/** Rutas REST de eliminación de novedades reportadas en apartamentos. */
@RestController
@RequestMapping("/api")
public class NovedadController {

    private final CrearNovedadUseCase crearNovedadUseCase;
    private final ConsultarNovedadUseCase consultarNovedadUseCase;
    private final EliminarNovedadUseCase eliminarNovedadUseCase;
    private final NovedadMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public NovedadController(CrearNovedadUseCase crearNovedadUseCase,
                             ConsultarNovedadUseCase consultarNovedadUseCase,
                             EliminarNovedadUseCase eliminarNovedadUseCase,
                             NovedadMapper mapper, SolicitanteActualPort solicitanteActual) {
        this.crearNovedadUseCase = crearNovedadUseCase;
        this.consultarNovedadUseCase = consultarNovedadUseCase;
        this.eliminarNovedadUseCase = eliminarNovedadUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    @PostMapping("/apartamentos/{id}/novedades")
    public ResponseEntity<NovedadResponse> crearNovedad(@PathVariable String id, @Valid @RequestBody RegistrarNovedadRequest solicitud) {
        var command = new CrearNovedadUseCase.Command(java.util.UUID.fromString(id), solicitud.autor(),
                solicitud.descripcion(), GravedadNovedad.valueOf(solicitud.gravedad()));
        return ResponseEntity.status(201).body(mapper.aRespuesta(crearNovedadUseCase.ejecutar(command)));
    }

    @GetMapping("/apartamentos/{id}/novedades")
    public ResponseEntity<List<NovedadResponse>> consultarNovedad(@PathVariable String id) {
        if (!solicitanteActual.obtener().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo el personal interno puede consultar novedades.");
        }
        var pagina = consultarNovedadUseCase.ejecutar(new ConsultarNovedadUseCase.Command(java.util.UUID.fromString(id), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aRespuesta).toList());
    }

    @DeleteMapping("/novedades/{id}")
    public ResponseEntity<Void> eliminarNovedad(@PathVariable String id) {
        eliminarNovedadUseCase.ejecutar(new EliminarNovedadUseCase.Command(java.util.UUID.fromString(id), solicitanteActual.obtener()));
        return ResponseEntity.noContent().build();
    }
}
