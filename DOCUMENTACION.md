# DevTickets – Documentación técnica

DevTickets es una plataforma de venta de boletos para eventos. Un usuario puede explorar el catálogo, elegir una función, seleccionar asientos en un mapa, reservarlos temporalmente, pagar (pago simulado) y consultar sus entradas.

El proyecto se compone de dos aplicativos independientes:

| Aplicativo         | Tecnología                                                                 | Carpetaria que                  |
| ------------------ | --------------------------------------------------------------------------- | -------------------------------- |
| Backend (API REST) | Java 21, Spring Boot 4.1, Spring Data JPA, Spring Security, JWT, MySQL/TiDB | `Backend/devtickets-backend`   |
| Frontend (SPA)     | Angular 21, TypeScript 5.9, RxJS, Signals                                   | `Frontend/devtickets-frontend` |

Otras carpetas: `Scripts/` (esquema SQL, migraciones y datos demo), `DatosDemo/` (eventos de ejemplo en CSV/JSON) y `Postman/` (colección para probar la API).

---

## 1. Arquitectura general y cómo interactúan

```
┌──────────────────────┐   HTTP/JSON + JWT    ┌──────────────────────┐    JDBC/TLS    ┌───────────────┐
│ Angular (puerto 4200)│ ───────────────────► │ Spring Boot (8080)   │ ─────────────► │ TiDB / MySQL  │
│ SPA en el navegador  │ ◄─────────────────── │ API REST /api/**     │ ◄───────────── │ Base de datos │
└──────────────────────┘                      └──────────────────────┘                └───────────────┘
```

- El frontend **no accede a la base de datos**: todo pasa por la API REST del backend (`environment.apiUrl = http://localhost:8080/api`).
- La comunicación es **JSON sobre HTTP**. Los nombres de los DTO del backend coinciden con las interfaces de `models/` del frontend.
- La autenticación usa **JWT sin estado (stateless)**: el login devuelve un token; Angular lo guarda en `localStorage` y un interceptor lo agrega en el header `Authorization: Bearer <token>` a cada petición.
- **CORS**: el backend solo acepta peticiones de los orígenes configurados en `app.cors.allowed-origins` (por defecto `http://localhost:4200`).

### Flujo principal de compra

1. `GET /api/eventos` y `GET /api/eventos/{id}`: el catálogo es público.
2. `GET /api/funciones/{id}/asientos`: mapa de asientos con disponibilidad (público).
3. `POST /api/auth/login` o `/registro`: devuelve el JWT.
4. `POST /api/ordenes` (autenticado): crea una orden **PENDIENTE** y reserva los asientos por un tiempo limitado (10 min por defecto). Máximo 8 asientos por orden.
5. `POST /api/ordenes/{id}/pago`: confirma el pago simulado; la orden pasa a pagada y se generan las entradas con su código.
6. `GET /api/ordenes`: "Mis entradas" del usuario.

Si la reserva expira, el backend libera los asientos automáticamente (`expirarPendientes` / `liberarReservasExpiradas`) y el frontend muestra una cuenta regresiva.

---

## 2. ¿Por qué estas tecnologías?

### Backend

| Tecnología                               | Motivo                                                                                                                                                                                                                                |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Java 21 + Spring Boot**           | Framework maduro y estándar para APIs empresariales. Autoconfiguración, inyección de dependencias y ecosistema completo (seguridad, datos, validación). Java 21 es LTS y permite `record` para DTOs.                            |
| **Spring Web MVC**                  | Exposición de endpoints REST con anotaciones simples.                                                                                                                                                                                |
| **Spring Data JPA / Hibernate**     | Mapea tablas a clases y evita SQL repetitivo; los repositorios se generan a partir de interfaces. Se usa `open-in-view=false` y `ddl-auto=none` para controlar las consultas y que el esquema solo lo modifiquen los scripts SQL. |
| **Spring Security + JWT (jjwt)**    | Seguridad estándar de la industria. JWT permite una API sin sesiones de servidor, ideal para una SPA y fácil de escalar. Las contraseñas se guardan con **BCrypt**.                                                          |
| **Bean Validation**                 | Valida las peticiones de forma declarativa (`@NotNull`, `@Size`, …) antes de llegar a la lógica de negocio.                                                                                                                     |
| **Lombok**                          | Reduce código repetitivo (getters/setters, constructores).                                                                                                                                                                           |
| **MySQL / TiDB Cloud**              | Base relacional compatible con MySQL; el modelo (órdenes, pagos, asientos) requiere transacciones e integridad referencial. Se conecta por TLS.                                                                                      |
| **Variables de entorno (`.env`)** | Secretos (BD, JWT) fuera del código fuente; ver `.env.example`.                                                                                                                                                                    |

### Frontend

