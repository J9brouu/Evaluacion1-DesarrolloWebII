# Evaluación — Proyecto: GameInventory (Desarrollo Web II)

## Resumen
Esta documentación resume el proyecto desarrollado para la evaluación práctica. El proyecto es una aplicación web Java (Spring Boot + Thymeleaf + JPA) para gestionar un inventario de juegos, usuarios y pedidos.

- Stack: Java 17, Spring Boot (MVC), Thymeleaf, Spring Data JPA, Maven.
- Estructura principal: controladores (`controller`), servicios (`service.impl`), DAOs (`dao`), entidades (`entity`) y plantillas Thymeleaf en `src/main/resources/templates`.

---

## Requisitos implementados (síntesis)
- Registro y login por `email` + `password` (sesión HTTP con atributo `user`).
- Roles: `admin` y `user` (condicionan la UI y permisos en controladores).
- CRUD básico de `Juegos` (admin), con creación/edición/eliminación.
- Flujo de `Pedido`:
  - Formulario `/pedidos/crear` que lista juegos desde BD.
  - Validaciones de cantidad y stock (cliente + servidor).
  - Decremento de stock al crear pedido.
  - `/pedidos/mis` — pedidos del usuario; `/pedidos/listar` — pedidos globales (admin).
- Gestión de usuarios: perfil, edición propia (requiere `oldPassword` para cambiar contraseña), admin puede listar/editar/eliminar otros usuarios.
- Mensajes flash y validaciones básicas en las vistas.

---

## Cómo ejecutar (Windows / PowerShell)
Requisitos locales:
- JDK 17 instalado (el proyecto se probó con JDK 17).
- Maven wrapper incluido (`mvnw.cmd`).

Pasos:
1. Abrir PowerShell en la raíz del proyecto.
2. Compilar (sin tests):

```powershell
.\mvnw.cmd -DskipTests package
```

3. Ejecutar la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
# o con JAR
java -jar target\evaluacion1-desarrolloweb-0.0.1-SNAPSHOT.jar
```

4. Abrir en el navegador: `http://localhost:8080` (o `http://localhost:8080/index`).

Nota: si el puerto 8080 está en uso, cámbialo en `src/main/resources/application.properties`.

---

## Endpoints principales (resumen)
- `/` y `/login` (GET): formulario de login.
- `/login` (POST): procesa login (email, password).
- `/registro` (GET/POST): registrar usuario nuevo.
- `/index` (GET): listado principal de juegos.

Juegos
- `/juegos/{id}` (GET): detalle del juego.
- `/form-editar` (GET) y `/juegos/editar` (POST): editar juego (admin).
- `/juegos/guardar` (POST): crear nuevo juego.
- `/juegos/eliminar/{id}` (POST): eliminar juego (admin).

Pedidos
- `/pedidos/crear` (GET): formulario para crear pedido.
- `/pedidos/save` (POST): guarda pedido; valida stock, disminuye stock si aplica.
- `/pedidos/mis` (GET): pedidos del usuario en sesión.
- `/pedidos/listar` (GET): listado de todos los pedidos (admin).
- `/pedidos/{id}/delete` (POST): eliminar pedido (admin).

Usuarios
- `/user/profile` (GET): perfil del usuario en sesión.
- `/user/update` (GET/POST): editar perfil (admin puede editar a otros con `id` query param).
- `/user/list` (GET): listar usuarios (admin).
- `/user/{id}` (GET): ver datos de un usuario.
- `/user/{id}/delete` (POST): eliminar usuario (admin desde lista).
- `/user/delete` (POST): eliminar propia cuenta desde perfil.

APIs JSON (para consumo):
- `/user/json` (GET): lista de usuarios en JSON.
- `/pedidos/json` (GET): lista de pedidos en JSON.

Paths de plantillas relevantes:
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/detalle.html`
- `src/main/resources/templates/pedido/create.html`
- `src/main/resources/templates/pedido/list.html`
- `src/main/resources/templates/user/profile.html`
- `src/main/resources/templates/user/list.html`
- `src/main/resources/templates/user/read.html`

---

## Base de datos y datos de ejemplo
- El proyecto usa JPA (configuración en `application.properties`).
- El archivo `src/main/resources/import.sql` puede contener datos de arranque (si está activo en la configuración, se insertan al iniciar la app).
- Para probar, revisa `import.sql` o crea usuarios manualmente con `/registro`.

---

## Notas de seguridad y mejoras pendientes (importante para la evaluación)
- Contraseñas actualmente se guardan y comparan en texto plano. **Recomendado y obligatorio en producción/para mayor nota:** mover a BCrypt (Spring Security + `BCryptPasswordEncoder`).
- CSRF: si se añade Spring Security, los formularios POST necesitarán token CSRF.
- Validación: existe validación manual; se recomienda usar `@Valid` + Bean Validation para centralizar y mejorar mensajes.
- Evitar exponer entidades JPA completas en la sesión o en el modelo — usar DTOs para evitar problemas con lazy-loading y seguridad de datos.

---

## Errores comunes y soluciones rápidas
- `Thymeleaf TemplateInputException` → revisar expresiones `th:attr` mal formadas o plantillas vacías.
- `No static resource ...` (404) al hacer POST a una ruta: significa que no existe mapping para esa ruta; añadir `@PostMapping(...)` o corregir `th:action` para apuntar al handler correcto.
- Navbar muestra "Iniciar sesión" a pesar de estar logueado: asegurarse de que el controlador añade `model.addAttribute("user", sessionUser)` o que `GlobalModelAttributeAdvice` (ya implementado) expone `user` desde la sesión.

---

## Checklist de evaluación (sugerida)
Marca las que se han verificado manualmente:
- [x] Registro de usuario en BD
- [x] Login por email+password y sesión
- [x] Roles (admin/user) con UI condicionada
- [x] Crear pedido con juegos desde BD
- [x] Decremento de stock al crear pedido
- [x] "Mis pedidos" y listado global (admin)
- [x] Perfil y edición de usuario (con validación de contraseña antigua)
- [x] Eliminación de cuenta (confirmación via JS y POST)
- [ ] Contraseñas hasheadas (pendiente: BCrypt)
- [ ] Pruebas automatizadas (pendiente)

---

## Cómo contribuir o preparar entrega final
1. Implementar BCrypt para contraseñas (registro/login/update).
2. Incluir README con pasos y credenciales de prueba (si procede).
3. Añadir un par de tests integrados (por ejemplo, `UserController` y `PedidoController`).
4. Hacer una pasada de QA: probar flujos admin/usuario, verify CSRF si se añade Spring Security.

---

## Contacto / notas finales
- Ubicación de archivos clave: `src/main/java/.../controller`, `src/main/resources/templates`, `src/main/resources/application.properties`.
- Si quieres, genero un README corto en la raíz con estos pasos o aplico BCrypt ahora (indícame si continúa la modificación del proyecto).

---

*Documento generado automáticamente por la herramienta de asistencia — revisa y edítalo si quieres personalizar mensajes o credenciales de prueba antes de pegarlo en la wiki de GitHub.*
