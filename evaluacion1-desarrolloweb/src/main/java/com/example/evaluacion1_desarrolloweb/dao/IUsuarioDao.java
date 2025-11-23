package com.example.evaluacion1_desarrolloweb.dao;

import org.springframework.data.repository.CrudRepository;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import java.util.Optional;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
	Optional<Usuario> findByEmail(String email);
	Optional<Usuario> findByEmailIgnoreCase(String email);
}
