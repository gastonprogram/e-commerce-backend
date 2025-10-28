package com.api.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.dto.ItemCarritoDTO;
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

    /**
     * Realiza el checkout del carrito de compras.
     * Valida el stock, crea el pedido y descuenta el inventario.
     * 
     * @param usuarioId    ID del usuario que realiza la compra
     * @param itemsCarrito Lista de items a comprar
     * @return El pedido creado
     * @throws RuntimeException si no hay stock suficiente o el usuario no existe
     */
    public Pedido realizarCheckout(Long usuarioId, List<ItemCarritoDTO> itemsCarrito) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        // Validar stock y crear detalles
        for (ItemCarritoDTO item : itemsCarrito) {
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
        for (ItemCarritoDTO item : itemsCarrito) {
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