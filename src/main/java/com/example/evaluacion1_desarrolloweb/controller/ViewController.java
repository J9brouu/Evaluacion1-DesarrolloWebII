package com.example.evaluacion1_desarrolloweb.controller;
import org.springframework.security.crypto.bcrypt.BCrypt;
    
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import com.example.evaluacion1_desarrolloweb.entity.Juegos;
import com.example.evaluacion1_desarrolloweb.service.impl.JuegosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import com.example.evaluacion1_desarrolloweb.service.impl.UsuarioServiceImpl;
import com.example.evaluacion1_desarrolloweb.dao.IRolDao;
import com.example.evaluacion1_desarrolloweb.entity.Rol;
import org.springframework.security.crypto.password.PasswordEncoder;


@Controller
public class ViewController {
    @Autowired
    private JuegosServiceImpl juegosService;
    @Autowired
    private UsuarioServiceImpl usuarioService;
    @Autowired
    private IRolDao rolDao;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String normalizeRole(String role) {
        if (role == null) return "";
        String r = role.trim();
        if (r.toUpperCase().startsWith("ROLE_")) {
            r = r.substring(5);
        }
        return r.toLowerCase();
    }
    
    // Consolidado: el método `index` con Model y HttpSession maneja "/" y "/index"

    //@PostMapping("/login")
    /*public String procesarLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {
        // normalizar inputs
        String userNormalized = username == null ? "" : username.trim().toLowerCase();
        String passNormalized = password == null ? "" : password.trim();

        // intentamos buscar por email
        Usuario u = usuarioService.findByEmail(userNormalized);
        if (u == null) {
            ra.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/login";
        }
        if (!u.getPassword().equals(passNormalized)) {
            ra.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:/login";
        }

        // autenticar: guardar usuario en sesión
        session.setAttribute("user", u);
     return "redirect:/index"; */

