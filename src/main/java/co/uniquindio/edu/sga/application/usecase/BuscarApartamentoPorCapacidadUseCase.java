package co.uniquindio.edu.sga.application.usecase;

import co.uniquindio.edu.sga.application.result.ApartamentoDetalle;
import co.uniquindio.edu.sga.application.support.Cargas;
import co.uniquindio.edu.sga.domain.repository.AlojamientoRepository;
import co.uniquindio.edu.sga.domain.repository.Pagina;
import org.springframework.stereotype.Service;

/** Busca apartamentos activos que permiten alojar la capacidad solicitada. */
@Service
public class BuscarApartamentoPorCapacidadUseCase {
    public record Command(int capacidad, int pagina) { }

    private final AlojamientoRepository alojamientoRepository;

    public BuscarApartamentoPorCapacidadUseCase(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public Pagina<ApartamentoDetalle> ejecutar(Command command) {
        var resultados = Cargas.alojamiento(alojamientoRepository).apartamentosActivos().stream()
                .filter(a -> a.getCapacidad().aforoMax() >= command.capacidad())
                .map(ApartamentoDetalle::de).toList();
        return Pagina.de(resultados, command.pagina());
    }
}
