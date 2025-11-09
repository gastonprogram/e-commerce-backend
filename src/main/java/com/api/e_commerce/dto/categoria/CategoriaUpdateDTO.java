package com.api.e_commerce.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar categorías existentes
 * Usado en PUT requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaUpdateDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;
}
