import { useParams } from 'react-router'
import { Encabezado } from '../componentes/Encabezado'
export default function DetalleApartamento() { const { identificacion } = useParams(); return <><Encabezado /><main className="contenedor"><h1>Apartamento {identificacion}</h1><p>Consulta disponibilidad y tarifas para este alojamiento.</p></main></> }
