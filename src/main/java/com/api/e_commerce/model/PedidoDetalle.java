package com.api.e_commerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "pedido_detalles")
@EqualsAndHashCode(exclude = { "pedido" }) // Evitar referencias circulares en equals/hashcode
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "detalles" })
    private Pedido pedido;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Producto producto;

    // Cantidad del producto en este pedido
    @Column(nullable = false)
    private Integer cantidad;

    // Precio unitario al momento del pedido (para mantener histórico)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // Subtotal calculado (cantidad * precioUnitario)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Método para calcular el subtotal automáticamente
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    // Constructor vacío
    public PedidoDetalle() {
    }

    // Constructor con parámetros
    public PedidoDetalle(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrice();
        calcularSubtotal();
    }
}