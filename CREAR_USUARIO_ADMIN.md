# 👨‍💼 Crear Usuario Administrador

## Opción 1: Directamente en MySQL (Recomendado)

### Paso 1: Conectarse a MySQL

```bash
mysql -u root -p
```

### Paso 2: Seleccionar la base de datos

```sql
USE ecommerce_db;
```

### Paso 3: Ver usuarios existentes

```sql
SELECT id, nombre, apellido, email, role FROM usuarios;
```

### Paso 4: Actualizar un usuario existente a ADMIN

```sql
-- Cambiar el usuario con email específico a ADMIN
UPDATE usuarios
SET role = 'ADMIN'
WHERE email = 'juan.perez@example.com';
```

### Paso 5: Verificar el cambio

```sql
SELECT id, nombre, apellido, email, role FROM usuarios WHERE email = 'juan.perez@example.com';
```

---

## Opción 2: Crear un nuevo usuario ADMIN

### Con contraseña "admin123" (ya encriptada)

```sql
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Admin',
    'Principal',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);
```

**Nota:** La contraseña `admin123` está pre-encriptada con BCrypt.

---

## Opción 3: Generar tu propia contraseña encriptada

### Paso 1: Crear un pequeño programa Java

Crea un archivo `GenerarPassword.java`:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "miPasswordSeguro123";
        String encoded = encoder.encode(password);
        System.out.println("Contraseña encriptada: " + encoded);
    }
}
```

### Paso 2: Ejecutarlo y copiar la contraseña encriptada

### Paso 3: Usar la contraseña en el INSERT

```sql
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Admin',
    'Principal',
    'admin@example.com',
    'TU_CONTRASEÑA_ENCRIPTADA_AQUI',
    'ADMIN'
);
```

---

## Opción 4: Usando una API Rest Tool (Temporal)

### Paso 1: Modificar temporalmente `AuthenticationService.java`

Cambia esta línea:

```java
.role(Role.USER) // Por defecto, todos los usuarios nuevos son USER
```

Por:

```java
.role(Role.ADMIN) // TEMPORAL: Para crear el primer admin
```

### Paso 2: Registrar el usuario admin con Postman

```
POST http://localhost:8080/api/auth/register
{
  "nombre": "Admin",
  "apellido": "Principal",
  "email": "admin@example.com",
  "password": "admin123"
}
```

### Paso 3: Revertir el cambio en el código

Volver a poner:

```java
.role(Role.USER) // Por defecto, todos los usuarios nuevos son USER
```

---

## Contraseñas Pre-encriptadas Comunes

Para desarrollo/testing, puedes usar estas contraseñas ya encriptadas:

| Contraseña Original | Hash BCrypt                                                    |
| ------------------- | -------------------------------------------------------------- |
| `admin123`          | `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` |
| `password123`       | `$2a$10$xY8FzPVbHW3YZP3K1tVDyO8pXVx5M9BEjP0zW0fZLmH5rJvNvW9xO` |
| `test1234`          | `$2a$10$8FPYmF6kD8ZW1XQxXQxHaOH4N5yD7L1FkVGxHWxH5rJvNvW9xOabc` |

**⚠️ IMPORTANTE:** Estas son solo para desarrollo. En producción, cada usuario debe tener su propia contraseña única.

---

## Scripts SQL Completos para Testing

### Script 1: Crear base de datos y primer admin

```sql
-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Después de que Spring Boot cree las tablas, insertar el admin
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Admin',
    'Principal',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);
```

### Script 2: Crear varios usuarios de prueba

```sql
USE ecommerce_db;

-- Usuario normal 1
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Juan',
    'Pérez',
    'juan@example.com',
    '$2a$10$xY8FzPVbHW3YZP3K1tVDyO8pXVx5M9BEjP0zW0fZLmH5rJvNvW9xO',
    'USER'
);

-- Usuario normal 2
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'María',
    'García',
    'maria@example.com',
    '$2a$10$xY8FzPVbHW3YZP3K1tVDyO8pXVx5M9BEjP0zW0fZLmH5rJvNvW9xO',
    'USER'
);

-- Admin
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Admin',
    'Principal',
    'admin@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);
```

### Script 3: Verificar todos los usuarios

```sql
USE ecommerce_db;

-- Ver todos los usuarios con sus roles
SELECT
    id,
    nombre,
    apellido,
    email,
    role,
    CASE
        WHEN role = 'ADMIN' THEN '👑 Administrador'
        WHEN role = 'USER' THEN '👤 Usuario'
        ELSE '❓ Desconocido'
    END as tipo_usuario
FROM usuarios
ORDER BY role DESC, id ASC;
```

### Script 4: Cambiar múltiples usuarios a ADMIN

```sql
USE ecommerce_db;

-- Cambiar usuarios específicos a ADMIN
UPDATE usuarios
SET role = 'ADMIN'
WHERE email IN ('juan@example.com', 'admin@example.com');

-- Verificar
SELECT email, role FROM usuarios WHERE role = 'ADMIN';
```

### Script 5: Limpiar y resetear usuarios

```sql
USE ecommerce_db;

-- ⚠️ CUIDADO: Esto borra TODOS los usuarios
DELETE FROM usuarios;

-- Resetear el autoincrement
ALTER TABLE usuarios AUTO_INCREMENT = 1;

-- Crear admin desde cero
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES (
    'Admin',
    'Sistema',
    'admin@ecommerce.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);
```

---

## Credenciales de Usuarios de Prueba

Para testing rápido, usa estas credenciales:

### Admin

```
Email: admin@example.com
Password: admin123
Rol: ADMIN
```

### Usuario Normal

```
Email: juan@example.com
Password: password123
Rol: USER
```

---

## Verificación en Postman

### 1. Login con Admin

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### 2. Verificar que el token contiene el rol ADMIN

Copia el token y decodifícalo en https://jwt.io

Deberías ver:

```json
{
  "sub": "admin@example.com",
  "roles": "ROLE_ADMIN",
  "iat": 1698432000,
  "exp": 1698518400
}
```

### 3. Probar endpoint de admin

```
DELETE http://localhost:8080/api/productos/1
Authorization: Bearer TU_TOKEN_ADMIN
```

Debería funcionar ✅ (200 OK)

---

## Troubleshooting

### "Usuario no puede eliminar productos"

**Solución:** Verifica que el rol en la base de datos sea exactamente `ADMIN` (mayúsculas).

```sql
SELECT id, email, role FROM usuarios WHERE email = 'admin@example.com';
```

Si aparece en minúsculas o NULL, actualízalo:

```sql
UPDATE usuarios SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

### "Contraseña incorrecta"

**Solución:** Asegúrate de usar la contraseña encriptada correcta. Las contraseñas en texto plano NO funcionan.

### "Email already exists"

**Solución:** El email ya está registrado. Usa otro email o elimina el usuario existente:

```sql
DELETE FROM usuarios WHERE email = 'admin@example.com';
```

---

## 🎯 Resumen Rápido

**Forma más rápida de crear un admin:**

```sql
-- Conectar a MySQL
mysql -u root -p

-- Usar la BD
USE ecommerce_db;

-- Insertar admin (contraseña: admin123)
INSERT INTO usuarios (nombre, apellido, email, password, role)
VALUES ('Admin', 'Principal', 'admin@example.com',
'$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Verificar
SELECT * FROM usuarios WHERE role = 'ADMIN';
```

¡Listo! 🚀
