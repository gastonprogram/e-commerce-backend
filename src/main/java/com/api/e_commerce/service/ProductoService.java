package com.api.e_commerce.service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.model.Producto;
import com.api.e_commerce.model.Categoria;
import com.api.e_commerce.model.Usuario;
import com.api.e_commerce.repository.ProductoRepository;
import com.api.e_commerce.repository.CategoriaRepository;
import com.api.e_commerce.repository.PedidoDetalleRepository;
import com.api.e_commerce.exception.ProductoEnPedidosException;
import com.api.e_commerce.exception.ProductoNotFoundException;
import com.api.e_commerce.model.PedidoDetalle;
import com.api.e_commerce.repository.UsuarioRepository;
import com.api.e_commerce.dto.producto.ProductoCreateDTO;
import com.api.e_commerce.dto.producto.ProductoDTO;
import com.api.e_commerce.dto.producto.ProductoMapper;
import com.api.e_commerce.dto.producto.ProductoUpdateDTO;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoMapper productoMapper;

    // Obtener todos los productos ordenados alfabéticamente (para home)
    public List<ProductoDTO> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepository.findAllByOrderByNameAsc();
        return productoMapper.toDTOList(productos);
    }

    // Obtener producto por ID (para detalle)
    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return productoMapper.toDTO(producto);
    }

    // Obtener productos por categoría ordenados alfabéticamente
    public List<ProductoDTO> obtenerProductosPorCategoria(Long categoriaId) {
        List<Producto> productos = productoRepository.findByCategoriasIdOrderByNameAsc(categoriaId);
        return productoMapper.toDTOList(productos);
    }

    // Buscar productos por nombre
    public List<ProductoDTO> buscarProductosPorNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNameContainingIgnoreCase(nombre);
        return productoMapper.toDTOList(productos);
    }

    /**
     * Crear nuevo producto usando DTO
     * 
     * @param dto   Datos del producto a crear
     * @param email Email del usuario autenticado (obtenido del JWT)
     * @return El producto creado como DTO
     */
    public ProductoDTO crearProducto(ProductoCreateDTO dto, String email) {
        // Buscar el usuario por email (obtenido del token JWT)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Convertir DTO a entidad
        Producto producto = productoMapper.toEntity(dto);
        producto.setUsuario(usuario);

        // Si se proporcionaron IDs de categorías, validar y asignar
        if (dto.getCategoriasIds() != null && !dto.getCategoriasIds().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>();
            for (Long categoriaId : dto.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
                categorias.add(categoria);
            }
            producto.setCategorias(categorias);

            // Mantener la relación bidireccional
            for (Categoria categoria : categorias) {
                categoria.getProductos().add(producto);
            }
        }

        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toDTO(productoGuardado);
    }

    // Actualizar producto usando DTO
    public ProductoDTO actualizarProducto(Long id, ProductoUpdateDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Actualizar campos usando el mapper
        productoMapper.updateEntity(producto, dto);

        // Actualizar categorías si se proporcionan
        if (dto.getCategoriasIds() != null && !dto.getCategoriasIds().isEmpty()) {
            Set<Categoria> categorias = new HashSet<>();
            for (Long categoriaId : dto.getCategoriasIds()) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
                categorias.add(categoria);
            }
            producto.setCategorias(categorias);
        }

        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toDTO(productoActualizado);
    }

    // Descontar stock (para cuando se realiza un checkout)
    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getName());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    // Verificar si hay stock disponible (para validaciones antes del checkout)
    public boolean tieneStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        return producto.getStock() >= cantidad;
    }

    // Eliminar producto (cualquier usuario autenticado puede hacerlo)
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }

        // Antes de eliminar, comprobar si existen detalles de pedido que referencien
        // este producto
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findByProductoId(id);
        if (detalles != null && !detalles.isEmpty()) {
            throw new ProductoEnPedidosException();
        }

        productoRepository.deleteById(id);
    }
}
