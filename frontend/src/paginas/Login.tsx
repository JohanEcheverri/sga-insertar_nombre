import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { esquemaLogin } from '../esquemas/login'
import type { FormularioLogin } from '../esquemas/login'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import { iniciarSesion } from '../servicios/authServicio'
import { useAuth } from '../contexto/AuthContext'
import { mensajeDeError } from '../utilidades/errores'

export default function Login() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormularioLogin>({ resolver: zodResolver(esquemaLogin) })
  const { actualizarSesion } = useAuth()
  const navegar = useNavigate()
  const [error, setError] = useState('')
  const enviar = async (datos: FormularioLogin) => {
    setError('')
    try {
      actualizarSesion(await iniciarSesion(datos))
      navegar('/')
    } catch (fallo) {
      setError(mensajeDeError(fallo))
    }
  }
  return <main className="contenedor"><h1>Iniciar sesión</h1><form onSubmit={handleSubmit(enviar)}>
    <label>Correo<input type="email" autoComplete="username" {...register('username')} /></label><p>{errors.username?.message}</p>
    <label>Contraseña<input type="password" autoComplete="current-password" {...register('password')} /></label><p>{errors.password?.message}</p>
    {error && <p role="alert">{error}</p>}<button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Ingresando…' : 'Ingresar'}</button>
  </form></main>
}
