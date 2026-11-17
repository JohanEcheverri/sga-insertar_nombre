# SGA — Sistema de Gestión de Alojamiento

API para administrar alojamientos, apartamentos, temporadas y tarifas, reservas, folios, cargos y políticas de cancelación. El alojamiento del catálogo es El Cam, en Lloró, Chocó.

## Arquitectura

El backend organiza el código en capas hexagonales:

- `domain/`: agregados, objetos de valor, reglas y servicios Java puros.
- `application/`: casos de uso, comandos, resultados, DTOs y puertos.
- `infraestructure/persistence/`: adaptadores JPA e implementaciones en memoria.
- `infraestructure/rest/`: controladores, mapeadores y errores HTTP.
- `infraestructure/config/`: configuración de Spring, servicios del dominio y OpenAPI.
- `frontend/src/`: modelos, servicios HTTP, esquemas Zod, componentes, páginas, rutas y contexto de sesión.

La estructura, responsabilidades y flujo de trabajo del backend se describen en [Documentación del backend](docs/backend.md).

## Requisitos

- Java 25.
- Node.js 22.x para el frontend (Node 22 o superior compatible).
- npm incluido con Node.

## Ejecución local

En la raíz del repositorio, inicia el backend:

```powershell
./gradlew bootRun
```

La API queda en `http://localhost:8080/api`. En desarrollo, H2 guarda la base en `data/sga.mv.db` y conserva usuarios y reservas entre reinicios; `data.sql` carga el catálogo inicial de forma idempotente. El directorio `data/` está excluido de Git. Se puede sustituir la URL local definiendo `SPRING_DATASOURCE_URL`. La documentación servida por Springdoc está en `http://localhost:8080/swagger-ui.html` y `http://localhost:8080/v3/api-docs`.

En otra terminal, inicia el frontend:

```powershell
cd frontend
npm install
npm run dev
```

Copia `frontend/.env.example` a `frontend/.env.local` y ajusta `VITE_API_URL` si la API no usa la URL local predeterminada.

Antes de iniciar el backend, copia `src/main/resources/application-secrets.properties.example` a `src/main/resources/application-secrets.properties`. El archivo local está ignorado por Git y excluido del JAR. El login es `POST /api/auth/login`, con `username` (correo) y `password`; `POST /api/auth/registro` crea siempre un usuario `HUESPED`. Envía el token como `Authorization: Bearer <token>` en las rutas protegidas.

## Datos y autenticación de prueba

El catálogo inicial incluye cuatro apartamentos Standard y Premium, tarifas base y de temporada, y la política versionada `el-cam-v1`. Los identificadores fijos de catálogo aparecen en `src/main/resources/data.sql`.

Los usuarios de desarrollo se crean si su correo no existe, con contraseñas BCrypt; se deshabilitan en el perfil `prod`.

| Rol | Correo | Contraseña |
|---|---|---|
| ADMINISTRADOR | `admin@sga.local` | `AdminSga2026` |
| RECEPCIONISTA | `recepcion@sga.local` | `RecepcionSga2026` |
| PERSONAL_SERVICIO | `servicio@sga.local` | `ServicioSga2026` |
| HUESPED | `huesped@sga.local` | `HuespedSga2026` |

## Despliegue

El `Dockerfile` construye el JAR con Gradle y ejecuta Java 25. Activa el perfil `prod` con `SPRING_PROFILES_ACTIVE=prod` y configura `SGA_CORS_ALLOWED_ORIGINS`, `SGA_JWT_SECRET` (mínimo 32 bytes) y `SGA_ADMIN_*` para el entorno. Si se proporcionan nombre, correo y clave del administrador, se crea al iniciar cuando ese correo no existe. El secreto local no se incluye en el JAR.

## Artefactos API

- Especificación OpenAPI exportada: `docs/openapi.json` (también disponible desde `/v3/api-docs`).
- Flujo HTTP: `docs/api-requests.http`.
- Reescritura SPA de Vercel: `frontend/vercel.json`.
