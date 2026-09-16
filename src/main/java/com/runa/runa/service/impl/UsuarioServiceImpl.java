package com.runa.runa.service.impl;

import com.runa.runa.model.entity.Usuario;
import com.runa.runa.repository.UsuarioRepository;
import com.runa.runa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        log.info("Buscando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario usuario) {
        log.info("Actualizando usuario ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setApellido(usuario.getApellido());
                    usuarioExistente.setTelefono(usuario.getTelefono());
                    usuarioExistente.setNacionalidad(usuario.getNacionalidad());
                    usuarioExistente.setRol(usuario.getRol());
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        log.info("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRol(String rol) {
        log.info("Buscando usuarios por rol: {}", rol);
        return usuarioRepository.findByRol(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findActivos() {
        log.info("Buscando usuarios activos");
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByNombreOrApellido(String nombre) {
        log.info("Buscando usuarios por nombre o apellido: {}", nombre);
        return usuarioRepository.findByNombreOrApellidoContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional
    public Usuario desactivarUsuario(Long id) {
        log.info("Desactivando usuario ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(false);
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Usuario activarUsuario(Long id) {
        log.info("Activando usuario ID: {}", id);

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(true);
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRol(String rol) {
        return usuarioRepository.countByRol(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmailAndActivo(String email) {
        return usuarioRepository.findByEmailAndActivoTrue(email);
    }
}
