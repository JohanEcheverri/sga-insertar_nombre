package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.RegistrarCargoRequest;
import co.uniquindio.edu.sga.application.dto.request.CerrarFolioRequest;
import co.uniquindio.edu.sga.application.dto.request.RegistrarPagoRequest;
import co.uniquindio.edu.sga.application.dto.response.CargoResponse;
import co.uniquindio.edu.sga.application.dto.response.FolioResponse;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.usecase.CerrarFolioUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarFolioUseCase;
import co.uniquindio.edu.sga.application.usecase.RegistrarCargoUseCase;
import co.uniquindio.edu.sga.application.usecase.RegistrarPagoUseCase;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.FolioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/** Endpoints para consultar y operar el folio asociado a una reserva. */
@RestController
@RequestMapping("/api/reservas/{codigo}/folio")
public class FolioController {

    private final ConsultarFolioUseCase consultarFolioUseCase;
    private final RegistrarPagoUseCase registrarPagoUseCase;
    private final RegistrarCargoUseCase registrarCargoUseCase;
    private final CerrarFolioUseCase cerrarFolioUseCase;
    private final FolioMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public FolioController(ConsultarFolioUseCase consultarFolioUseCase,
                           RegistrarPagoUseCase registrarPagoUseCase,
                           RegistrarCargoUseCase registrarCargoUseCase,
                           CerrarFolioUseCase cerrarFolioUseCase,
                           FolioMapper mapper, SolicitanteActualPort solicitanteActual) {
        this.consultarFolioUseCase = consultarFolioUseCase;
        this.registrarPagoUseCase = registrarPagoUseCase;
        this.registrarCargoUseCase = registrarCargoUseCase;
        this.cerrarFolioUseCase = cerrarFolioUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // GET -> /api/reservas/{codigo}/folio
    @GetMapping
    public ResponseEntity<FolioResponse> consultarFolio(@PathVariable String codigo) {
        var folio = consultarFolioUseCase.ejecutar(new ConsultarFolioUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aRespuesta(folio));
    }

    // POST -> /api/reservas/{codigo}/folio/pagos
    @PostMapping("/pagos")
    public ResponseEntity<FolioResponse> registrarPago(@PathVariable String codigo, @Valid @RequestBody RegistrarPagoRequest solicitud) {
        var folio = registrarPagoUseCase.ejecutar(new RegistrarPagoUseCase.Command(
                java.util.UUID.fromString(codigo), co.uniquindio.edu.sga.domain.valueobject.MedioPago.valueOf(solicitud.medio()),
                solicitud.monto(), solicitud.referencia(), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aRespuesta(folio));
    }

    // POST -> /api/reservas/{codigo}/folio/cargos
    @PostMapping("/cargos")
    public ResponseEntity<CargoResponse> registrarCargo(@PathVariable String codigo, @Valid @RequestBody RegistrarCargoRequest solicitud) {
        var folio = registrarCargoUseCase.ejecutar(new RegistrarCargoUseCase.Command(
                java.util.UUID.fromString(codigo), co.uniquindio.edu.sga.domain.valueobject.TipoCargo.valueOf(solicitud.tipoCargo()),
                solicitud.concepto(), solicitud.monto(), solicitanteActual.obtener()));
        var cargo = folio.cargos().get(folio.cargos().size() - 1);
        return ResponseEntity.status(201).body(new CargoResponse(cargo.tipo().name(), cargo.concepto(), cargo.monto(), cargo.fecha()));
    }

    // PUT -> /api/reservas/{codigo}/folio/cerrar
    @PutMapping("/cerrar")
    public ResponseEntity<FolioResponse> cerrarFolio(@PathVariable String codigo, @Valid @RequestBody CerrarFolioRequest solicitud) {
        var folio = cerrarFolioUseCase.ejecutar(new CerrarFolioUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener(), solicitud.motivo()));
        return ResponseEntity.ok(mapper.aRespuesta(folio));
    }
}
