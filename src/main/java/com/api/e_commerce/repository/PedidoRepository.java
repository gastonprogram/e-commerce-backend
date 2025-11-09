package com.api.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Todos los métodos personalizados fueron removidos ya que no se usan
}