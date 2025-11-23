package com.example.evaluacion1_desarrolloweb.service;
import com.example.evaluacion1_desarrolloweb.entity.Juegos;
import java.util.List;

public interface JuegosService {
    public List<Juegos> findAll();
    public Juegos findOne(Long id);
    public void delete(Long id);
    public Juegos save(Juegos juego);
}
