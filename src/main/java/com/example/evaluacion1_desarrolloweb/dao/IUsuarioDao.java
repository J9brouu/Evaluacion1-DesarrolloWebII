package com.example.evaluacion1_desarrolloweb.dao;

import org.springframework.data.repository.CrudRepository;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import java.util.Optional;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
	/**
	 * 
	 * @param email
	 * @return
	*/
	Optional<Usuario> findByEmail(String email);
	/**
	 * 
	 * @param rut
	 * @return
	 */
	Optional<Usuario> findByRut(String rut);
	/**
	 * 
	 * @param email
	 * @return
	 */
	boolean existsByEmail(String email);
	/**
	 * 
	 * @param rut
	 * @return
	 */
	boolean existsByRut(String rut);

	Optional<Usuario> findByEmailIgnoreCase(String email);
}
