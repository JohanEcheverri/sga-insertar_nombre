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
}