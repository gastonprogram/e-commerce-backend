package com.api.e_commerce.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de Usuario
 * Usado dentro de ProductoDTO para evitar exponer información sensible
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSimpleDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
}
