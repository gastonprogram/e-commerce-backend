# ✅ Implementación JWT Completa

## 📋 Resumen de lo Implementado

### 1. ✅ Configuración JWT en application.properties

```properties
# JWT Configuration
jwt.secret=miClaveSecretaSuperSeguraParaJWT2024EcommerceAplicacionesInteractivas
jwt.expiration=86400000  # 24 horas en milisegundos
```

**Ubicación:** `src/main/resources/application.properties`

**Explicación:**

- `jwt.secret`: Clave secreta para firmar los tokens (mínimo 32 caracteres para HS256)
- `jwt.expiration`: Tiempo de vida del token en milisegundos (86400000 ms = 24 horas)

---

### 2. ✅ Filtro JWT agregado en SecurityConfig

```java
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

**Ubicación:** `SecurityConfig.java` línea 75

**Explicación:**

- Este filtro intercepta TODAS las peticiones HTTP
- Valida el token JWT del header `Authorization`
- Si el token es válido, autentica al usuario automáticamente
- Si no hay token o es inválido, la petición continúa sin autenticación
- Se ejecuta ANTES del filtro estándar de Spring Security

---

### 3. ✅ Generación de Token en AuthenticationService

```java
public String authenticate(LoginRequest request) {
    // 1. Valida las credenciales
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));

    // 2. Obtiene el usuario
    Usuario user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();

    // 3. Extrae los roles
    Set<String> roles = user.getAuthorities().stream()
        .map(grantedAuthority -> grantedAuthority.getAuthority())
        .collect(Collectors.toSet());

    // 4. Genera y retorna el token JWT
    return jwtUtil.generateToken(user.getEmail(), roles);
}
```

**Ubicación:** `AuthenticationService.java` método `authenticate()`

**Flujo:**

1. **Autenticación**: Valida email y contraseña contra la base de datos
2. **Carga del usuario**: Obtiene el usuario completo de la BD
3. **Extracción de roles**: Convierte las authorities a Set<String> (ej: "ROLE_USER")
4. **Generación del token**: Crea el JWT con email, roles y fecha de expiración
5. **Retorno**: Envía el token al cliente para que lo use en futuras peticiones

---

## 🔄 Flujo Completo de Autenticación y Autorización

### Paso 1: Registro de Usuario

```
Cliente → POST /api/auth/register
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "password": "password123"
}
```

**Proceso:**

1. `AuthController` recibe la petición
2. `AuthenticationService.register()` es llamado
3. Se valida que el email no exista
4. Se crea un nuevo usuario con:
   - Contraseña encriptada (BCrypt)
   - Rol USER por defecto
5. Se guarda en la base de datos
6. Responde: "User registered successfully"

---

### Paso 2: Login

```
Cliente → POST /api/auth/login
{
  "email": "juan@example.com",
  "password": "password123"
}
```

**Proceso:**

1. `AuthController` recibe la petición
2. `AuthenticationService.authenticate()` es llamado
3. `AuthenticationManager` valida las credenciales:
   - Usa `CustomUserDetailsService` para cargar el usuario
   - Compara la contraseña con BCrypt
4. Si es válido, genera el token JWT con:
   - Subject (sub): email del usuario
   - Roles: "ROLE_USER" o "ROLE_ADMIN"
   - Fecha de emisión (iat)
   - Fecha de expiración (exp): 24 horas
5. Firma el token con la clave secreta
6. Responde con el token: `"eyJhbGciOiJIUzI1NiJ9..."`

---

### Paso 3: Petición Autenticada

```
Cliente → GET /api/pedidos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Proceso:**

1. La petición llega al servidor
2. `JwtFilter` intercepta la petición
3. Extrae el token del header `Authorization`
4. `JwtUtil.validateToken()` valida:
   - Que la firma sea correcta
   - Que no haya expirado
5. Si es válido:
   - Extrae el email y roles del token
   - Crea un `Authentication` con las authorities
   - Lo guarda en el `SecurityContext`
6. Spring Security evalúa las reglas de autorización:
   - ¿El endpoint requiere autenticación? ✅
   - ¿El usuario tiene el rol necesario? ✅
7. Si pasa las validaciones → Ejecuta el controlador
8. Si falla → Responde 401 (sin token) o 403 (sin permisos)

---

## 🔐 Componentes del Sistema JWT

### JwtUtil.java

**Responsabilidades:**

- ✅ Generar tokens JWT
- ✅ Validar tokens JWT
- ✅ Extraer información del token (email, roles)

**Métodos principales:**

```java
generateToken(email, roles)  // Crea el token
validateToken(token)          // Valida si es correcto y no expiró
getUsername(token)            // Extrae el email
getRoles(token)               // Extrae los roles
```

