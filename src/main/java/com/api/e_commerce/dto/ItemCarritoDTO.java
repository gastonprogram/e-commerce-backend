package com.api.e_commerce.dto;

/**
 * DTO que representa un item del carrito de compras.
 * Contiene el ID del producto y la cantidad que el usuario quiere comprar.
 */
public class ItemCarritoDTO {

    // ID del producto que se quiere comprar
    private Long productoId;

    // Cantidad de unidades de este producto
    private Integer cantidad;

    // Constructor vacío necesario para que Spring pueda deserializar el JSON
    public ItemCarritoDTO() {
    }

    // Constructor con parámetros para facilitar la creación
    public ItemCarritoDTO(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    // Getters y Setters
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
