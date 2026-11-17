import { useState } from 'react'
import { Link, NavLink } from 'react-router'
import { useAuth } from '../contexto/useAuth'

export function Encabezado() {
  const { sesion, estaAutenticado, tieneRol, cerrarSesion } = useAuth()
  const [menuAbierto, setMenuAbierto] = useState(false)
  const claseEnlace = ({ isActive }: { isActive: boolean }) => `nav-link${isActive ? ' active' : ''}`
  const rolPrincipal = sesion?.roles[0]?.replaceAll('_', ' ')
  return <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div className="container">
      <Link className="navbar-brand fw-bold" to="/">El Cam</Link>
      <button className="navbar-toggler" type="button" aria-controls="navegacionPrincipal" aria-expanded={menuAbierto} onClick={() => setMenuAbierto(!menuAbierto)} aria-label="Abrir menú">
        <span className="navbar-toggler-icon" />
      </button>
      <div className={`navbar-collapse${menuAbierto ? ' show' : ' collapse'}`} id="navegacionPrincipal">
        <div className="navbar-nav me-auto">
          <NavLink className={claseEnlace} to="/">Inicio / Catálogo</NavLink>
          {tieneRol(['HUESPED']) && <NavLink className={claseEnlace} to="/mis-reservas">Mis Reservas</NavLink>}
          {tieneRol(['RECEPCIONISTA', 'ADMINISTRADOR']) && <NavLink className={claseEnlace} to="/recepcion">Panel Recepción</NavLink>}
          {tieneRol(['ADMINISTRADOR']) && <NavLink className={claseEnlace} to="/admin">Administración</NavLink>}
        </div>
        <div className="d-flex align-items-lg-center gap-2 flex-column flex-lg-row py-2 py-lg-0">
          {!estaAutenticado ? <>
            <Link className="btn btn-outline-light btn-sm" to="/login">Iniciar Sesión</Link>
            <Link className="btn btn-warning btn-sm" to="/registro">Registrarse</Link>
          </> : <>
            <span className="text-white small">{sesion?.correo}</span>
            <span className="badge text-bg-info">{rolPrincipal}</span>
            <button className="btn btn-outline-light btn-sm" type="button" onClick={cerrarSesion}>Cerrar Sesión</button>
          </>}
        </div>
      </div>
    </div>
  </nav>
}
