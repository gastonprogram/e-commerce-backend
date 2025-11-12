package com.api.e_commerce.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un producto.
 * Esta excepción se usa cuando se busca un producto por ID y no existe en la
 * base de datos.
 */
public class ProductoNotFoundException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje personalizado.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    public ProductoNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe un ID y crea un mensaje automático.
     * 
     * @param id El ID del producto que no fue encontrado
     */
    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }
}
