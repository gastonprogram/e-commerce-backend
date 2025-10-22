package com.api.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por nombre
    List<Producto> findByNameContainingIgnoreCase(String name);

    // Buscar productos por categoría (relación ManyToMany)
    List<Producto> findByCategoriasId(Long categoriaId);

    // Buscar productos con stock disponible
    List<Producto> findByStockGreaterThan(Integer stock);

    // Buscar productos por usuario (productos publicados por un usuario)
    List<Producto> findByUsuarioId(Long usuarioId);

    // Buscar productos por categoría con stock disponible (relación ManyToMany)
    List<Producto> findByCategoriasIdAndStockGreaterThan(Long categoriaId, Integer stock);

    // Ordenar productos alfabéticamente
    List<Producto> findAllByOrderByNameAsc();

    // Buscar productos por categoría ordenados alfabéticamente (relación
    // ManyToMany)
    List<Producto> findByCategoriasIdOrderByNameAsc(Long categoriaId);
}