    @GetMapping({"/", "/index"})
    public String index(Model model, Principal principal) {
        // mantener usuario autenticado en el modelo mediante Spring Security
        if (principal != null) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario != null) model.addAttribute("user", usuario);
        }

        List<Juegos> juegos = juegosService.findAll();
        model.addAttribute("juegos", juegos);
        // Estadísticas dinámicas basadas en la lista de juegos
        int totalJuegos = juegos != null ? juegos.size() : 0;
        int totalStock = 0;
        java.util.Set<String> generos = new java.util.HashSet<>();
        java.util.Set<String> plataformas = new java.util.HashSet<>();
        if (juegos != null) {
            for (Juegos j : juegos) {
                if (j.getStock() != null) totalStock += j.getStock();
                if (j.getGenero() != null && !j.getGenero().isBlank()) generos.add(j.getGenero());
                if (j.getPlataforma() != null && !j.getPlataforma().isBlank()) plataformas.add(j.getPlataforma());
            }
        }
        model.addAttribute("totalJuegos", totalJuegos);
        model.addAttribute("totalGeneros", generos.size());
        model.addAttribute("totalPlataformas", plataformas.size());
        model.addAttribute("totalStock", totalStock);
        return "index";
    }

    @PostMapping("/comprar/{id}")
    public String comprarJuego(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        if (principal == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para comprar");
            return "redirect:/login";
        }
        String email = principal.getName();
        Usuario u = usuarioService.findByEmail(email);
        String rol = normalizeRole(u.getRol());
        if (!rol.equals("user") && !rol.equals("usuario")) {
            ra.addFlashAttribute("error", "No tienes permisos para comprar");
            return "redirect:/index";
        }

        Juegos juego = juegosService.findOne(id);
        if (juego == null) {
            ra.addFlashAttribute("error", "Juego no encontrado");
            return "redirect:/index";
        }

        if (juego.getStock() == null || juego.getStock() <= 0) {
            ra.addFlashAttribute("error", "No hay stock disponible");
            return "redirect:/index";
        }

        juego.setStock(juego.getStock() - 1);
        juegosService.save(juego);
        ra.addFlashAttribute("success", "Compra realizada con éxito");
        return "redirect:/index";
    }

    @GetMapping("/form-crear")
    public String formCrear() {
        return "form-crear";
    }

    @GetMapping("/form-editar")
    public String formEditar(@RequestParam(required = false) Long id, Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario != null) model.addAttribute("user", usuario);
        }

        if (id != null) {
            Juegos juego = juegosService.findOne(id);
            if (juego == null) {
                return "redirect:/index";
            }
            model.addAttribute("juego", juego); // llena el form con el juego existente
        } else {
            model.addAttribute("juego", new Juegos()); // formulario vacío para crear si lo deseas
        }
        return "form-editar";
    }

    @PostMapping("/juegos/editar")
    public String guardarEdicion(@ModelAttribute("juego") Juegos formJuego,
                                 RedirectAttributes ra) {

        Juegos existente = juegosService.findOne(formJuego.getId());
        if (existente == null) {
            ra.addFlashAttribute("error", "Juego no encontrado");
            return "redirect:/index";
        }

        // Actualizar campos permitidos
        existente.setTitulo(formJuego.getTitulo());
        existente.setGenero(formJuego.getGenero());
        existente.setPlataforma(formJuego.getPlataforma());
        existente.setStock(formJuego.getStock());
        existente.setPrecio(formJuego.getPrecio());

        juegosService.save(existente);
        ra.addFlashAttribute("success", "Juego actualizado correctamente");

        return "redirect:/juegos/" + existente.getId();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes ra) {
        // El logout real lo maneja Spring Security; este endpoint solo informa
        ra.addFlashAttribute("success", "Sesión cerrada correctamente");
        return "redirect:/login";
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirm,
            RedirectAttributes ra) {

        if (nombre == null || nombre.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            ra.addFlashAttribute("error", "Nombre, email y contraseña son obligatorios");
            return "redirect:/registro";
        }

        if (!password.equals(confirm)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/registro";
        }

        Usuario u = new Usuario();
        // generar un rut simple único si no se pide (evita null constraint)
        String rut = "USR" + System.currentTimeMillis();
        u.setRut(rut);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setActivo(true);
        u.setPassword(passwordEncoder.encode(password));

        // asignar rol USER existente o crearlo si no existe
        String roleName = "ROLE_USER";
        Rol roleEntity = rolDao.findByNombreIgnoreCase(roleName)
            .orElseGet(() -> rolDao.save(new Rol(roleName)));
        java.util.Set<Rol> roles = new java.util.HashSet<>();
        roles.add(roleEntity);
        u.setRoles(roles);

        usuarioService.save(u);
        ra.addFlashAttribute("success", "Registro exitoso. Puedes iniciar sesión.");
        return "redirect:/login";
    }

    @GetMapping("/user")
    public String userProfile(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario != null) model.addAttribute("user", usuario);
        }
        return "user/profile";
    }
    
    @GetMapping("/juegos/{id}")
    public String detalleJuego(@PathVariable Long id, Model model, Principal principal) {
        // mantener usuario en model si existe mediante Principal
        if (principal != null) {
            String email = principal.getName();
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario != null) model.addAttribute("user", usuario);
        }

        // cargar juego desde el servicio
        Juegos juego = juegosService.findOne(id);
        if (juego == null) {
            return "redirect:/index";
        }
        model.addAttribute("juego", juego);
        return "detalle";
    }

    @PostMapping("/juegos/guardar")
    public String guardarNuevoJuego(
            @RequestParam String titulo,
            @RequestParam String genero,
            @RequestParam String plataforma,
            @RequestParam(required = false, defaultValue = "1") Integer stock,
            @RequestParam(required = false) String precio,
            RedirectAttributes ra) {

        Juegos juego = new Juegos();
        juego.setTitulo(titulo);
        juego.setGenero(genero);
        juego.setPlataforma(plataforma);
        juego.setStock(stock != null ? stock : 1);

        // Normaliza y convierte precio a double (acepta formatos con puntos/comas)
        double precioVal = 0.0;
        if (precio != null && !precio.isBlank()) {
            try {
                String digits = precio.replaceAll("[^\\d]", "");
                if (!digits.isEmpty()) precioVal = Double.parseDouble(digits);
            } catch (NumberFormatException e) {
                precioVal = 0.0;
            }
        }
        juego.setPrecio(precioVal);

        juegosService.save(juego);
        ra.addFlashAttribute("success", "Juego agregado correctamente");
        return "redirect:/index";
    }

    @PostMapping("/juegos/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        if (principal == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para eliminar juegos");
            return "redirect:/login";
        }
        String email = principal.getName();
        Usuario u = usuarioService.findByEmail(email);
        String rol = normalizeRole(u.getRol());
        if (!rol.equals("admin") && !rol.equals("administrator")) {
            ra.addFlashAttribute("error", "No tienes permisos para eliminar juegos");
            return "redirect:/index";
        }

        Juegos juego = juegosService.findOne(id);
        if (juego == null) {
            ra.addFlashAttribute("error", "Juego no encontrado");
            return "redirect:/index";
        }

        try {
            juegosService.delete(id);
            ra.addFlashAttribute("success", "Juego eliminado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al eliminar el juego");
        }
        return "redirect:/index";
    }
}
