package com.api.e_commerce.dto;

import java.util.List;

/**
 * DTO para recibir la solicitud de checkout del carrito de compras.
 * Este DTO se usa cuando el usuario finaliza su compra.
 */
public class CheckoutRequestDTO {

    // Lista de items (productos) que el usuario quiere comprar
    private List<ItemCarritoDTO> items;

    // Constructor vacío necesario para que Spring pueda deserializar el JSON
    public CheckoutRequestDTO() {
    }

    // Constructor con parámetros para facilitar la creación en tests
    public CheckoutRequestDTO(List<ItemCarritoDTO> items) {
        this.items = items;
    }

    public List<ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
    }
}
