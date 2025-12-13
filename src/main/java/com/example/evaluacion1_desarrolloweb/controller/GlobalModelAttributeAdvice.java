package com.example.evaluacion1_desarrolloweb.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    @ModelAttribute("user")
    public Object addUserToModel(HttpSession session) {
        if (session == null) return null;
        return session.getAttribute("user");
    }
}
