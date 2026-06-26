# Sistema de Ventas de Libros

Proyecto ejecutable en Spring Tool Suite / Spring Boot Dashboard.

## Stack

- Java 21
- Spring Boot 3
- Spring Security + JWT
- BCrypt
- Spring Data JPA
- MySQL
- Maven

## Credenciales iniciales

- Email: admin@ventaslibros.com
- Password: Admin12345

## Base de datos

Por defecto usa MySQL local:

- Host: localhost
- Puerto: 3306
- Base de datos: ventas_libros_db
- Usuario: root
- Password: root

Puedes levantar MySQL con Docker:

```bash
docker compose up -d mysql
```

## Ejecutar en Spring Tool Suite

1. Abrir Spring Tool Suite.
2. File > Import > Existing Maven Projects.
3. Seleccionar la carpeta `ventas-libros`.
4. Esperar a que Maven descargue dependencias.
5. Ejecutar `VentasLibrosApplication.java` como Spring Boot App.

## Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@ventaslibros.com",
  "password": "Admin12345"
}
```

Luego usar el token con:

```http
Authorization: Bearer TU_TOKEN
```

## Endpoints principales

### Libros

- GET /api/libros
- GET /api/libros/{id}
- GET /api/libros/categoria/{categoriaId}
- POST /api/libros
- PUT /api/libros/{id}
- DELETE /api/libros/{id}

### Categorías

- GET /api/categorias

### Clientes

- GET /api/clientes
- POST /api/clientes

### Ventas

- GET /api/ventas
- GET /api/ventas/{id}
- POST /api/ventas
- PATCH /api/ventas/{id}/anular

## Reglas de negocio

- No se elimina físicamente un libro; se marca como eliminado e INACTIVO.
- El ISBN no puede repetirse.
- El libro debe pertenecer a una categoría activa.
- El stock no puede ser negativo.
- Una venta descuenta stock.
- Una venta anulada devuelve stock.
- Las respuestas usan el formato estándar: `exito`, `mensaje`, `datos`.
