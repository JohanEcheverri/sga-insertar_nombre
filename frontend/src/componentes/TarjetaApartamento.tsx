import type { ApartamentoResumen } from '../modelos/apartamento'
import { Link } from 'react-router'
export function TarjetaApartamento({ apartamento }: { apartamento: ApartamentoResumen }) { return <article className="tarjeta"><h2>{apartamento.nombre ?? `Apartamento ${apartamento.identificacion}`}</h2><p>{apartamento.descripcion}</p><p>Capacidad: {apartamento.capacidad ?? 'Por confirmar'}</p><Link to={`/apartamentos/${apartamento.identificacion}`}>Ver detalle</Link></article> }
