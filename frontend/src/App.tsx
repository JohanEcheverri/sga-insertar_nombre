import { Route, Routes } from 'react-router'
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

export default function App() {
  return <Routes>
    <Route path="/" element={<Inicio />} />
    <Route path="/apartamentos/:identificacion" element={<DetalleApartamento />} />
    <Route path="/login" element={<Login />} />
    <Route path="/registro" element={<Registro />} />
    <Route element={<RutaProtegida />}>
      <Route path="/reservas/nueva" element={<NuevaReserva />} />
      <Route path="/mis-reservas" element={<MisReservas />} />
    </Route>
    <Route element={<RutaProtegida roles={['RECEPCIONISTA', 'ADMINISTRADOR']} />}>
      <Route path="/recepcion" element={<PanelRecepcion />} />
    </Route>
    <Route element={<RutaProtegida roles={['ADMINISTRADOR']} />}>
      <Route path="/admin" element={<PanelAdmin />} />
    </Route>
    <Route path="*" element={<NoEncontrada />} />
  </Routes>
}
