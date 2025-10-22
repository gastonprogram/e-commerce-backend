package com.api.e_commerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.model.Producto;
import com.api.e_commerce.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET /api/productos - Obtener todos los productos ordenados alfabéticamente
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} - Obtener producto por ID (detalle)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/productos/categoria/{categoryId} - Obtener productos por categoría
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Long categoryId) {
        List<Producto> productos = productoService.obtenerProductosPorCategoria(categoryId);
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/buscar?nombre=laptop - Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/usuario/{usuarioId} - Obtener productos publicados por un
    // usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Producto>> obtenerProductosPorUsuario(@PathVariable Long usuarioId) {
        List<Producto> productos = productoService.obtenerProductosPorUsuario(usuarioId);
        return ResponseEntity.ok(productos);
    }

    // POST /api/productos?usuarioId={id}&categoriasIds={id1,id2,id3} - Crear nuevo
    // producto
    // (publicación) con múltiples categorías
    @PostMapping
    public ResponseEntity<Producto> crearProducto(
            @RequestBody Producto producto,
            @RequestParam Long usuarioId,
            @RequestParam List<Long> categoriasIds) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto, usuarioId, categoriasIds);
            return ResponseEntity.ok(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/productos/{id} - Actualizar producto (cualquier usuario autenticado)
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/productos/{id}/stock?stock={cantidad} - Actualizar stock
    @PutMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        try {
            Producto producto = productoService.actualizarStock(id, stock);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/productos/{id}/stock/{cantidad} - Verificar si hay stock disponible
    @GetMapping("/{id}/stock/{cantidad}")
    public ResponseEntity<Boolean> verificarStock(@PathVariable Long id, @PathVariable Integer cantidad) {
        try {
            boolean tieneStock = productoService.tieneStock(id, cantidad);
            return ResponseEntity.ok(tieneStock);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    // PUT /api/productos/{id}/categorias/{categoriaId} - Agregar categoría a
    // producto
    @PutMapping("/{id}/categorias/{categoriaId}")
    public ResponseEntity<Producto> agregarCategoriaAProducto(
            @PathVariable Long id,
            @PathVariable Long categoriaId) {
        try {
            Producto producto = productoService.agregarCategoriaAProducto(id, categoriaId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/productos/{id}/categorias/{categoriaId} - Quitar categoría de
    // producto
    @DeleteMapping("/{id}/categorias/{categoriaId}")
    public ResponseEntity<Producto> quitarCategoriaDeProducto(
            @PathVariable Long id,
            @PathVariable Long categoriaId) {
        try {
            Producto producto = productoService.quitarCategoriaDeProducto(id, categoriaId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/productos/{id} - Eliminar producto (cualquier usuario
    // autenticado)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
