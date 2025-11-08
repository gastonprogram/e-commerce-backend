package com.api.e_commerce.exception;

public class ProductoEnPedidosException extends RuntimeException {
    public ProductoEnPedidosException() {
        super("El producto no puede ser eliminado porque está asociado a pedidos existentes.");
    }
}
