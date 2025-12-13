package com.example.evaluacion1_desarrolloweb.dao;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.example.evaluacion1_desarrolloweb.entity.Rol;

public interface IRolDao extends CrudRepository<Rol, Long> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);
}
