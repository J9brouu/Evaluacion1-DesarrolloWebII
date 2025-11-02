package com.example.evaluacion1_desarrolloweb.service;

import com.example.evaluacion1_desarrolloweb.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, User> usersById = new LinkedHashMap<>();
    private final Map<String, User> usersByUsername = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);
    private final BCryptPasswordEncoder encoder;

    public UserService(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public synchronized User createUser(String username, String email, String rawPassword, String role) {
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Usuario requerido");
        if (usersByUsername.containsKey(username)) throw new IllegalArgumentException("Usuario ya existe");
        Long id = seq.getAndIncrement();
        String hash = encoder.encode(rawPassword == null ? "" : rawPassword);
        User u = new User(id, username, email, hash, role);
        usersById.put(id, u);
        usersByUsername.put(username, u);
        return u;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public boolean validateCredentials(String username, String rawPassword) {
        User u = usersByUsername.get(username);
        if (u == null) return false;
        return encoder.matches(rawPassword == null ? "" : rawPassword, u.getPasswordHash());
    }

    public List<User> findAll() { return new ArrayList<>(usersById.values()); }
}
