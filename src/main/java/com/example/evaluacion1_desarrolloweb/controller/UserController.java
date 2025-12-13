package com.example.evaluacion1_desarrolloweb.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import com.example.evaluacion1_desarrolloweb.entity.Rol;
import com.example.evaluacion1_desarrolloweb.dao.IRolDao;
import com.example.evaluacion1_desarrolloweb.service.impl.UsuarioServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
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
    

    @GetMapping("/profile")
    public String profile(java.security.Principal principal, Model model) {
        // Obtener el usuario autenticado usando Spring Security
        String email = principal.getName();
        Usuario usuario = usuarioService.findByEmail(email);
        
        if (usuario != null) {
            model.addAttribute("user", usuario);
        }
        return "user/profile";
    }

    @GetMapping("/settings")
    public String settings() {
        return "user/settings";
    }

    //CRUD de usuarios (Create, Read, Update, Delete)
    //Crear usuario
    @GetMapping("/create")
    public String createUser() {
        return "user/create";
    }

            @PostMapping("/create")
        public String createUser(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirect) {
                if (result.hasErrors()) {
                        String mensaje = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Datos inválidos";
                        redirect.addFlashAttribute("error", mensaje);
                        return "redirect:/user/create";
                }
        // Establecer valores por defecto que no vienen del formulario
        usuario.setActivo(true);
        
        // guarda en la BD (UsuarioService debe implementar save)
        usuarioService.save(usuario);
        redirect.addFlashAttribute("success", "Usuario creado correctamente");
        return "redirect:/user/list";
    }
    
    //Leer usuario
    @GetMapping("/read")
    public String readUser() {
        return "user/read";
    }

    //Listar usuario
    @GetMapping("/list")
    public String listUsers(Model model, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        String email = principal.getName();
        Usuario u = usuarioService.findByEmail(email);
        if (u == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para ver esta página");
            return "redirect:/login";
        }
        String rol = normalizeRole(u.getRol());
        if (!rol.equals("admin") && !rol.equals("administrator")) {
            ra.addFlashAttribute("error", "No tienes permisos para ver los usuarios");
            return "redirect:/index";
        }

        List<Usuario> usuarios = usuarioService.findAll();
        /*List<Usuario> usuarios = new ArrayList<>();
        // crea 20 usuarios de ejemplo
        for (int i = 1; i <= 50; i++) {
            String nombre = "Usuario" + i;
            String apellido = "Apellido" + i;
            String password = "pass" + i;
            String email = "usuario" + i + "@example.com";
            String rol = (i % 5 == 0) ? "ADMIN" : "USER";
            usuarios.add(new Usuario(nombre, apellido, password, email, rol));
        }*/
        model.addAttribute("users", usuarios);
        model.addAttribute("user", u);
        return "user/list";
    }

    // Ver detalles de un usuario por id
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        String email = principal.getName();
        com.example.evaluacion1_desarrolloweb.entity.Usuario current = usuarioService.findByEmail(email);
        if (current == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para ver este usuario");
            return "redirect:/login";
        }
        // si no es el mismo usuario, solo admin puede ver
        if (!current.getId().equals(id)) {
            String rol = normalizeRole(current.getRol());
            if (!rol.equals("admin") && !rol.equals("administrator")) {
                ra.addFlashAttribute("error", "No tienes permisos para ver ese usuario");
                return "redirect:/index";
            }
        }

        com.example.evaluacion1_desarrolloweb.entity.Usuario target = usuarioService.findOne(id);
        if (target == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/user/list";
        }

        model.addAttribute("target", target);
        model.addAttribute("user", current);
        return "user/read";
    }
    //Actualizar usuario (form)
    @GetMapping("/update")
    public String updateUser(@RequestParam(required = false) Long id, Model model, java.security.Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        String email = principal.getName();
        Usuario current = usuarioService.findByEmail(email);
        if (current == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para editar el perfil");
            return "redirect:/login";
        }
        Usuario target = null;
        if (id != null) {
            // sólo admin puede editar a otro usuario
            String rol = normalizeRole(current.getRol());
            if (!rol.equals("admin") && !rol.equals("administrator")) {
                ra.addFlashAttribute("error", "No tienes permisos para editar a otros usuarios");
                return "redirect:/index";
            }
            target = usuarioService.findOne(id);
            if (target == null) {
                ra.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
        } else {
            // editar perfil propio
            target = current;
        }

        model.addAttribute("user", current);
        model.addAttribute("target", target); // usuario a editar
        return "user/update";
    }

    @PostMapping("/update")
    public String doUpdate(@RequestParam(required = false) Long id,
                           @RequestParam String nombre,
                           @RequestParam(required = false) String apellido,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String rut,
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String confirm,
                           @RequestParam(required = false) String oldPassword,
                           java.security.Principal principal,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        String authEmail = principal.getName();
        Usuario current = usuarioService.findByEmail(authEmail);
        if (current == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para editar el perfil");
            return "redirect:/login";
        }

        Usuario target;
        boolean editingOther = false;
        if (id != null && !id.equals(current.getId())) {
            // trying to edit another user -> must be admin
            boolean isAdmin = current.getRoles() != null && current.getRoles().stream()
                .anyMatch(r -> r.getNombre() != null && r.getNombre().toUpperCase().contains("ADMIN"));
            if (!isAdmin) {
                ra.addFlashAttribute("error", "No tienes permisos para editar a otros usuarios");
                return "redirect:/index";
            }
            target = usuarioService.findOne(id);
            if (target == null) {
                ra.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/user/list";
            }
            editingOther = true;
        } else {
            target = current;
        }

        // actualizar campos permitidos
        if (nombre != null && !nombre.isBlank()) target.setNombre(nombre.trim());
        target.setApellido(apellido != null ? apellido.trim() : null);
        target.setEmail(email != null ? email.trim() : null);

        // rut (usuario identificador) solo admin puede cambiarlo
        if (editingOther) {
            if (rut != null && !rut.isBlank()) target.setRut(rut.trim());
            if (role != null && !role.isBlank()) {
                String normalizedRole = role.trim().toUpperCase();
                if (!normalizedRole.startsWith("ROLE_")) {
                    normalizedRole = "ROLE_" + normalizedRole;
                }
                Rol roleEntity = rolDao.findByNombreIgnoreCase(normalizedRole)
                    .orElse(null);
                if (roleEntity == null) {
                    ra.addFlashAttribute("error", "Rol no encontrado: " + normalizedRole);
                    return editingOther ? "redirect:/user/update?id=" + target.getId() : "redirect:/user/update";
                }
                target.getRoles().clear();
                target.getRoles().add(roleEntity);
                target.setUpdatedAt(new java.util.Date());
            }
        }

        // cambiar contraseña si se entregó
        if (password != null && !password.isBlank()) {
            // si el usuario edita su propio perfil, exigir contraseña antigua
            if (!editingOther) {
                if (oldPassword == null || oldPassword.isBlank()) {
                    ra.addFlashAttribute("error", "Debes ingresar tu contraseña actual para cambiarla");
                    return "redirect:/user/update";
                }
                // comparar usando PasswordEncoder
                if (!passwordEncoder.matches(oldPassword, current.getPassword())) {
                    ra.addFlashAttribute("error", "La contraseña actual es incorrecta");
                    return "redirect:/user/update";
                }
            }
            if (confirm == null || !password.equals(confirm)) {
                ra.addFlashAttribute("error", "Las contraseñas no coinciden");
                return editingOther ? "redirect:/user/update?id=" + target.getId() : "redirect:/user/update";
            }
            target.setPassword(passwordEncoder.encode(password));
        }

        try {
            usuarioService.update(target);
            // Si editó su propio perfil, no es necesario tocar la sesión; Security gestiona autenticación
            ra.addFlashAttribute("success", "Usuario actualizado correctamente");
            return editingOther ? "redirect:/user/list" : "redirect:/user/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al actualizar el usuario");
            return editingOther ? "redirect:/user/update?id=" + target.getId() : "redirect:/user/update";
        }
    }
    //Eliminar usuario (borra la cuenta del usuario en sesión)
    @PostMapping("/delete")
    public String deleteUser(java.security.Principal principal, RedirectAttributes ra) {
        String email = principal.getName();
        Usuario sessionUser = usuarioService.findByEmail(email);
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para eliminar tu cuenta");
            return "redirect:/login";
        }
        try {
            Usuario u = sessionUser;
            Long id = u.getId();
            if (id != null) {
                usuarioService.delete(id);
            }
            ra.addFlashAttribute("success", "Tu cuenta ha sido eliminada correctamente");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al eliminar la cuenta");
            return "redirect:/user/profile";
        }
    }

    // Eliminar usuario por id (admin desde la lista)
    @PostMapping("/{id}/delete")
    public String deleteUserById(@org.springframework.web.bind.annotation.PathVariable Long id,
                                 java.security.Principal principal,
                                 RedirectAttributes ra) {
        String email = principal.getName();
        Usuario sessionUser = usuarioService.findByEmail(email);
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para eliminar un usuario");
            return "redirect:/login";
        }
        Usuario current = sessionUser;
        String rol = normalizeRole(current.getRol());
        if (!rol.equals("admin") && !rol.equals("administrator")) {
            ra.addFlashAttribute("error", "No tienes permisos para eliminar usuarios");
            return "redirect:/index";
        }

        Usuario target = usuarioService.findOne(id);
        if (target == null) {
            ra.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/user/list";
        }

        try {
            usuarioService.delete(id);
            ra.addFlashAttribute("success", "Usuario eliminado correctamente");
            return "redirect:/user/list";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al eliminar el usuario");
            return "redirect:/user/list";
        }
    }

    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/json")
    @ResponseBody
    public List<Usuario> listUsersJson() {
        return usuarioService.findAll();
    }
}
