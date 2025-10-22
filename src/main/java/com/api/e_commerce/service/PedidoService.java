package com.api.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.model.Pedido;
import com.api.e_commerce.model.PedidoDetalle;
import com.api.e_commerce.model.Producto;
import com.api.e_commerce.model.Usuario;
import com.api.e_commerce.repository.PedidoRepository;
import com.api.e_commerce.repository.ProductoRepository;
import com.api.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoService productoService;

    // Clase helper para representar items del carrito
    public static class ItemCarrito {
        private Long productoId;
        private Integer cantidad;

        public ItemCarrito() {
        }

        public ItemCarrito(Long productoId, Integer cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    // Realizar checkout del carrito
    public Pedido realizarCheckout(Long usuarioId, List<ItemCarrito> itemsCarrito) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        // Validar stock y crear detalles
        for (ItemCarrito item : itemsCarrito) {
            // Verificar que hay stock suficiente
            if (!productoService.tieneStock(item.getProductoId(), item.getCantidad())) {
                Producto producto = productoRepository.findById(item.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getName());
            }

            // Obtener el producto
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + item.getProductoId()));

            // Crear detalle del pedido
            PedidoDetalle detalle = new PedidoDetalle(producto, item.getCantidad());
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
        }

        // Guardar el pedido (esto calculará automáticamente el monto total)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Descontar stock de todos los productos
        for (ItemCarrito item : itemsCarrito) {
            productoService.descontarStock(item.getProductoId(), item.getCantidad());
        }

        return pedidoGuardado;
    }

    // Obtener pedidos de un usuario
    public List<Pedido> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId);
    }

    // Obtener pedido por ID
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // Obtener todos los pedidos
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }
}