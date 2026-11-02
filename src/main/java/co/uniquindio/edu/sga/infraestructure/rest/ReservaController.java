package co.uniquindio.edu.sga.infraestructure.rest;

import co.uniquindio.edu.sga.application.dto.request.ModificarReservaRequest;
import co.uniquindio.edu.sga.application.dto.request.CrearReservaRequest;
import co.uniquindio.edu.sga.application.dto.response.ReservaDetalleResponse;
import co.uniquindio.edu.sga.application.dto.response.ReservaResumenResponse;
import co.uniquindio.edu.sga.application.command.OcupanteDatos;
import co.uniquindio.edu.sga.application.command.TitularDatos;
import co.uniquindio.edu.sga.application.security.SolicitanteActualPort;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.usecase.CancelarReservaUseCase;
import co.uniquindio.edu.sga.application.usecase.ConfirmarReservaUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarLlegadasPorDiaUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarReservaPorCodigoUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarReservasUseCase;
import co.uniquindio.edu.sga.application.usecase.ConsultarSalidasPorDiaUseCase;
import co.uniquindio.edu.sga.application.usecase.CrearReservaUseCase;
import co.uniquindio.edu.sga.application.usecase.DeclararNoShowUseCase;
import co.uniquindio.edu.sga.application.usecase.ModificarReservaUseCase;
import co.uniquindio.edu.sga.application.usecase.RegistrarLlegadaUseCase;
import co.uniquindio.edu.sga.application.usecase.RegistrarSalidaUseCase;
import co.uniquindio.edu.sga.domain.repository.FiltroReservas;
import co.uniquindio.edu.sga.domain.valueobject.CanalOrigen;
import co.uniquindio.edu.sga.domain.valueobject.EstadoReserva;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.infraestructure.rest.mapper.ReservaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


