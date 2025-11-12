package com.api.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.dto.pedido.ItemCarritoDTO;
import com.api.e_commerce.dto.pedido.PedidoMapper;
import com.api.e_commerce.dto.pedido.PedidoResponseDTO;
import com.api.e_commerce.exception.ProductoNotFoundException;
import com.api.e_commerce.exception.StockInsuficienteException;
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
     * @param email        Email del usuario que realiza la compra
     * @param itemsCarrito Lista de items a comprar
     * @return El pedido creado como DTO
     * @throws RuntimeException           si el usuario no existe
     * @throws ProductoNotFoundException  si algún producto no existe
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    public PedidoResponseDTO realizarCheckout(String email, List<ItemCarritoDTO> itemsCarrito) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);

        // Validar stock y crear detalles
        for (ItemCarritoDTO item : itemsCarrito) {
            // Verificar que hay stock suficiente
            if (!productoService.tieneStock(item.getProductoId(), item.getCantidad())) {
                Producto producto = productoRepository.findById(item.getProductoId())
                        .orElseThrow(() -> new ProductoNotFoundException(item.getProductoId()));
                throw new StockInsuficienteException(producto.getName(), producto.getStock(), item.getCantidad());
            }

            // Obtener el producto
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException(item.getProductoId()));

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

        return PedidoMapper.toResponseDTO(pedidoGuardado);
    }

    // Obtener pedido por ID
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
        return PedidoMapper.toResponseDTO(pedido);
    }
}