import { useParams } from 'react-router'

export default function DetalleApartamento() {
  const { id } = useParams()
  return <main className="container py-5"><h1 className="h2">Apartamento {id}</h1><p className="text-secondary">Aquí podrás consultar disponibilidad y tarifas del alojamiento.</p></main>
}
