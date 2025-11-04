package com.api.e_commerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "pedidos")
@EqualsAndHashCode(exclude = { "detalles" })
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que realizó el pedido
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "pedidos", "password" })
    private Usuario usuario;

    // Fecha y hora del pedido
    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    // Monto total del pedido
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    // Detalles del pedido (productos con cantidades)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PedidoDetalle> detalles = new ArrayList<>();

    // Constructor vacío
    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.montoTotal = BigDecimal.ZERO;
    }

    // Método para calcular el monto total
    @PrePersist
    @PreUpdate
    public void calcularMontoTotal() {
        this.montoTotal = detalles.stream()
                .map(PedidoDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}