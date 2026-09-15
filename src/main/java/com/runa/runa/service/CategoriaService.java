package com.runa.runa.service;

import com.runa.runa.model.entity.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    List<Categoria> findAll();

    Optional<Categoria> findById(Long id);

    Categoria save(Categoria categoria);

    Categoria update(Long id, Categoria categoria);

    void deleteById(Long id);

    // Métodos específicos
    Optional<Categoria> findByNombre(String nombre);

    List<Categoria> findByNombreContaining(String nombre);

    List<Categoria> buscarPorTermino(String termino);

    boolean existsByNombre(String nombre);
}
