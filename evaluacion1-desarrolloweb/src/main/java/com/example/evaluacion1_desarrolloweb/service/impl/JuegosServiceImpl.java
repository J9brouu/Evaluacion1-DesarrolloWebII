package com.example.evaluacion1_desarrolloweb.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.evaluacion1_desarrolloweb.service.JuegosService;
import com.example.evaluacion1_desarrolloweb.dao.IJuegosDao;
import com.example.evaluacion1_desarrolloweb.entity.Juegos;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class JuegosServiceImpl implements JuegosService {
    @Autowired
    private IJuegosDao juegosDao;

    @Transactional
    public List<Juegos> findAll() {
        return (List<Juegos>) juegosDao.findAll();
    }

    @Transactional
    public Juegos findOne(Long id) {
        return juegosDao.findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        juegosDao.deleteById(id);
    }

    @Transactional
    public Juegos save(Juegos juego) {
        return juegosDao.save(juego);
    }

}
