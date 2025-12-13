package com.example.evaluacion1_desarrolloweb.entity;

import java.util.Date;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    @Column(length = 50, unique = true, nullable = false)
    private String rut;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
    @Column(length = 100, nullable = false)
    private String nombre;

    @Size(max = 100, message = "El apellido no debe superar 100 caracteres")
    @Column(length = 100)
    private String apellido;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 255, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(length = 255, nullable = false)
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 150, message = "El email no debe superar 150 caracteres")
    @Column(length = 150)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean activo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

     
    @PrePersist
    protected void onCreate() { 
        this.createdAt = new Date(); 
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = new Date(); }
    
    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String password, Set<Rol> roles) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.activo = true;
    }
    // getters y setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() {
        // Devuelve el primer rol encontrado o "" si no hay roles
        return roles.stream()
                    .findFirst()
                    .map(Rol::getNombre) // Asumiendo que Rol tiene un método getNombre()
                    .orElse("");
    }
    public void setRol(String rol){
        if (rol == null || rol.isBlank()) {
            return;
        }
        String normalizedInput = rol.trim().toUpperCase();
        final String normalized = normalizedInput.startsWith("ROLE_")
            ? normalizedInput
            : "ROLE_" + normalizedInput;

        // Reutiliza el rol existente si ya está asignado
        Rol matched = this.roles.stream()
            .filter(r -> normalized.equalsIgnoreCase(r.getNombre()))
            .findFirst()
            .orElse(null);

        Rol resolved = matched != null ? matched : new Rol(normalized);

        this.roles.clear();
        this.roles.add(resolved);
        this.updatedAt = new Date();
    }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    //... otros getters y setters ...
    public Set<Rol> getRoles() { return roles; }
    public void setRoles(Set<Rol> roles) { this.roles = roles; this.updatedAt = new Date(); }
    /**
     * Agrega un rol al usuario
     * @param rol
     */
    public void addRol(Rol rol) { this.roles.add(rol); this.updatedAt = new Date(); }
    /**
     * Remueve un rol del usuario
     * @param rol
     */
    public void removeRol(Rol rol) { this.roles.remove(rol); this.updatedAt = new Date(); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /**
     * Obtiene el nombre de usuario (email) para autenticación
     * @return Email del usuario
     */
    public String getUsername() {
        return this.email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", rut='" + rut + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", roles=" + roles +
                '}';
    }
   
}
