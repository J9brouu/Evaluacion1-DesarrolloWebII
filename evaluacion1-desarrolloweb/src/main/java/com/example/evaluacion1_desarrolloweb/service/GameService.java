package com.example.evaluacion1_desarrolloweb.service;

import com.example.evaluacion1_desarrolloweb.model.Game;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameService {
    private final Map<Long, Game> repo = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public GameService() {
        // Datos iniciales de ejemplo
        this.create(new Game(null, "Horizon Forbidden West", "Aventura", "PS5", 12, 59990L, "/assets/libraryofgames.png"));
        this.create(new Game(null, "Legend of Zelda: Tears", "Acción", "Switch", 4, 49990L, "/assets/libraryofgames.png"));
        this.create(new Game(null, "Forza Horizon 5", "Carreras", "Xbox", 9, 44990L, "/assets/libraryofgames.png"));
    }

    public List<Game> findAll() { return new ArrayList<>(repo.values()); }

    public Optional<Game> findById(Long id) { return Optional.ofNullable(repo.get(id)); }

    public Game create(Game g) {
        Long id = seq.getAndIncrement();
        g.setId(id);
        repo.put(id, g);
        return g;
    }

    public Optional<Game> update(Long id, Game g) {
        if (!repo.containsKey(id)) return Optional.empty();
        g.setId(id);
        repo.put(id, g);
        return Optional.of(g);
    }

    public boolean delete(Long id) { return repo.remove(id) != null; }
}
