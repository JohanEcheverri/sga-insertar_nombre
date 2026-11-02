package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.RegistrarCargoRequest;
import co.uniquindio.edu.sga.application.dto.request.CerrarFolioRequest;
import co.uniquindio.edu.sga.application.dto.request.RegistrarPagoRequest;
import co.uniquindio.edu.sga.application.dto.response.CargoResponse;
import co.uniquindio.edu.sga.application.dto.response.FolioResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/** Endpoints para consultar y operar el folio asociado a una reserva. */
@RestController
@RequestMapping("/api/reservas/{codigo}/folio")
public class FolioController {

    // GET -> /api/reservas/{codigo}/folio
    @GetMapping
    public ResponseEntity<FolioResponse> consultarFolio(@PathVariable String codigo) {
        return RestStub.noImplementado();
    }

    // POST -> /api/reservas/{codigo}/folio/pagos
    @PostMapping("/pagos")
    public ResponseEntity<FolioResponse> registrarPago(@PathVariable String codigo, @Valid @RequestBody RegistrarPagoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // POST -> /api/reservas/{codigo}/folio/cargos
    @PostMapping("/cargos")
    public ResponseEntity<CargoResponse> registrarCargo(@PathVariable String codigo, @Valid @RequestBody RegistrarCargoRequest solicitud) {
        return RestStub.noImplementado();
    }

    // PUT -> /api/reservas/{codigo}/folio/cerrar
    @PutMapping("/cerrar")
    public ResponseEntity<FolioResponse> cerrarFolio(@PathVariable String codigo, @Valid @RequestBody CerrarFolioRequest solicitud) {
        return RestStub.noImplementado();
    }
}
