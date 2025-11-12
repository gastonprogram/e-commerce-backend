package com.api.e_commerce.dto.producto;

import com.api.e_commerce.dto.categoria.CategoriaSimpleDTO;
import com.api.e_commerce.dto.usuario.UsuarioSimpleDTO;
import com.api.e_commerce.model.Categoria;
import com.api.e_commerce.model.Producto;
import com.api.e_commerce.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Producto y sus DTOs
 */
@Component
public class ProductoMapper {

    /**
     * Convierte una entidad Producto a ProductoDTO
     */
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setName(producto.getName());
        dto.setDescription(producto.getDescription());
        dto.setPrice(producto.getPrice());
        dto.setStock(producto.getStock());
        dto.setImage(producto.getImage());

        // Mapear categorías
        if (producto.getCategorias() != null) {
            Set<CategoriaSimpleDTO> categoriasDTO = producto.getCategorias().stream()
                    .map(this::categoriaToSimpleDTO)
                    .collect(Collectors.toSet());
            dto.setCategorias(categoriasDTO);
        }

        // Mapear usuario
        if (producto.getUsuario() != null) {
            dto.setUsuario(usuarioToSimpleDTO(producto.getUsuario()));
        }

        return dto;
    }

    /**
     * Convierte una lista de productos a lista de DTOs
     */
    public List<ProductoDTO> toDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un ProductoCreateDTO a entidad Producto
     */
    public Producto toEntity(ProductoCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Producto producto = new Producto();
        producto.setName(dto.getName());
        producto.setDescription(dto.getDescription());
        producto.setPrice(dto.getPrice());
        producto.setStock(dto.getStock());
        producto.setImage(dto.getImage());
        producto.setCategorias(new HashSet<>());

        return producto;
    }

    /**
     * Actualiza una entidad Producto con los datos de ProductoUpdateDTO
     */
    public void updateEntity(Producto producto, ProductoUpdateDTO dto) {
        if (dto.getName() != null) {
            producto.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            producto.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            producto.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            producto.setStock(dto.getStock());
        }
        if (dto.getImage() != null) {
            producto.setImage(dto.getImage());
        }
    }

    /**
     * Convierte una Categoría a CategoriaSimpleDTO
     */
    private CategoriaSimpleDTO categoriaToSimpleDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaSimpleDTO(categoria.getId(), categoria.getName());
    }

    /**
     * Convierte un Usuario a UsuarioSimpleDTO
     */
    private UsuarioSimpleDTO usuarioToSimpleDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioSimpleDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail());
    }
}
