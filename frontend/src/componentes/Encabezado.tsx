import { Link } from 'react-router'
export function Encabezado() { return <header className="barra"><Link to="/" className="marca">SGA</Link><nav><Link to="/">Inicio</Link> <Link to="/login">Ingresar</Link></nav></header> }
