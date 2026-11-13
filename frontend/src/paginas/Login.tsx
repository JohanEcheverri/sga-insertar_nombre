import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { esquemaLogin } from '../esquemas/login'
import type { FormularioLogin } from '../esquemas/login'
export default function Login() { const { register, handleSubmit, formState: { errors } } = useForm<FormularioLogin>({ resolver: zodResolver(esquemaLogin) }); return <main className="contenedor"><h1>Iniciar sesión</h1><form onSubmit={handleSubmit(() => undefined)}><label>Correo<input type="email" {...register('correo')} /></label><p>{errors.correo?.message}</p><label>Clave<input type="password" {...register('clave')} /></label><p>{errors.clave?.message}</p><button type="submit">Ingresar</button></form></main> }
