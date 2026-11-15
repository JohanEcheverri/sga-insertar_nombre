# SGA — Sistema de Gestión de Alojamiento

API para administrar alojamientos, apartamentos, temporadas y tarifas, reservas, folios, cargos y políticas de cancelación. El alojamiento de ejemplo del catálogo es Triquiñuelas, en Salento, Quindío.

## Arquitectura

El backend organiza el código en capas hexagonales:

- `domain/`: agregados, objetos de valor, reglas y servicios Java puros.
- `application/`: casos de uso, comandos, resultados, DTOs y puertos.
- `infraestructure/persistence/`: adaptadores JPA e implementaciones en memoria.
- `infraestructure/rest/`: controladores, mapeadores y errores HTTP.
- `infraestructure/config/`: configuración de Spring, servicios del dominio y OpenAPI.
- `frontend/src/`: modelos, servicios HTTP, esquemas Zod, componentes, páginas, rutas y contexto de sesión.

## Requisitos

- Java 25.
- Node.js 22.x para el frontend (Node 22 o superior compatible).
- npm incluido con Node.

## Ejecución local

En la raíz del repositorio, inicia el backend:

```powershell
./gradlew bootRun
```

La API queda en `http://localhost:8080/api`. H2 usa una base en memoria y `data.sql` carga el catálogo al iniciar. La documentación servida por Springdoc está en `http://localhost:8080/swagger-ui.html` y `http://localhost:8080/v3/api-docs`.

En otra terminal, inicia el frontend:

```powershell
cd frontend
npm install
npm run dev
```

Copia `frontend/.env.example` a `frontend/.env.local` y ajusta `VITE_API_URL` si la API no usa la URL local predeterminada.

## Datos y autenticación de prueba

El catálogo inicial incluye cuatro apartamentos, tarifas base y de temporada, y la política versionada `triquinuelas-v1`. Los identificadores fijos de catálogo aparecen en `src/main/resources/data.sql`.

El repositorio todavía no configura un proveedor de inicio de sesión ni credenciales demo. Las operaciones que requieren identidad esperan que el contenedor web proporcione un `Principal` con usuario y rol; no hay usuario/clave de prueba que se pueda usar directamente con Basic o JWT. La implementación de JWT queda pendiente. El registro HTTP de `docs/api-requests.http` sirve como guion y requiere un entorno de autenticación para completar operaciones internas.

## Despliegue

El `Dockerfile` construye el JAR con Gradle y ejecuta Java 25. Activa el perfil `prod` con `SPRING_PROFILES_ACTIVE=prod` y configura `SGA_CORS_ALLOWED_ORIGINS`, `SGA_JWT_SECRET` y las variables `SGA_ADMIN_*` según el entorno de despliegue. La integración de JWT y la provisión del usuario administrador aún deben completarse en backend.

## Artefactos API

- Especificación OpenAPI exportada: `docs/openapi.json` (también disponible desde `/v3/api-docs`).
- Flujo HTTP: `docs/api-requests.http`.
- Reescritura SPA de Vercel: `frontend/vercel.json`.
