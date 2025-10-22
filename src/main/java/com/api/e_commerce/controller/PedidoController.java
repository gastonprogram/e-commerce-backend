package com.api.e_commerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.model.Pedido;
import com.api.e_commerce.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Clase helper para recibir el request del checkout
    public static class CheckoutRequest {
        private Long usuarioId;
        private List<PedidoService.ItemCarrito> items;

        public CheckoutRequest() {
        }

        public Long getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
        }

        public List<PedidoService.ItemCarrito> getItems() {
            return items;
        }

        public void setItems(List<PedidoService.ItemCarrito> items) {
            this.items = items;
        }
    }

    // POST /api/pedidos/checkout - Realizar checkout del carrito
    @PostMapping("/checkout")
    public ResponseEntity<Pedido> realizarCheckout(@RequestBody CheckoutRequest request) {
        try {
            Pedido pedido = pedidoService.realizarCheckout(request.getUsuarioId(), request.getItems());
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/pedidos/usuario/{usuarioId} - Obtener historial de pedidos del
    // usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    // GET /api/pedidos/{id} - Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id);
        return pedido.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/pedidos - Obtener todos los pedidos (para admin)
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }
}