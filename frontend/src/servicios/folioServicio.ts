import type { FolioDetalle } from '../modelos/folio'
import { api } from './api'
export async function obtenerFolio(id: number): Promise<FolioDetalle> { return (await api.get<FolioDetalle>(`/folios/${id}`)).data }
