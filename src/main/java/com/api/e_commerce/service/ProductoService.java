package com.api.e_commerce.service;

import java.util.List;
import java.util.Optional;
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
import com.api.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los productos ordenados alfabéticamente (para home)
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAllByOrderByNameAsc();
    }

    // Obtener producto por ID (para detalle)
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    // Obtener productos por categoría ordenados alfabéticamente
    public List<Producto> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriasIdOrderByNameAsc(categoriaId);
    }

    // Buscar productos por nombre
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNameContainingIgnoreCase(nombre);
    }

    // Obtener productos publicados por un usuario
    public List<Producto> obtenerProductosPorUsuario(Long usuarioId) {
        return productoRepository.findByUsuarioId(usuarioId);
    }

    // Crear nuevo producto (publicación)
    public Producto crearProducto(Producto producto, Long usuarioId, List<Long> categoriasIds) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        // Verificar que las categorías existen y obtenerlas
        Set<Categoria> categorias = new HashSet<>();
        for (Long categoriaId : categoriasIds) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
            categorias.add(categoria);
        }

        producto.setUsuario(usuario);
        producto.setCategorias(categorias);

        return productoRepository.save(producto);
    }

    // Actualizar producto (cualquier usuario autenticado puede hacerlo)
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    // Actualizar campos
                    if (productoActualizado.getName() != null) {
                        producto.setName(productoActualizado.getName());
                    }
                    if (productoActualizado.getDescription() != null) {
                        producto.setDescription(productoActualizado.getDescription());
                    }
                    if (productoActualizado.getPrice() != null) {
                        producto.setPrice(productoActualizado.getPrice());
                    }
                    if (productoActualizado.getStock() != null) {
                        producto.setStock(productoActualizado.getStock());
                    }
                    if (productoActualizado.getImage() != null) {
                        producto.setImage(productoActualizado.getImage());
                    }

                    // Actualizar categorías si se proporcionan
                    if (productoActualizado.getCategorias() != null && !productoActualizado.getCategorias().isEmpty()) {
                        Set<Categoria> nuevasCategorias = new HashSet<>();
                        for (Categoria cat : productoActualizado.getCategorias()) {
                            if (cat.getId() != null) {
                                Categoria categoria = categoriaRepository.findById(cat.getId())
                                        .orElseThrow(() -> new RuntimeException(
                                                "Categoría no encontrada con id: " + cat.getId()));
                                nuevasCategorias.add(categoria);
                            }
                        }
                        producto.setCategorias(nuevasCategorias);
                    }

                    return productoRepository.save(producto);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    // Actualizar solo el stock (cualquier usuario autenticado puede hacerlo)
    public Producto actualizarStock(Long id, Integer nuevoStock) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setStock(nuevoStock);
                    return productoRepository.save(producto);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
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

    // Agregar categoría a un producto existente
    public Producto agregarCategoriaAProducto(Long productoId, Long categoriaId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));

        producto.getCategorias().add(categoria);
        return productoRepository.save(producto);
    }

    // Quitar categoría de un producto existente
    public Producto quitarCategoriaDeProducto(Long productoId, Long categoriaId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));

        producto.getCategorias().remove(categoria);
        return productoRepository.save(producto);
    }

    // Eliminar producto (cualquier usuario autenticado puede hacerlo)
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }

        productoRepository.deleteById(id);
    }
}