| Tecnología                      | Motivo                                                                                                             |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| **Angular 21**             | Framework completo (routing, formularios, HTTP, DI) con estructura opinada, adecuada para aplicaciones que crecen. |
| **Componentes standalone** | Sin `NgModule`: cada componente declara sus dependencias en `imports`, menos configuración.                   |
| **Signals**                | Estado reactivo simple y eficiente (`signal`, `computed`) para la UI: asientos, selección, orden, etc.        |
| **RxJS**                   | Manejo de peticiones HTTP y flujos asíncronos (por ejemplo el temporizador con `interval`/`switchMap`).       |
| **TypeScript**             | Tipado estático; las interfaces de `models/` documentan el contrato con la API.                                 |
| **Reactive Forms**         | Formularios con validación en login, registro y pago.                                                             |
| **SCSS**                   | Estilos por componente con variables y anidación.                                                                 |
| **Vitest + jsdom**         | Pruebas unitarias rápidas (`ng test`).                                                                          |

---

## 3. Backend – tipos de archivo

Paquete base: `com.devtickets.devtickets_backend`. Sigue una **arquitectura por capas**:

```
Controller  →  Service  →  Repository  →  Base de datos
   (DTO)       (lógica)     (Entity/Model)
```

### 3.1 `DevticketsBackendApplication`

Punto de entrada (`@SpringBootApplication`). Arranca el servidor embebido y escanea los componentes.

### 3.2 `controller/` – Controladores

Clases `@RestController` que **exponen los endpoints HTTP**. Solo reciben la petición, validan el cuerpo (`@Valid`), delegan al servicio y devuelven un DTO. No contienen lógica de negocio.

- `AuthController` → `/api/auth/login`, `/api/auth/registro`
- `CatalogoController` → `/api/categorias`, `/api/eventos`, `/api/eventos/{id}`, `/api/funciones/{id}/asientos`
- `OrdenController` → `/api/ordenes` (crear, listar) y `/api/ordenes/{id}/pago`. Toma el usuario de `Authentication` (el correo del token).

### 3.3 `service/` – Servicios

Clases `@Service` con la **lógica de negocio**. Aquí se aplican las reglas y se manejan las transacciones (`@Transactional`).

- `AuthService`: registro, login, emisión del token.
- `CatalogoService`: consulta y arma los DTO del catálogo y el mapa de asientos.
- `OrdenService`: reglas de la compra: función programada y con venta abierta, asientos de la función y disponibles, sin duplicados, misma moneda, cálculo de totales, reserva con expiración, pago, generación de entradas y listado de órdenes.

### 3.4 `repository/` – Repositorios

Interfaces que extienden `JpaRepository`. Spring genera la implementación. Contienen consultas derivadas (`findByCorreoElectronico`) y `@Query` en JPQL, incluyendo operaciones `@Modifying` para reservar o liberar asientos en una sola sentencia (evita que dos usuarios tomen el mismo asiento).

### 3.5 `model/` – Entidades

Clases `@Entity` que representan las tablas: `Usuario`, `Rol`, `Categoria`, `Recinto`, `Evento`, `ImagenEvento`, `FuncionEvento`, `TipoEntrada`, `AsientoFuncion`, `OrdenCompra`, `DetalleOrden`, `Entrada`, `Pago`. Definen columnas, relaciones y constantes de estado (por ejemplo `ESTADO_PENDIENTE`), además de pequeños métodos de dominio como `ventaAbierta(...)`.

### 3.6 `dto/` – Data Transfer Objects

`record` de Java organizados por dominio (`auth`, `catalogo`, `compra`). Definen **qué datos entran y salen** de la API sin exponer las entidades. Los de entrada llevan anotaciones de validación (`CrearOrdenRequest`: `@NotEmpty`, `@Size(max = 8)`).

### 3.7 `security/` – Seguridad JWT

- `JwtService`: genera y valida tokens firmados con `app.jwt.secret`.
- `JwtAuthenticationFilter`: filtro (`OncePerRequestFilter`) que lee `Authorization: Bearer …`, valida el token, carga el usuario y lo registra en el `SecurityContext`.
- `UsuarioDetailsService`: carga el usuario y sus roles desde la BD para Spring Security.

### 3.7.1 `config/` – Configuración

- `SecurityConfig`: define la cadena de filtros: CSRF deshabilitado (API stateless), sesión `STATELESS`, CORS, 401 como respuesta por defecto, rutas públicas (login, registro, catálogo, mapa de asientos) y el resto autenticadas. También declara `PasswordEncoder` (BCrypt) y `AuthenticationManager`.

### 3.8 `exception/` – Manejo de errores

- `ApiException`: excepción de negocio con estado HTTP (no encontrado, conflicto, solicitud inválida).
- `GlobalExceptionHandler` (`@RestControllerAdvice`): convierte excepciones en respuestas JSON uniformes `{"error": "..."}`, y para validaciones `{"error": "...", "campos": {...}}`.

### 3.9 Recursos y archivos de proyecto

