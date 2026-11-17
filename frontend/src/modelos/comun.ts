/** Estructura de PaginaResponse del backend (páginas desde 0, 10 por página). */
export interface Pagina<T> {
  contenido: T[]
  pagina: number
  tamanio: number
  totalElementos: number
  totalPaginas: number
}
