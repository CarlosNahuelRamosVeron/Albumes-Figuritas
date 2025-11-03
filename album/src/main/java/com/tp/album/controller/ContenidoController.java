package com.tp.album.controller;

import com.tp.album.model.dto.ContenidoResponseDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.mapper.ContenidoMapper;
import com.tp.album.service.ContenidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contenidos")
public class ContenidoController {

    private final ContenidoService contenidoService;
    private final ContenidoMapper contenidoMapper;

    public ContenidoController(ContenidoService contenidoService, ContenidoMapper contenidoMapper) {
        this.contenidoService = contenidoService;
        this.contenidoMapper = contenidoMapper;
    }

    @GetMapping("/{contenidoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContenidoResponseDTO> obtenerContenido(@PathVariable Long contenidoId) {
        Contenido contenido = contenidoService.obtenerContenido(contenidoId);
        ContenidoResponseDTO contenidoDTO = contenidoMapper.toContenidoResponseDTO(contenido);
        return ResponseEntity.ok(contenidoDTO);
    }

    @DeleteMapping("/{contenidoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarContenido(@PathVariable Long contenidoId) {
        contenidoService.eliminarContenido(contenidoId);
        return ResponseEntity.noContent().build();
    }
}
