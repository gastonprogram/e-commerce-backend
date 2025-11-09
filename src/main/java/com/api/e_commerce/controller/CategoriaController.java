package com.api.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.e_commerce.dto.categoria.CategoriaCreateDTO;
import com.api.e_commerce.dto.categoria.CategoriaDTO;
import com.api.e_commerce.dto.categoria.CategoriaUpdateDTO;
import com.api.e_commerce.service.CategoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // GET /api/categorias - Obtener todas las categorías ordenadas alfabéticamente
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodasLasCategorias() {
        List<CategoriaDTO> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    // GET /api/categorias/{id} - Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/categorias - Crear nueva categoría
    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaCreateDTO categoriaDTO) {
        try {
            CategoriaDTO nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);
            return ResponseEntity.ok(nuevaCategoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/categorias/{id} - Actualizar categoría
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdateDTO categoriaDTO) {
        try {
            CategoriaDTO categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaDTO);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/categorias/{id} - Eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}