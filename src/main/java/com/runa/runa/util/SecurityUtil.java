package com.runa.runa.util;

import com.runa.runa.model.entity.Usuario;
import com.runa.runa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el email del usuario autenticado desde el SecurityContext
     */
    public Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        
        return Optional.empty();
    }

    /**
     * Obtiene el usuario completo desde la base de datos usando el email del token JWT
     */
    public Optional<Usuario> getCurrentUser() {
        return getCurrentUserEmail()
                .flatMap(usuarioRepository::findByEmail);
    }

    /**
     * Obtiene el ID del usuario actual
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser()
                .map(Usuario::getIdUsuario);
    }
}

