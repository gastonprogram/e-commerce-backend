package com.api.e_commerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.dto.CheckoutRequestDTO;
import com.api.e_commerce.dto.PedidoResponseDTO;
import com.api.e_commerce.dto.PedidoMapper;
import com.api.e_commerce.model.Pedido;
import com.api.e_commerce.service.PedidoService;

/**
 * Controlador REST para gestionar pedidos.
 * Maneja las operaciones de checkout, consulta de pedidos individuales y
 * listado completo.
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173/" })
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

            Pedido pedido = pedidoService.realizarCheckout(email, request.getItems());

            // Convertir la entidad a DTO para la respuesta
            PedidoResponseDTO response = PedidoMapper.toResponseDTO(pedido);

            return ResponseEntity.ok(response);
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
        Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id);

        // Si el pedido existe, lo convertimos a DTO y lo devolvemos
        return pedido.map(p -> ResponseEntity.ok(PedidoMapper.toResponseDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/pedidos
     * Obtiene todos los pedidos del sistema (para administradores).
     * 
     * @return ResponseEntity con lista de todos los pedidos (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();

        // Convertir la lista de entidades a lista de DTOs
        List<PedidoResponseDTO> response = PedidoMapper.toResponseDTOList(pedidos);

        return ResponseEntity.ok(response);
    }
}