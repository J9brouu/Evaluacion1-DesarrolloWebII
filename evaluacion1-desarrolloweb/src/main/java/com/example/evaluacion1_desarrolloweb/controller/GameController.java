package com.example.evaluacion1_desarrolloweb.controller;

import com.example.evaluacion1_desarrolloweb.model.Game;
import com.example.evaluacion1_desarrolloweb.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/juegos/guardar")
    public String guardar(@RequestParam String titulo,
                          @RequestParam String genero,
                          @RequestParam String plataforma,
                          @RequestParam(required = false, defaultValue = "0") int stock,
                          @RequestParam(required = false) String precio,
                          @RequestParam(required = false) MultipartFile imagen,
                          HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Long precioVal = null;
        try { if (precio != null && !precio.isBlank()) precioVal = Long.parseLong(precio.replaceAll("\\D", "")); } catch (NumberFormatException e) { precioVal = 0L; }

        String imagePath = "/assets/libraryofgames.png";
        if (imagen != null && !imagen.isEmpty()) {
            String assets = "src/main/resources/static/assets";
            try {
                Files.createDirectories(Paths.get(assets));
                String filename = System.currentTimeMillis() + "-" + imagen.getOriginalFilename().replaceAll("[^a-zA-Z0-9.\\-_]", "");
                Path target = Paths.get(assets, filename);
                imagen.transferTo(target.toFile());
                imagePath = "/assets/" + filename;
            } catch (IOException ex) {
                // fall back to default
            }
        }

        Game g = new Game(null, titulo, genero, plataforma, stock, precioVal, imagePath);
        gameService.create(g);
        return "redirect:/index";
    }

    @PostMapping("/juegos/editar")
    public String editar(@RequestParam Long id,
                         @RequestParam String titulo,
                         @RequestParam String genero,
                         @RequestParam String plataforma,
                         @RequestParam(required = false, defaultValue = "0") int stock,
                         @RequestParam(required = false) String precio,
                         @RequestParam(required = false) MultipartFile imagen,
                         HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Long precioVal = null;
        try { if (precio != null && !precio.isBlank()) precioVal = Long.parseLong(precio.replaceAll("\\D", "")); } catch (NumberFormatException e) { precioVal = 0L; }

        Optional<Game> existing = gameService.findById(id);
        if (existing.isEmpty()) return "redirect:/index";

        Game g = existing.get();
        g.setTitulo(titulo);
        g.setGenero(genero);
        g.setPlataforma(plataforma);
        g.setStock(stock);
        g.setPrecio(precioVal);

        if (imagen != null && !imagen.isEmpty()) {
            String assets = "src/main/resources/static/assets";
            try {
                Files.createDirectories(Paths.get(assets));
                String filename = System.currentTimeMillis() + "-" + imagen.getOriginalFilename().replaceAll("[^a-zA-Z0-9.\\-_]", "");
                Path target = Paths.get(assets, filename);
                imagen.transferTo(target.toFile());
                g.setImagen("/assets/" + filename);
            } catch (IOException ex) {
                // ignore and keep existing image
            }
        }

        gameService.update(id, g);
        return "redirect:/index";
    }

    @PostMapping("/juegos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/login";
        gameService.delete(id);
        return "redirect:/index";
    }

    @GetMapping("/juegos/{id}")
    public String ver(@PathVariable Long id, Model model, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/login";
        gameService.findById(id).ifPresent(g -> model.addAttribute("juego", g));
        model.addAttribute("user", user);
        return "detalle"; // detalle.html si existe
    }
}
