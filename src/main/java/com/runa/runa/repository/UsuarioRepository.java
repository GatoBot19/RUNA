package com.runa.runa.repository;

import com.runa.runa.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe un usuario por email
    boolean existsByEmail(String email);

    // Buscar usuarios por rol
    List<Usuario> findByRol(String rol);

    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar usuarios por nacionalidad
    List<Usuario> findByNacionalidad(String nacionalidad);

    // Buscar usuarios por nombre o apellido (case insensitive)
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> findByNombreOrApellidoContainingIgnoreCase(@Param("nombre") String nombre);

    // Contar usuarios por rol
    long countByRol(String rol);

    // Buscar usuarios por email y activo
    Optional<Usuario> findByEmailAndActivoTrue(String email);
}
