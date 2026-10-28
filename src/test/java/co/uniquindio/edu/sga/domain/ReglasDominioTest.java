package co.uniquindio.edu.sga.domain;

import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Bloqueo;
import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.exception.FolioSaldoPendienteException;
import co.uniquindio.edu.sga.domain.exception.ReglaDominioException;
import co.uniquindio.edu.sga.domain.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio del SGA.
 * Cada regla de negocio se prueba en su caso válido y en el caso en que se viola.
 *
 * NOTA: las listas se construyen con ArrayList porque List.of(...).contains(null)
 * lanza NullPointerException, y varios agregados validan "!lista.contains(null)".
 */
class ReglasDominioTest {

    // =====================================================================
    // Fábricas de apoyo
    // =====================================================================

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 28, 10, 0);

    private static NombreCompleto nombre() {
        return new NombreCompleto("Ana", "Pérez");
    }

    private static DocumentoIdentidad documento() {
        return new DocumentoIdentidad("1094000111");
    }

    private static Ocupante ocupante() {
        return new Ocupante(nombre(), documento(), new FechaNacimiento(LocalDate.of(1990, 1, 1)));
    }

    private static Titular titular() {
        return new Titular(nombre(), UsuarioId.generar(), CorreoElectronico.of("ana@correo.com"),
                new Telefono("3001234567"), documento());
    }

    private static PoliticaCancelacion politica() {
        List<TramoCancelacion> tramos = new ArrayList<>();
        tramos.add(new TramoCancelacion(0, 1.0));   // menos de 24 h
        tramos.add(new TramoCancelacion(1, 0.25));  // entre 24 y 72 h
        tramos.add(new TramoCancelacion(3, 0.10));  // más de 72 h
        return new PoliticaCancelacion(new VersionPolitica("v1"), tramos, 1.0);
    }

    private static Estancia estancia(LocalDate entrada, LocalDate salida) {
        return new Estancia(entrada, salida);
    }

    private static Reserva reserva(LocalDate entrada, LocalDate salida) {
        List<Ocupante> ocupantes = new ArrayList<>();
        ocupantes.add(ocupante());
        return Reserva.crear(ReservaId.generar(), FolioId.generar(), ApartamentoId.generar(),
                titular(), ocupantes, estancia(entrada, salida), "EXT-1",
                LocalTime.of(15, 0), CanalOrigen.PORTAL, new Dinero(240000),
                politica(), AHORA);
    }

    private static Reserva reservaValida() {
        return reserva(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
    }

    private static Apartamento apartamento(String identificador, int aforo, EstadoOperativo estado,
                                           boolean disponible) {
        List<Dormitorio> dormitorios = new ArrayList<>();
        dormitorios.add(new Dormitorio("Dormitorio 1", 2, "Sencilla"));
        return Apartamento.crear(new Capacidad((byte) aforo), "Apartamento de prueba", dormitorios,
                estado, ApartamentoId.generar(), identificador, disponible);
    }

    private static Apartamento apartamento(String identificador, int aforo) {
        return apartamento(identificador, aforo, EstadoOperativo.PREPARADO, true);
    }

    private static List<Tarifa> tarifas(TemporadaId temporadaId, double precio, Apartamento... aptos) {
        List<Tarifa> lista = new ArrayList<>();
        for (Apartamento a : aptos) {
            lista.add(new Tarifa(temporadaId, a.getId(), new Dinero(precio)));
        }
        return lista;
    }

    private static Temporada base(Apartamento... aptos) {
        TemporadaId id = TemporadaId.generar();
        return Temporada.crearBase(id, tarifas(id, 80000, aptos));
    }

    private static Temporada especial(String nombre, LocalDate ini, LocalDate fin, double precio,
                                      Apartamento... aptos) {
        TemporadaId id = TemporadaId.generar();
        return Temporada.crear(id, nombre, new RangoFechas(ini, fin), tarifas(id, precio, aptos));
    }

    private static Alojamiento alojamiento(List<Apartamento> aptos, List<Temporada> temporadas) {
        return Alojamiento.crear(AlojamientoId.generar(), "El Cam",
                new Ubicacion("Chocó", "Lloró", "Calle 1"),
                new ConfiguracionAlojamiento(5, LocalTime.of(15, 0), LocalTime.of(12, 0), 3, 4,
                        LocalTime.of(18, 0)),
                aptos, temporadas, politica());
    }

    private static AutorizacionCierre autorizacion() {
        return new AutorizacionCierre("admin", "Cierre autorizado", AHORA);
    }

    private static Cargo cargo(double monto) {
        return new Cargo(TipoCargo.ALOJAMIENTO, "Alojamiento", new Dinero(monto), AHORA);
    }

    private static Pago pago(double monto) {
        return new Pago(new Dinero(monto), MedioPago.EFECTIVO, "REF-1", AHORA);
    }

    private static Folio folio() {
        return Folio.crear(FolioId.generar(), ReservaId.generar());
    }

    // =====================================================================
    // Objetos de valor
    // =====================================================================

    @Nested
    @DisplayName("Objetos de valor")
    class ObjetosDeValor {

        @Test
        void dineroRechazaCeroNegativoYNoFinito() {
            assertThrows(ReglaDominioException.class, () -> new Dinero(0));
            assertThrows(ReglaDominioException.class, () -> new Dinero(-1));
            assertThrows(ReglaDominioException.class, () -> new Dinero(Double.NaN));
            assertThrows(ReglaDominioException.class, () -> new Dinero(Double.POSITIVE_INFINITY));
            assertEquals(1000.0, new Dinero(1000).valor());
        }

        @Test
        void capacidadDebeSerMayorACero() {
            assertThrows(ReglaDominioException.class, () -> new Capacidad((byte) 0));
            assertThrows(ReglaDominioException.class, () -> new Capacidad((byte) -1));
            assertEquals(6, new Capacidad((byte) 6).aforoMax());
        }

        @Test
        void dormitorioValidaCamposYNormalizaTexto() {
            assertThrows(ReglaDominioException.class, () -> new Dormitorio(" ", 1, "Doble"));
            assertThrows(ReglaDominioException.class, () -> new Dormitorio("D1", 0, "Doble"));
            assertThrows(ReglaDominioException.class, () -> new Dormitorio("D1", 1, null));
            Dormitorio d = new Dormitorio("  D1 ", 2, " Doble ");
            assertEquals("D1", d.nombre());
            assertEquals("Doble", d.tipoCamas());
        }

        @Test
        void correoValidaFormato() {
            assertThrows(ReglaDominioException.class, () -> CorreoElectronico.of(null));
            assertThrows(ReglaDominioException.class, () -> CorreoElectronico.of("   "));
            assertThrows(ReglaDominioException.class, () -> CorreoElectronico.of("sin-arroba"));
            assertThrows(ReglaDominioException.class, () -> CorreoElectronico.of("a@b"));
            assertEquals("ana@correo.com", CorreoElectronico.of("  ana@correo.com ").direccion());
        }

        @Test
        void fechaNacimientoNoPuedeSerNulaNiFutura() {
            assertThrows(ReglaDominioException.class, () -> new FechaNacimiento(null));
            assertThrows(ReglaDominioException.class,
                    () -> new FechaNacimiento(LocalDate.now().plusDays(1)));
            assertDoesNotThrow(() -> new FechaNacimiento(LocalDate.now()));
        }

        @Test
        void nombreCompletoExigeNombreYApellidoYLosRecorta() {
            assertThrows(ReglaDominioException.class, () -> new NombreCompleto("", "X"));
            assertThrows(ReglaDominioException.class, () -> new NombreCompleto("X", null));
            assertEquals("Ana Pérez", new NombreCompleto(" Ana ", " Pérez ").obtenerNombreCompleto());
        }

        @Test
        void documentoYTelefonoNoPuedenSerVacios() {
            assertThrows(ReglaDominioException.class, () -> new DocumentoIdentidad(" "));
            assertThrows(ReglaDominioException.class, () -> new DocumentoIdentidad(null));
            assertThrows(ReglaDominioException.class, () -> new Telefono(""));
            assertThrows(ReglaDominioException.class, () -> new Telefono(null));
        }

        @Test
        void ocupanteExigeTodosSusDatos() {
            FechaNacimiento fn = new FechaNacimiento(LocalDate.of(2000, 1, 1));
            assertThrows(ReglaDominioException.class, () -> new Ocupante(null, documento(), fn));
            assertThrows(ReglaDominioException.class, () -> new Ocupante(nombre(), null, fn));
            assertThrows(ReglaDominioException.class, () -> new Ocupante(nombre(), documento(), null));
        }

        @Test
        void titularExigeNombreYDatosDeContacto() {
            UsuarioId uid = UsuarioId.generar();
            CorreoElectronico c = CorreoElectronico.of("a@b.co");
            Telefono t = new Telefono("300");
            assertThrows(ReglaDominioException.class, () -> new Titular(null, uid, c, t, documento()));
            assertThrows(ReglaDominioException.class, () -> new Titular(nombre(), null, c, t, documento()));
            assertThrows(ReglaDominioException.class, () -> new Titular(nombre(), uid, null, t, documento()));
            assertThrows(ReglaDominioException.class, () -> new Titular(nombre(), uid, c, null, documento()));
            assertThrows(ReglaDominioException.class, () -> new Titular(nombre(), uid, c, t, null));
        }

        @Test
        void identificadoresNoAceptanNulosYAlojamientoIdParseaTexto() {
            assertThrows(ReglaDominioException.class, () -> new ReservaId(null));
            assertThrows(ReglaDominioException.class, () -> new FolioId(null));
            assertThrows(ReglaDominioException.class, () -> new ApartamentoId(null));
            assertThrows(ReglaDominioException.class, () -> new UsuarioId(null));
            assertThrows(ReglaDominioException.class, () -> new TemporadaId(null));
            assertThrows(ReglaDominioException.class, () -> new BloqueoId(null));
            assertThrows(ReglaDominioException.class, () -> new NovedadId(null));
            assertThrows(ReglaDominioException.class, () -> AlojamientoId.of("no-es-uuid"));
            assertThrows(ReglaDominioException.class, () -> AlojamientoId.of(" "));
            AlojamientoId id = AlojamientoId.generar();
            assertEquals(id, AlojamientoId.of(id.toString()));
        }

        @Test
        void ubicacionExigeTodosSusCampos() {
            assertThrows(ReglaDominioException.class, () -> new Ubicacion("", "Lloró", "Calle 1"));
            assertThrows(ReglaDominioException.class, () -> new Ubicacion("Chocó", " ", "Calle 1"));
            assertThrows(ReglaDominioException.class, () -> new Ubicacion("Chocó", "Lloró", null));
        }

        @Test
        void configuracionAlojamientoValidaParametros() {
            LocalTime in = LocalTime.of(15, 0), out = LocalTime.of(12, 0), ns = LocalTime.of(18, 0);
            assertThrows(ReglaDominioException.class,
                    () -> new ConfiguracionAlojamiento(-1, in, out, 3, 4, ns));
            assertThrows(ReglaDominioException.class,
                    () -> new ConfiguracionAlojamiento(5, null, out, 3, 4, ns));
            assertThrows(ReglaDominioException.class,
                    () -> new ConfiguracionAlojamiento(5, in, out, 0, 4, ns));
            assertThrows(ReglaDominioException.class,
                    () -> new ConfiguracionAlojamiento(5, in, out, 3, 0, ns));
            // no-show antes del check-in
            assertThrows(ReglaDominioException.class,
                    () -> new ConfiguracionAlojamiento(5, in, out, 3, 4, LocalTime.of(14, 0)));
            assertDoesNotThrow(() -> new ConfiguracionAlojamiento(5, in, out, 3, 4, ns));
        }

        @Test
        void autorizacionCierreExigeAutorMotivoYFecha() {
            assertThrows(ReglaDominioException.class, () -> new AutorizacionCierre(" ", "m", AHORA));
            assertThrows(ReglaDominioException.class, () -> new AutorizacionCierre("a", null, AHORA));
            assertThrows(ReglaDominioException.class, () -> new AutorizacionCierre("a", "m", null));
        }

        @Test
        void pagoYCargoValidanCamposObligatorios() {
            assertThrows(ReglaDominioException.class, () -> new Pago(null, MedioPago.EFECTIVO, null, AHORA));
            assertThrows(ReglaDominioException.class, () -> new Pago(new Dinero(1), null, null, AHORA));
            assertThrows(ReglaDominioException.class, () -> new Pago(new Dinero(1), MedioPago.EFECTIVO, null, null));
            assertNull(new Pago(new Dinero(1), MedioPago.EFECTIVO, "   ", AHORA).referencia());

            assertThrows(ReglaDominioException.class, () -> new Cargo(null, "c", new Dinero(1), AHORA));
            assertThrows(ReglaDominioException.class, () -> new Cargo(TipoCargo.ALOJAMIENTO, " ", new Dinero(1), AHORA));
            assertThrows(ReglaDominioException.class, () -> new Cargo(TipoCargo.ALOJAMIENTO, "c", null, AHORA));
            assertThrows(ReglaDominioException.class, () -> new Cargo(TipoCargo.ALOJAMIENTO, "c", new Dinero(1), null));
        }
    }

    // =====================================================================
    // RN-03 / 3.1 Estancia y RangoFechas
    // =====================================================================

    @Nested
    @DisplayName("RN-03 Estancia y rango de fechas")
    class EstanciaYRango {

        @Test
        void salidaDebeSerPosteriorALaEntrada() {
            LocalDate d = LocalDate.of(2026, 11, 10);
            assertThrows(ReglaDominioException.class, () -> new Estancia(d, d));
            assertThrows(ReglaDominioException.class, () -> new Estancia(d, d.minusDays(1)));
            assertThrows(ReglaDominioException.class, () -> new Estancia(null, d));
            assertDoesNotThrow(() -> new Estancia(d, d.plusDays(1)));
        }

        @Test
        void rangosSemiabiertosQueSoloSeTocanNoSolapan() {
            RangoFechas a = new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
            RangoFechas b = new RangoFechas(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14));
            assertFalse(a.intersecta(b));
            assertFalse(b.intersecta(a));
        }

        @Test
        void rangosQueCompartenUnaNocheSeSolapan() {
            RangoFechas a = new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 13));
            RangoFechas b = new RangoFechas(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14));
            assertTrue(a.intersecta(b));
            assertTrue(b.intersecta(a));
        }

        @Test
        void cubreUnRangoContenido() {
            RangoFechas grande = new RangoFechas(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
            RangoFechas chico = new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
            assertTrue(grande.cubre(chico));
            assertFalse(chico.cubre(grande));
        }

        @Test
        void rangoRechazaFinNoPosteriorYConsultaNula() {
            LocalDate d = LocalDate.of(2026, 1, 1);
            assertThrows(ReglaDominioException.class, () -> new RangoFechas(d, d));
            RangoFechas r = new RangoFechas(d, d.plusDays(1));
            assertThrows(ReglaDominioException.class, () -> r.intersecta(null));
        }

        @Test
        void estanciaSeConvierteEnRango() {
            Estancia e = estancia(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
            assertEquals(new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12)),
                    e.rangoFechas());
        }
    }

    // =====================================================================
    // Reserva: RN-04, RN-08, RN-12, RN-22
    // =====================================================================

    @Nested
    @DisplayName("Reserva")
    class ReservaTests {

        @Test
        void nuevaReservaNaceEnPendienteConValorYPoliticaCongelados() {
            Reserva r = reservaValida();
            assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
            assertEquals(240000.0, r.getValorCongelado().valor());
            assertEquals("v1", r.getPoliticaCancelacion().version().valor());
        }

        @Test
        void rn04NoSePuedeCrearConEntradaAnteriorAHoy() {
            ReglaDominioException ex = assertThrows(ReglaDominioException.class,
                    () -> reserva(LocalDate.of(2026, 10, 27), LocalDate.of(2026, 10, 29)));
            assertTrue(ex.getMessage().contains("RN-04"));
        }

        @Test
        void rn04SePuedeCrearConEntradaIgualAHoy() {
            assertDoesNotThrow(() -> reserva(LocalDate.of(2026, 10, 28), LocalDate.of(2026, 10, 29)));
        }

        @Test
        void exigeAlMenosUnOcupanteYDatosObligatorios() {
            Estancia e = estancia(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
            assertThrows(ReglaDominioException.class, () -> Reserva.crear(ReservaId.generar(),
                    FolioId.generar(), ApartamentoId.generar(), titular(), new ArrayList<>(), e, null,
                    LocalTime.of(15, 0), CanalOrigen.PORTAL, new Dinero(1), politica(), AHORA));
            List<Ocupante> uno = new ArrayList<>();
            uno.add(ocupante());
            assertThrows(ReglaDominioException.class, () -> Reserva.crear(ReservaId.generar(),
                    FolioId.generar(), ApartamentoId.generar(), titular(), uno, e, null,
                    null, CanalOrigen.PORTAL, new Dinero(1), politica(), AHORA));
            assertThrows(ReglaDominioException.class, () -> Reserva.crear(ReservaId.generar(),
                    FolioId.generar(), ApartamentoId.generar(), titular(), uno, e, null,
                    LocalTime.of(15, 0), null, new Dinero(1), politica(), AHORA));
            assertThrows(ReglaDominioException.class, () -> Reserva.crear(ReservaId.generar(),
                    FolioId.generar(), ApartamentoId.generar(), titular(), uno, e, null,
                    LocalTime.of(15, 0), CanalOrigen.PORTAL, null, politica(), AHORA));
            assertThrows(ReglaDominioException.class, () -> Reserva.crear(ReservaId.generar(),
                    FolioId.generar(), ApartamentoId.generar(), titular(), uno, e, null,
                    LocalTime.of(15, 0), CanalOrigen.PORTAL, new Dinero(1), null, AHORA));
        }

        @Test
        void identificadorExternoEnBlancoSeNormalizaANulo() {
            List<Ocupante> uno = new ArrayList<>();
            uno.add(ocupante());
            Reserva r = Reserva.crear(ReservaId.generar(), FolioId.generar(), ApartamentoId.generar(),
                    titular(), uno, estancia(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12)),
                    "   ", LocalTime.of(15, 0), CanalOrigen.DIRECTO, new Dinero(1), politica(), AHORA);
            assertNull(r.getIdentificadorExterno());
        }

        @Test
        void listaDeOcupantesDeLaReservaEsInmutable() {
            Reserva r = reservaValida();
            assertThrows(UnsupportedOperationException.class, () -> r.getOcupantes().add(ocupante()));
        }

        // ---- RN-08: transiciones ----

        @Test
        void rn08CicloCompletoPendienteConfirmadaEnCursoFinalizada() {
            Reserva r = reservaValida();
            r.confirmar();
            assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
            r.iniciarEstancia();
            assertEquals(EstadoReserva.EN_CURSO, r.getEstado());
            r.finalizar();
            assertEquals(EstadoReserva.FINALIZADA, r.getEstado());
        }

        @Test
        void rn08RechazaTransicionesInvalidas() {
            Reserva pendiente = reservaValida();
            assertThrows(ReglaDominioException.class, pendiente::iniciarEstancia);
            assertThrows(ReglaDominioException.class, pendiente::finalizar);
            assertThrows(ReglaDominioException.class, pendiente::marcarNoShow);

            Reserva confirmada = reservaValida();
            confirmada.confirmar();
            assertThrows(ReglaDominioException.class, confirmada::confirmar);
            assertThrows(ReglaDominioException.class, confirmada::finalizar);

            Reserva enCurso = reservaValida();
            enCurso.confirmar();
            enCurso.iniciarEstancia();
            assertThrows(ReglaDominioException.class, enCurso::cancelar);
            assertThrows(ReglaDominioException.class, enCurso::marcarNoShow);
            assertThrows(ReglaDominioException.class, enCurso::iniciarEstancia);
        }

        @Test
        void rn08PuedeCancelarseDesdePendienteYConfirmada() {
            Reserva p = reservaValida();
            p.cancelar();
            assertEquals(EstadoReserva.CANCELADA, p.getEstado());

            Reserva c = reservaValida();
            c.confirmar();
            c.cancelar();
            assertEquals(EstadoReserva.CANCELADA, c.getEstado());
        }

        @Test
        void rn08NoShowSoloDesdeConfirmada() {
            Reserva r = reservaValida();
            r.confirmar();
            r.marcarNoShow();
            assertEquals(EstadoReserva.NO_SHOW, r.getEstado());
        }

        @Test
        void rn08EstadosTerminalesNoPuedenCancelarse() {
            Reserva cancelada = reservaValida();
            cancelada.cancelar();
            assertThrows(ReglaDominioException.class, cancelada::cancelar);

            Reserva noShow = reservaValida();
            noShow.confirmar();
            noShow.marcarNoShow();
            assertThrows(ReglaDominioException.class, noShow::cancelar);

            Reserva finalizada = reservaValida();
            finalizada.confirmar();
            finalizada.iniciarEstancia();
            finalizada.finalizar();
            assertThrows(ReglaDominioException.class, finalizada::cancelar);
        }

        // ---- RN-12 / Sección 8: retención de disponibilidad ----

        @Test
        void rn12SoloLosEstadosActivosRetienenDisponibilidad() {
            assertTrue(EstadoReserva.PENDIENTE.retieneDisponibilidad());
            assertTrue(EstadoReserva.CONFIRMADA.retieneDisponibilidad());
            assertTrue(EstadoReserva.EN_CURSO.retieneDisponibilidad());
            assertFalse(EstadoReserva.FINALIZADA.retieneDisponibilidad());
            assertFalse(EstadoReserva.CANCELADA.retieneDisponibilidad());
            assertFalse(EstadoReserva.NO_SHOW.retieneDisponibilidad());
        }

        @Test
        void terminalesSonExactamenteFinalizadaCanceladaYNoShow() {
            for (EstadoReserva e : EstadoReserva.values()) {
                boolean terminal = e == EstadoReserva.FINALIZADA || e == EstadoReserva.CANCELADA
                        || e == EstadoReserva.NO_SHOW;
                assertEquals(terminal, e.esTerminal(), e.name());
                assertEquals(!terminal, e.esActiva(), e.name());
            }
        }
    }

    // =====================================================================
    // Apartamento: RN-02, RN-11, RN-24, RN-31
    // =====================================================================

    @Nested
    @DisplayName("Apartamento")
    class ApartamentoTests {

        @Test
        void rn02NoAdmiteMasOcupantesQueElAforo() {
            Apartamento a = apartamento("101", 3);
            assertTrue(a.capacidadPermitida(3));
            assertTrue(a.capacidadPermitida(1));
            assertFalse(a.capacidadPermitida(4));
            assertThrows(ReglaDominioException.class, () -> a.capacidadPermitida(-1));
        }

        @Test
        void rn11SoloUnApartamentoPreparadoYDisponibleRecibeGrupo() {
            assertTrue(apartamento("101", 3).puedeRecibirGrupo());
            assertFalse(apartamento("102", 3, EstadoOperativo.PENDIENTE_PREPARACION, true).puedeRecibirGrupo());
            assertFalse(apartamento("103", 3, EstadoOperativo.EN_PREPARACION, true).puedeRecibirGrupo());
            assertFalse(apartamento("104", 3, EstadoOperativo.FUERA_DE_SERVICIO, false).puedeRecibirGrupo());
            assertFalse(apartamento("105", 3, EstadoOperativo.PREPARADO, false).puedeRecibirGrupo());
        }

        @Test
        void rn11NoPuedeMarcarseOcupadoSiNoEstaPreparado() {
            Apartamento a = apartamento("101", 3, EstadoOperativo.PENDIENTE_PREPARACION, true);
            assertThrows(ReglaDominioException.class, a::marcarOcupado);
        }

        @Test
        void cicloOperativoPreparadoOcupadoPendientePreparadoYNoSePuedeOcuparDosVeces() {
            Apartamento a = apartamento("101", 3);
            a.marcarOcupado();
            assertEquals(EstadoOperativo.OCUPADO, a.getEstadoOperativo());
            assertFalse(a.isDisponible());
            assertThrows(ReglaDominioException.class, a::marcarOcupado);
            a.marcarPreparado();
            assertEquals(EstadoOperativo.PREPARADO, a.getEstadoOperativo());
            assertTrue(a.isDisponible());
        }

        @Test
        void noPuedeRetirarseDeServicioUnApartamentoOcupado() {
            Apartamento a = apartamento("101", 3);
            a.marcarOcupado();
            assertThrows(ReglaDominioException.class, a::marcarFueraDeServicio);
        }

        @Test
        void unApartamentoPreparadoPuedePasarAFueraDeServicio() {
            Apartamento a = apartamento("101", 3);
            a.marcarFueraDeServicio();
            assertEquals(EstadoOperativo.FUERA_DE_SERVICIO, a.getEstadoOperativo());
            assertFalse(a.isDisponible());
        }

        @Test
        void noPuedeEstarOcupadoYDisponibleALaVez() {
            assertThrows(ReglaDominioException.class,
                    () -> apartamento("101", 3, EstadoOperativo.OCUPADO, true));
        }

        @Test
        void exigeDatosObligatorios() {
            List<Dormitorio> vacios = new ArrayList<>();
            assertThrows(ReglaDominioException.class, () -> Apartamento.crear(new Capacidad((byte) 3),
                    "desc", vacios, EstadoOperativo.PREPARADO, ApartamentoId.generar(), "101", true));
            List<Dormitorio> uno = new ArrayList<>();
            uno.add(new Dormitorio("D", 1, "Sencilla"));
            assertThrows(ReglaDominioException.class, () -> Apartamento.crear(null,
                    "desc", uno, EstadoOperativo.PREPARADO, ApartamentoId.generar(), "101", true));
            assertThrows(ReglaDominioException.class, () -> Apartamento.crear(new Capacidad((byte) 3),
                    " ", uno, EstadoOperativo.PREPARADO, ApartamentoId.generar(), "101", true));
            assertThrows(ReglaDominioException.class, () -> Apartamento.crear(new Capacidad((byte) 3),
                    "desc", uno, EstadoOperativo.PREPARADO, ApartamentoId.generar(), " ", true));
        }

        @Test
        void rn31ReportarUnaNovedadNoCambiaElEstadoOperativo() {
            Apartamento a = apartamento("101", 3);
            Novedad n = Novedad.reportar(a.getId(), "camarera", "Grifo dañado", GravedadNovedad.CRITICA);
            assertNotNull(n.getId());
            assertEquals(EstadoOperativo.PREPARADO, a.getEstadoOperativo());
            assertTrue(a.isDisponible());
        }
    }

    // =====================================================================
    // Bloqueo: RN-07, RN-24
    // =====================================================================

    @Nested
    @DisplayName("Bloqueo")
    class BloqueoTests {

        private Bloqueo bloqueo() {
            return Bloqueo.crear(ApartamentoId.generar(),
                    new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15)),
                    "Mantenimiento");
        }

        @Test
        void rn07BloqueoVigenteSeCruzaConEstanciaQueComparteNoches() {
            assertTrue(bloqueo().seCruzaConEstancia(
                    estancia(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14))));
            assertTrue(bloqueo().seCruzaConEstancia(
                    estancia(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 17))));
        }

        @Test
        void rn07EstanciaQueEmpiezaElDiaDeFinDelBloqueoNoSeCruza() {
            assertFalse(bloqueo().seCruzaConEstancia(
                    estancia(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 17))));
            assertFalse(bloqueo().seCruzaConEstancia(
                    estancia(LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 10))));
        }

        @Test
        void bloqueoDesactivadoYaNoBloquea() {
            Bloqueo b = bloqueo();
            b.desactivar();
            assertFalse(b.seCruzaConEstancia(
                    estancia(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14))));
            assertFalse(b.bloqueaTodoElRango(
                    new RangoFechas(LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 12))));
            assertThrows(ReglaDominioException.class, b::desactivar);
            b.reactivar();
            assertTrue(b.isActivo());
            assertThrows(ReglaDominioException.class, b::reactivar);
        }

        @Test
        void bloqueaTodoElRangoSoloSiLoCubreCompleto() {
            Bloqueo b = bloqueo();
            assertTrue(b.bloqueaTodoElRango(
                    new RangoFechas(LocalDate.of(2026, 11, 11), LocalDate.of(2026, 11, 13))));
            assertFalse(b.bloqueaTodoElRango(
                    new RangoFechas(LocalDate.of(2026, 11, 13), LocalDate.of(2026, 11, 18))));
        }

        @Test
        void exigeMotivoYRango() {
            ApartamentoId id = ApartamentoId.generar();
            RangoFechas r = new RangoFechas(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15));
            assertThrows(ReglaDominioException.class, () -> Bloqueo.crear(id, r, " "));
            assertThrows(ReglaDominioException.class, () -> Bloqueo.crear(id, null, "m"));
            assertThrows(ReglaDominioException.class, () -> Bloqueo.crear((ApartamentoId) null, r, "m"));
        }
    }

    // =====================================================================
    // Temporadas, tarifas y alojamiento: RN-05, RN-27, F-05
    // =====================================================================

    @Nested
    @DisplayName("Temporadas, tarifas y alojamiento")
    class TemporadasYAlojamiento {

        @Test
        void rn05TarifaCalculaValorDeUnaNochePorOcupantesFacturables() {
            Tarifa t = new Tarifa(TemporadaId.generar(), ApartamentoId.generar(), new Dinero(80000));
            assertEquals(0, new BigDecimal("240000").compareTo(t.calcularValorNoche(3)));
        }

        @Test
        void rn05NoSeCalculaConCeroOcupantesFacturables() {
            Tarifa t = new Tarifa(TemporadaId.generar(), ApartamentoId.generar(), new Dinero(80000));
            assertThrows(ReglaDominioException.class, () -> t.calcularValorNoche(0));
        }

        @Test
        void tarifaExigeTemporadaApartamentoYPrecio() {
            assertThrows(ReglaDominioException.class,
                    () -> new Tarifa(null, ApartamentoId.generar(), new Dinero(1)));
            assertThrows(ReglaDominioException.class,
                    () -> new Tarifa(TemporadaId.generar(), null, new Dinero(1)));
            assertThrows(ReglaDominioException.class,
                    () -> new Tarifa(TemporadaId.generar(), ApartamentoId.generar(), null));
        }

        @Test
        void rn27TemporadaAplicableUsaEspecialDentroDeSuRangoYBaseFuera() {
            Apartamento a = apartamento("101", 3);
            Temporada base = base(a);
            Temporada fiestas = especial("Fiestas Patronales", LocalDate.of(2027, 7, 10),
                    LocalDate.of(2027, 7, 21), 120000, a);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base);
            ts.add(fiestas);
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);

            assertEquals(fiestas.getId(), al.temporadaAplicable(LocalDate.of(2027, 7, 15)).getId());
            assertEquals(fiestas.getId(), al.temporadaAplicable(LocalDate.of(2027, 7, 10)).getId());
            // fin exclusivo: el 21 ya es base
            assertEquals(base.getId(), al.temporadaAplicable(LocalDate.of(2027, 7, 21)).getId());
            assertEquals(base.getId(), al.temporadaAplicable(LocalDate.of(2027, 3, 1)).getId());
        }

        @Test
        void rn05TarifaAplicableDependeDeLaTemporadaDeLaNoche() {
            Apartamento a = apartamento("101", 3);
            Temporada base = base(a);
            Temporada fiestas = especial("Fiestas", LocalDate.of(2027, 7, 10),
                    LocalDate.of(2027, 7, 21), 120000, a);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base);
            ts.add(fiestas);
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);

            assertEquals(80000.0, al.tarifaAplicable(a.getId(), LocalDate.of(2027, 3, 1))
                    .precioPorNochePorOcupante().valor());
            assertEquals(120000.0, al.tarifaAplicable(a.getId(), LocalDate.of(2027, 7, 12))
                    .precioPorNochePorOcupante().valor());
        }

        @Test
        void temporadaQueCruzaElFinDeAnioFunciona() {
            Apartamento a = apartamento("101", 3);
            Temporada base = base(a);
            Temporada fiesta = especial("Festival de la Piña", LocalDate.of(2026, 12, 15),
                    LocalDate.of(2027, 1, 16), 140000, a);
            assertTrue(fiesta.cubreFecha(LocalDate.of(2026, 12, 31)));
            assertTrue(fiesta.cubreFecha(LocalDate.of(2027, 1, 15)));
            assertFalse(fiesta.cubreFecha(LocalDate.of(2027, 1, 16)));
            assertFalse(base.cubreFecha(LocalDate.of(2026, 12, 31)));
        }

        @Test
        void rn27NoSePuedenSolaparTemporadasAlCrearElAlojamiento() {
            Apartamento a = apartamento("101", 3);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a));
            ts.add(especial("T1", LocalDate.of(2027, 7, 10), LocalDate.of(2027, 7, 21), 1000, a));
            ts.add(especial("T2", LocalDate.of(2027, 7, 20), LocalDate.of(2027, 7, 25), 1000, a));
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            assertThrows(ReglaDominioException.class, () -> alojamiento(as, ts));
        }

        @Test
        void rn27NoSePuedeAgregarUnaTemporadaSolapada() {
            Apartamento a = apartamento("101", 3);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a));
            ts.add(especial("T1", LocalDate.of(2027, 7, 10), LocalDate.of(2027, 7, 21), 1000, a));
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);

            Temporada solapada = especial("T2", LocalDate.of(2027, 7, 15), LocalDate.of(2027, 7, 30), 1000, a);
            assertThrows(ReglaDominioException.class, () -> al.agregarTemporada(solapada));

            Temporada contigua = especial("T3", LocalDate.of(2027, 7, 21), LocalDate.of(2027, 7, 30), 1000, a);
            assertDoesNotThrow(() -> al.agregarTemporada(contigua));
            assertEquals(3, al.getTemporadas().size());
        }

        @Test
        void f05DebeExistirExactamenteUnaTemporadaBase() {
            Apartamento a = apartamento("101", 3);
            List<Apartamento> as = new ArrayList<>();
            as.add(a);

            List<Temporada> sinBase = new ArrayList<>();
            sinBase.add(especial("T1", LocalDate.of(2027, 7, 10), LocalDate.of(2027, 7, 21), 1000, a));
            assertThrows(ReglaDominioException.class, () -> alojamiento(as, sinBase));

            List<Temporada> dosBases = new ArrayList<>();
            dosBases.add(base(a));
            dosBases.add(base(a));
            assertThrows(ReglaDominioException.class, () -> alojamiento(as, dosBases));
        }

        @Test
        void f05LaTemporadaBaseNoSePuedeRetirar() {
            Apartamento a = apartamento("101", 3);
            Temporada base = base(a);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base);
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);
            assertThrows(ReglaDominioException.class, () -> al.retirarTemporada(base.getId()));
        }

        @Test
        void unaTemporadaEspecialSiSePuedeRetirarYLasFechasVuelvenABase() {
            Apartamento a = apartamento("101", 3);
            Temporada base = base(a);
            Temporada t1 = especial("T1", LocalDate.of(2027, 7, 10), LocalDate.of(2027, 7, 21), 1000, a);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base);
            ts.add(t1);
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);
            al.retirarTemporada(t1.getId());
            assertEquals(base.getId(), al.temporadaAplicable(LocalDate.of(2027, 7, 15)).getId());
        }

        @Test
        void todoApartamentoDebeTenerTarifaEnCadaTemporada() {
            Apartamento a1 = apartamento("101", 3);
            Apartamento a2 = apartamento("102", 6);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a1)); // falta tarifa para a2
            List<Apartamento> as = new ArrayList<>();
            as.add(a1);
            as.add(a2);
            assertThrows(ReglaDominioException.class, () -> alojamiento(as, ts));
        }

        @Test
        void noSePuedeAgregarUnApartamentoSinTarifasCompletas() {
            Apartamento a1 = apartamento("101", 3);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a1));
            List<Apartamento> as = new ArrayList<>();
            as.add(a1);
            Alojamiento al = alojamiento(as, ts);

            Apartamento nuevo = apartamento("102", 3);
            assertThrows(ReglaDominioException.class, () -> al.agregarApartamento(nuevo));

            // tras registrar su tarifa en la temporada, sí puede agregarse
            al.establecerTarifa(new Tarifa(ts.get(0).getId(), nuevo.getId(), new Dinero(80000)));
        }

        @Test
        void noSePuedenRepetirIdentificadoresUnicosDeApartamento() {
            Apartamento a1 = apartamento("101", 3);
            Apartamento a2 = apartamento("101", 6);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a1, a2));
            List<Apartamento> as = new ArrayList<>();
            as.add(a1);
            as.add(a2);
            assertThrows(ReglaDominioException.class, () -> alojamiento(as, ts));
        }

        @Test
        void noSePuedeEstablecerTarifaParaApartamentoAjeno() {
            Apartamento a1 = apartamento("101", 3);
            Temporada base = base(a1);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base);
            List<Apartamento> as = new ArrayList<>();
            as.add(a1);
            Alojamiento al = alojamiento(as, ts);
            Tarifa ajena = new Tarifa(base.getId(), ApartamentoId.generar(), new Dinero(1000));
            assertThrows(ReglaDominioException.class, () -> al.establecerTarifa(ajena));
        }

        @Test
        void cambiarPoliticaVigenteConservaLaVersionCongeladaDeLasReservas() {
            Reserva r = reservaValida();
            Apartamento a = apartamento("101", 3);
            List<Temporada> ts = new ArrayList<>();
            ts.add(base(a));
            List<Apartamento> as = new ArrayList<>();
            as.add(a);
            Alojamiento al = alojamiento(as, ts);

            List<TramoCancelacion> tramos = new ArrayList<>();
            tramos.add(new TramoCancelacion(0, 0.5));
            al.cambiarPoliticaVigente(new PoliticaCancelacion(new VersionPolitica("v2"), tramos, 0.5));

            assertEquals("v2", al.getPoliticaVigente().version().valor());
            assertEquals("v1", r.getPoliticaCancelacion().version().valor()); // RN-13 / RN-22
        }
    }

    // =====================================================================
    // Política de cancelación: RN-13, RN-30
    // =====================================================================

    @Nested
    @DisplayName("Política de cancelación")
    class PoliticaTests {

        @Test
        void rn13LaRetencionSeDeterminaPorLaAntelacion() {
            PoliticaCancelacion p = politica();
            assertEquals(1.0, p.retencionParaDias(0));
            assertEquals(0.25, p.retencionParaDias(1));
            assertEquals(0.25, p.retencionParaDias(2));
            assertEquals(0.10, p.retencionParaDias(3));
            assertEquals(0.10, p.retencionParaDias(30));
        }

        @Test
        void rechazaAntelacionNegativa() {
            assertThrows(ReglaDominioException.class, () -> politica().retencionParaDias(-1));
        }

        @Test
        void retencionPorNoShowViveEnLaPolitica() {
            assertEquals(1.0, politica().retencionPorNoShow());
        }

        @Test
        void exigeTramoDesdeCeroDiasSinDuplicados() {
            List<TramoCancelacion> sinCero = new ArrayList<>();
            sinCero.add(new TramoCancelacion(1, 0.5));
            assertThrows(ReglaDominioException.class,
                    () -> new PoliticaCancelacion(new VersionPolitica("v"), sinCero, 1.0));

            List<TramoCancelacion> duplicados = new ArrayList<>();
            duplicados.add(new TramoCancelacion(0, 0.5));
            duplicados.add(new TramoCancelacion(0, 0.3));
            assertThrows(ReglaDominioException.class,
                    () -> new PoliticaCancelacion(new VersionPolitica("v"), duplicados, 1.0));

            assertThrows(ReglaDominioException.class,
                    () -> new PoliticaCancelacion(new VersionPolitica("v"), new ArrayList<>(), 1.0));
        }

        @Test
        void validaVersionYPorcentajeDeNoShow() {
            List<TramoCancelacion> t = new ArrayList<>();
            t.add(new TramoCancelacion(0, 0.5));
            assertThrows(ReglaDominioException.class, () -> new PoliticaCancelacion(null, t, 1.0));
            assertThrows(ReglaDominioException.class,
                    () -> new PoliticaCancelacion(new VersionPolitica("v"), t, 1.5));
            assertThrows(ReglaDominioException.class,
                    () -> new PoliticaCancelacion(new VersionPolitica("v"), t, -0.1));
        }

        @Test
        void tramoValidaDiasYPorcentaje() {
            assertThrows(ReglaDominioException.class, () -> new TramoCancelacion(-1, 0.5));
            assertThrows(ReglaDominioException.class, () -> new TramoCancelacion(1, 1.01));
            assertThrows(ReglaDominioException.class, () -> new TramoCancelacion(1, -0.01));
            assertThrows(ReglaDominioException.class, () -> new TramoCancelacion(1, Double.NaN));
        }

        @Test
        void versionNoPuedeEstarVacia() {
            assertThrows(ReglaDominioException.class, () -> new VersionPolitica(" "));
            assertEquals("v1", new VersionPolitica(" v1 ").valor());
        }
    }

    // =====================================================================
    // Folio: RN-15, RN-16, RN-17
    // =====================================================================

    @Nested
    @DisplayName("Folio")
    class FolioTests {

        @Test
        void nuevoFolioNaceAbiertoYVacio() {
            Folio f = folio();
            assertEquals(EstadoFolio.ABIERTO, f.getEstado());
            assertTrue(f.getCargos().isEmpty());
            assertTrue(f.getPagos().isEmpty());
            assertEquals(0, BigDecimal.ZERO.compareTo(f.saldoPendiente()));
        }

        @Test
        void rn15SaldoEsDiferenciaEntreCargosYPagos() {
            Folio f = folio();
            f.registrarCargo(cargo(240000));
            f.registrarCargo(cargo(30000));
            f.registrarPago(pago(60000));
            assertEquals(0, new BigDecimal("270000").compareTo(f.totalCargos()));
            assertEquals(0, new BigDecimal("60000").compareTo(f.totalPagos()));
            assertEquals(0, new BigDecimal("210000").compareTo(f.saldoPendiente()));
            assertTrue(f.tieneSaldoPendiente());
        }

        @Test
        void rn15AdmitePagosParcialesConMediosDistintos() {
            Folio f = folio();
            f.registrarCargo(cargo(100000));
            f.registrarPago(new Pago(new Dinero(40000), MedioPago.EFECTIVO, null, AHORA));
            f.registrarPago(new Pago(new Dinero(60000), MedioPago.TRANSFERENCIA, "TRX-9", AHORA));
            assertFalse(f.tieneSaldoPendiente());
            assertEquals(2, f.getPagos().size());
        }

        @Test
        void saldoAFavorSeConsideraSaldado() {
            Folio f = folio();
            f.registrarCargo(cargo(100000));
            f.registrarPago(pago(150000));
            assertEquals(0, BigDecimal.ZERO.compareTo(f.saldoPendiente()));
            assertFalse(f.tieneSaldoPendiente());
        }

        @Test
        void rn16LasListasExpuestasSonInmutables() {
            Folio f = folio();
            f.registrarCargo(cargo(1000));
            f.registrarPago(pago(500));
            assertThrows(UnsupportedOperationException.class, () -> f.getCargos().clear());
            assertThrows(UnsupportedOperationException.class, () -> f.getPagos().clear());
        }

        @Test
        void rn17NoPuedeCerrarseConSaldoPendiente() {
            Folio f = folio();
            f.registrarCargo(cargo(100000));
            assertThrows(FolioSaldoPendienteException.class, () -> f.cerrar(autorizacion()));
            assertEquals(EstadoFolio.ABIERTO, f.getEstado());
        }

        @Test
        void rn17CierraConSaldoCeroYConservaLaAutorizacion() {
            Folio f = folio();
            f.registrarCargo(cargo(100000));
            f.registrarPago(pago(100000));
            AutorizacionCierre auth = autorizacion();
            f.cerrar(auth);
            assertEquals(EstadoFolio.CERRADO, f.getEstado());
            assertEquals(auth, f.getAutorizacionCierre());
        }

        @Test
        void elCierreExigeAutorizacion() {
            Folio f = folio();
            assertThrows(ReglaDominioException.class, () -> f.cerrar(null));
        }

        @Test
        void unFolioCerradoNoRecibeCargosNiPagosNiVuelveACerrarse() {
            Folio f = folio();
            f.cerrar(autorizacion());
            assertThrows(ReglaDominioException.class, () -> f.registrarCargo(cargo(1000)));
            assertThrows(ReglaDominioException.class, () -> f.registrarPago(pago(1000)));
            assertThrows(ReglaDominioException.class, () -> f.cerrar(autorizacion()));
        }

        @Test
        void reabrirSoloAplicaAUnFolioCerradoYConAutorizacion() {
            Folio f = folio();
            assertThrows(ReglaDominioException.class, () -> f.reabrir(autorizacion()));
            f.cerrar(autorizacion());
            assertThrows(ReglaDominioException.class, () -> f.reabrir(null));
            f.reabrir(autorizacion());
            assertEquals(EstadoFolio.ABIERTO, f.getEstado());
        }

        @Test
        void rechazaCargosYPagosNulos() {
            Folio f = folio();
            assertThrows(ReglaDominioException.class, () -> f.registrarCargo(null));
            assertThrows(ReglaDominioException.class, () -> f.registrarPago(null));
        }

        @Test
        void exigeIdentificadorYReserva() {
            assertThrows(ReglaDominioException.class, () -> Folio.crear(null, ReservaId.generar()));
            assertThrows(ReglaDominioException.class, () -> Folio.crear(FolioId.generar(), null));
        }
    }

    // =====================================================================
    // Usuario y Novedad
    // =====================================================================

    @Nested
    @DisplayName("Usuario y Novedad")
    class UsuarioYNovedad {

        private Usuario usuario() {
            return Usuario.registrar(nombre(), documento(), CorreoElectronico.of("ana@correo.com"),
                    new Telefono("300"), "hash", RolUsuario.HUESPED,
                    new FechaNacimiento(LocalDate.of(1990, 1, 1)));
        }

        @Test
        void registrarCreaUsuarioActivoConRol() {
            Usuario u = usuario();
            assertTrue(u.estaActivo());
            assertTrue(u.tieneRol(RolUsuario.HUESPED));
            assertFalse(u.tieneRol(RolUsuario.ADMINISTRADOR));
            assertNotNull(u.getId());
        }

        @Test
        void activarYDesactivarNoSePuedenRepetir() {
            Usuario u = usuario();
            assertThrows(ReglaDominioException.class, u::activar);
            u.desactivar();
            assertFalse(u.estaActivo());
            assertThrows(ReglaDominioException.class, u::desactivar);
            u.activar();
            assertTrue(u.estaActivo());
        }

        @Test
        void cambiarRolYContraseniaValidanNulos() {
            Usuario u = usuario();
            assertThrows(ReglaDominioException.class, () -> u.cambiarRol(null));
            u.cambiarRol(RolUsuario.RECEPCIONISTA);
            assertTrue(u.tieneRol(RolUsuario.RECEPCIONISTA));
            assertThrows(ReglaDominioException.class, () -> u.cambiarContrasenia(" "));
            assertThrows(ReglaDominioException.class, () -> u.actualizarCorreo(null));
            assertThrows(ReglaDominioException.class, () -> u.actualizarTelefono(null));
        }

        @Test
        void usuarioExigeContraseniaYRol() {
            assertThrows(ReglaDominioException.class, () -> Usuario.registrar(nombre(), documento(),
                    CorreoElectronico.of("a@b.co"), new Telefono("3"), "", RolUsuario.HUESPED,
                    new FechaNacimiento(LocalDate.of(1990, 1, 1))));
            assertThrows(ReglaDominioException.class, () -> Usuario.registrar(nombre(), documento(),
                    CorreoElectronico.of("a@b.co"), new Telefono("3"), "hash", null,
                    new FechaNacimiento(LocalDate.of(1990, 1, 1))));
        }

        @Test
        void novedadExigeTodosSusDatosYNormalizaTexto() {
            ApartamentoId apto = ApartamentoId.generar();
            assertThrows(ReglaDominioException.class,
                    () -> Novedad.reportar(null, "a", "d", GravedadNovedad.BAJA));
            assertThrows(ReglaDominioException.class,
                    () -> Novedad.reportar(apto, " ", "d", GravedadNovedad.BAJA));
            assertThrows(ReglaDominioException.class,
                    () -> Novedad.reportar(apto, "a", " ", GravedadNovedad.BAJA));
            assertThrows(ReglaDominioException.class,
                    () -> Novedad.reportar(apto, "a", "d", null));
            Novedad n = Novedad.reportar(apto, " Juan ", " Falta toalla ", GravedadNovedad.MEDIA);
            assertEquals("Juan", n.getAutor());
            assertEquals("Falta toalla", n.getDescripcion());
            assertNotNull(n.getFechaReporte());
        }
    }
}
