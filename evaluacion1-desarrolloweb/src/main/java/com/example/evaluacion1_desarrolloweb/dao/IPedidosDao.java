package com.example.evaluacion1_desarrolloweb.dao;

import org.springframework.data.repository.CrudRepository;
import com.example.evaluacion1_desarrolloweb.entity.Pedido;

public interface IPedidosDao extends CrudRepository<Pedido, Long> {
	java.util.List<Pedido> findByUsuarioId_Id(Long usuarioId);
}
