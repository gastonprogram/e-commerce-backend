package com.api.e_commerce.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta crear una categoría
 * con un nombre que ya existe en la base de datos.
 * Los nombres de categorías deben ser únicos.
 */
public class CategoriaDuplicadaException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje personalizado.
     * 
     * @param mensaje El mensaje de error que se mostrará al usuario
     */
    public CategoriaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor que recibe el nombre de la categoría duplicada.
     * 
     * @param nombreCategoria El nombre de la categoría que ya existe
     */
    public CategoriaDuplicadaException(String nombreCategoria, boolean isUpdate) {
        super(isUpdate
                ? "Ya existe otra categoría con el nombre: " + nombreCategoria
                : "Ya existe una categoría con el nombre: " + nombreCategoria);
    }
}
