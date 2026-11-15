package co.uniquindio.edu.sga.application.dto.response;

import co.uniquindio.edu.sga.domain.repository.Pagina;
import java.util.List;
import java.util.function.Function;

/** Metadatos estables para todos los listados REST. */
public record PaginaResponse<T>(List<T> contenido, int pagina, int tamanio,
                                long totalElementos, int totalPaginas) {
    public static <S, T> PaginaResponse<T> de(Pagina<S> pagina, Function<S, T> mapper) {
        return new PaginaResponse<>(pagina.contenido().stream().map(mapper).toList(), pagina.pagina(),
                pagina.tamanio(), pagina.totalElementos(), pagina.totalPaginas());
    }
}
