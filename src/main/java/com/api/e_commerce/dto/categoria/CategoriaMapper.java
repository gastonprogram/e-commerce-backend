package com.api.e_commerce.dto.categoria;

import com.api.e_commerce.model.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Categoria y sus DTOs
 */
@Component
public class CategoriaMapper {

    /**
     * Convierte una entidad Categoria a CategoriaDTO
     */
    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setName(categoria.getName());

        // Contar productos si la colección está inicializada
        if (categoria.getProductos() != null) {
            dto.setProductosCount(categoria.getProductos().size());
        } else {
            dto.setProductosCount(0);
        }

        return dto;
    }

    /**
     * Convierte una lista de categorías a lista de DTOs
     */
    public List<CategoriaDTO> toDTOList(List<Categoria> categorias) {
        return categorias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un CategoriaCreateDTO a entidad Categoria
     */
    public Categoria toEntity(CategoriaCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Categoria categoria = new Categoria();
        categoria.setName(dto.getName());

        return categoria;
    }

    /**
     * Actualiza una entidad Categoria con los datos de CategoriaUpdateDTO
     */
    public void updateEntity(Categoria categoria, CategoriaUpdateDTO dto) {
        if (dto.getName() != null) {
            categoria.setName(dto.getName());
        }
    }
}
