package com.example.evaluacion1_desarrolloweb.controller;

import com.example.evaluacion1_desarrolloweb.model.User;
import com.example.evaluacion1_desarrolloweb.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de autenticación que utiliza UserService en memoria.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam(required = false) String username,
                          @RequestParam(required = false) String password,
                          HttpSession session,
                          Model model) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Usuario y contraseña son requeridos");
            return "login";
        }
        if (userService.validateCredentials(username.trim(), password)) {
            User u = userService.findByUsername(username.trim()).get();
            session.setAttribute("user", java.util.Map.of("username", u.getUsername(), "role", u.getRole()));
            return "redirect:/index";
        }
        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    @PostMapping("/registro")
    public String doRegistro(@RequestParam(required = false) String usuario,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String confirm,
                             HttpSession session,
                             Model model) {
        if (usuario == null || usuario.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Usuario y email son requeridos");
            return "registro";
        }
        if (password == null || !password.equals(confirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "registro";
        }
        try {
            User u = userService.createUser(usuario.trim(), email.trim(), password, "USER");
            session.setAttribute("user", java.util.Map.of("username", u.getUsername(), "role", u.getRole()));
            return "redirect:/index";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
