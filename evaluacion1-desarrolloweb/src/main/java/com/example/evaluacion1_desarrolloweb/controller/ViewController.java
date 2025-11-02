package com.example.evaluacion1_desarrolloweb.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.evaluacion1_desarrolloweb.service.GameService;

/**
 * Controlador simple para devolver vistas estáticas (Thymeleaf templates).
 * Añadido para que las rutas '/' y '/form-crear' muestren las plantillas HTML.
 */
@Controller
public class ViewController {

    private final GameService gameService;

    public ViewController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String root() {
        // Redirige a login al inicio de la aplicación
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        // Si el usuario no ha iniciado sesión, redirige al login
        Object user = session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("juegos", gameService.findAll());
        return "index";
    }

    @GetMapping("/form-crear")
    public String formCrear() {
        // Devuelve src/main/resources/templates/form-crear.html
        return "form-crear";
    }

    @GetMapping("/form-editar")
    public String formEditar(@RequestParam(required = false) Long id, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (id != null) {
            gameService.findById(id).ifPresent(g -> model.addAttribute("juego", g));
        }
        model.addAttribute("user", user);
        return "form-editar";
    }

    @GetMapping("/login")
    public String login() {
        // Devuelve src/main/resources/templates/login.html
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        // Devuelve src/main/resources/templates/registro.html
        return "registro";
    }
}
