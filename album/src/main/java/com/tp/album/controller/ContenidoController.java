package com.tp.album.controller;

import com.tp.album.model.dto.ContenidoDTO;
import com.tp.album.model.entities.Contenido;
import com.tp.album.model.enumeration.ModoDistribucion;
import com.tp.album.service.ContenidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/contenidos")
public class ContenidoController {

    private final ContenidoService contenidoService;

    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @PostMapping("/albums/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Contenido>> cargarContenido(@PathVariable("id") Long albumId,
                                                           @RequestParam(name = "modo", defaultValue = "AUTOMATICO") ModoDistribucion modo,
                                                           @Valid @RequestBody List<ContenidoDTO> contenidosDTO) {
        try {
            List<Contenido> creados = contenidoService.cargarContenido(albumId, contenidosDTO, modo);
            return ResponseEntity.status(HttpStatus.CREATED).body(creados);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/albums/{id}")
    public ResponseEntity<List<Contenido>> obtenerContenidosByAlbumId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(contenidoService.obtenerContenidoByAlbumId(id));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{contenidoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contenido> obtenerContenido(@RequestParam Long contenidoId) {
        try {
            Contenido contenido = contenidoService.obtenerContenido(contenidoId);
            return ResponseEntity.ok(contenido);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("/{contenidoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarContenido(@RequestParam Long contenidoId) {
        contenidoService.eliminarContenido(contenidoId);
        return ResponseEntity.noContent().build();
    }

}
