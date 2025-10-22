package com.api.e_commerce.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Data
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String image;

    // Relación Many-to-Many con Categoria (un producto puede tener muchas
    // categorías)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "producto_categoria", joinColumns = @JoinColumn(name = "producto_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "productos" })
    private Set<Categoria> categorias = new HashSet<>();

    // Usuario que creó/publica el producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "contrasena" })
    private Usuario usuario;
}
