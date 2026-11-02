package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.command.OcupanteDatos;
import co.uniquindio.edu.sga.application.command.TitularDatos;
import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.exception.OperacionNoPermitidaException;
import co.uniquindio.edu.sga.application.exception.RecursoDuplicadoException;
import co.uniquindio.edu.sga.application.port.NotificacionPort;
import co.uniquindio.edu.sga.application.result.FolioDetalle;
import co.uniquindio.edu.sga.application.result.ReservaDetalle;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.exception.AforoExcedidoException;
import co.uniquindio.edu.sga.domain.exception.ApartamentoNoDisponibleException;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.service.*;
import co.uniquindio.edu.sga.domain.valueobject.*;
import co.uniquindio.edu.sga.infraestructure.persistence.inmemory.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de flujo de los casos de uso con repositorios en memoria (I-02, I-08: sin servicios externos).
 * Escenario: alojamiento con apartamento 101 (capacidad 3, $80.000) y 201 (capacidad 6, $150.000);
 * temporada "Fiestas" del 10 al 20 de julio de 2027 ($120.000 / $220.000); anticipo 25 %.
 */
class CasosDeUsoTest {

    static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    /** Reloj controlable: permite probar vencimientos, no-show y registro sin esperar. */
    static class RelojMutable extends Clock {
        private Instant ahora;

        RelojMutable(LocalDateTime inicial) { fijar(inicial); }

        void fijar(LocalDateTime t) { ahora = t.atZone(BOGOTA).toInstant(); }

        @Override public ZoneId getZone() { return BOGOTA; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return ahora; }
    }

    final RelojMutable reloj = new RelojMutable(LocalDateTime.of(2026, 10, 28, 10, 0));
    final AlojamientoRepositoryInMemory alojamientos = new AlojamientoRepositoryInMemory();
    final ReservaRepositoryInMemory reservas = new ReservaRepositoryInMemory();
    final FolioRepositoryInMemory folios = new FolioRepositoryInMemory();
    final BloqueoRepositoryInMemory bloqueos = new BloqueoRepositoryInMemory();
    final NovedadRepositoryInMemory novedades = new NovedadRepositoryInMemory();
    final UsuarioRepositoryInMemory usuarios = new UsuarioRepositoryInMemory();
    final NotificacionEnMemoria notificacion = new NotificacionEnMemoria();

    final DisponibilidadApartamentoService disponibilidad = new DisponibilidadApartamentoService(reservas, bloqueos);
    final CotizadorEstanciaService cotizador = new CotizadorEstanciaService();
    final ValidacionAforoOcupantesService validacionGrupo = new ValidacionAforoOcupantesService();
    final CancelacionReservaService cancelacion = new CancelacionReservaService();

    final Apartamento apto101;
    final Apartamento apto201;
    final Temporada base;
    final Temporada fiestas;

    final Solicitante recepcion = new Solicitante(UsuarioId.generar(), "Rita Recepción", RolUsuario.RECEPCIONISTA);
    final Solicitante admin = new Solicitante(UsuarioId.generar(), "Admin", RolUsuario.ADMINISTRADOR);
    final Solicitante servicio = new Solicitante(UsuarioId.generar(), "Sandra Servicio", RolUsuario.PERSONAL_SERVICIO);

    CasosDeUsoTest() {
        apto101 = apartamento("101", 3);
        apto201 = apartamento("201", 6);
        TemporadaId idBase = TemporadaId.generar();
        base = Temporada.crearBase(idBase, tarifas(idBase, 80000, 150000));
        TemporadaId idFiestas = TemporadaId.generar();
        fiestas = Temporada.crear(idFiestas, "Fiestas Patronales",
                new RangoFechas(LocalDate.of(2027, 7, 10), LocalDate.of(2027, 7, 21)), tarifas(idFiestas, 120000, 220000));

        List<TramoCancelacion> tramos = new ArrayList<>();
        tramos.add(new TramoCancelacion(0, 1.0));
        tramos.add(new TramoCancelacion(1, 0.25));
        tramos.add(new TramoCancelacion(3, 0.10));
        PoliticaCancelacion politica = new PoliticaCancelacion(new VersionPolitica("v1"), tramos, 1.0);

        List<Apartamento> aptos = new ArrayList<>(List.of(apto101, apto201));
        List<Temporada> temporadas = new ArrayList<>(List.of(base, fiestas));
        alojamientos.guardar(Alojamiento.crear(AlojamientoId.generar(), "El Cam",
                new Ubicacion("Chocó", "Lloró", "Calle 1"),
                new ConfiguracionAlojamiento(5, LocalTime.of(15, 0), LocalTime.of(12, 0), 3, 4,
                        LocalTime.of(18, 0), 0.25),
                aptos, temporadas, politica));
    }

