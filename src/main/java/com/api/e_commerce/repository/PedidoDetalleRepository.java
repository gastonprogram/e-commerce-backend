package com.api.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.PedidoDetalle;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

    // Buscar detalles por pedido
    List<PedidoDetalle> findByPedidoId(Long pedidoId);

    // Buscar detalles por producto
    List<PedidoDetalle> findByProductoId(Long productoId);

    // Buscar detalle específico de un producto en un pedido
    Optional<PedidoDetalle> findByPedidoIdAndProductoId(Long pedidoId, Long productoId);

    // Eliminar todos los detalles de un pedido
    void deleteByPedidoId(Long pedidoId);

    // Contar cuántas veces se ha pedido un producto
    @Query("SELECT COUNT(pd) FROM PedidoDetalle pd WHERE pd.producto.id = :productoId")
    Long contarPedidosDeProducto(@Param("productoId") Long productoId);
}