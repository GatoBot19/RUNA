package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Categoria;
import com.runa.runa.repository.CategoriaRepository;
import com.runa.runa.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        log.info("Buscando todas las categorías");
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        log.info("Guardando categoría: {}", categoria.getNombre());

        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException(
                    "Ya existe una categoría con el nombre: " + categoria.getNombre()
            );
        }

        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria update(Long id, Categoria categoria) {
        log.info("Actualizando categoría ID: {}", id);

        return categoriaRepository.findById(id)
                .map(categoriaExistente -> {
                    categoriaExistente.setNombre(categoria.getNombre());
                    categoriaExistente.setDescripcion(categoria.getDescripcion());

                    return categoriaRepository.save(categoriaExistente);
                })
                .orElseThrow(() ->
                        new RuntimeException("Categoría no encontrada con ID: " + id)
                );
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando categoría ID: {}", id);
        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findByNombreContaining(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> buscarPorTermino(String termino) {
        return categoriaRepository
                .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                        termino,
                        termino
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}
