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
}
