package com.api.e_commerce.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de Categoría
 * Usado dentro de ProductoDTO para evitar referencias circulares
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaSimpleDTO {
    private Long id;
    private String name;
}
