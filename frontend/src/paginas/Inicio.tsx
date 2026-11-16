import { Link } from 'react-router'

export default function Inicio() {
  return <main className="container py-5"><section className="p-5 rounded-4 bg-light shadow-sm">
    <p className="text-primary fw-semibold">Alojamiento turístico en Armenia, Quindío</p>
    <h1 className="display-5 fw-bold">Bienvenido a El Cam</h1>
    <p className="lead">Encuentra un espacio para descansar y disfruta tu estancia.</p>
    <Link className="btn btn-primary mt-3" to="/">Explorar catálogo</Link>
  </section></main>
}
