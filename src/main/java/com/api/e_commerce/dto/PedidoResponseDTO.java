package com.api.e_commerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * DTO para enviar la información completa de un pedido como respuesta.
 * Evita problemas de serialización circular y expone solo los datos necesarios.
 */
public class PedidoResponseDTO {

    // ID único del pedido
    private Long id;

    // ID del usuario que realizó el pedido
    private Long usuarioId;

    // Nombre completo del usuario (para mostrar en el frontend)
    private String nombreUsuario;

    // Fecha y hora en que se realizó el pedido
    private LocalDateTime fechaPedido;

    // Monto total del pedido
    private BigDecimal montoTotal;

    // Lista de detalles del pedido (productos comprados)
    private List<PedidoDetalleDTO> detalles;

    // Constructor vacío
    public PedidoResponseDTO() {
        this.detalles = new ArrayList<>();
    }

    // Constructor completo para facilitar la creación
    public PedidoResponseDTO(Long id, Long usuarioId, String nombreUsuario,
            LocalDateTime fechaPedido, BigDecimal montoTotal,
            List<PedidoDetalleDTO> detalles) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.fechaPedido = fechaPedido;
        this.montoTotal = montoTotal;
        this.detalles = detalles != null ? detalles : new ArrayList<>();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public List<PedidoDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDetalleDTO> detalles) {
        this.detalles = detalles;
    }
}