    // ------------------------------------------------------------------ fábricas

    private static Apartamento apartamento(String id, int aforo) {
        List<Dormitorio> dormitorios = new ArrayList<>();
        dormitorios.add(new Dormitorio("Principal", 2, "Doble"));
        return Apartamento.crear(new Capacidad((byte) aforo), "Apartamento " + id, dormitorios,
                EstadoOperativo.PREPARADO, ApartamentoId.generar(), id, true);
    }

    private List<Tarifa> tarifas(TemporadaId t, long precio101, long precio201) {
        List<Tarifa> l = new ArrayList<>();
        l.add(new Tarifa(t, apto101.getId(), Dinero.pesos(precio101)));
        l.add(new Tarifa(t, apto201.getId(), Dinero.pesos(precio201)));
        return l;
    }

    private TitularDatos titular(UUID usuarioId) {
        return new TitularDatos("Ana", "Pérez", usuarioId, "ana@correo.com", "3001112233", "100");
    }

    private OcupanteDatos adulto() { return new OcupanteDatos("Ana", "Pérez", "100", LocalDate.of(1990, 1, 1)); }

    private OcupanteDatos nino() { return new OcupanteDatos("Leo", "Pérez", "200", LocalDate.of(2024, 1, 1)); }

    private CrearReservaUseCase crearUC() {
        return new CrearReservaUseCase(alojamientos, reservas, folios, disponibilidad, cotizador, validacionGrupo,
                notificacion, reloj);
    }

    private CrearReservaUseCase.Result crear(Apartamento apto, LocalDate entrada, LocalDate salida,
                                              CanalOrigen canal, String idExterno, UUID usuarioId, List<OcupanteDatos> ocupantes) {
        return crearUC().ejecutar(new CrearReservaUseCase.Command(titular(usuarioId), ocupantes, apto.getId().valor(),
                entrada, salida, LocalTime.of(15, 0), canal, idExterno));
    }

    private CrearReservaUseCase.Result crear101(LocalDate entrada, LocalDate salida) {
        return crear(apto101, entrada, salida, CanalOrigen.DIRECTO, null, null, List.of(adulto(), nino()));
    }

    private CrearReservaUseCase.Result reserva10al12() {
        return crear101(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
    }

    private void pagar(UUID reservaId, long monto) {
        new RegistrarPagoUseCase(folios, reloj).ejecutar(
                new RegistrarPagoUseCase.Command(reservaId, MedioPago.TRANSFERENCIA, BigDecimal.valueOf(monto), "TRX"));
    }

    private void confirmar(UUID reservaId) {
        new ConfirmarReservaUseCase(alojamientos, reservas, folios, notificacion)
                .ejecutar(new ConfirmarReservaUseCase.Command(reservaId));
    }

    private FolioDetalle folio(UUID reservaId) {
        return new ConsultarFolioUseCase(reservas, folios)
                .ejecutar(new ConsultarFolioUseCase.Command(reservaId, recepcion));
    }

    private ReservaDetalle detalle(UUID reservaId, Solicitante s) {
        return new ConsultarReservaPorCodigoUseCase(reservas)
                .ejecutar(new ConsultarReservaPorCodigoUseCase.Command(reservaId, s));
    }

    private Apartamento apartamentoActual(Apartamento a) {
        return alojamientos.obtener().orElseThrow().buscarApartamento(a.getId()).orElseThrow();
    }

    // ------------------------------------------------------------------ cotización y creación

    @Test
    void cotizarCruzandoTemporadasLiquidaNochePorNoche() {
        var r = new CotizarEstanciaUseCase(alojamientos, cotizador).ejecutar(new CotizarEstanciaUseCase.Command(
                apto101.getId().valor(), LocalDate.of(2027, 7, 9), LocalDate.of(2027, 7, 11),
                List.of(LocalDate.of(1990, 1, 1))));
        assertEquals(2, r.lineas().size());
        assertEquals("Base", r.lineas().get(0).temporada());
        assertEquals("Fiestas Patronales", r.lineas().get(1).temporada());
        assertEquals(new BigDecimal("200000"), r.total());          // 80.000 + 120.000
    }

    @Test
    void ocupantesMenoresAlUmbralCuentanParaCapacidadPeroNoCobran() {
        var r = crear101(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        assertEquals(new BigDecimal("160000"), r.valorTotal());     // 1 facturable × 80.000 × 2 noches
        assertEquals(new BigDecimal("40000"), r.anticipoRequerido());
        assertEquals(EstadoReserva.PENDIENTE, r.estado());
        assertEquals(new BigDecimal("160000"), folio(r.reservaId()).saldo());
    }

    @Test
    void rn19MismoCanalEIdentificadorExternoNoDuplicaLaReserva() {
        var a = crear(apto101, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.EXTERNO, "EXT-1", null, List.of(adulto()));
        var b = crear(apto101, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.EXTERNO, "EXT-1", null, List.of(adulto()));
        assertFalse(a.yaExistia());
        assertTrue(b.yaExistia());
        assertEquals(a.reservaId(), b.reservaId());
    }

    @Test
    void rn01NoSePuedeReservarUnaEstanciaSolapada() {
        reserva10al12();
        assertThrows(ApartamentoNoDisponibleException.class,
                () -> crear101(LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 13)));
    }

    @Test
    void rn01UnaEstanciaQueEmpiezaElDiaDeSalidaSiSePermiteConTiempoDePreparacionSuficiente() {
        reserva10al12();   // salida 12:00, entrada 15:00 → ventana 3 h = tiempo de preparación (3 h)
        assertDoesNotThrow(() -> crear101(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14)));
    }

