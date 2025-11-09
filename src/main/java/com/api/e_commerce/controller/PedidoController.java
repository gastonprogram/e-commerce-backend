package com.api.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.dto.pedido.CheckoutRequestDTO;
import com.api.e_commerce.dto.pedido.PedidoResponseDTO;
import com.api.e_commerce.service.PedidoService;

/**
 * Controlador REST para gestionar pedidos.
 * Maneja las operaciones de checkout
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * POST /api/pedidos/checkout
     * Realiza el checkout del carrito de compras.
     * Recibe una lista de productos con cantidades y crea un nuevo pedido.
     * 
     * Ejemplo de JSON de entrada:
     * {
     * "usuarioId": 1,
     * "items": [
     * {"productoId": 1, "cantidad": 2},
     * {"productoId": 3, "cantidad": 1}
     * ]
     * }
     * 
     * @param request DTO con el ID del usuario y los items del carrito
     * @return ResponseEntity con el pedido creado (200 OK) o error (400 Bad
     *         Request)
     */
    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponseDTO> realizarCheckout(
            @RequestBody CheckoutRequestDTO request,
            Authentication authentication) {
        try {
            // Procesar el checkout usando el servicio
            String email = authentication.getName();
            PedidoResponseDTO pedido = pedidoService.realizarCheckout(email, request.getItems());
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            // Si hay algún error (stock insuficiente, usuario no existe, etc.)
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/pedidos/{id}
     * Obtiene un pedido específico por su ID.
     * 
     * @param id ID del pedido a buscar
     * @return ResponseEntity con el pedido (200 OK) o not found (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}