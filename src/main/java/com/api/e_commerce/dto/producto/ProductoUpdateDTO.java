package com.api.e_commerce.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO para actualizar productos existentes
 * Usado en PUT requests
 * Todos los campos son opcionales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateDTO {
    private String name;
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private String image;
    private Set<Long> categoriasIds;
}
