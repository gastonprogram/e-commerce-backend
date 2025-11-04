package com.api.e_commerce.service;

import java.util.ArrayList;
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
import com.api.e_commerce.repository.PedidoDetalleRepository;
import com.api.e_commerce.exception.ProductoEnPedidosException;
import com.api.e_commerce.model.PedidoDetalle;
import com.api.e_commerce.repository.UsuarioRepository;

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

    /**
     * Crear nuevo producto de forma simple - método principal
     * Las categorías vienen en el JSON del producto
     * 
     * @param producto Datos del producto a crear (con categorías incluidas)
     * @param email    Email del usuario autenticado (obtenido del JWT)
     * @return El producto creado
     */
    public Producto crearProductoSimple(Producto producto, String email) {
        // Asegurarnos de que no intentamos hacer merge de una entidad con id
        // proporcionado por el cliente (evita errores tipo "Row was updated or deleted...")
        producto.setId(null);
        // Buscar el usuario por email (obtenido del token JWT)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        producto.setUsuario(usuario);

        // Si el producto tiene categorías en el JSON, validar que existan
        if (producto.getCategorias() != null && !producto.getCategorias().isEmpty()) {
            // Extraer ids y recuperar entidades gestionadas desde la base
            List<Long> categoriaIds = new ArrayList<>();
            for (Categoria categoria : producto.getCategorias()) {
                if (categoria.getId() != null) {
                    categoriaIds.add(categoria.getId());
                }
            }

            Set<Categoria> categoriasValidadas = new HashSet<>();
            for (Long categoriaId : categoriaIds) {
                Categoria categoriaDB = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
                categoriasValidadas.add(categoriaDB);
            }

            // Asignar el conjunto gestionado al producto (evita problemas de entidades detached)
            producto.setCategorias(categoriasValidadas);
            // Mantener la relación bidireccional: agregar el producto a la colección de cada categoría
            for (Categoria categoriaDB : categoriasValidadas) {
                categoriaDB.getProductos().add(producto);
            }
        }

        return productoRepository.save(producto);
    }

    /**
     * Crear nuevo producto usando el email del usuario autenticado
     * 
     * @param producto      Datos del producto a crear
     * @param email         Email del usuario autenticado (obtenido del JWT)
     * @param categoriasIds Lista de IDs de categorías (opcional)
     * @return El producto creado
     */
    public Producto crearProductoPorEmail(Producto producto, String email, List<Long> categoriasIds) {
        // Buscar el usuario por email (obtenido del token JWT)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Verificar que las categorías existen y obtenerlas (si se proporcionaron)
        Set<Categoria> categorias = new HashSet<>();
        if (categoriasIds != null && !categoriasIds.isEmpty()) {
            for (Long categoriaId : categoriasIds) {
                Categoria categoria = categoriaRepository.findById(categoriaId)
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));
                categorias.add(categoria);
            }
        }

        producto.setUsuario(usuario);
        producto.setCategorias(categorias);
        // mantener bidireccional
        for (Categoria c : categorias) {
            c.getProductos().add(producto);
        }

        return productoRepository.save(producto);
    }

    // Crear nuevo producto (publicación) - MÉTODO ANTIGUO mantenido para
    // compatibilidad
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
        // mantener bidireccional: agregar el producto al set de cada categoría
        for (Categoria c : categorias) {
            c.getProductos().add(producto);
        }
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

        // Antes de eliminar, comprobar si existen detalles de pedido que referencien este producto
        List<PedidoDetalle> detalles = pedidoDetalleRepository.findByProductoId(id);
        if (detalles != null && !detalles.isEmpty()) {
            throw new ProductoEnPedidosException();
        }

        productoRepository.deleteById(id);
    }
}
