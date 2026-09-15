package com.runa.runa.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;

    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Métodos estáticos para respuestas rápidas
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "Operación exitosa", data);
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }
}
