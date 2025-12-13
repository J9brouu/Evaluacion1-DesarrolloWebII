package com.example.evaluacion1_desarrolloweb.service;
import com.example.evaluacion1_desarrolloweb.entity.Pedido;
import java.util.List;


public interface PedidoService {
    public List<Pedido> findAll();

    public List<Pedido> findByUsuarioId(Long usuarioId);

    public Pedido findOne(Long id);

    public void delete(Long id);

    public Pedido save(Pedido pedido);


}
