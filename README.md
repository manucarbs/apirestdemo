# apirestdemo (API REST)

Servicio web RESTful de la evaluación práctica de DAM235. Construido con
Spring Boot, Spring Security y JWT. Lo consume la app Android "Inventario".

## Requisitos

- JDK 21
- Maven (o el wrapper `mvnw` incluido)
- IntelliJ IDEA (recomendado) o cualquier IDE con soporte Maven
- Postman para probar los endpoints
- Opcional: MariaDB, solo si se usa el perfil `mariadb`

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