- `application.properties`: configuración parametrizada con variables de entorno (BD, JWT, CORS, minutos de reserva).
- `pom.xml`: dependencias y versiones (Maven). `mvnw`/`mvnw.cmd`: Maven Wrapper.
- `.env` / `.env.example`: variables locales; `run-local.ps1`: script para arrancar en local.

### 3.10 Scripts SQL (`/Scripts`)

- `schema.sql`: crea las tablas.
- `migracion_001_asientos.sql`, `migracion_002_reservas_temporales.sql`: cambios incrementales (asientos por función y reservas temporales).
- `datos_demo.sql`: datos de ejemplo.

---

## 4. Frontend – tipos de archivo

Ruta base: `Frontend/devtickets-frontend/src/app`.

```
app/
├── app.ts / app.html / app.scss   Componente raíz
├── app.config.ts                  Configuración global (providers)
├── app.routes.ts                  Rutas y guard
├── core/                          Lógica transversal (singleton)
│   ├── interceptors/
│   ├── guards/
│   └── services/
├── features/                      Pantallas por funcionalidad
│   ├── auth/ (login, registro)
│   └── tickets/ (event-list, mis-entradas)
├── shared/components/             Componentes reutilizables
└── models/                        Interfaces TypeScript
```

### 4.1 Archivos de arranque y configuración

- `main.ts`: arranca la aplicación (`bootstrapApplication`).
- `app.config.ts`: registra providers globales: router, `HttpClient` con el interceptor, idioma `es-MX`.
- `app.routes.ts`: tabla de rutas (`''`, `login`, `registro`, `mis-entradas`, comodín `**`).
- `angular.json`, `tsconfig*.json`, `package.json`: build, compilador y dependencias.
- `environments/environment.ts`: URL del backend.

### 4.2 Interceptores (`core/interceptors`)

Funciones `HttpInterceptorFn` que **procesan todas las peticiones HTTP**. `auth.interceptor.ts`:

- Agrega el header `Authorization: Bearer <token>` solo a las URLs del backend.
- Si recibe un **401** con sesión activa (token vencido o inválido), cierra la sesión.

### 4.3 Guards (`core/guards` y `app.routes.ts`)

Funciones que **deciden si se puede entrar a una ruta** (`canActivate`). El guard `soloAutenticados` protege `/mis-entradas`: si no hay sesión redirige a `/login`. (La carpeta `core/guards` está preparada para moverlo allí.)

### 4.4 Servicios (`core/services`)

Clases `@Injectable({ providedIn: 'root' })`, instancia única. Encapsulan la comunicación con la API y el estado compartido:

- `AuthService`: login, registro, logout, sesión en `localStorage` y signals `sesion` y `autenticado`; controla la expiración del token.
- `EventoService` y `CategoriaService`: consultas del catálogo.
- `CompraService`: detalle de evento, asientos, reservar, confirmar pago y mis órdenes.
- `FiltroCategoriasService`: estado compartido (signal) entre la barra de categorías y el listado.

### 4.5 Modelos (`models/`)

Interfaces TypeScript (`auth`, `categoria`, `evento`, `compra`) que reflejan los DTO del backend. Dan tipado a las respuestas HTTP.

### 4.6 Features / páginas (`features/`)

Componentes "contenedor" asociados a una ruta; orquestan servicios y estado:

- `auth/login`, `auth/registro`: formularios reactivos de acceso.
- `tickets/event-list`: catálogo, selección de función y asientos, reserva, cuenta regresiva y pago.
- `tickets/mis-entradas`: entradas compradas por el usuario.

Cada componente tiene tres archivos: `.ts` (lógica), `.html` (plantilla) y `.scss` (estilos).

### 4.7 Componentes compartidos (`shared/components`)

Componentes reutilizables sin lógica de negocio propia: `navbar`, `footer`, `categorias` y `mapa-asientos` (dibuja los asientos y emite la selección).

---

## 5. Seguridad: resumen del ciclo del JWT

1. El usuario envía credenciales a `/api/auth/login`.
2. `AuthService` (backend) valida contra la BD (BCrypt) y `JwtService` firma el token.
3. Angular guarda la sesión y calcula `expiraEn`.
4. Cada petición pasa por `authInterceptor`, que adjunta el token.
5. En el servidor, `JwtAuthenticationFilter` valida el token y establece el usuario; `SecurityConfig` decide si la ruta es pública o requiere autenticación.
6. Si el token expira, el 401 hace que el interceptor cierre sesión y el guard redirija al login.

---

## 6. Cómo ejecutar

**Base de datos:** ejecutar `Scripts/schema.sql`, las migraciones y opcionalmente `datos_demo.sql`.

**Backend:** copiar `.env.example` a `.env`, completar valores y ejecutar `run-local.ps1` o `./mvnw spring-boot:run` (puerto 8080).

**Frontend:** en `Frontend/devtickets-frontend`: `npm install` y `npm start` (puerto 4200).