---

### JwtFilter.java

**Responsabilidades:**

- ✅ Interceptar todas las peticiones HTTP
- ✅ Validar el token JWT
- ✅ Autenticar al usuario automáticamente

**Flujo:**

```
1. Recibe la petición
2. Busca el header "Authorization"
3. Extrae el token (después de "Bearer ")
4. Valida el token con JwtUtil
5. Si es válido:
   - Carga las authorities
   - Crea Authentication
   - Lo guarda en SecurityContext
6. Continúa con la petición
```

---

### SecurityConfig.java

**Responsabilidades:**

- ✅ Configurar reglas de autorización
- ✅ Agregar el filtro JWT
- ✅ Configurar CORS
- ✅ Configurar encriptación de contraseñas

**Reglas implementadas:**

```java
// Públicos
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

// Solo ADMIN
.requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
.requestMatchers("/api/admin/**").hasRole("ADMIN")

// Usuarios autenticados
.requestMatchers(HttpMethod.POST, "/api/productos").authenticated()
.requestMatchers("/api/pedidos/**").authenticated()
```

---

### AuthenticationService.java

**Responsabilidades:**

- ✅ Registrar nuevos usuarios
- ✅ Autenticar usuarios (login)
- ✅ Generar tokens JWT

**Métodos:**

```java
register(RegisterRequest)     // Crea nuevo usuario con rol USER
authenticate(LoginRequest)    // Valida credenciales y genera token
```

---

### CustomUserDetailsService.java

**Responsabilidades:**

- ✅ Cargar usuarios desde la base de datos
- ✅ Usado por Spring Security durante la autenticación

**Método:**

```java
loadUserByUsername(email)  // Busca usuario por email
```

---

## 🎯 Características del Sistema

### ✅ Seguridad

- **Contraseñas encriptadas**: BCrypt con salt automático
- **Tokens firmados**: HMAC SHA-256 con clave secreta
- **Tokens con expiración**: 24 horas de validez
- **Stateless**: No usa sesiones, todo en el token
- **CORS configurado**: Solo frontend autorizado

### ✅ Autorización por Roles

- **USER**: Puede comprar, crear productos, ver pedidos
- **ADMIN**: Puede eliminar, acceder a rutas administrativas
- **Endpoints públicos**: Ver productos sin autenticación

### ✅ Buenas Prácticas

- **Separación de responsabilidades**: Cada clase tiene un propósito
- **DTOs**: No expone entidades directamente
- **Manejo de excepciones**: GlobalExceptionHandler
- **Inyección de dependencias**: Constructor injection
- **Configuración externa**: Properties en application.properties

---

## 📊 Estructura del Token JWT

```
Header (encabezado)
{
  "alg": "HS256",  // Algoritmo de firma
  "typ": "JWT"     // Tipo de token
}

Payload (datos)
{
  "sub": "juan@example.com",     // Subject: email del usuario
  "roles": "ROLE_USER",           // Roles del usuario
  "iat": 1698432000,              // Issued At: fecha de creación
  "exp": 1698518400               // Expiration: fecha de expiración
}

Signature (firma)
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**Resultado final:**

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE2OTg0MzIwMDAsImV4cCI6MTY5ODUxODQwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

---

## 🧪 Próximos Pasos para Testing

1. **Inicia la aplicación:**

   ```bash
   ./mvnw spring-boot:run
   ```

2. **Abre Postman** y sigue la guía: `TESTING_JWT_POSTMAN.md`

3. **Crea un usuario** (registro)

4. **Haz login** y obtén el token

5. **Prueba los endpoints** con y sin el token

6. **Crea un usuario ADMIN** y prueba endpoints administrativos

---

## ✅ Checklist de Implementación

- [x] Configuración JWT en application.properties
- [x] JwtUtil para generar y validar tokens
- [x] JwtFilter para interceptar peticiones
- [x] SecurityConfig con reglas de autorización
- [x] AuthenticationService genera tokens en login
- [x] CustomUserDetailsService carga usuarios
- [x] Usuario implementa UserDetails con roles
- [x] Role enum con USER y ADMIN
- [x] Filtro JWT agregado a la cadena de seguridad
- [x] CORS configurado para el frontend
- [x] DTOs para requests y responses
- [x] Documentación completa

---

## 🎉 ¡Sistema JWT Completo!

Todo está listo para probar. Sigue la guía `TESTING_JWT_POSTMAN.md` para testear cada endpoint.

**Recuerda:**

- El token expira en 24 horas
- Los usuarios nuevos son USER por defecto
- Solo ADMIN puede eliminar recursos
- El token debe enviarse en cada petición protegida
