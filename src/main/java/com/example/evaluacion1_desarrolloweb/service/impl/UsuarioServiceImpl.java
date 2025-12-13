package com.example.evaluacion1_desarrolloweb.service.impl;
import com.example.evaluacion1_desarrolloweb.dao.IUsuarioDao;
import com.example.evaluacion1_desarrolloweb.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.evaluacion1_desarrolloweb.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {
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
            if (byIgnore.isPresent()) {
                Usuario u = byIgnore.get();
                ensureLegacyRolFromAuthorities(u);
                return u;
            }
        } catch (Exception e) {
            // si el método no está soportado por alguna razón, caeremos al findByEmail normal
        }
        Optional<Usuario> opt = usuarioDao.findByEmail(email);
        if (opt.isPresent()) {
            Usuario u = opt.get();
            ensureLegacyRolFromAuthorities(u);
            return u;
        }
        return null;
    }

    @Transactional
    public void update(Usuario usuario) {
        usuarioDao.save(usuario);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar al usuario en la base de datos por email
        Usuario usuario = usuarioDao.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con email: " + username
                ));
        // Verificar si la cuenta está activa
        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException(
                "la cuenta de " + username + " está desactivada. contacte al administrador."
            );
        }
        // Convertir los roles a nuestra entidad a GrantedAuthority de Spring 
        // Spring Security necesita que los permisos implementen GrantedAuthority
        Set<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toSet());
        
        // Si el usuario no tiene roles en la tabla de relación.
        // Intentar usar el campo 'rol' legacy (para compatibilida)
        if (authorities.isEmpty() && usuario.getRol() != null) {
            String rolNombre = usuario.getRol().startsWith("ROLE_")
                ? usuario.getRol()
                : "ROLE_" + usuario.getRol().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(rolNombre));
        }

        // Si aún no tiene roles, lanzar excepción
        if (authorities.isEmpty()) {
            throw new UsernameNotFoundException(
                "El usuario " + username + " no tiene roles asignados."
            );
        }

        // Devolver un objeto UserDetails que Spring Security entiende
        // Este objeto contiene:
        // - username: identificador del usuario (email)
        // - password: contraseña encriptada (Spring Security la compara con la ingresada)
        // - authorities: roles/privilegios del usuario
        // - flags de estado de la cuenta (habilitada, no expirada, no bloqueada, credenciales válidas)
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword()) // Ya debe estar encriptada con BCrypt
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!usuario.isActivo())
            .build();
    }

    /**
     * Asegura que el campo legacy `rol` del usuario se complete
     * a partir de las autoridades/roles relacionales cuando esté vacío.
     */
    private void ensureLegacyRolFromAuthorities(Usuario usuario) {
        try {
            if (usuario == null) return;
            String legacy = usuario.getRol();
            if (legacy != null && !legacy.isBlank()) return;
            if (usuario.getRoles() == null) return;
            boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(r -> {
                    String n = r.getNombre();
                    return n != null && n.toUpperCase().contains("ROLE_ADMIN");
                });
            boolean isUser = usuario.getRoles().stream()
                .anyMatch(r -> {
                    String n = r.getNombre();
                    return n != null && n.toUpperCase().contains("ROLE_USER");
                });
            if (isAdmin) {
                usuario.setRol("ADMIN");
            } else if (isUser) {
                usuario.setRol("USER");
            }
        } catch (Exception ignore) {}
    }
        /**
         * Método auxiliar para verificar si un usuario existe
         * (puede ser útil en controladores)
         * 
         * @param email El email a verificar
         * @return true si el usuario existe, false si no
         */
        public boolean existeUsuario(String email) {
            return usuarioDao.existsByEmail(email);
        }

}
