package com.example.evaluacion1_desarrolloweb.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.TemporalType;


import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "pedidos")//OPCIONAL
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name="usuario_id")
    private Long id;
    private String producto;
    private Integer cantidad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id")
    private Usuario usuarioId;
    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date createAt;
    @Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd-MM-yyyy")
    private Date updateAt;

    @PrePersist
    protected void prePersist() {
        this.createAt = new Date();
    }
    @PreUpdate
    protected void preUpdate() {
        this.updateAt = new Date();
    }

    public Pedido(Long id, String producto, Integer cantidad, Long usuarioId) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Pedido() {
    }





     //getter y setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Usuario getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Usuario usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}

