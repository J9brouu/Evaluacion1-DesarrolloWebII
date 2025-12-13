package com.example.evaluacion1_desarrolloweb.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
/**
 * @author Sistema de Evaluacion Web II
 * @version 1.0
 */

@Entity
@Table(name = "roles")
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Nombre del rol (ROLE_ADMIN, ROLE_USER, etc.)
     * 
     * IMPORTANTE: Debe incluir el prefijo "ROLE_"
     * ejemplos:
     * - ROLE_ADMIN
     * - ROLE_USER
     */
    @Column(unique = false, nullable = false, length = 50)
    private String nombre;

    /**
     * Descripcion del rol (opcional)
     * Ejemplo: "Rol de administrador con todos los permisos"
     */
    @Column(length = 200)
    private String descripcion;

    /**
     * Relacion Many-to-Many inversa con Usuario
     * Un rol puede ser asignado a muchos usuarios
     * 
     * MappedBy: Indica que la relacion es gestionada por el atributo "roles" en la entidad Usuario
     * (en el atributo 'roles' de Usuario)
     */
    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios = new HashSet<>();

    public Rol() {
    }

    /**
     * Constructor con nombre
     * @param nombre
     */
    public Rol(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con nombre y descripcion
     * @param nombre
     * @param descripcion
     */
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    // Metodos Override

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rol)) return false;

        Rol rol = (Rol) o;

        return nombre != null && nombre.equals(rol.nombre);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