    @Test
    void rn02NoSeExcedeLaCapacidad() {
        List<OcupanteDatos> cuatro = List.of(adulto(), nino(),
                new OcupanteDatos("A", "B", "300", LocalDate.of(2020, 1, 1)),
                new OcupanteDatos("C", "D", "400", LocalDate.of(2019, 1, 1)));
        assertThrows(AforoExcedidoException.class, () -> crear(apto101, LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12), CanalOrigen.DIRECTO, null, null, cuatro));
    }

    @Test
    void rn23ElTitularDebeSerMayorDeEdadYEstarEntreLosOcupantes() {
        OcupanteDatos titularMenor = new OcupanteDatos("Ana", "Pérez", "100", LocalDate.of(2015, 1, 1));
        assertThrows(ReglaDominioException.class, () -> crear(apto101, LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12), CanalOrigen.DIRECTO, null, null, List.of(titularMenor)));
        assertThrows(ReglaDominioException.class, () -> crear(apto101, LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12), CanalOrigen.DIRECTO, null, null, List.of(nino())));
    }

    @Test
    void rn04NoSePuedeReservarEnElPasado() {
        assertThrows(ReglaDominioException.class,
                () -> crear101(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 22)));
    }

    @Test
    void i05UnaNotificacionFallidaNoImpideReservar() {
        NotificacionPort rota = new NotificacionPort() {
            public void reservaCreada(Reserva r) { throw new IllegalStateException("SMTP caído"); }
            public void reservaConfirmada(Reserva r) { throw new IllegalStateException("SMTP caído"); }
            public void reservaCancelada(Reserva r) { throw new IllegalStateException("SMTP caído"); }
        };
        var uc = new CrearReservaUseCase(alojamientos, reservas, folios, disponibilidad, cotizador, validacionGrupo, rota, reloj);
        var r = uc.ejecutar(new CrearReservaUseCase.Command(titular(null), List.of(adulto()), apto101.getId().valor(),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), LocalTime.of(15, 0), CanalOrigen.PORTAL, null));
        assertEquals(EstadoReserva.PENDIENTE, r.estado());
    }

    // ------------------------------------------------------------------ confirmar / cancelar / vencer / no-show

    @Test
    void rn26SinAnticipoNoSeConfirma() {
        var r = reserva10al12();
        assertThrows(ReglaDominioException.class, () -> confirmar(r.reservaId()));
        pagar(r.reservaId(), 40000);
        confirmar(r.reservaId());
        assertEquals(EstadoReserva.CONFIRMADA, detalle(r.reservaId(), recepcion).estado());
    }

    @Test
    void rn13CancelarAplicaLaRetencionDeLaPoliticaCongeladaYGeneraSaldoAFavor() {
        var r = reserva10al12();
        pagar(r.reservaId(), 40000);
        var uc = new CancelarReservaUseCase(reservas, folios, cancelacion, notificacion, reloj);
        var res = uc.ejecutar(new CancelarReservaUseCase.Command(r.reservaId(), recepcion));   // 13 días de antelación → 10 %

        assertEquals(EstadoReserva.CANCELADA, res.estado());
        assertEquals(0.10, res.porcentajeRetenido(), 0.0001);
        assertEquals(new BigDecimal("16000"), res.penalidad());
        assertEquals(new BigDecimal("24000"), res.saldoAFavor());        // 40.000 pagados − 16.000 retenidos
        // RN-12: libera las noches de inmediato
        assertDoesNotThrow(() -> reserva10al12());
    }

    @Test
    void cancelarConMenosDeUnDiaRetieneTodo() {
        var r = reserva10al12();
        pagar(r.reservaId(), 40000);
        reloj.fijar(LocalDateTime.of(2026, 11, 10, 9, 0));                   // el mismo día de la entrada
        var res = new CancelarReservaUseCase(reservas, folios, cancelacion, notificacion, reloj)
                .ejecutar(new CancelarReservaUseCase.Command(r.reservaId(), recepcion));
        assertEquals(1.0, res.porcentajeRetenido(), 0.0001);
        assertEquals(new BigDecimal("160000"), res.penalidad());
        assertEquals(new BigDecimal("120000"), res.saldoPendiente());   // debe 160.000 − 40.000
    }

    @Test
    void unHuespedSoloPuedeCancelarSuPropiaReserva() {
        UUID duenio = UUID.randomUUID();
        var r = crear(apto101, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.PORTAL, null, duenio, List.of(adulto()));
        Solicitante otro = new Solicitante(UsuarioId.generar(), "Otro", RolUsuario.HUESPED);
        var uc = new CancelarReservaUseCase(reservas, folios, cancelacion, notificacion, reloj);
        assertThrows(AccesoDenegadoException.class, () -> uc.ejecutar(new CancelarReservaUseCase.Command(r.reservaId(), otro)));
        assertDoesNotThrow(() -> uc.ejecutar(new CancelarReservaUseCase.Command(r.reservaId(),
                new Solicitante(UsuarioId.of(duenio), "Ana", RolUsuario.HUESPED))));
    }

    @Test
    void rn21LaReservaPendienteVencidaSeCancelaSolaSinPenalidad() {
        var r = reserva10al12();
        var uc = new VencerReservasPendientesUseCase(alojamientos, reservas, folios, cancelacion, notificacion, reloj);
        reloj.fijar(LocalDateTime.of(2026, 10, 28, 13, 0));                  // 3 h: aún dentro del plazo de 4 h
        assertEquals(0, uc.ejecutar());
        reloj.fijar(LocalDateTime.of(2026, 10, 28, 15, 0));                  // 5 h: vencida
        assertEquals(1, uc.ejecutar());
        assertEquals(EstadoReserva.CANCELADA, detalle(r.reservaId(), recepcion).estado());
        assertEquals(BigDecimal.ZERO, folio(r.reservaId()).saldo());
    }

    @Test
    void noShowSoloDesdeLaHoraLimiteYAplicaLaPenalidadDeLaPolitica() {
        var r = reserva10al12();
        pagar(r.reservaId(), 160000);
        confirmar(r.reservaId());
        var uc = new DeclararNoShowUseCase(alojamientos, reservas, folios, new NoShowProcesamientoServicio(), reloj);

        reloj.fijar(LocalDateTime.of(2026, 11, 10, 17, 0));
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(new DeclararNoShowUseCase.Command(r.reservaId())));

        reloj.fijar(LocalDateTime.of(2026, 11, 10, 18, 0));
        var res = uc.ejecutar(new DeclararNoShowUseCase.Command(r.reservaId()));
        assertEquals(EstadoReserva.NO_SHOW, res.estado());
        assertEquals(new BigDecimal("160000"), res.penalidad());
        assertEquals(BigDecimal.ZERO, res.saldoPendiente());
    }

    // ------------------------------------------------------------------ modificar

    @Test
    void rn14ModificarRecalculaElValorYRegistraLaDiferenciaComoAjuste() {
        var r = reserva10al12();
        var uc = new ModificarReservaUseCase(alojamientos, reservas, folios, disponibilidad, cotizador, validacionGrupo, reloj);
        var res = uc.ejecutar(new ModificarReservaUseCase.Command(r.reservaId(), null,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), List.of(adulto(), nino()), null));
        assertEquals(new BigDecimal("160000"), res.valorAnterior());
        assertEquals(new BigDecimal("240000"), res.valorNuevo());
        assertEquals(new BigDecimal("80000"), res.diferencia());
        assertEquals(new BigDecimal("240000"), folio(r.reservaId()).saldo());
    }

    @Test
    void modificarNoChocaConLaPropiaReservaPeroSiConOtras() {
        var a = reserva10al12();
        crear101(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 16));
        var uc = new ModificarReservaUseCase(alojamientos, reservas, folios, disponibilidad, cotizador, validacionGrupo, reloj);
        // extender a la propia reserva hasta el 13: libre
        assertDoesNotThrow(() -> uc.ejecutar(new ModificarReservaUseCase.Command(a.reservaId(), null,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13), List.of(adulto()), null)));
        // extenderla hasta el 15: choca con la reserva del 14 al 16
        assertThrows(ApartamentoNoDisponibleException.class, () -> uc.ejecutar(new ModificarReservaUseCase.Command(a.reservaId(), null,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15), List.of(adulto()), null)));
    }

    // ------------------------------------------------------------------ operación diaria y folio

    @Test
    void flujoCompletoRegistroFolioSalidaYPreparacion() {
        var r = reserva10al12();
        pagar(r.reservaId(), 160000);
        confirmar(r.reservaId());

        var llegada = new RegistrarLlegadaUseCase(alojamientos, reservas, new EntregaApartamentoService(), reloj);
        // RN-10: no antes de la fecha de entrada
        assertThrows(ReglaDominioException.class, () -> llegada.ejecutar(new RegistrarLlegadaUseCase.Command(r.reservaId())));

        reloj.fijar(LocalDateTime.of(2026, 11, 10, 16, 0));
        var l = llegada.ejecutar(new RegistrarLlegadaUseCase.Command(r.reservaId()));
        assertEquals(EstadoReserva.EN_CURSO, l.estadoReserva());
        assertEquals(EstadoOperativo.OCUPADO, l.estadoApartamento());

        var salida = new RegistrarSalidaUseCase(alojamientos, reservas, folios, new LiquidacionSalidaService());
        // RN-29: la salida exige el folio cerrado
        assertThrows(ReglaDominioException.class, () -> salida.ejecutar(new RegistrarSalidaUseCase.Command(r.reservaId())));

        new CerrarFolioUseCase(folios, reloj).ejecutar(new CerrarFolioUseCase.Command(r.reservaId(), recepcion, null));
        var s = salida.ejecutar(new RegistrarSalidaUseCase.Command(r.reservaId()));
        assertEquals(EstadoReserva.FINALIZADA, s.estadoReserva());
        assertEquals(EstadoOperativo.PENDIENTE_PREPARACION, s.estadoApartamento());

        // personal de servicio: PENDIENTE → EN_PREPARACION → PREPARADO
        var estado = new ModificarEstadoOperativoApartamentoUseCase(alojamientos);
        estado.ejecutar(new ModificarEstadoOperativoApartamentoUseCase.Command(apto101.getId().valor(), EstadoOperativo.EN_PREPARACION, servicio));
        var listo = estado.ejecutar(new ModificarEstadoOperativoApartamentoUseCase.Command(apto101.getId().valor(), EstadoOperativo.PREPARADO, servicio));
        assertEquals(EstadoOperativo.PREPARADO, listo.estadoOperativo());
    }

    @Test
    void rn11NoSeRegistraLlegadaEnUnApartamentoNoPreparado() {
        var r = reserva10al12();
        pagar(r.reservaId(), 160000);
        confirmar(r.reservaId());
        apartamentoActual(apto101).marcarFueraDeServicio();
        reloj.fijar(LocalDateTime.of(2026, 11, 10, 16, 0));
        assertThrows(ReglaDominioException.class, () -> new RegistrarLlegadaUseCase(alojamientos, reservas,
                new EntregaApartamentoService(), reloj).ejecutar(new RegistrarLlegadaUseCase.Command(r.reservaId())));
    }

    @Test
    void rn17CerrarConSaldoPendienteSoloLoHaceElAdministradorConMotivo() {
        var r = reserva10al12();
        var uc = new CerrarFolioUseCase(folios, reloj);
        assertThrows(AccesoDenegadoException.class, () -> uc.ejecutar(new CerrarFolioUseCase.Command(r.reservaId(), recepcion, "x")));
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(new CerrarFolioUseCase.Command(r.reservaId(), admin, " ")));
        var f = uc.ejecutar(new CerrarFolioUseCase.Command(r.reservaId(), admin, "Cortesía de la gerencia"));
        assertEquals(EstadoFolio.CERRADO, f.estado());
    }

    @Test
    void rn16LosCargosManualesSonLimitadosYNoSeEditan() {
        var r = reserva10al12();
        var uc = new RegistrarCargoUseCase(folios, reloj);
        assertThrows(OperacionNoPermitidaException.class, () -> uc.ejecutar(
                new RegistrarCargoUseCase.Command(r.reservaId(), TipoCargo.PENALIDAD_NO_SHOW, "x", BigDecimal.valueOf(1000))));
        var f = uc.ejecutar(new RegistrarCargoUseCase.Command(r.reservaId(), TipoCargo.SERVICIO_ADICIONAL,
                "Desayuno", BigDecimal.valueOf(10000)));
        assertEquals(new BigDecimal("170000"), f.saldo());
        var corregido = uc.ejecutar(new RegistrarCargoUseCase.Command(r.reservaId(), TipoCargo.AJUSTE_A_FAVOR,
                "Corrección desayuno", BigDecimal.valueOf(10000)));
        assertEquals(new BigDecimal("160000"), corregido.saldo());
        assertEquals(3, corregido.cargos().size());                           // nada se borra: solo se agrega el inverso
    }

    @Test
    void unHuespedNoVeReservasNiFoliosAjenos() {
        UUID duenio = UUID.randomUUID();
        var r = crear(apto101, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.PORTAL, null, duenio, List.of(adulto()));
        Solicitante intruso = new Solicitante(UsuarioId.generar(), "Intruso", RolUsuario.HUESPED);
        assertThrows(AccesoDenegadoException.class, () -> detalle(r.reservaId(), intruso));
        assertThrows(AccesoDenegadoException.class, () -> new ConsultarFolioUseCase(reservas, folios)
                .ejecutar(new ConsultarFolioUseCase.Command(r.reservaId(), intruso)));
        assertEquals(r.reservaId(), detalle(r.reservaId(), new Solicitante(UsuarioId.of(duenio), "Ana", RolUsuario.HUESPED)).id());
    }

    // ------------------------------------------------------------------ disponibilidad, bloqueos, apartamentos

    @Test
    void disponibilidadListaSoloLoVendibleOrdenadoPorPrecio() {
        var uc = new ConsultarDisponibilidadApartamentoPorFechaUseCase(alojamientos, disponibilidad, cotizador);
        var cmd = new ConsultarDisponibilidadApartamentoPorFechaUseCase.Command(LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12), List.of(LocalDate.of(1990, 1, 1)), null, null, 0);
        var antes = uc.ejecutar(cmd);
        assertEquals(2, antes.contenido().size());
        assertEquals("101", antes.contenido().get(0).apartamento().identificador());   // el más barato primero
        reserva10al12();
        assertEquals(1, uc.ejecutar(cmd).contenido().size());
    }

    @Test
    void rn07YRn28BloqueosYReservasNoConviven() {
        var crearBloqueo = new CrearBloqueoUseCase(alojamientos, bloqueos, new GestionBloqueosMantenimientoService(reservas));
        crearBloqueo.ejecutar(new CrearBloqueoUseCase.Command(apto101.getId().valor(),
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 25), "Pintura"));
        // RN-07: no se vende sobre un bloqueo vigente
        assertThrows(ApartamentoNoDisponibleException.class,
                () -> crear101(LocalDate.of(2026, 11, 22), LocalDate.of(2026, 11, 24)));
        // RN-28: no se bloquea sobre reservas activas
        reserva10al12();
        assertThrows(ReglaDominioException.class, () -> crearBloqueo.ejecutar(new CrearBloqueoUseCase.Command(
                apto101.getId().valor(), LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 13), "Fumigación")));
    }

    @Test
    void rn24ApartamentoFueraDeServicioSinBloqueoSigueVendibleAFuturo() {
        new ModificarEstadoOperativoApartamentoUseCase(alojamientos).ejecutar(new ModificarEstadoOperativoApartamentoUseCase.Command(
                apto101.getId().valor(), EstadoOperativo.FUERA_DE_SERVICIO, admin));
        assertDoesNotThrow(() -> reserva10al12());
    }

    @Test
    void elPersonalDeServicioNoPuedeDeclararFueraDeServicio() {
        assertThrows(AccesoDenegadoException.class, () -> new ModificarEstadoOperativoApartamentoUseCase(alojamientos)
                .ejecutar(new ModificarEstadoOperativoApartamentoUseCase.Command(apto101.getId().valor(),
                        EstadoOperativo.FUERA_DE_SERVICIO, servicio)));
    }

    @Test
    void rn31UnaNovedadNoCambiaElEstadoOperativo() {
        new CrearNovedadUseCase(alojamientos, novedades, reloj).ejecutar(new CrearNovedadUseCase.Command(
                apto101.getId().valor(), "Sandra", "Grifo dañado", GravedadNovedad.CRITICA));
        assertEquals(EstadoOperativo.PREPARADO, apartamentoActual(apto101).getEstadoOperativo());
        var lista = new ConsultarNovedadUseCase(alojamientos, novedades)
                .ejecutar(new ConsultarNovedadUseCase.Command(apto101.getId().valor(), 0));
        assertEquals(1, lista.contenido().size());
        new EliminarNovedadUseCase(novedades).ejecutar(new EliminarNovedadUseCase.Command(lista.contenido().get(0).id()));
        assertEquals(0, new ConsultarNovedadUseCase(alojamientos, novedades)
                .ejecutar(new ConsultarNovedadUseCase.Command(apto101.getId().valor(), 0)).contenido().size());
    }

    @Test
    void noSeRetiraUnApartamentoConReservasActivasPeroSiUnoLibre() {
        reserva10al12();
        var uc = new EliminarApartamentoUseCase(alojamientos, reservas, reloj);
        assertThrows(OperacionNoPermitidaException.class, () -> uc.ejecutar(new EliminarApartamentoUseCase.Command(apto101.getId().valor())));
        uc.ejecutar(new EliminarApartamentoUseCase.Command(apto201.getId().valor()));
        assertFalse(apartamentoActual(apto201).isActivo());
        assertThrows(ApartamentoNoDisponibleException.class, () -> crear(apto201, LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12), CanalOrigen.DIRECTO, null, null, List.of(adulto())));
    }

    @Test
    void cambiarLaCapacidadNoTocaReservasPeroAdvierte() {
        var r = crear101(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));   // 2 ocupantes
        var res = new ModificarApartamentoUseCase(alojamientos, reservas).ejecutar(new ModificarApartamentoUseCase.Command(
                apto101.getId().valor(), 1, "Más pequeño", List.of(new co.uniquindio.edu.sga.application.command.DormitorioDatos("P", 1, "Sencilla"))));
        assertEquals(List.of(r.reservaId()), res.reservasQueExcedenLaNuevaCapacidad());
        assertEquals(EstadoReserva.PENDIENTE, detalle(r.reservaId(), recepcion).estado());
    }

    @Test
    void crearApartamentoExigeTarifaEnCadaTemporada() {
        var uc = new CrearApartamentoUseCase(alojamientos);
        var dorm = List.of(new co.uniquindio.edu.sga.application.command.DormitorioDatos("P", 2, "Doble"));
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(new CrearApartamentoUseCase.Command(
                "301", "Nuevo", 4, dorm,                 Map.of(base.getId().valor(), new BigDecimal("90000")))));            // falta la temporada Fiestas
        var creado = uc.ejecutar(new CrearApartamentoUseCase.Command("301", "Nuevo", 4, dorm,
                Map.of(base.getId().valor(), new BigDecimal("90000"),
                        fiestas.getId().valor(), new BigDecimal("130000"))));
        assertEquals("301", creado.identificador());
    }

    // ------------------------------------------------------------------ temporadas, tarifas, política

    @Test
    void rn27NoSeCreanTemporadasSolapadas() {
        var uc = new CrearTemporadaUseCase(alojamientos);
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(
                new CrearTemporadaUseCase.Command("Solapada", LocalDate.of(2027, 7, 15), LocalDate.of(2027, 7, 25), null)));
        var ok = uc.ejecutar(new CrearTemporadaUseCase.Command("Festival de la Piña",
                LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 15), null));
        assertEquals(LocalDate.of(2027, 1, 15), ok.hasta());
        assertEquals(2, ok.tarifas().size());                                  // copió las tarifas de la base
    }

    @Test
    void rn22UnCambioDeTarifaNoAlteraLasReservasYaCreadas() {
        var antes = crear101(LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 22));
        var festival = new CrearTemporadaUseCase(alojamientos).ejecutar(new CrearTemporadaUseCase.Command(
                "Festival", LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 15), null));
        new AsignarTarifaUseCase(alojamientos).ejecutar(
                new AsignarTarifaUseCase.Command(festival.id(), apto101.getId().valor(), new BigDecimal("140000")));

        assertEquals(new BigDecimal("160000"), detalle(antes.reservaId(), recepcion).valor());      // congelada
        var nueva = new CotizarEstanciaUseCase(alojamientos, cotizador).ejecutar(new CotizarEstanciaUseCase.Command(
                apto101.getId().valor(), LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 22), List.of(LocalDate.of(1990, 1, 1))));
        assertEquals(new BigDecimal("280000"), nueva.total());                                       // 2 × 140.000
    }

    @Test
    void laBaseNoSeEliminaPeroUnaTemporadaEspecialSi() {
        var uc = new EliminarTemporadaUseCase(alojamientos);
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(new EliminarTemporadaUseCase.Command(base.getId().valor())));
        uc.ejecutar(new EliminarTemporadaUseCase.Command(fiestas.getId().valor()));
        var lista = new BuscarTemporadaUseCase(alojamientos).ejecutar(new BuscarTemporadaUseCase.Command(0));
        assertEquals(1, lista.contenido().size());
    }

    @Test
    void rn13ActualizarLaPoliticaCreaUnaVersionNuevaYLasReservasConservanLaSuya() {
        var r = reserva10al12();
        var res = new ActualizarPoliticaCancelacionUseCase(alojamientos).ejecutar(
                new ActualizarPoliticaCancelacionUseCase.Command(List.of(
                        new ActualizarPoliticaCancelacionUseCase.Tramo(0, 0.5),
                        new ActualizarPoliticaCancelacionUseCase.Tramo(2, 0.2)), 0.5));
        assertEquals("v1", res.versionAnterior());
        assertEquals("v2", res.versionNueva());
        assertEquals("v1", detalle(r.reservaId(), recepcion).versionPolitica());
        var nueva = crear(apto201, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.DIRECTO, null, null, List.of(adulto()));
        assertEquals("v2", detalle(nueva.reservaId(), recepcion).versionPolitica());
    }

    // ------------------------------------------------------------------ usuarios y reportes

    @Test
    void registroPublicoCreaHuespedConContraseniaSeguraYCorreoUnico() {
        var uc = new CrearUsuarioUseCase(usuarios, plana -> "hash:" + plana);
        var base = new CrearUsuarioUseCase.Command("Luis", "Mora", "555", "luis@correo.com", "300", "Clave1234",
                null, LocalDate.of(1995, 5, 5), null);
        assertThrows(ReglaDominioException.class, () -> uc.ejecutar(new CrearUsuarioUseCase.Command("Luis", "Mora", "555",
                "luis@correo.com", "300", "debil", null, LocalDate.of(1995, 5, 5), null)));
        var creado = uc.ejecutar(base);
        assertEquals(RolUsuario.HUESPED, creado.rol());
        assertThrows(RecursoDuplicadoException.class, () -> uc.ejecutar(base));
        assertThrows(AccesoDenegadoException.class, () -> uc.ejecutar(new CrearUsuarioUseCase.Command("X", "Y", "1",
                "x@correo.com", "3", "Clave1234", RolUsuario.ADMINISTRADOR, LocalDate.of(1990, 1, 1), null)));
    }

    @Test
    void noSeEliminaUnUsuarioTitularDeReservasActivas() {
        var uc = new CrearUsuarioUseCase(usuarios, plana -> "hash:" + plana);
        var u = uc.ejecutar(new CrearUsuarioUseCase.Command("Luis", "Mora", "555", "luis@correo.com", "300",
                "Clave1234", null, LocalDate.of(1995, 5, 5), null));
        crear(apto101, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12), CanalOrigen.PORTAL, null, u.id(), List.of(adulto()));
        assertThrows(OperacionNoPermitidaException.class,
                () -> new EliminarUsuarioUseCase(usuarios, reservas).ejecutar(new EliminarUsuarioUseCase.Command(u.id())));
    }

    @Test
    void reportesDeOcupacionCanalEIngresos() {
        var a = reserva10al12();
        pagar(a.reservaId(), 40000);
        crear(apto201, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 11), CanalOrigen.PORTAL, null, null, List.of(adulto()));

        var ocupacion = new ConsultarOcupacionApartamentoUseCase(alojamientos, reservas).ejecutar(
                new ConsultarOcupacionApartamentoUseCase.Command(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 11)));
        assertEquals(2L, ocupacion.nochesVendidas());                          // la noche del 10 en cada apartamento
        assertEquals(20L, ocupacion.nochesDisponibles());                      // 2 apartamentos × 10 noches
        assertEquals(10.0, ocupacion.porcentajeOcupacion(), 0.001);

        var porCanal = new ConsultarReservaPorCanalUseCase(reservas).ejecutar(
                new ConsultarReservaPorCanalUseCase.Command(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 1)));
        assertEquals(1L, porCanal.stream().filter(c -> c.canal() == CanalOrigen.PORTAL).findFirst().orElseThrow().total());
        assertEquals(1L, porCanal.stream().filter(c -> c.canal() == CanalOrigen.DIRECTO).findFirst().orElseThrow().total());

        var ingresos = new ConsultarIngresosUseCase(folios).ejecutar(
                new ConsultarIngresosUseCase.Command(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1)));
        assertEquals(new BigDecimal("40000"), ingresos.total());          // solo pagos, no cargos
        assertEquals(40000.0, ingresos.porMedioPago().get(MedioPago.TRANSFERENCIA).doubleValue(), 0.001);
    }

    @Test
    void llegadasYSalidasDelDia() {
        var r = reserva10al12();
        pagar(r.reservaId(), 160000);
        confirmar(r.reservaId());
        var llegadas = new ConsultarLlegadasPorDiaUseCase(reservas)
                .ejecutar(new ConsultarLlegadasPorDiaUseCase.Command(LocalDate.of(2026, 11, 10), 0));
        assertEquals(1, llegadas.contenido().size());
        assertEquals(0, new ConsultarSalidasPorDiaUseCase(reservas)
                .ejecutar(new ConsultarSalidasPorDiaUseCase.Command(LocalDate.of(2026, 11, 12), 0)).contenido().size());
    }
}
