import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useLocation, useNavigate } from 'react-router'
import { loginSchema } from '../esquemas/login'
import type { FormularioLogin } from '../esquemas/login'
import { useAuth } from '../contexto/useAuth'
import { extraerMensajeError } from '../utilidades/errores'

export default function Login() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormularioLogin>({ resolver: zodResolver(loginSchema) })
  const { iniciarSesion } = useAuth()
  const navegar = useNavigate()
  const estado = useLocation().state
  const mensajeRegistro = typeof estado === 'object' && estado !== null && 'mensaje' in estado && typeof estado.mensaje === 'string' ? estado.mensaje : ''
  const [error, setError] = useState('')
  const enviar = async (datos: FormularioLogin) => {
    setError('')
    try {
      const sesion = await iniciarSesion(datos)
      navegar(sesion.roles.includes('ADMINISTRADOR') ? '/admin' : sesion.roles.includes('RECEPCIONISTA') ? '/recepcion' : '/', { replace: true })
    } catch (fallo: unknown) {
      setError(extraerMensajeError(fallo))
    }
  }
  return <main className="container py-5">
    <div className="row justify-content-center"><div className="col-12 col-md-8 col-lg-5">
      <section className="card border-0 shadow">
        <div className="card-body p-4 p-md-5">
          <p className="text-primary fw-semibold mb-1">El Cam · Armenia, Quindío</p>
          <h1 className="h3 mb-4">Iniciar Sesión</h1>
          {mensajeRegistro && <div className="alert alert-success" role="status">{mensajeRegistro}</div>}
          {error && <div className="alert alert-danger" role="alert">{error}</div>}
          <form onSubmit={handleSubmit(enviar)} noValidate>
            <div className="mb-3">
              <label className="form-label" htmlFor="username">Correo electrónico</label>
              <input className={`form-control${errors.username ? ' is-invalid' : ''}`} id="username" type="email" autoComplete="username" {...register('username')} />
              {errors.username && <div className="invalid-feedback">{errors.username.message}</div>}
            </div>
            <div className="mb-4">
              <label className="form-label" htmlFor="password">Contraseña</label>
              <input className={`form-control${errors.password ? ' is-invalid' : ''}`} id="password" type="password" autoComplete="current-password" {...register('password')} />
              {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
            </div>
            <button className="btn btn-primary w-100" type="submit" disabled={isSubmitting}>{isSubmitting ? 'Ingresando...' : 'Ingresar'}</button>
          </form>
          <p className="text-center mt-4 mb-0">¿Aún no tienes cuenta? <Link to="/registro">Regístrate</Link></p>
        </div>
      </section>
    </div></div>
  </main>
}
