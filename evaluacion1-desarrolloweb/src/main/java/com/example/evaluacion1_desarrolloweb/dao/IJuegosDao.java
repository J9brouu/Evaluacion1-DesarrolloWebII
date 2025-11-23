package com.example.evaluacion1_desarrolloweb.dao;
import org.springframework.data.repository.CrudRepository;
import com.example.evaluacion1_desarrolloweb.entity.Juegos;


public interface IJuegosDao extends CrudRepository<Juegos, Long> {
    
}
