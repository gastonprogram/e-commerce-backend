package com.api.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.e_commerce.model.Categoria;
import com.api.e_commerce.repository.CategoriaRepository;
import com.api.e_commerce.dto.categoria.CategoriaCreateDTO;
import com.api.e_commerce.dto.categoria.CategoriaDTO;
import com.api.e_commerce.dto.categoria.CategoriaMapper;
import com.api.e_commerce.dto.categoria.CategoriaUpdateDTO;
import com.api.e_commerce.exception.CategoriaNotFoundException;
import com.api.e_commerce.exception.CategoriaDuplicadaException;

@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaMapper categoriaMapper;

    // Obtener todas las categorías ordenadas alfabéticamente
    public List<CategoriaDTO> obtenerTodasLasCategorias() {
        List<Categoria> categorias = categoriaRepository.findAllByOrderByNameAsc();
        return categoriaMapper.toDTOList(categorias);
    }

    // Obtener categoría por ID
    public CategoriaDTO obtenerCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        return categoriaMapper.toDTO(categoria);
    }

    // Crear nueva categoría
    public CategoriaDTO crearCategoria(CategoriaCreateDTO dto) {
        // Verificar que no existe una categoría con el mismo nombre
        if (categoriaRepository.existsByName(dto.getName())) {
            throw new CategoriaDuplicadaException(dto.getName(), false);
        }

        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(categoriaGuardada);
    }

    // Actualizar categoría
    public CategoriaDTO actualizarCategoria(Long id, CategoriaUpdateDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));

        // Verificar que no existe otra categoría con el mismo nombre
        if (!categoria.getName().equals(dto.getName()) &&
                categoriaRepository.existsByName(dto.getName())) {
            throw new CategoriaDuplicadaException(dto.getName(), true);
        }

        categoriaMapper.updateEntity(categoria, dto);
        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        return categoriaMapper.toDTO(categoriaActualizada);
    }

    // Eliminar categoría
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        categoriaRepository.deleteById(id);
    }
}