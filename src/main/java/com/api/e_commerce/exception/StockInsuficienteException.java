package com.api.e_commerce.exception;

/**
 * Excepción personalizada que se lanza cuando no hay suficiente stock
 * disponible para realizar una operación (checkout, reserva, etc.).
 */
public class StockInsuficienteException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje personalizado.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe detalles del producto y stock.
     * 
     * @param nombreProducto     El nombre del producto sin stock suficiente
     * @param stockDisponible    El stock disponible actual
     * @param cantidadSolicitada La cantidad que se intentó solicitar
     */
    public StockInsuficienteException(String nombreProducto, Integer stockDisponible, Integer cantidadSolicitada) {
        super(String.format(
                "Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                nombreProducto, stockDisponible, cantidadSolicitada));
    }
}
