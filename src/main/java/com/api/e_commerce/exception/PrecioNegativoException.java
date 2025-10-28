package com.api.e_commerce.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta crear o actualizar
 * un producto con un precio negativo.
 * Los precios deben ser siempre mayores o iguales a cero.
 */
public class PrecioNegativoException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje personalizado.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    public PrecioNegativoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe el precio inválido y crea un mensaje automático.
     * 
     * @param precio El precio negativo que se intentó usar
     */
    public PrecioNegativoException(Double precio) {
        super("El precio no puede ser negativo. Precio recibido: " + precio);
    }
}
