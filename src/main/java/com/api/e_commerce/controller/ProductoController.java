package com.api.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.dto.producto.ProductoCreateDTO;
import com.api.e_commerce.dto.producto.ProductoDTO;
import com.api.e_commerce.dto.producto.ProductoUpdateDTO;
import com.api.e_commerce.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET /api/productos - Obtener todos los productos ordenados alfabéticamente
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        List<ProductoDTO> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} - Obtener producto por ID (detalle)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.obtenerProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/productos/categoria/{categoryId} - Obtener productos por categoría
    @GetMapping("/categoria/{categoryId}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductosPorCategoria(@PathVariable Long categoryId) {
        List<ProductoDTO> productos = productoService.obtenerProductosPorCategoria(categoryId);
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/buscar?nombre=laptop - Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductosPorNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * POST /api/productos
     * Crear nuevo producto
     * El usuarioId se obtiene automáticamente del token JWT (usuario autenticado)
     * 
     * Ejemplo de JSON:
     * {
     * "name": "Mouse Gamer",
     * "description": "Mouse RGB con 7 botones",
     * "price": 50.0,
     * "stock": 15,
     * "image": "https://example.com/mouse.jpg",
     * "categoriasIds": [1, 2]
     * }
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(
            @Valid @RequestBody ProductoCreateDTO productoDTO,
            Authentication authentication) {
        try {
            // Obtener el email del usuario autenticado desde el token JWT
            String email = authentication.getName();

            System.out.println("=== CREANDO PRODUCTO ===");
            System.out.println("Email usuario: " + email);
            System.out.println("Producto: " + productoDTO.getName());
            System.out.println("Precio: " + productoDTO.getPrice());
            System.out.println("Stock: " + productoDTO.getStock());

            // Crear el producto usando el email del usuario autenticado
            ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO, email);

            System.out.println("Producto creado exitosamente con ID: " + nuevoProducto.getId());

            return ResponseEntity.ok(nuevoProducto);
        } catch (Exception e) {
            System.err.println("ERROR al crear producto: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT /api/productos/{id} - Actualizar producto (cualquier usuario autenticado)
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO productoDTO) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/productos/{id} - Eliminar producto (usuario admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (com.api.e_commerce.exception.ProductoEnPedidosException ex) {
            // Devolver 409 Conflict con los detalles de pedido que impiden la eliminación
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
