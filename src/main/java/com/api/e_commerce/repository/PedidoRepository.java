package com.api.e_commerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por usuario ordenados por fecha descendente
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId);

    // Buscar pedidos por rango de fechas
    List<Pedido> findByFechaPedidoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

}