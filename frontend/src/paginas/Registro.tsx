import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from 'react-router'
import { registroSchema } from '../esquemas/login'
import type { FormularioRegistro } from '../esquemas/login'
import { registrarHuesped } from '../servicios/authServicio'
import { extraerMensajeError, extraerErroresCampos } from '../utilidades/errores'

export default function Registro() {
  const { register, handleSubmit, setError, formState: { errors, isSubmitting } } = useForm<FormularioRegistro>({ resolver: zodResolver(registroSchema) })
  const [error, setErrorGeneral] = useState('')
  const navegar = useNavigate()
  const enviar = async ({ confirmarPassword: _confirmarPassword, ...datos }: FormularioRegistro) => {
    setErrorGeneral('')
    try {
      await registrarHuesped(datos)
      navegar('/login', { replace: true, state: { mensaje: 'Tu cuenta fue creada. Ya puedes iniciar sesión.' } })
    } catch (fallo: unknown) {
      for (const [campo, mensaje] of Object.entries(extraerErroresCampos(fallo))) {
        if (campo in datos) setError(campo as keyof typeof datos, { message: mensaje })
      }
      setErrorGeneral(extraerMensajeError(fallo))
    }
  }
  return <main className="container py-5">
    <div className="row justify-content-center"><div className="col-12 col-lg-8">
      <section className="card border-0 shadow"><div className="card-body p-4 p-md-5">
        <p className="text-primary fw-semibold mb-1">El Cam · Armenia, Quindío</p>
        <h1 className="h3 mb-4">Crear cuenta de huésped</h1>
        {error && <div className="alert alert-danger" role="alert">{error}</div>}
        <form onSubmit={handleSubmit(enviar)} noValidate>
          <div className="row g-3">
            {([['nombre', 'Nombre', 'text'], ['apellido', 'Apellido', 'text'], ['documento', 'Documento', 'text'], ['correo', 'Correo electrónico', 'email'], ['telefono', 'Teléfono', 'tel'], ['fechaNacimiento', 'Fecha de nacimiento', 'date'], ['password', 'Contraseña', 'password'], ['confirmarPassword', 'Confirmar contraseña', 'password']] as const).map(([campo, etiqueta, tipo]) => {
              const errorCampo = errors[campo]?.message
              return <div className="col-12 col-md-6" key={campo}>
                <label className="form-label" htmlFor={campo}>{etiqueta}</label>
                <input className={`form-control${errorCampo ? ' is-invalid' : ''}`} id={campo} type={tipo} autoComplete={campo === 'password' ? 'new-password' : undefined} {...register(campo)} />
                {errorCampo && <div className="invalid-feedback">{errorCampo}</div>}
              </div>
            })}
          </div>
          <button className="btn btn-primary w-100 mt-4" type="submit" disabled={isSubmitting}>{isSubmitting ? 'Creando cuenta...' : 'Crear cuenta'}</button>
        </form>
        <p className="text-center mt-4 mb-0">¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link></p>
      </div></section>
    </div></div>
  </main>
}
