import { Route, Routes } from 'react-router'
import { Encabezado } from './componentes/Encabezado'
import { PiePagina } from './componentes/PiePagina'
import { RutaProtegida } from './rutas/RutaProtegida'
import Inicio from './paginas/Inicio'
import DetalleApartamento from './paginas/DetalleApartamento'
import Login from './paginas/Login'
import Registro from './paginas/Registro'
import NuevaReserva from './paginas/NuevaReserva'
import MisReservas from './paginas/MisReservas'
import PanelRecepcion from './paginas/PanelRecepcion'
import PanelAdmin from './paginas/PanelAdmin'
import NoEncontrada from './paginas/NoEncontrada'
import PanelServicio from './paginas/PanelServicio'
import { Navigate } from 'react-router'
import { useAuth } from './contexto/useAuth'

function InicioPorRol() {
  const { tieneRol } = useAuth()
  return tieneRol(['PERSONAL_SERVICIO']) ? <Navigate to="/servicio" replace /> : <Inicio />
}

export default function App() {
  return <div className="d-flex flex-column min-vh-100">
    <Encabezado />
    <div className="flex-grow-1">
      <Routes>
        <Route path="/" element={<InicioPorRol />} />
        <Route element={<RutaProtegida rolesPermitidos={['HUESPED', 'RECEPCIONISTA', 'ADMINISTRADOR']} />}>
          <Route path="/apartamentos/:id" element={<DetalleApartamento />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
        <Route element={<RutaProtegida rolesPermitidos={['HUESPED']} />}>
          <Route path="/reservar/:id" element={<NuevaReserva />} />
          <Route path="/reservas/nueva" element={<NuevaReserva />} />
          <Route path="/mis-reservas" element={<MisReservas />} />
        </Route>
        <Route element={<RutaProtegida rolesPermitidos={['RECEPCIONISTA', 'ADMINISTRADOR']} />}>
          <Route path="/recepcion" element={<PanelRecepcion />} />
        </Route>
        <Route element={<RutaProtegida rolesPermitidos={['ADMINISTRADOR']} />}>
          <Route path="/admin" element={<PanelAdmin />} />
        </Route>
        <Route element={<RutaProtegida rolesPermitidos={['PERSONAL_SERVICIO']} />}>
          <Route path="/servicio" element={<PanelServicio />} />
        </Route>
        <Route path="*" element={<NoEncontrada />} />
      </Routes>
    </div>
    <PiePagina />
  </div>
}
