# 🧪 Testing JWT con Postman

Esta guía te ayudará a probar el sistema de autenticación y autorización JWT con Postman.

## 📋 Configuración Previa

### 1. Asegúrate de que la aplicación esté corriendo

```bash
# Desde la raíz del proyecto
./mvnw spring-boot:run
```

La aplicación debería estar corriendo en: `http://localhost:8080`

### 2. Verifica que la base de datos esté activa

- MySQL debe estar corriendo en `localhost:3306`
- La base de datos `ecommerce_db` debe existir

---

## 🔐 Paso 1: Registrar un Usuario

### Request

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

### Body (JSON)

```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@example.com",
  "password": "password123"
}
```

### Response Esperada (200 OK)

```json
"User registered successfully"
```

**Importante:** El usuario se registra automáticamente con el rol `USER`.

---

## 🔑 Paso 2: Hacer Login

### Request

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

### Body (JSON)

```json
{
  "email": "juan.perez@example.com",
  "password": "password123"
}
```

### Response Esperada (200 OK)

```json
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuLnBlcmV6QGV4YW1wbGUuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE2OTg0MzIwMDAsImV4cCI6MTY5ODUxODQwMH0...."
```

**¡IMPORTANTE!** Copia este token JWT completo. Lo usarás en los siguientes pasos.

---

## 📦 Paso 3: Probar Endpoint Público (sin token)

### Ver todos los productos (GET)

### Request

```
GET http://localhost:8080/api/productos
```

### Response Esperada (200 OK)

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1000.0,
    "stock": 5
  }
]
```

**✅ Este endpoint funciona sin autenticación** porque en `SecurityConfig` lo configuramos como público:

```java
.requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
```

---

## 🔒 Paso 4: Probar Endpoint Autenticado (con token)

### Crear un producto (POST)

### Request

```
POST http://localhost:8080/api/productos
Content-Type: application/json
Authorization: Bearer TU_TOKEN_JWT_AQUI
```

**En Postman:**

1. Ve a la pestaña **Headers**
2. Agrega un nuevo header:
   - Key: `Authorization`
   - Value: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJq...` (tu token completo)
   - **IMPORTANTE:** Debe haber un espacio después de "Bearer"

### Body (JSON)

```json
{
  "name": "Mouse Gamer",
  "description": "Mouse RGB con 7 botones",
  "price": 50.0,
  "stock": 15,
  "imagen": "https://example.com/mouse.jpg",
  "categoria": {
    "id": 1
  }
}
```

### Response Esperada (200 OK)

```json
{
  "id": 2,
  "name": "Mouse Gamer",
  "description": "Mouse RGB con 7 botones",
  "price": 50.0,
  "stock": 15,
  "imagen": "https://example.com/mouse.jpg"
}
```

**✅ Funciona porque:**

- Enviaste un token válido
- El usuario autenticado (USER) puede crear productos

---

## ❌ Paso 5: Probar Endpoint de Admin (Usuario normal)

### Intentar eliminar un producto (DELETE)

### Request

```
DELETE http://localhost:8080/api/productos/1
Authorization: Bearer TU_TOKEN_JWT_AQUI
```

### Response Esperada (403 Forbidden)

```
Access Denied
```

**❌ Falla porque:**

- El usuario tiene rol `USER`
- Solo los `ADMIN` pueden eliminar productos
- Configurado así en `SecurityConfig`:
  ```java
  .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
  ```

---

## 👨‍💼 Paso 6: Crear un Usuario Admin

### Opción A: Directamente en la Base de Datos (Recomendado)

```sql
-- Conectarse a MySQL
mysql -u root -p

-- Usar la base de datos
USE ecommerce_db;

-- Actualizar un usuario existente a ADMIN
UPDATE usuarios
SET role = 'ADMIN'
WHERE email = 'juan.perez@example.com';

-- O crear un nuevo usuario admin (la contraseña debe estar encriptada con BCrypt)
-- Esta contraseña es "admin123" encriptada
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES ('Admin', 'Principal', 'admin@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'ADMIN');
```

### Opción B: Modificar temporalmente el código

En `AuthenticationService.java`, cambia temporalmente:

```java
.role(Role.ADMIN) // Cambiar de USER a ADMIN solo para crear el admin
```

Registra un usuario, luego vuelve a cambiar a `Role.USER`.

---

## 👑 Paso 7: Probar con Usuario Admin

### 1. Hacer login con el admin

```
POST http://localhost:8080/api/auth/login
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### 2. Copiar el nuevo token JWT

### 3. Eliminar un producto

```
DELETE http://localhost:8080/api/productos/1
Authorization: Bearer TOKEN_DEL_ADMIN
```

### Response Esperada (200 OK)

```
Producto eliminado exitosamente
```

**✅ Funciona porque el usuario tiene rol ADMIN**

---

## 🧪 Paso 8: Probar Otros Endpoints

### Ver pedidos (requiere autenticación)

```
GET http://localhost:8080/api/pedidos
Authorization: Bearer TU_TOKEN_JWT
```

### Crear un pedido (requiere autenticación)

```
POST http://localhost:8080/api/pedidos/checkout
Authorization: Bearer TU_TOKEN_JWT
Content-Type: application/json

