package com.api.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Anotación que indica que esta clase manejará excepciones de forma global para todos los controladores.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Anotación que indica que este método manejará las excepciones de tipo
    // ProductoNotFoundException.
    @ExceptionHandler(ProductoNotFoundException.class)
    // Este método se ejecuta cuando se lanza una ProductoNotFoundException.
    public ResponseEntity<String> manejarProductoNoEncontrado(ProductoNotFoundException ex) {

        // Devuelve una respuesta con el código de estado HTTP 404 (Not Found) y un
        // cuerpo con el mensaje "Producto no encontrado :)"
        // representa la respuesta HTTP completa que se envía desde tu controlador al
        // cliente (navegador, aplicación móvil, etc.).
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        // not_found es el código de error
        // body va el json con el mensaje de error

    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<String> manejarCategoriaNoEncontrada(CategoriaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PrecioNegativoException.class)
    public ResponseEntity<String> manejarPrecioNegativo(PrecioNegativoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<String> manejarStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CategoriaDuplicadaException.class)
    public ResponseEntity<String> manejarCategoriaDuplicada(CategoriaDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<String> manejarEmailDuplicado(EmailDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ProductoEnPedidosException.class)
    public ResponseEntity<String> manejarProductoEnPedidos(ProductoEnPedidosException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErroresGenerales(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
    }
}