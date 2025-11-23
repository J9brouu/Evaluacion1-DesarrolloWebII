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
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import com.example.evaluacion1_desarrolloweb.service.impl.UsuarioServiceImpl;
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UsuarioServiceImpl usuarioService;
    

    @GetMapping("/profile")
    public String profile(jakarta.servlet.http.HttpSession session, Model model) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser != null) model.addAttribute("user", sessionUser);
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
    public String createUser(@ModelAttribute Usuario usuario, RedirectAttributes redirect) {
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
    public String listUsers(Model model, jakarta.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        // LOG: show session user info for debugging access issues
        if (sessionUser != null) {
            try {
                com.example.evaluacion1_desarrolloweb.entity.Usuario su = (com.example.evaluacion1_desarrolloweb.entity.Usuario) sessionUser;
                logger.info("[UserController.listUsers] session user id={}, rut={}, email={}, rol={}", su.getId(), su.getRut(), su.getEmail(), su.getRol());
            } catch (Exception e) {
                logger.info("[UserController.listUsers] session user present but cannot cast: {}", sessionUser.getClass().getName());
            }
        } else {
            logger.info("[UserController.listUsers] no session user found");
        }
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para ver esta página");
            return "redirect:/login";
        }
        Usuario u = (Usuario) sessionUser;
        String rol = u.getRol() != null ? u.getRol().toLowerCase() : "";
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
        // exponer usuario de sesión al modelo para que fragmentos (navbar) lo muestren
        model.addAttribute("user", sessionUser);
        return "user/list";
    }

    // Ver detalles de un usuario por id
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, jakarta.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para ver este usuario");
            return "redirect:/login";
        }

        com.example.evaluacion1_desarrolloweb.entity.Usuario current = (com.example.evaluacion1_desarrolloweb.entity.Usuario) sessionUser;
        // si no es el mismo usuario, solo admin puede ver
        if (!current.getId().equals(id)) {
            String rol = current.getRol() != null ? current.getRol().toLowerCase() : "";
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
        model.addAttribute("user", sessionUser);
        return "user/read";
    }
    //Actualizar usuario (form)
    @GetMapping("/update")
    public String updateUser(@RequestParam(required = false) Long id, Model model, jakarta.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para editar el perfil");
            return "redirect:/login";
        }

        Usuario current = (Usuario) sessionUser;
        Usuario target = null;
        if (id != null) {
            // sólo admin puede editar a otro usuario
            String rol = current.getRol() != null ? current.getRol().toLowerCase() : "";
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

        model.addAttribute("user", current); // session user for conditionals
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
                           jakarta.servlet.http.HttpSession session,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para editar el perfil");
            return "redirect:/login";
        }
        Usuario current = (Usuario) sessionUser;

        Usuario target;
        boolean editingOther = false;
        if (id != null && !id.equals(current.getId())) {
            // trying to edit another user -> must be admin
            String rol = current.getRol() != null ? current.getRol().toLowerCase() : "";
            if (!rol.equals("admin") && !rol.equals("administrator")) {
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
            if (role != null) target.setRol(role);
        }

        // cambiar contraseña si se entregó
        if (password != null && !password.isBlank()) {
            // si el usuario edita su propio perfil, exigir contraseña antigua
            if (!editingOther) {
                if (oldPassword == null || oldPassword.isBlank()) {
                    ra.addFlashAttribute("error", "Debes ingresar tu contraseña actual para cambiarla");
                    return "redirect:/user/update";
                }
                // comparar con la contraseña actual en sesión (texto plano en este proyecto)
                if (!current.getPassword().equals(oldPassword)) {
                    ra.addFlashAttribute("error", "La contraseña actual es incorrecta");
                    return "redirect:/user/update";
                }
            }
            if (confirm == null || !password.equals(confirm)) {
                ra.addFlashAttribute("error", "Las contraseñas no coinciden");
                return editingOther ? "redirect:/user/update?id=" + target.getId() : "redirect:/user/update";
            }
            target.setPassword(password);
        }

        try {
            usuarioService.update(target);
            // si editó su propio perfil, actualizar sesión
            if (!editingOther) {
                session.setAttribute("user", target);
            }
            ra.addFlashAttribute("success", "Usuario actualizado correctamente");
            return editingOther ? "redirect:/user/list" : "redirect:/user/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ocurrió un error al actualizar el usuario");
            return editingOther ? "redirect:/user/update?id=" + target.getId() : "redirect:/user/update";
        }
    }
    //Eliminar usuario (borra la cuenta del usuario en sesión)
    @PostMapping("/delete")
    public String deleteUser(jakarta.servlet.http.HttpSession session, RedirectAttributes ra) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para eliminar tu cuenta");
            return "redirect:/login";
        }
        try {
            Usuario u = (Usuario) sessionUser;
            Long id = u.getId();
            if (id != null) {
                usuarioService.delete(id);
            }
            try {
                session.invalidate();
            } catch (IllegalStateException ignore) {}
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
                                 jakarta.servlet.http.HttpSession session,
                                 RedirectAttributes ra) {
        Object sessionUser = (session != null) ? session.getAttribute("user") : null;
        if (sessionUser == null) {
            ra.addFlashAttribute("error", "Debes iniciar sesión para eliminar un usuario");
            return "redirect:/login";
        }
        Usuario current = (Usuario) sessionUser;
        String rol = current.getRol() != null ? current.getRol().toLowerCase() : "";
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
