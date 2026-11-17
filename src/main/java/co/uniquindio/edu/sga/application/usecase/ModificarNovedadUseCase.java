package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.exception.AccesoDenegadoException;
import co.uniquindio.edu.sga.application.exception.NovedadNoEncontradaException;
import co.uniquindio.edu.sga.application.result.NovedadDetalle;
import co.uniquindio.edu.sga.application.security.Solicitante;
import co.uniquindio.edu.sga.domain.entity.Novedad;
import co.uniquindio.edu.sga.domain.repository.NovedadRepository;
import co.uniquindio.edu.sga.domain.valueobject.GravedadNovedad;
import co.uniquindio.edu.sga.domain.valueobject.NovedadId;
import co.uniquindio.edu.sga.domain.valueobject.RolUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso que modifica los datos de una novedad. */
@Service
@Transactional
public class ModificarNovedadUseCase {
    public record Command(UUID id, String autor, String descripcion, GravedadNovedad gravedad, Solicitante solicitante) { }
    private final NovedadRepository novedades;
    public ModificarNovedadUseCase(NovedadRepository novedades) { this.novedades = novedades; }
    public NovedadDetalle ejecutar(Command cmd) {
        if (cmd.solicitante() == null || (!cmd.solicitante().esAdministrador()
                && cmd.solicitante().rol() != RolUsuario.PERSONAL_SERVICIO)) {
            throw new AccesoDenegadoException("Solo administración o personal de servicio puede editar novedades.");
        }
        Novedad novedad = novedades.buscarPorId(NovedadId.of(cmd.id()))
                .orElseThrow(() -> new NovedadNoEncontradaException(cmd.id().toString()));
        novedad.actualizar(cmd.autor(), cmd.descripcion(), cmd.gravedad());
        novedades.guardar(novedad);
        return NovedadDetalle.de(novedad);
    }
}
