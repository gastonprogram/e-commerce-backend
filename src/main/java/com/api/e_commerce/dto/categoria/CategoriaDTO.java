package com.api.e_commerce.dto.categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar información de categorías
 * Usado en GET requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String name;
    private Integer productosCount; // Cantidad de productos en esta categoría
}
