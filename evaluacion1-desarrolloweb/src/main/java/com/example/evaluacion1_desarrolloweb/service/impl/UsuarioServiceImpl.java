package com.example.evaluacion1_desarrolloweb.service.impl;
import com.example.evaluacion1_desarrolloweb.service.UsuarioService;
import jakarta.transaction.Transactional;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.evaluacion1_desarrolloweb.dao.IUsuarioDao;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private IUsuarioDao usuarioDao;

    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioDao.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        usuarioDao.deleteById(id);
    }

    @Transactional
    public Usuario findOne(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Transactional
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioDao.findAll();
    }

    @Transactional
    public Usuario findByEmail(String email) {
        // intentar búsqueda insensible a mayúsculas primero
        try {
            Optional<Usuario> byIgnore = usuarioDao.findByEmailIgnoreCase(email);
            if (byIgnore.isPresent()) return byIgnore.get();
        } catch (Exception e) {
            // si el método no está soportado por alguna razón, caeremos al findByEmail normal
        }
        return usuarioDao.findByEmail(email).orElse(null);
    }

    @Transactional
    public void update(Usuario usuario) {
        usuarioDao.save(usuario);
    }
    
}
