package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.RegistrarNovedadRequest;
import co.uniquindio.edu.sga.application.dto.response.NovedadResponse;
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
    @PostMapping("/apartamentos/{id}/novedades")
    public ResponseEntity<NovedadResponse> crearNovedad(@PathVariable String id, @Valid @RequestBody RegistrarNovedadRequest solicitud) {
        return RestStub.noImplementado();
    }

    @GetMapping("/apartamentos/{id}/novedades")
    public ResponseEntity<List<NovedadResponse>> consultarNovedad(@PathVariable String id) {
        return RestStub.noImplementado();
    }

    @DeleteMapping("/novedades/{id}")
    public ResponseEntity<Void> eliminarNovedad(@PathVariable String id) {
        return RestStub.noImplementado();
    }
}
