package com.api.e_commerce.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.e_commerce.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar categoría por nombre
    Optional<Categoria> findByName(String name);

    // Verificar si existe una categoría con ese nombre
    boolean existsByName(String name);

    // Obtener todas las categorías ordenadas alfabéticamente
    List<Categoria> findAllByOrderByNameAsc();
}