package com.runa.runa.repository;

import com.runa.runa.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar categoría por nombre exacto
    Optional<Categoria> findByNombre(String nombre);

    // Buscar categorías por nombre
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    // Buscar categorías por descripción
    List<Categoria> findByDescripcionContainingIgnoreCase(String descripcion);

    // Buscar categorías por nombre o descripción
    List<Categoria> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre,
            String descripcion
    );

    // Verificar si existe una categoría por nombre
    boolean existsByNombre(String nombre);
}