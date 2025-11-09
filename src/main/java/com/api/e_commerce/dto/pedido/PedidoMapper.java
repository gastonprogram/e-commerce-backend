package com.api.e_commerce.dto.pedido;

import com.api.e_commerce.model.Pedido;
import com.api.e_commerce.model.PedidoDetalle;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para convertir entre entidades de Pedido y sus DTOs.
 * Evita la exposición directa de las entidades JPA en los endpoints.
 */
public class PedidoMapper {

    /**
     * Convierte una entidad Pedido a su DTO de respuesta.
     * Incluye todos los detalles del pedido.
     * 
     * @param pedido Entidad de pedido a convertir
     * @return DTO con la información del pedido
     */
    public static PedidoResponseDTO toResponseDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        // Crear el DTO principal del pedido
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setUsuarioId(pedido.getUsuario().getId());
        dto.setNombreUsuario(pedido.getUsuario().getNombre());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setMontoTotal(pedido.getMontoTotal());

        // Convertir cada detalle del pedido a su DTO
        List<PedidoDetalleDTO> detallesDTO = pedido.getDetalles().stream()
                .map(PedidoMapper::toDetalleDTO)
                .collect(Collectors.toList());

        dto.setDetalles(detallesDTO);

        return dto;
    }

    /**
     * Convierte una entidad PedidoDetalle a su DTO.
     * 
     * @param detalle Entidad de detalle a convertir
     * @return DTO con la información del detalle
     */
    public static PedidoDetalleDTO toDetalleDTO(PedidoDetalle detalle) {
        if (detalle == null) {
            return null;
        }

        return new PedidoDetalleDTO(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getProducto().getName(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal());
    }

    /**
     * Convierte una lista de pedidos a una lista de DTOs.
     * Útil para el endpoint que devuelve todos los pedidos.
     * 
     * @param pedidos Lista de entidades de pedido
     * @return Lista de DTOs
     */
    public static List<PedidoResponseDTO> toResponseDTOList(List<Pedido> pedidos) {
        if (pedidos == null) {
            return null;
        }

        return pedidos.stream()
                .map(PedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
