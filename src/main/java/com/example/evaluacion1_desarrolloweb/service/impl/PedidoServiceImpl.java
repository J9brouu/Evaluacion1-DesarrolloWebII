package com.example.evaluacion1_desarrolloweb.service.impl;
import com.example.evaluacion1_desarrolloweb.dao.IPedidosDao;
import com.example.evaluacion1_desarrolloweb.entity.Pedido;
import com.example.evaluacion1_desarrolloweb.service.PedidoService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoServiceImpl implements PedidoService {
    @Autowired
    private IPedidosDao pedidosDao;

    @Transactional
    public Pedido save(Pedido pedido) {
        return pedidosDao.save(pedido);
    }

    @Transactional
    public void delete(Long id) {
        pedidosDao.deleteById(id);
    }

    @Transactional
    public Pedido findOne(Long id) {
        return pedidosDao.findById(id).orElse(null);
    }

    @Transactional
    public List<Pedido> findAll() {
        return (List<Pedido>) pedidosDao.findAll();
    }

    @Transactional
    public List<Pedido> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) return java.util.Collections.emptyList();
        return pedidosDao.findByUsuarioId_Id(usuarioId);
    }
}