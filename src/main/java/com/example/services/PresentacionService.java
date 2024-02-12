package com.example.services;

import java.util.List;

import com.example.entities.Presentacion;

public interface PresentacionService {

    public List<Presentacion> findAll();
    public Presentacion findById(int id);
    public void save(Presentacion presentacion); // es void pq hemos decidido que no vamos a crear ninguna presentación nuevo
    public void delete(Presentacion presentacion);

}