{
  "usuarioId": 1,
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    }
  ]
}
```

### Acceder a ruta de admin (solo ADMIN)

```
GET http://localhost:8080/api/admin/usuarios
Authorization: Bearer TOKEN_DEL_ADMIN
```

---

## 🐛 Solución de Problemas Comunes

### Error: "401 Unauthorized"

**Causa:** No enviaste el token o el token es inválido.

**Solución:**

1. Verifica que el header `Authorization` esté presente
2. Verifica que el formato sea: `Bearer TOKEN` (con espacio)
3. Verifica que el token no haya expirado (24 horas)
4. Si expiró, haz login nuevamente para obtener un nuevo token

### Error: "403 Forbidden"

**Causa:** No tienes permisos suficientes.

**Solución:**

- Si necesitas permisos de admin, usa un usuario con rol `ADMIN`
- Verifica que el endpoint requiera el rol que tienes

### Error: "500 Internal Server Error"

**Causa:** Puede ser un problema con la base de datos o la aplicación.

**Solución:**

1. Revisa los logs de la aplicación en la consola
2. Verifica que la base de datos esté corriendo
3. Verifica que los datos del body sean válidos

### Token No Funciona

**Posibles causas:**

1. La clave secreta en `application.properties` cambió
2. El token expiró (válido por 24 horas)
3. El formato del header es incorrecto

**Solución:**

- Hacer login nuevamente para obtener un token fresco
- Verificar que el header sea: `Authorization: Bearer TOKEN`

---

## 📊 Resumen de Endpoints por Rol

| Endpoint                   | Método | Sin Auth | USER | ADMIN |
| -------------------------- | ------ | -------- | ---- | ----- |
| /api/auth/register         | POST   | ✅       | ✅   | ✅    |
| /api/auth/login            | POST   | ✅       | ✅   | ✅    |
| /api/productos (ver)       | GET    | ✅       | ✅   | ✅    |
| /api/productos (crear)     | POST   | ❌       | ✅   | ✅    |
| /api/productos (editar)    | PUT    | ❌       | ✅   | ✅    |
| /api/productos (eliminar)  | DELETE | ❌       | ❌   | ✅    |
| /api/pedidos/\*            | ALL    | ❌       | ✅   | ✅    |
| /api/categorias (ver)      | GET    | ✅       | ✅   | ✅    |
| /api/categorias (crear)    | POST   | ❌       | ✅   | ✅    |
| /api/categorias (editar)   | PUT    | ❌       | ✅   | ✅    |
| /api/categorias (eliminar) | DELETE | ❌       | ❌   | ✅    |
| /api/admin/\*              | ALL    | ❌       | ❌   | ✅    |

---

## 🔍 Verificar el Token JWT (Opcional)

Puedes decodificar tu token JWT en: https://jwt.io

**Ejemplo de token decodificado:**

```json
{
  "sub": "juan.perez@example.com",
  "roles": "ROLE_USER",
  "iat": 1698432000,
  "exp": 1698518400
}
```

- **sub**: Email del usuario
- **roles**: Rol del usuario (ROLE_USER o ROLE_ADMIN)
- **iat**: Fecha de emisión
- **exp**: Fecha de expiración

---

## ✅ Checklist de Testing

- [ ] Registrar un usuario
- [ ] Hacer login y obtener token
- [ ] Ver productos sin token (público)
- [ ] Crear producto con token (autenticado)
- [ ] Intentar eliminar producto con USER (debería fallar)
- [ ] Crear usuario ADMIN en la base de datos
- [ ] Login con ADMIN y obtener token
- [ ] Eliminar producto con ADMIN (debería funcionar)
- [ ] Crear un pedido con usuario autenticado
- [ ] Verificar que el token expira después de 24 horas

---

## 🚀 Tips para Postman

### Crear una Colección

1. Crea una colección llamada "E-commerce API"
2. Guarda todas tus requests allí
3. Organízalas en carpetas: Auth, Productos, Pedidos, Admin

### Usar Variables de Entorno

1. Crea un environment en Postman
2. Agrega estas variables:
   - `baseUrl`: `http://localhost:8080`
   - `token`: (aquí pegas el JWT después de login)
3. Usa `{{baseUrl}}/api/productos` en tus requests
4. Usa `{{token}}` en el header Authorization

### Pre-request Scripts (Avanzado)

Puedes automatizar la renovación del token cuando expire usando scripts de Postman.

---

¡Listo para testear! 🎉
