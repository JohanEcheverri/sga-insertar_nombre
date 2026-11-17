interface Props {
  /** Página actual, empezando en 1. */
  pagina: number
  totalPaginas: number
  alCambiar: (pagina: number) => void
}

export function Paginacion({ pagina, totalPaginas, alCambiar }: Props) {
  if (totalPaginas <= 1) return null
  return <nav aria-label="Paginación">
    <ul className="pagination justify-content-center mb-0">
      <li className={`page-item${pagina <= 1 ? ' disabled' : ''}`}>
        <button className="page-link" type="button" onClick={() => alCambiar(pagina - 1)} disabled={pagina <= 1}>Anterior</button>
      </li>
      <li className="page-item disabled"><span className="page-link">Página {pagina} de {totalPaginas}</span></li>
      <li className={`page-item${pagina >= totalPaginas ? ' disabled' : ''}`}>
        <button className="page-link" type="button" onClick={() => alCambiar(pagina + 1)} disabled={pagina >= totalPaginas}>Siguiente</button>
      </li>
    </ul>
  </nav>
}
