package com.example.evaluacion1_desarrolloweb.config;
/**
 * @author Sistema de Evaluacion Web
 * @version 1.0
 */
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.evaluacion1_desarrolloweb.service.impl.UsuarioServiceImpl;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UsuarioServiceImpl userDetailsService;
    public SecurityConfig(UsuarioServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    /**
     * Bean para el codificador de contraseñas
     * 
     * BCrypt es un algoritmo de cigrado de una sola via:
     * - Puedes convertir "micontraseña123" a "$2a$10$..."
     * - Pero no puedes convertir "$2a$10$..." de vuelta a "micontraseña123"
     * 
     * Esto protege las contraseñas incluso si alguien roba la base de datos+
     * 
     * ejemplo de uso en controladores:
     * <pre>
     * {@code
     * @Autowired
     * private PasswordEncoder passwordEncoder;
     * 
     * String passwordCifrada = passwordEncoder.encode("micontraseña123");
     * // Resultado: "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW"
     * }
     * </pre>
     * 
     * @return PasswordEncoder configurado con BCrypt (10 rondas)
     */
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 10 rondas de hashing (2^10 = 1024 iteraciones)
        // Más rondas = más seguro pero más lento
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Proveedor de autenticación que conecta:
     * - CustomUserDetailsService (carga usuarios de BD)
     * - PasswordEncoder (cifra y verifica contraseñas)
     * 
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager para procesar autenticaciones
     * (requerido en Spring Security 5.7+)
     * 
     * @param authConfig configuración de autenticación
     * @return AuthenticationManager configurado
     * @throws Exception si hay error en configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * @param http objeto HttpSecurity para configurar seguridad web
     * @return SecurityFilterChain configurado
     * @throws Exception si hay error en configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Registrar explícitamente el DaoAuthenticationProvider que usa tu UserDetailsService + PasswordEncoder
        http.authenticationProvider(authenticationProvider());

        // Permitir el H2 Console (solo para desarrollo)
        http.csrf(csrf -> csrf
            .ignoringRequestMatchers(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/h2-console/**"))
        );

        // Permitir que la consola H2 se renderice en iframes (same origin)
        http.headers(headers -> headers.frameOptions().sameOrigin());

        return http
        // =========================================
        // AUTORIZACIÓN: ¿Quién puede acceder a qué?
        // =========================================
        .authorizeHttpRequests(auth -> auth
            // 1. RECURSOS ESTÁTICOS: Acceso público (CSS, JS, imágenes)
            // CRÍTICO: Sin esto, la página de login se verá sin estilos ni scripts
            // EXPLICACIÓN: Los recursos en /css/**, /js/**, /images/** son públicos
            .requestMatchers(
                "/css/**",
                "/js/**",
                "/images/**",
                "/webjars/**",
                "/favicon.ico",
                "/error",
                "/h2-console/**"        // <- permitir acceso a H2 Console
            ).permitAll()

            // 2. PÁGINAS PÚBLICAS: Login y registro accesibles sin autenticar
            // EXPLICACIÓN: Los usuarios deben poder ver la página de login
            // Sin haber iniciado sesión
            .requestMatchers(
                "/",
                "/login",
                "/registro",
                "/user/guardar" // para procesar el formulario de registro
            ).permitAll()

            // 3. RUTAS ADMIN: Solo usuarios con rol ADMIN pueden acceder
            // Esto es la seguridad REAL (no solo ocultar botones en HTML)
            // hasRole() automaticamente agrega el prefijo "ROLE_"
            // Entonces "ADMIN" busca "ROLE_ADMIN" en la base de datos
            .requestMatchers(
                "/form-crear",
                "/usuarios/list",
                "/user/list",
                "/user/create",
                "/user/read",
                "/user/update",
                "/user/{id}",
                "/form-editar",
                "/juegos/crear",
                "/juegos/editar/**",
                "/juegos/guardar",
                "/juegos/eliminar/**",
                "/pedidos/listar",
                "/pedidos/delete/**",
                "/pedidos/edit/**",
                "/pedidos/json"
            ).hasRole("ADMIN")
            
            // 4. RUTAS COMPARTIDAS: Tanto USER como ADMIN pueden acceder
            .requestMatchers(
                "/pedidos/mis",
                "/pedidos/crear",
                "/pedidos/guardar",
                "/pedidos/save",
                "/user/settings",
                "/user/update"
            ).hasAnyRole("USER", "ADMIN")
            .requestMatchers(
                "/user/profile"
            ).authenticated()
            // 5. CUALQUIER OTRA RUTA: Requiere autenticación
            // (Puede ser Admin o User, pero debe haber iniciado sesión)
            .anyRequest().authenticated()
        )
        // ===========================================
        //    AUTENTICACIÓN: ¿Cómo se inicia sesión?
        // ===========================================
        .formLogin(login -> login
            // Página de login personalizada (nuestra plantilla de Thymeleaf)
            // Si no ponemos esto, Spring Security usa su propia página por defecto
            .loginPage("/login")

            // URL que procesa el formulario de login (POST /login)
            // Esta URL la maneja spring security automáticamente, NO necesitas crear un controlador
            // Por defecto espera parámetros: username y password
            .loginProcessingUrl("/login")

            // Parámetros del formulario (si quieres usar nombres diferentes)
            // Por defecto son "username" y "password"
            .usernameParameter("username") // Debe coincidir con el 'name' del input en el formulario
            .passwordParameter("password") // Debe coincidir con el 'name' del input en el formulario

            // Redirigir después de login exitoso
            // El "true" fuerza la redirección a esta URL siempre
            // Si fuera "false", redirige a la URL original que el usuario quería acceder
            .defaultSuccessUrl("/index", true)

            // Redirigir si falla el login
            // El parámetro "error=true" se puede usar en la plantilla para mostrar un mensaje
            .failureUrl("/login?error=true")

            // Permitir acceso a la página de login sin autenticar
            .permitAll()
        )

        // ==============================
        //      CONFIGURACIÓN LOGOUT
        // ==============================
        .logout(logout -> logout
            // URL para cerrar sesión (GET o POST /logout)
            // Spring Security la maneja automáticamente
            .logoutUrl("/logout")

            // Página a donde redirigir después de logout
            .logoutSuccessUrl("/login?logout=true")

            // Invalida la sesión HTTP (destruye JSESSIONID)
            // Esto borra toda la información de sesión del servidor
            .invalidateHttpSession(true)

            // Borra las cookies de autenticación del navegador
            .deleteCookies("JSESSIONID")
            
            // Limpiar atributos de sesión
            .clearAuthentication(true)

            // Permitir acceso a la URL de logout sin autenticar
            .permitAll()
        )

        // =====================================================
        // CSRF: Protección contra falsificación de peticiones
        // =====================================================
        // Habilitado por defecto - NO se recomienda deshabilitarlo
        //
        // ¿Qué es CSRF?
        // Un sitio malicioso engaña al navegador para enviar una petición
        // usando la sesión activa del usuario
        //
        // Analogía: Es como si alguien falsificara tu firma en un cheque.
        // El banco (servidor) debe verificar que la firma (token CSRF)
        // 
        // Solución: Spring inyecta un token secreto en cada formulario
        // Requiere que TODOS los formularios POST usen th:action en Thymeleaf
        //
        // CORRECTO: <form th:action="@{/pedidos/guardar}" method="post">
        // INCORRECTO: <form action="/pedidos/guardar" method="post">
        //
        // El token se inyecta automáticamente como:
        // <input type="hidden" name="_csrf" value="...token..."/>
        // 
        // Si intentas enviar un formulario sin el token obtendrás:
        // HTTP 403 Forbidden
        //
        // Si necesitas deshabilitar CSRF (no recomendado), descomenta la línea siguiente:
        //.csrf(csrf -> csrf.disable())
        .build();
    }
}
