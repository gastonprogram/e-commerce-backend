package com.api.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.model.Categoria;
import com.api.e_commerce.repository.CategoriaRepository;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Obtener todas las categorías ordenadas alfabéticamente (para home)
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAllByOrderByNameAsc();
    }

    // Obtener categoría por ID
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    // Crear nueva categoría
    public Categoria crearCategoria(Categoria categoria) {
        // Verificar que no existe una categoría con el mismo nombre
        if (categoriaRepository.existsByName(categoria.getName())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getName());
        }
        return categoriaRepository.save(categoria);
    }

    // Actualizar categoría
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    // Verificar que no existe otra categoría con el mismo nombre
                    if (!categoria.getName().equals(categoriaActualizada.getName()) &&
                            categoriaRepository.existsByName(categoriaActualizada.getName())) {
                        throw new RuntimeException(
                                "Ya existe una categoría con el nombre: " + categoriaActualizada.getName());
                    }

                    categoria.setName(categoriaActualizada.getName());
                    return categoriaRepository.save(categoria);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
    }

    // Eliminar categoría
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}