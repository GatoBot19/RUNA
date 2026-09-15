package com.runa.runa.service;

import com.runa.runa.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    // CRUD básico
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    Usuario update(Long id, Usuario usuario);
    void deleteById(Long id);

    // Métodos de negocio
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(String rol);
    List<Usuario> findActivos();
    List<Usuario> findByNombreOrApellido(String nombre);
    Usuario desactivarUsuario(Long id);
    Usuario activarUsuario(Long id);
    long countByRol(String rol);
    Optional<Usuario> findByEmailAndActivo(String email);
}