/** Rutas REST de reservas */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final CrearReservaUseCase crearReservaUseCase;
    private final ConsultarReservasUseCase consultarReservasUseCase;
    private final ConsultarReservaPorCodigoUseCase consultarReservaPorCodigoUseCase;
    private final ConfirmarReservaUseCase confirmarReservaUseCase;
    private final CancelarReservaUseCase cancelarReservaUseCase;
    private final RegistrarLlegadaUseCase registrarLlegadaUseCase;
    private final RegistrarSalidaUseCase registrarSalidaUseCase;
    private final DeclararNoShowUseCase declararNoShowUseCase;
    private final ModificarReservaUseCase modificarReservaUseCase;
    private final ConsultarLlegadasPorDiaUseCase consultarLlegadasPorDiaUseCase;
    private final ConsultarSalidasPorDiaUseCase consultarSalidasPorDiaUseCase;
    private final ReservaMapper mapper;
    private final SolicitanteActualPort solicitanteActual;

    public ReservaController(CrearReservaUseCase crearReservaUseCase, ConsultarReservasUseCase consultarReservasUseCase,
            ConsultarReservaPorCodigoUseCase consultarReservaPorCodigoUseCase, ConfirmarReservaUseCase confirmarReservaUseCase,
            CancelarReservaUseCase cancelarReservaUseCase, RegistrarLlegadaUseCase registrarLlegadaUseCase,
            RegistrarSalidaUseCase registrarSalidaUseCase, DeclararNoShowUseCase declararNoShowUseCase,
            ModificarReservaUseCase modificarReservaUseCase, ConsultarLlegadasPorDiaUseCase consultarLlegadasPorDiaUseCase,
            ConsultarSalidasPorDiaUseCase consultarSalidasPorDiaUseCase, ReservaMapper mapper,
            SolicitanteActualPort solicitanteActual) {
        this.crearReservaUseCase = crearReservaUseCase;
        this.consultarReservasUseCase = consultarReservasUseCase;
        this.consultarReservaPorCodigoUseCase = consultarReservaPorCodigoUseCase;
        this.confirmarReservaUseCase = confirmarReservaUseCase;
        this.cancelarReservaUseCase = cancelarReservaUseCase;
        this.registrarLlegadaUseCase = registrarLlegadaUseCase;
        this.registrarSalidaUseCase = registrarSalidaUseCase;
        this.declararNoShowUseCase = declararNoShowUseCase;
        this.modificarReservaUseCase = modificarReservaUseCase;
        this.consultarLlegadasPorDiaUseCase = consultarLlegadasPorDiaUseCase;
        this.consultarSalidasPorDiaUseCase = consultarSalidasPorDiaUseCase;
        this.mapper = mapper;
        this.solicitanteActual = solicitanteActual;
    }

    // POST -> /api/reservas/
    @PostMapping
    public ResponseEntity<ReservaDetalleResponse> crearReserva(@Valid @RequestBody CrearReservaRequest solicitud) {
        var titular = new TitularDatos(solicitud.nombreTitular(), solicitud.apellidoTitular(),
                java.util.UUID.fromString(solicitud.usuarioId()), solicitud.correoTitular(), solicitud.telefonoTitular(),
                solicitud.documentoTitular());
        var ocupantes = solicitud.ocupantes().stream().map(o -> new OcupanteDatos(o.nombre(), o.apellido(),
                o.documento(), o.fechaNacimiento())).toList();
        var comando = new CrearReservaUseCase.Command(titular, ocupantes, java.util.UUID.fromString(solicitud.apartamentoId()),
                solicitud.fechaEntrada(), solicitud.fechaSalida(), solicitud.horaEstimadaLlegada(),
                CanalOrigen.valueOf(solicitud.canalOrigen()), solicitud.identificadorExterno());
        var resultado = crearReservaUseCase.ejecutar(comando);
        return ResponseEntity.status(resultado.yaExistia() ? 200 : 201).body(mapper.aDetalle(resultado.detalle()));
    }

    // GET -> /api/reservas
    @GetMapping
    public ResponseEntity<List<ReservaResumenResponse>> consultarReservasPorEstado(@RequestParam String estado) {
        exigirPersonalInterno();
        var estados = java.util.Arrays.stream(estado.split(",")).map(String::trim).map(EstadoReserva::valueOf).collect(java.util.stream.Collectors.toSet());
        var pagina = consultarReservasUseCase.ejecutar(new ConsultarReservasUseCase.Command(
                new FiltroReservas(estados, null, null, null, null, null, null, null, null), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aResumen).toList());
    }

    // GET -> /api/reservas/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> consultarReservasPorCodigo(@PathVariable String codigo) {
        var resultado = consultarReservaPorCodigoUseCase.ejecutar(new ConsultarReservaPorCodigoUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado));
    }

    // PUT -> /api/reservas/{codigo}/confirmar
    @PutMapping("/{codigo}/confirmar")
    public ResponseEntity<ReservaDetalleResponse> confirmarReserva(@PathVariable String codigo) {
        var resultado = confirmarReservaUseCase.ejecutar(new ConfirmarReservaUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // PUT -> /api/reservas/{codigo}/cancelar
    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<ReservaDetalleResponse> cancelarReserva(@PathVariable String codigo) {
        var resultado = cancelarReservaUseCase.ejecutar(new CancelarReservaUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // PUT -> api/reservas/{codigo}/registrar-llegada
    @PutMapping("/{codigo}/registrar-llegada")
    public ResponseEntity<ReservaDetalleResponse> registrarLlegada(@PathVariable String codigo) {
        var resultado = registrarLlegadaUseCase.ejecutar(new RegistrarLlegadaUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // PUT -> api/reservas/{codigo}/registrar-salida
    @PutMapping("/{codigo}/registrar-salida")
    public ResponseEntity<ReservaDetalleResponse> registrarSalida(@PathVariable String codigo) {
        var resultado = registrarSalidaUseCase.ejecutar(new RegistrarSalidaUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // PUT -> api/reservas/{codigo}/no-show
    @PutMapping("/{codigo}/no-show")
    public ResponseEntity<ReservaDetalleResponse> declararNoShow(@PathVariable String codigo) {
        var resultado = declararNoShowUseCase.ejecutar(new DeclararNoShowUseCase.Command(
                java.util.UUID.fromString(codigo), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // PUT -> api/reservas/{codigo}
    @PutMapping("/{codigo}")
    public ResponseEntity<ReservaDetalleResponse> modificarReserva(@PathVariable String codigo, @Valid @RequestBody ModificarReservaRequest solicitud) {
        var ocupantes = solicitud.ocupantes().stream().map(o -> new OcupanteDatos(o.nombre(), o.apellido(),
                o.documento(), o.fechaNacimiento())).toList();
        var resultado = modificarReservaUseCase.ejecutar(new ModificarReservaUseCase.Command(java.util.UUID.fromString(codigo),
                solicitud.apartamentoId() == null ? null : java.util.UUID.fromString(solicitud.apartamentoId()),
                solicitud.fechaEntrada(), solicitud.fechaSalida(), ocupantes, solicitud.horaEstimadaLlegada(), solicitanteActual.obtener()));
        return ResponseEntity.ok(mapper.aDetalle(resultado.detalle()));
    }

    // GET -> api/reservas/llegadas
    @GetMapping("/llegadas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarLlegadasPorDia(@RequestParam String fecha) {
        exigirPersonalInterno();
        var pagina = consultarLlegadasPorDiaUseCase.ejecutar(new ConsultarLlegadasPorDiaUseCase.Command(java.time.LocalDate.parse(fecha), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aResumen).toList());
    }

    // GET -> api/reservas/salidas
    @GetMapping("/salidas")
    public ResponseEntity<List<ReservaResumenResponse>> consultarSalidasPorDia(@RequestParam String fecha) {
        exigirPersonalInterno();
        var pagina = consultarSalidasPorDiaUseCase.ejecutar(new ConsultarSalidasPorDiaUseCase.Command(java.time.LocalDate.parse(fecha), 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aResumen).toList());
    }
    // GET -> api/reservas/canales
    @GetMapping("/canales")
    public ResponseEntity<List<ReservaResumenResponse>> consultarReservasPorCanal(@RequestParam String canal) {
        exigirPersonalInterno();
        var filtro = new FiltroReservas(java.util.Set.of(), null, CanalOrigen.valueOf(canal), null, null, null, null, null, null);
        var pagina = consultarReservasUseCase.ejecutar(new ConsultarReservasUseCase.Command(filtro, 0));
        return ResponseEntity.ok(pagina.contenido().stream().map(mapper::aResumen).toList());
    }

    private void exigirPersonalInterno() {
        if (!solicitanteActual.obtener().esPersonalInterno()) {
            throw new AccesoDenegadoException("Solo el personal interno puede consultar estos listados.");
        }
    }
}
