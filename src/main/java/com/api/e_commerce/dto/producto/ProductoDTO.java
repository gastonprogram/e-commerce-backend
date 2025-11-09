package com.api.e_commerce.dto.producto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

import com.api.e_commerce.dto.categoria.CategoriaSimpleDTO;
import com.api.e_commerce.dto.usuario.UsuarioSimpleDTO;

/**
 * DTO para mostrar información de productos
 * Usado en GET requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Set<CategoriaSimpleDTO> categorias;
    private UsuarioSimpleDTO usuario;
}
