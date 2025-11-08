package com.api.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por nombre
    List<Producto> findByNameContainingIgnoreCase(String name);

    // Ordenar productos alfabéticamente
    List<Producto> findAllByOrderByNameAsc();

    // Buscar productos por categoría ordenados alfabéticamente (relación
    // ManyToMany)
    List<Producto> findByCategoriasIdOrderByNameAsc(Long categoriaId);
}