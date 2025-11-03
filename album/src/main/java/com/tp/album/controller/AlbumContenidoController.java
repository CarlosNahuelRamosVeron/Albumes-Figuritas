package com.tp.album.controller;


import com.tp.album.model.dto.CargaContenidoResponseDTO;
import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.enumeration.ModoDistribucion;
import com.tp.album.model.mapper.ContenidoMapper;
import com.tp.album.service.ContenidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumContenidoController {

    private final ContenidoService contenidoService;
    private final ContenidoMapper contenidoMapper;

    public AlbumContenidoController(ContenidoService contenidoService, ContenidoMapper contenidoMapper) {
        this.contenidoService = contenidoService;
        this.contenidoMapper = contenidoMapper;
    }
    @PostMapping("/{albumId}/contenidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CargaContenidoResponseDTO> cargarContenido(@PathVariable("albumId") Long albumId, @RequestParam(name = "modo", defaultValue = "AUTOMATICO") ModoDistribucion modo,
                                                                     @Valid @RequestBody List<ContenidoDTO> contenidosDTO) {
        List<Contenido> creados = contenidoService.cargarContenido(albumId, contenidosDTO, modo);
        CargaContenidoResponseDTO response = contenidoMapper.toCargaContenidoResponseDTO(creados);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
