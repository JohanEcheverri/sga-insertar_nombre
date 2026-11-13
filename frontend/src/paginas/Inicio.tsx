import { Link } from 'react-router'
import { Encabezado } from '../componentes/Encabezado'
import { PiePagina } from '../componentes/PiePagina'
export default function Inicio() { return <><Encabezado /><main className="contenedor"><h1>Encuentra tu alojamiento</h1><p>Consulta apartamentos y gestiona tus reservas en un solo lugar.</p><Link to="/login">Iniciar sesión</Link></main><PiePagina /></> }
