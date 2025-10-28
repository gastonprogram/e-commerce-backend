package com.api.e_commerce.dto;

import java.math.BigDecimal;

/**
 * DTO que representa un detalle individual dentro de un pedido.
 * Contiene la información de un producto específico comprado con su cantidad y
 * precio.
 */
public class PedidoDetalleDTO {

    // ID del detalle
    private Long id;

    // ID del producto comprado
    private Long productoId;

    // Nombre del producto (para mostrar en el frontend sin hacer consultas
    // adicionales)
    private String nombreProducto;

    // Cantidad de unidades compradas
    private Integer cantidad;

    // Precio unitario al momento de la compra (histórico)
    private BigDecimal precioUnitario;

    // Subtotal de este item (cantidad * precioUnitario)
    private BigDecimal subtotal;

    // Constructor vacío
    public PedidoDetalleDTO() {
    }

    // Constructor completo para facilitar la creación
    public PedidoDetalleDTO(Long id, Long productoId, String nombreProducto,
            Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id = id;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
