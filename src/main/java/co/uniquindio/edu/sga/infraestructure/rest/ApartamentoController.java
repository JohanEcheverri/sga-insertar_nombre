package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.CrearApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.request.ModificarApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.request.CambiarEstadoApartamentoRequest;
import co.uniquindio.edu.sga.application.dto.response.ApartamentoResponse;
import co.uniquindio.edu.sga.application.usecase.BuscarApartamentoPorCapacidadUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarApartamentoPreparacionPendienteUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarDisponibilidadApartamentoPorFechaUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearApartamentoUseCase;
import co.uniquindio.edu.sga.application.usecase.EliminarApartamentoUseCase;
import co.uniquindio.edu.sga.application.usecase.ModificarApartamentoUseCase;
import co.uniquindio.edu.sga.application.usecase.ModificarEstadoOperativoApartamentoUseCase;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.ApartamentoMapper;
import co.uniquindio.edu.sga.domain.valueobject.EstadoOperativo;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;


/** Rutas REST de apartamentos y recursos dependientes de estos. */
@RestController
@RequestMapping("/api/apartamentos")
public class ApartamentoController {

    private final CrearApartamentoUseCase crearApartamentoUseCase;
    private final ModificarApartamentoUseCase modificarApartamentoUseCase;
    private final EliminarApartamentoUseCase eliminarApartamentoUseCase;
    private final ConsultarApartamentoPreparacionPendienteUseCase consultarApartamentoPreparacionPendienteUseCase;
    private final ModificarEstadoOperativoApartamentoUseCase modificarEstadoOperativoApartamentoUseCase;
    private final ConsultarDisponibilidadApartamentoPorFechaUseCase consultarDisponibilidadApartamentoPorFechaUseCase;
    private final BuscarApartamentoPorCapacidadUseCase buscarApartamentoPorCapacidadUseCase;
    private final ApartamentoMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public ApartamentoController(CrearApartamentoUseCase crearApartamentoUseCase,
            ModificarApartamentoUseCase modificarApartamentoUseCase,
            EliminarApartamentoUseCase eliminarApartamentoUseCase,
            ConsultarApartamentoPreparacionPendienteUseCase consultarApartamentoPreparacionPendienteUseCase,
            ModificarEstadoOperativoApartamentoUseCase modificarEstadoOperativoApartamentoUseCase,
            ConsultarDisponibilidadApartamentoPorFechaUseCase consultarDisponibilidadApartamentoPorFechaUseCase,
            BuscarApartamentoPorCapacidadUseCase buscarApartamentoPorCapacidadUseCase,
            ApartamentoMapper mapper, SolicitanteActualPort solicitanteActual) {
        this.crearApartamentoUseCase = crearApartamentoUseCase;
        this.modificarApartamentoUseCase = modificarApartamentoUseCase;
        this.eliminarApartamentoUseCase = eliminarApartamentoUseCase;
        this.consultarApartamentoPreparacionPendienteUseCase = consultarApartamentoPreparacionPendienteUseCase;
        this.modificarEstadoOperativoApartamentoUseCase = modificarEstadoOperativoApartamentoUseCase;
        this.consultarDisponibilidadApartamentoPorFechaUseCase = consultarDisponibilidadApartamentoPorFechaUseCase;
        this.buscarApartamentoPorCapacidadUseCase = buscarApartamentoPorCapacidadUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/apartamentos
    @PostMapping
    public ResponseEntity<ApartamentoResponse> crearApartamento(@Valid @RequestBody CrearApartamentoRequest solicitud) {
        var resultado = crearApartamentoUseCase.ejecutar(mapper.aComando(solicitud, solicitanteActual.obtener()));
        return ResponseEntity.status(201).body(mapper.aRespuesta(resultado,
                solicitud.dormitorios()));
    }

    // PUT -> /api/apartamentos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApartamentoResponse> modificarApartamento(@PathVariable String id, @Valid @RequestBody ModificarApartamentoRequest solicitud) {
        var command = new ModificarApartamentoUseCase.Command(java.util.UUID.fromString(id), solicitud.aforoMaximo(),
                solicitud.descripcion(), solicitud.dormitorios().stream().map(d ->
                new co.uniquindio.edu.sga.application.command.DormitorioDatos(d.nombre(), d.cantidadCamas(), d.tipoCamas())).toList(),
                solicitanteActual.obtener());
        var resultado = modificarApartamentoUseCase.ejecutar(command);
        return ResponseEntity.ok(mapper.aRespuesta(resultado.apartamento(), solicitud.dormitorios()));
    }

    // DELETE -> /api/apartamentos/{id{
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarApartamento(@PathVariable String id) {
        eliminarApartamentoUseCase.ejecutar(new EliminarApartamentoUseCase.Command(java.util.UUID.fromString(id), solicitanteActual.obtener()));
        return ResponseEntity.noContent().build();
    }

    // GET -> /api/apartamentos/preparacion-pendiente
    @GetMapping("/preparacion-pendiente")
    public ResponseEntity<List<ApartamentoResponse>> consultarApartamentosPreparacionPendientes() {
        if (!solicitanteActual.obtener().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo el personal interno puede consultar unidades pendientes de preparación.");
        }
        var pagina = consultarApartamentoPreparacionPendienteUseCase.ejecutar(new ConsultarApartamentoPreparacionPendienteUseCase.Command(0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aRespuesta).toList());
    }

    // PATCH -> /api/aparatmentos/{id}/estado-operativo
    @PatchMapping("/{id}/estado-operativo")
    public ResponseEntity<ApartamentoResponse> modificarEstadoOperativoApartamento(@PathVariable String id, @Valid @RequestBody CambiarEstadoApartamentoRequest solicitud) {
        var comando = new ModificarEstadoOperativoApartamentoUseCase.Command(java.util.UUID.fromString(id),
                EstadoOperativo.valueOf(solicitud.estadoOperativo()), solicitanteActual.obtener());
        return ResponseEntity.ok(mapper.aRespuesta(modificarEstadoOperativoApartamentoUseCase.ejecutar(comando)));
    }

    // GET -> api/apartamentos/disponibilidad
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<ApartamentoResponse>> consultarDisponibilidadApartamentoPorFecha(@RequestParam LocalDate fechaEntrada, @RequestParam LocalDate fechaSalida,
            @RequestParam List<LocalDate> fechasNacimiento) {
        var comando = new ConsultarDisponibilidadApartamentoPorFechaUseCase.Command(fechaEntrada, fechaSalida,
                fechasNacimiento, null, null, 0);
        var pagina = consultarDisponibilidadApartamentoPorFechaUseCase.ejecutar(comando);
        return ResponseEntity.ok(pagina.contenido().stream().map(disponible -> mapper.aRespuesta(disponible.apartamento())).toList());
    }

    // GET -> api/apartamentos/buscar-capacidad
    @GetMapping("/buscar-capacidad") public ResponseEntity<List<ApartamentoResponse>> buscarApartamentoPorCapacidad(@RequestParam int capacidad) {
        var pagina = buscarApartamentoPorCapacidadUseCase.ejecutar(new BuscarApartamentoPorCapacidadUseCase.Command(capacidad, 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aRespuesta).toList());
    }
}
