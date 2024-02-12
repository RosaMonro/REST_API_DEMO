package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.PresentacionDao;
import com.example.entities.Presentacion;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresentacionServiceImpl implements PresentacionService {

    private final PresentacionDao presentacionDao;

    @Override
    public List<Presentacion> findAll() {
        return presentacionDao.findAll();
    }

    @Override
    public Presentacion findById(int id) {
        return presentacionDao.findById(id).get(); // necesitamos el get pq es un opcional. Y es un opcional porq en el dao no hemos puesto ningún método
    }

    @Override
    public void save(Presentacion presentacion) {
        presentacionDao.save(presentacion); // es void pq hemos decidido que no vamos a crear ninguna presentación nuevo
    }

    @Override
    public void delete(Presentacion presentacion) {
        presentacionDao.delete(presentacion);
    }

    

}
