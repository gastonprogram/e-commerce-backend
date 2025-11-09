package com.api.e_commerce.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una categoría.
 * Esta excepción se usa cuando se busca una categoría por ID y no existe en la
 * base de datos.
 */
public class CategoriaNotFoundException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje personalizado.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    public CategoriaNotFoundException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe un ID y crea un mensaje automático.
     * 
     * @param id El ID de la categoría que no fue encontrada
     */
    public CategoriaNotFoundException(Long id) {
        super("Categoría no encontrada con ID: " + id);
    }
}
