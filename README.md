# GameInventory

Aplicación web en Spring Boot para gestionar un inventario de juegos (CRUD) con autenticación en memoria, subida simple de imágenes y vistas con Thymeleaf + Bootstrap.

## Resumen rápido
- CRUD completo (crear / ver / editar / eliminar) en memoria.
- Autenticación básica con usuarios en memoria y contraseñas hasheadas con BCrypt.
- Plantillas Thymeleaf en `src/main/resources/templates`.
- Recursos estáticos en `src/main/resources/static` (CSS e imágenes).

## Requisitos
- JDK 17
- Maven (se incluye wrapper)

## Ejecutar localmente
Windows PowerShell (desde la raíz del proyecto):
```powershell
.\mvnw.cmd spring-boot:run
```
Abrir en el navegador:
- Login: http://localhost:8080/login
- Index (tras login): http://localhost:8080/index

## Credenciales por defecto
- admin / admin123 (rol ADMIN)  
- user / user123 (rol USER)

## Endpoints y vistas principales
- GET `/login`, POST `/login` — autenticación
- GET `/registro`, POST `/registro` — crear cuenta
- GET `/index` — lista de juegos (requiere sesión)
- GET `/form-crear`, POST `/juegos/guardar` — crear juego
- GET `/form-editar?id={id}`, POST `/juegos/editar` — editar juego
- GET `/juegos/{id}` — ver detalle
- POST `/juegos/{id}/eliminar` — eliminar juego
- GET `/logout` — cerrar sesión

## Notas importantes
- Persistencia: datos en memoria (se pierden al reiniciar). Para persistencia entre reinicios integrar JPA + H2.
- Imágenes: se guardan en `src/main/resources/static/assets` (útil en desarrollo).
- Seguridad: autenticación simple en sesión; para producción usar Spring Security completo.

## Siguientes mejoras recomendadas
- Persistencia con Spring Data JPA (H2)
- Integrar Spring Security y autorización por roles
- Validación con `@Valid` y mensajes de error en Thymeleaf
- Guardar archivos fuera del JAR o en almacenamiento dedicado

## Estructura clave (resumen)
- `src/main/java/.../controller` — controladores MVC y de acciones
- `src/main/java/.../service` — lógica (GameService, UserService)
- `src/main/resources/templates` — vistas Thymeleaf
- `src/main/resources/static` — css / assets
