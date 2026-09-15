package com.runa.runa.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String nacionalidad;
    private String dni;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Método helper para nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
