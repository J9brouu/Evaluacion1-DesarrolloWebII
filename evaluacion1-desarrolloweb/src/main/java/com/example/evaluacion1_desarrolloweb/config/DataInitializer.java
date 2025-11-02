package com.example.evaluacion1_desarrolloweb.config;

import com.example.evaluacion1_desarrolloweb.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            userService.createUser("admin", "admin@example.com", "admin123", "ADMIN");
            userService.createUser("user", "user@example.com", "user123", "USER");
            System.out.println("Usuarios iniciales creados: admin / user");
        } catch (Exception e) {
            // Si ya existen, ignorar
        }
    }
}
