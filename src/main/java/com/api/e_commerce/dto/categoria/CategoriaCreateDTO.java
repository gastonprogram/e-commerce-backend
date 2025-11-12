package com.api.e_commerce.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear nuevas categorías
 * Usado en POST requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCreateDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;
}
