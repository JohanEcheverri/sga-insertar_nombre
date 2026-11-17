# Documentación del backend

El backend expone una API REST para la gestión del alojamiento. Está desarrollado con Java 25 y Spring Boot, y organiza las reglas de negocio independientemente de HTTP y de la persistencia.

## Estructura

El código fuente está en `src/main/java/co/uniquindio/edu/sga`.

| Paquete | Responsabilidad |
| --- | --- |
| `domain/entity` | Agregados que protegen invariantes y transiciones del negocio. |
| `domain/valueobject` | Tipos del dominio con validación y significado propio, como dinero, fechas de estancia e identificadores. |
| `domain/service` | Reglas que involucran varios objetos del dominio o cálculos de negocio. |
| `domain/repository` | Contratos de acceso a los agregados y consultas del dominio. |
| `application/usecase` | Operaciones de la aplicación. Coordinan dominio, repositorios y puertos externos. |
| `application/dto`, `command`, `result` | Modelos de entrada y salida de la aplicación y datos entre capas. |
| `application/port` | Contratos para servicios externos, por ejemplo notificaciones y codificación de contraseñas. |
| `infraestructure/rest` | Controladores HTTP, conversión de modelos REST y traducción de errores. |
| `infraestructure/security` | Configuración de Spring Security, autenticación JWT y adaptación del usuario autenticado. |
| `infraestructure/persistence/jpa` | Entidades JPA, repositorios y mapeadores para persistencia relacional. |
| `infraestructure/persistence/inmemory` | Adaptadores en memoria, útiles para pruebas y ejecución sin base de datos. |
| `infraestructure/config` | Configuración de beans del dominio y documentación OpenAPI. |

## Flujo de una solicitud

1. Un controlador REST recibe la solicitud, valida sus campos con Bean Validation y la convierte a un comando de aplicación.
2. El caso de uso aplica la operación dentro de una transacción cuando corresponde. Carga los agregados mediante contratos de repositorio, coordina servicios de dominio y guarda los cambios.
3. El dominio comprueba invariantes y controla los cambios de estado. Las entidades JPA no reemplazan estas reglas: son modelos de almacenamiento.
4. El caso de uso entrega un resultado; un mapper lo convierte en DTO de respuesta y el controlador define el estado HTTP.
5. `GlobalExceptionHandler` transforma errores de validación, aplicación, dominio y autenticación en un formato JSON común (`ErrorResponse`).

### Ejemplo: crear una reserva

`ReservaController` obtiene el solicitante autenticado y comprueba las restricciones asociadas al canal de origen. Luego crea el comando para `CrearReservaUseCase`. El caso de uso valida el canal externo y la idempotencia, carga alojamiento y apartamento, comprueba grupo y disponibilidad, calcula la cotización, crea la reserva y su folio y persiste ambos. Finalmente devuelve el detalle; el controlador lo mapea a la respuesta REST.

## Límites de responsabilidad

- Mantener las reglas del negocio en `domain`, evitando dependencias de Spring, HTTP o JPA en esa capa.
- Usar casos de uso para coordinar operaciones; los controladores deben limitarse a traducir entre HTTP y aplicación.
- Depender de interfaces de repositorio y puertos desde la aplicación. Las implementaciones concretas viven en infraestructura.
- Mantener separados los DTO de API, los comandos/resultados de aplicación, las entidades del dominio y las entidades JPA. Los mapeadores hacen explícita la conversión entre representaciones.
- Validar la forma de la solicitud en los DTO de entrada y las invariantes del negocio en el dominio. Las comprobaciones de permisos deben realizarse en la configuración de seguridad y, cuando dependen del recurso o la operación, en el controlador/caso de uso correspondiente.
- Inyectar dependencias por constructor. Usar `Clock` cuando una regla dependa de la hora facilita un comportamiento determinista.

## Seguridad y errores

La API usa sesiones sin estado y tokens JWT. `SecurityConfig` define las rutas públicas y los roles requeridos; `JwtAuthenticationFilter` valida el token y `SolicitanteActualSecurityAdapter` expone la identidad actual mediante el puerto de aplicación. Las contraseñas se codifican con BCrypt. Los orígenes CORS se configuran mediante propiedades.

Las excepciones específicas se traducen a códigos HTTP en `GlobalExceptionHandler`. La respuesta común incluye fecha, estado, mensaje, ruta y, para errores de validación, los campos afectados. No se deben incluir secretos ni datos sensibles en esos mensajes.

## Persistencia y configuración

Los repositorios de dominio se implementan con adaptadores JPA o en memoria. JPA traduce entre entidades de persistencia y agregados; las consultas específicas de base de datos se mantienen en los repositorios Spring Data. En desarrollo se usa H2. Los detalles de ejecución, perfiles y variables de entorno están en el [README](../README.md).

## Contrato HTTP

La referencia completa de rutas, parámetros y esquemas está disponible en Swagger UI (`/swagger-ui.html`) y OpenAPI (`/v3/api-docs`) al ejecutar la aplicación. El archivo `docs/openapi.json` contiene una exportación del contrato y `docs/api-requests.http` incluye solicitudes de ejemplo.

## Pruebas

Las pruebas del backend se encuentran en `src/test/java`. `ReglasDominioTest` cubre reglas del dominio y `CasosDeUsoTest` verifica flujos de aplicación. Para ejecutar la suite: `./gradlew test`.
