package co.uniquindio.edu.sga.application.support;

import co.uniquindio.edu.sga.application.exception.AlojamientoNoEncontradoException;
import co.uniquindio.edu.sga.application.exception.ApartamentoNoEncontradoException;
import co.uniquindio.edu.sga.application.exception.FolioNoEncontradoException;
import co.uniquindio.edu.sga.application.exception.ReservaNoEncontradaException;
import co.uniquindio.edu.sga.application.exception.TemporadaNoEncontradaException;
import co.uniquindio.edu.sga.application.exception.UsuarioNoEncontradoException;
import co.uniquindio.edu.sga.domain.entity.Alojamiento;
import co.uniquindio.edu.sga.domain.entity.Apartamento;
import co.uniquindio.edu.sga.domain.entity.Folio;
import co.uniquindio.edu.sga.domain.entity.Reserva;
import co.uniquindio.edu.sga.domain.entity.Temporada;
import co.uniquindio.edu.sga.domain.entity.Usuario;
import co.uniquindio.edu.sga.domain.repository.AlojamientoRepository;
import co.uniquindio.edu.sga.domain.repository.FolioRepository;
import co.uniquindio.edu.sga.domain.repository.ReservaRepository;
import co.uniquindio.edu.sga.domain.repository.UsuarioRepository;
import co.uniquindio.edu.sga.domain.valueobject.ApartamentoId;
import co.uniquindio.edu.sga.domain.valueobject.ReservaId;
import co.uniquindio.edu.sga.domain.valueobject.TemporadaId;
import co.uniquindio.edu.sga.domain.valueobject.UsuarioId;

import java.util.UUID;

/** Cargas "o falla": convierten un Optional vacío en la excepción de aplicación correspondiente. */
public final class Cargas {

    private Cargas() { }

    public static Alojamiento alojamiento(AlojamientoRepository repo) {
        return repo.obtener().orElseThrow(() -> new AlojamientoNoEncontradoException("único"));
    }

    public static Reserva reserva(ReservaRepository repo, UUID id) {
        return repo.buscarPorId(ReservaId.of(id))
                .orElseThrow(() -> new ReservaNoEncontradaException(String.valueOf(id)));
    }

    public static Folio folioDeReserva(FolioRepository repo, UUID reservaId) {
        return repo.buscarPorReservaId(ReservaId.of(reservaId))
                .orElseThrow(() -> new FolioNoEncontradoException(String.valueOf(reservaId)));
    }

    public static Apartamento apartamento(Alojamiento alojamiento, UUID id) {
        return alojamiento.buscarApartamento(ApartamentoId.of(id))
                .orElseThrow(() -> new ApartamentoNoEncontradoException(String.valueOf(id)));
    }

    public static Temporada temporada(Alojamiento alojamiento, UUID id) {
        return alojamiento.buscarTemporada(TemporadaId.of(id))
                .orElseThrow(() -> new TemporadaNoEncontradaException(String.valueOf(id)));
    }

    public static Usuario usuario(UsuarioRepository repo, UUID id) {
        return repo.buscarPorId(UsuarioId.of(id))
                .orElseThrow(() -> new UsuarioNoEncontradoException(String.valueOf(id)));
    }
}
