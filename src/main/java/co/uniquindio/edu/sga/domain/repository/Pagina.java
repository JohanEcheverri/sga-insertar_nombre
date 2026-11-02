package co.uniquindio.edu.sga.domain.repository;

import java.util.List;
import java.util.function.Function;

/** Resultado paginado. 12.4: todos los listados usan 10 elementos por página. Páginas desde 0. */
public record Pagina<T>(List<T> contenido, int pagina, int tamanio, long totalElementos) {

    public static final int TAMANIO_ESTANDAR = 10;

    public int totalPaginas() {
        return (int) Math.ceil((double) totalElementos / tamanio);
    }

    /** Utilidad para repositorios en memoria: pagina una lista ya filtrada y ordenada. */
    public static <T> Pagina<T> de(List<T> todos, int pagina) {
        int p = Math.max(0, pagina);
        int desde = Math.min(p * TAMANIO_ESTANDAR, todos.size());
        int hasta = Math.min(desde + TAMANIO_ESTANDAR, todos.size());
        return new Pagina<>(List.copyOf(todos.subList(desde, hasta)), p, TAMANIO_ESTANDAR, todos.size());
    }

    public <R> Pagina<R> mapear(Function<T, R> f) {
        return new Pagina<>(contenido.stream().map(f).toList(), pagina, tamanio, totalElementos);
    }
}
