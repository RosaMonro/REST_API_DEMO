package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dao.ProductoDao;
import com.example.entities.Producto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoDao productoDao;

    @Override
    public Page<Producto> findAll(Pageable pageable) {
        return productoDao.findAll(pageable);
    }

    @Override
    public List<Producto> findAll(Sort sort) {
        return productoDao.findAll(sort);
    }

    @Override
    public List<Producto> findAll() {
        return productoDao.findAll();
    }


    @Override
    public Producto findById(int id) {
        return productoDao.findBy(id); // no necesita get() pq no es un opcional ya que es un método que he creado yo misma. 
    }

    @Override
    public Producto save(Producto producto) {
        return productoDao.save(producto);
    }

    @Override
    public void delete(Producto producto) {
        productoDao.delete(producto);
    }

    }

    

