import { Link } from 'react-router'

export default function NoEncontrada() {
  return <main className="container py-5 text-center"><p className="display-1 fw-bold text-primary">404</p><h1 className="h2">No encontramos esta página</h1><p className="text-secondary mb-4">Revisa la dirección o regresa al catálogo de El Cam.</p><Link className="btn btn-primary" to="/">Volver al inicio</Link></main>
}
