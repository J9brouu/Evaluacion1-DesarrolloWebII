package com.example.evaluacion1_desarrolloweb.service;

import java.util.List;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;

public interface UsuarioService {
    public List<Usuario> findAll();
    
    public Usuario save(Usuario usuario);

    public Usuario findByEmail(String email);

    public Usuario findOne(Long id);

    public void delete(Long id);

    public void update(Usuario usuario);

}
