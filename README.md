# apirestdemo (API REST)

Servicio web RESTful de la evaluación práctica de DAM235. Construido con
Spring Boot, Spring Security y JWT. Lo consume la app Android "Inventario".

**Guía base:** "Guía práctica: Creación de WS API REST", Ing. Edenilson López

## Requisitos

- JDK 21
- Maven (o el wrapper `mvnw` incluido)
- IntelliJ IDEA (recomendado) o cualquier IDE con soporte Maven
- Postman para probar los endpoints
- Opcional: MariaDB, solo si se usa el perfil `mariadb`

## Lo que HAY QUE CAMBIAR según el entorno

### 1. Base de datos (obligatorio decidir)

El servidor MariaDB de la cátedra (`52.7.114.182:3306`, base `DBTemp`) estaba
apagado durante el desarrollo (timeout confirmado). Por eso el proyecto tiene
dos perfiles de Spring, que se eligen en
`src/main/resources/application.properties`:

```properties
spring.profiles.active=h2
```

| Perfil | Cuándo usarlo | Archivo |
|--------|---------------|---------|
| `h2` (por defecto) | No requiere instalar nada. BD en memoria cargada con `data.sql` | `application-h2.properties` |
| `mariadb` | Cuando se tenga una MariaDB disponible | `application-mariadb.properties` |

**Con H2:**
- Los datos se pierden al reiniciar la API: cuentas creadas, productos
  agregados y tokens revocados.
- Al arrancar siempre existen: el usuario de prueba y 2 productos.

**Para usar MariaDB:**
1. Crear la base y las tablas con el script SQL del anexo de la guía.
2. Cambiar a `spring.profiles.active=mariadb`.
3. Editar `application-mariadb.properties` con la URL, usuario y clave:

```properties
spring.datasource.url=jdbc:mariadb://HOST:3306/DBTemp
spring.datasource.username=USUARIO
spring.datasource.password=CLAVE
```

   Con la BD de la cátedra: `HOST` = `52.7.114.182`. Con MariaDB local
   (Docker): `localhost`.

### 2. Puerto y ruta (opcional)

En `application.properties`:

```properties
server.port=8085
server.servlet.context-path=/api/v1/demoapirestdam235
```

Si se cambian, hay que actualizar `BASE_URL` en la app Android.

### 3. Clave secreta del JWT

`JwtUtil.java` trae la clave escrita en el código (`secretKey`), como en la
guía. Es válido para la práctica, pero **no** debe usarse así en producción.

## Cómo ejecutarla

1. Abrir la carpeta `apirestdemo` en IntelliJ IDEA y esperar a que Maven
   descargue las dependencias.
2. Verificar `spring.profiles.active` (ver arriba).
3. Ejecutar la clase `ApirestdemoApplication`.
4. Verificar en la consola que Spring Boot arrancó en el puerto 8085.

URL base: `http://localhost:8085/api/v1/demoapirestdam235`

Desde terminal: `./mvnw spring-boot:run`

Comprobación rápida (debe responder 401 con el texto "No autorizado: Token no
es el correcto o no proporcionado"):

```bash
curl -i http://localhost:8085/api/v1/demoapirestdam235/products
```

## Usuario de prueba

- Correo: `mmartinez@temp.com`
- Contraseña: `Temp123.` (con el punto final)

## Endpoints

| Método | Ruta | Descripción | Token |
|--------|------|-------------|-------|
| POST | `/auth/login` | Inicia sesión y devuelve el JWT | No |
| POST | `/auth/register` | Crea una cuenta de usuario | No |
| POST | `/auth/logout` | Revoca el token actual | Sí |
| GET | `/products` | Lista los productos | Sí |
| POST | `/products` | Crea un producto | Sí |
| GET | `/products/{code}` | Consulta un producto | Sí |
| PUT | `/products/{code}` | Actualiza un producto | Sí |
| DELETE | `/products/{code}` | Elimina un producto | Sí |

El token se envía en el header `Authorization: Bearer <token>`.
Dura 30 minutos.

### Cuerpos de ejemplo

```json
// POST /auth/login
{ "user": "mmartinez@temp.com", "pass": "Temp123." }

// POST /auth/register
{ "name": "Ana", "lastName": "López", "email": "ana@temp.com", "password": "Clave123" }

// POST y PUT /products
{ "name": "Cuaderno", "status": true }
```

### Códigos de respuesta

| Endpoint | Códigos |
|----------|---------|
| `POST /auth/login` | 200 correcto, 401 credenciales incorrectas |
| `POST /auth/register` | 201 creado, 409 correo duplicado, 400 datos incompletos |
| `POST /auth/logout` | 200 token revocado |
| `GET /products` | 200 con lista, 204 sin productos, 401 sin token o revocado |
| `POST /products` | 201 creado, 401 |
| `GET/PUT/DELETE /products/{code}` | 200 correcto, 404 no existe, 401 |

## Cómo funciona la seguridad

1. `POST /auth/login` valida las credenciales con Spring Security y devuelve
   un JWT firmado (HS256).
2. `JwtFilter` intercepta cada petición, extrae el token del header
   `Authorization` y lo valida. Si falta o no es válido responde 401.
3. Las rutas públicas son `/auth/login` y `/auth/register`.
4. **Logout:** la clase `TokenBlacklist` (en `utilities`) guarda en memoria
   los tokens revocados y `JwtFilter` los rechaza. La lista se pierde al
   reiniciar la API.
5. Las contraseñas se guardan cifradas con BCrypt (prefijo `{bcrypt}`).

## Estructura

```
src/main/java/edu/sv/ues/dam235/apirestdemo/
├── configs/          SecurityConfig, JwtFilter, CustomerDetailServices
├── controllers/      AuthController, ProductController
├── dtos/             LoginDTO, TokenDTO, ProductsDTO, ...
├── entities/         User, Product
├── repositories/     UserRepository, ProductRepository
├── services/         AuthServices, ProductServices (interfaces)
├── implementations/  AuthServicesImpl, ProductsImpl
└── utilities/        JwtUtil, TokenBlacklist
src/main/resources/
├── application.properties
├── application-h2.properties
├── application-mariadb.properties
└── data.sql          Datos iniciales para H2
```

## Pruebas sugeridas en Postman

1. `POST /auth/login` con el usuario de prueba: copiar el `token`.
2. `GET /products` sin token: 401.
3. `GET /products` con `Authorization: Bearer <token>`: 200 y 2 productos.
4. `POST /products`, `PUT /products/{code}` y `DELETE /products/{code}`.
5. `POST /auth/register` con una cuenta nueva y luego login con ella.
6. `POST /auth/logout` y repetir `GET /products` con el mismo token: 401.

## Limitaciones conocidas

- La guía advierte que no es una implementación lista para producción.
- Credenciales y clave JWT en el código y los `.properties`.
- Tokens revocados en memoria (se pierden al reiniciar).
- `POST /products` no valida que `name` venga con contenido.
- CORS abierto (`*`) desde `JwtFilter`.
- Tráfico en `http`, sin cifrar.

## Problemas frecuentes

| Síntoma | Causa y solución |
|---------|------------------|
| Falla al arrancar por conexión a la BD | Perfil `mariadb` activo sin servidor disponible; usar `h2` |
| Puerto 8085 en uso | Otra instancia corriendo; detenerla o cambiar `server.port` |
| Error de Lombok / getters no encontrados | Activar *Annotation Processing* en IntelliJ |
| 401 con un token que antes servía | Token vencido (30 min), revocado con logout o API reiniciada |
| La app móvil no llega a la API | Revisar IP, misma WiFi y firewall (`sudo ufw allow 8085`) |
